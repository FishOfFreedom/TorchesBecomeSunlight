# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 在此仓库中工作时提供指引。

## 项目概述

`Gathering Torches Become Sunlight`（`torchesbecomesunlight`）—— 明日方舟主题的 Minecraft Mod：包含 Boss 战、数据驱动的对话/剧情系统、玩家战斗技能系统、干员同伴（partner）、结构生成，以及烹饪与家具内容。**强依赖 RosmontisLib**（同作者，提供 ModularUI 框架、`@Persisted` NBT 同步、粒子/材质系统）。

包根目录：`com.freefish.torchesbecomesunlight`。代码注释与对话描述大量使用中文；语言文件同时提供 `en_us` 与 `zh_cn`。

> ⚠️ **本分支正在从 Forge 1.20.1 移植到 NeoForge 1.21.1**（分支 `feature/1.21.1-neoforge`）。
> **构建环境已完成；Java 代码迁移进行中，目前尚不能编译通过**（错误 3518 → 226）。
> 架构级重写（注册表、网络、Capability、粒子、进度触发器）均已完成，
> **符号解析阶段已清零**，剩余错误全部来自其后的类型/访问检查阶段。
> 详见文末「移植状态」。`master` 分支保持 1.20.1 Forge 不变。
>
> 下文架构描述已同步到 NeoForge 现状；涉及尚未移植部分会另行标注。

## 目标平台

与整合包**「最后的冬天」**（`D:\MC\snow`）对齐，版本号取自该整合包内实际 jar：

| 组件 | 版本 |
| --- | --- |
| Minecraft | 1.21.1 |
| NeoForge | 21.1.242 |
| Java | **21** |
| ModDevGradle | 2.0.144 |
| Parchment | 2024.11.17-1.21.1 |
| GeckoLib | `geckolib-neoforge-1.21.1:4.9.2` |
| JEI | `jei-1.21.1-neoforge:19.39.0.369` |
| RosmontisLib | `1.21.1-0.5.0-port.1`（本地 jar） |

整合包使用 **Sodium**，**不含 Oculus / Iris / Xenon**。

## 常用命令

```bash
./gradlew build                 # 编译 + 打包到 build/libs
./gradlew runClient             # 开发客户端
./gradlew runServer             # 开发服务端（--nogui）
./gradlew runData               # 数据生成 -> src/generated/resources（已入库，见下）
./gradlew createMinecraftArtifacts   # 拉取 NeoForge、反编译 MC 并应用 AT（改完 AT 后跑这个验证）
./gradlew --refresh-dependencies
```

**必须用 JDK 21 运行 Gradle**（ModDevGradle 的 NeoForm 运行时要求）。本机 `JAVA_HOME` 默认指向 JRE 8，需覆盖：

```bash
export JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-21.0.11.10-hotspot"
```

或取消 `gradle.properties` 中 `org.gradle.java.home` 那行的注释。

**RosmontisLib 走本地 jar**：`libs/rosmontislib-neoforge-1.21.1-0.5.0-port.1.jar`，由 `build.gradle` 的 `flatDir` 仓库提供。它没有公开 maven 坐标（jar 内无 pom 元数据），来源是整合包的 mods 目录。原 1.20.1 分支所用的私有 Nexus（`localhost:8081`，需 `MAVENNAME`/`PASSWORD`）已不再使用。

**本仓库没有测试**：不存在 `src/test`，也没有任何 `@GameTest` 方法。因此"验证改动"意味着执行 `build` 再加 `runClient` 实测。

版本与元数据位于 `gradle.properties`，由 `processResources` 展开写入 `META-INF/neoforge.mods.toml` 与 `pack.mcmeta`。

## 架构

### 包结构 —— `client/` 与 `server/` 是*逻辑端*划分，不是物理端

`server/` 存放全部 common + 逻辑服务端代码（注册表、实体、物品、方块、能力、剧情、配置、封包）。只有 `client/` 是真正仅客户端的内容（渲染器、模型、粒子、着色器、GUI、按键绑定）。`mixin/` 与二者并列（`compat/oculus` 已在移植时剥离）。`CommonProxy` / `ClientProxy` 主要用于注册 CLIENT 配置规格，选型改用 `FMLEnvironment.dist`（`DistExecutor` 在 1.21.1 已被移除）。

[TorchesBecomeSunlight.java](src/main/java/com/freefish/torchesbecomesunlight/TorchesBecomeSunlight.java) 是入口：**mod 总线由构造函数注入**（`TorchesBecomeSunlight(IEventBus bus, ModContainer container)`，不再有 `FMLJavaModLoadingContext`），所有 `DeferredRegister` 在此挂到总线；`commonSetup` 通过 `enqueueWork` 调用 `PartnerHandler.init()` 与结构处理器注册。静态成员只剩 `LOGGER`/`MOD_ID`/`random` 等，原来的 `NETWORK` 通道已随 `SimpleChannel` 一并移除。

### 注册表

全部位于 [server/init/](src/main/java/com/freefish/torchesbecomesunlight/server/init/)，以 `*Handle` 类持有 `DeferredRegister` 字段（`ItemHandle`、`BlockHandle`、`EntityHandle`、`SoundHandle`、`EffectHandle`、`ParticleHandler`、`MenuHandle` 等）。新增内容的流程：在对应 `*Handle` 中注册；若需要渲染，再到 [client/event/ClientEvent.java](src/main/java/com/freefish/torchesbecomesunlight/client/event/ClientEvent.java) 注册渲染器——该类统一处理 `RegisterRenderers`、粒子 provider、菜单 Screen 与配方书分类。

### 技能系统（玩家战斗）—— 按下标同步，服务端权威

源自 Mowzie's Mobs 的设计。一个 `Ability` 按固定的 `AbilitySection[]` 轨道推进（瞬时段 / 持续段），带每 tick 钩子、冷却与 NBT 持久化；`PlayerAbility` 在此之上增加第一/第三人称 GeckoLib 动画与手部渲染覆盖。实例保存在每个实体的 `AbilityCapability` 上。

流程：武器物品（或 AI）在**服务端**调用 `AbilityHandler.INSTANCE.sendAbilityMessage(entity, TYPE)` → capability 校验 `canUse()` / `tryAbility()` 并启动 → `MessageUseAbility` 发往 `TRACKING_ENTITY_AND_SELF`，客户端重放表现。

> **封包传的是技能在 `AbilityHandler.PLAYER_ABILITIES` 中的*下标*，不是名称。** 新增玩家技能必须**追加**到该数组末尾；重排该数组会破坏网络与存档兼容性。

[server/item/weapon/](src/main/java/com/freefish/torchesbecomesunlight/server/item/weapon/) 下的武器是主要触发点——每把武器把某个使用/攻击交互映射到一个 `AbilityType`。

### 怪物动画系统 —— 独立于技能系统，但有同样的下标约束

怪物使用 `AnimationAct`（`RawAnimation` + 时长 + 优先级，带 `start`/`tickUpdate`/`stop` 钩子），通过 `IAnimatedEntity` 驱动。`AnimationActHandler.INSTANCE.sendAnimationMessage(entity, act)` 在服务端设置动画，并**按 `entity.getAnimations()` 中的下标**同步——因此该数组就是协议契约，必须保持稳定且完整。播放中的高优先级动画不会被低优先级动画打断。

### 实体继承链

`PathfinderMob` → `FreeFishEntity`（Boss 血条、通过实体事件播放 Boss 音乐、以瞬态属性修饰符形式在**构造函数中**施加配置驱动的血量/攻击倍率）→ `AnimatedEntity`（GeckoLib `GeoEntity` + `AnimationAct` 状态机，自定义 `tickDeath` 与死亡动画时长挂钩）→ 各阵营基类，如 `GuerrillasEntity`、`RhodesIslandEntity`。Boss 的攻击逻辑位于 [server/entity/ai/entity/](src/main/java/com/freefish/torchesbecomesunlight/server/entity/ai/entity/)，每个 Boss 一个 `*AttackAI` goal。

### 网络

封包实现 `CustomPacketPayload`，各自持有 `TYPE`（`CustomPacketPayload.Type`，内含 ResourceLocation）与 `STREAM_CODEC`；序列化仍复用原有的静态 `serialize`/`deserialize`，由 `StreamCodec.of` 包装。注册集中在 [ServerNetwork.java](src/main/java/com/freefish/torchesbecomesunlight/server/event/ServerNetwork.java) 的 `RegisterPayloadHandlersEvent` 中，按方向调用 `playToClient` / `playToServer`。封包分置于 `event/packet/toclient` 与 `event/packet/toserver`，处理逻辑是各自的静态 `handle(msg, IPayloadContext)`。

> **契约变化**：NeoForge 按 `Type` 里的 ResourceLocation 寻址，**注册顺序不再是协议的一部分**（Forge 时代"只能追加"的限制在这里已解除）。但 `MessageUseAbility` / `AnimationActMessage` 传的仍是**数组下标**，所以 `AbilityHandler.PLAYER_ABILITIES` 与 `entity.getAnimations()` 那两个数组的"只能追加"约束**依然成立**。

发送请用 `ServerNetwork.toClientMessage` / `toServerMessage` / `toPlayer` / `toLevel` / `toAll`，它们封装了 `PacketDistributor`。

### Capability —— 已迁到 Data Attachments

NeoForge 移除了 Forge 的整套 Capability（`LazyOptional` / `ICapabilitySerializable` / `AttachCapabilitiesEvent`）。本 Mod 的四份数据是**挂在实体上的持久化状态**，因此迁到 **Data Attachments**（[TBSAttachments](src/main/java/com/freefish/torchesbecomesunlight/server/init/TBSAttachments.java)），而不是 NeoForge 新的 `EntityCapability`（后者面向物品栏/流体/能量这类能力查询）。

[CapabilityHandle](src/main/java/com/freefish/torchesbecomesunlight/server/capability/CapabilityHandle.java) 刻意保留了 `getCapability(entity, XXX_CAPABILITY)` 的调用形式，使 60 余处调用点无需改动；类型由 `Capability<T>` 换成了 `Supplier<AttachmentType<T>>`。

**一个语义要点**：attachment 的 `getData` 在数据不存在时会直接按默认值创建，所以 `CapabilityHandle` 里补了原 `attachEntityCapability` 的挂载条件（ABILITY/PLAYER 仅玩家、FROZEN 仅 `LivingEntity`、DIALOGUE 仅 `IDialogueEntity`），否则会给不该有该数据的实体凭空建出一份。顺带修好了 1.20.1 分支上 `DIALOGUE_CAPABILITY` 被 attach 却没在 `registerCapabilities` 注册的问题。

玩家死亡后的数据延续仍由 `EventListener` 的 `PlayerEvent.Clone` 处理，**未**改用 `copyOnDeath()`，以免悄悄改变原有行为。

方块物品栏走的是另一条路：那是真正的能力查询，仍用 Capability，在 [TBSCapabilities](src/main/java/com/freefish/torchesbecomesunlight/server/init/TBSCapabilities.java) 里通过 `RegisterCapabilitiesEvent` 按面注册（原先各 BlockEntity 覆写 `getCapability` 返回 `LazyOptional` 的写法已不存在）。

`PlayerCapability` 仍承载同伴管理器与部分技能触发逻辑。

### 剧情 / 对话 —— 数据驱动

对话是位于 `data/torchesbecomesunlight/dialogue/<角色>/*.json` 的 JSON，由 `DialogueManager` 从服务端 `ResourceManager` **按需读取**（不是 reload listener）。一段对话是以 `id`/`nextid` 相连的 `entries` 图，每个节点含说话人、作为 `text` 的**语言键**、时长，以及可选的 `triggers` / 选项。

四种多态类型通过自定义 deserializer 注册进 GSON，并各有一个 `*Type` 注册类：`Trigger`、`CanOption`、`Choose`、`Generatext`。新增变体时，除了写类，还**必须**在对应的 `*Type` 中注册。

`IDialogueEntity.startTalk(path, 参与者…)` 会向世界生成一个 `DialogueEntity` 来驱动播放与渲染；玩家侧进度记录在 `PlayerStoryStoneData`，任务位于 `story/task/`（`Task`、`TaskHandle`、`KillEntityTask`）。

### 同伴系统

`PartnerHandler` 在 `commonSetup` 阶段构建 `BiMap<ResourceLocation, PartnerType<?>>`（因而可以引用此时已构建好的 `EntityType`）。同伴既可包装原版生物（`partner/vanilla/`），也可包装本 Mod 的 Boss（`partner/self/`）。指令轮盘 GUI 是在 setup 阶段注册的 RosmontisLib `UIFactory`，由 `KeyHandler` 打开。

### 数据生成

`src/generated/resources` **已入库**，并被注册为 `main` 的资源目录。所有 provider 在 [DataGenerators.java](src/main/java/com/freefish/torchesbecomesunlight/server/init/generator/DataGenerators.java) 中接入（配方、伤害类型、物品模型、方块状态、进度、实体/结构标签、战利品表）。修改 provider 后需运行 `./gradlew runData` 并提交重新生成的产物。手写数据（对话、结构、世界生成、烹饪配方）位于 `src/main/resources/data/`，不参与生成。

### Mixin 与 Access Transformer

[torchesbecomesunlight.mixins.json](src/main/resources/torchesbecomesunlight.mixins.json) 在 `mixins` 下列出通用 mixin，在 `client` 下列出仅客户端 mixin——**新增的 mixin 类必须加进去，否则会静默失效**。`defaultRequire` 为 1。

NeoForge 下的几点与 Forge 不同：

- mixin 配置在 [neoforge.mods.toml](src/main/resources/META-INF/neoforge.mods.toml) 的 `[[mixins]]` 中声明，**不再**需要 mixin gradle 插件、`-mixin.config` 启动参数或 manifest 的 `MixinConfigs` 属性。
- **不需要 refmap**（NeoForge 直接使用 Mojang 官方映射），`refmap` 字段已移除；`compatibilityLevel` 为 `JAVA_21`。
- **AT 使用 Mojang 官方名，不是 SRG 名**：写 `skyBuffer` 而不是 `f_109472_`。改动 `META-INF/accesstransformer.cfg` 后用 `./gradlew createMinecraftArtifacts` 验证——条目无效会在这一步报错。
- MixinExtras 由 NeoForge 21.1 内置提供，无需单独引入或 jarJar。

若只是需要放开可见性，优先加 AT 条目而不是写 mixin。

### 兼容层

`ModDetectionCache` 负责判定可选联动。JEI 采用 API `compileOnly` + 完整 mod `runtimeOnly`。`compat/rosmontis/` 将本 Mod 实体适配到 RosmontisLib 的粒子/材质效果。

⚠️ **光影兼容需要重新决策**：`compat/oculus/ForgeOculusHandle` 与 `TorchesBecomeSunlight.isUsingShaderPack()` 直接引用 Oculus API，但目标整合包使用 Sodium 且**不含 Oculus/Iris**，原 `xenon` / `oculus` 两个 CurseForge 依赖已从 `build.gradle` 移除（保留了 TODO 注释）。在剥离这段代码或改接 Iris 之前，这两个文件无法编译。

原开发期运行时依赖（`mob-battle-mod`、`eeeabs-mobs`、`neat`）的 1.20.1 文件 ID 已失效，需要时再补 1.21.1 版本。

## 约定

- 可使用 Lombok（`@Getter`/`@Setter`），剧情数据类中已在用。
- 单例统一写作 `enum X { INSTANCE }`（`AbilityHandler`、`DialogueManager`、`AnimationActHandler`）。
- 全局静态成员（`MOD_ID`、`LOGGER`、`random`）都挂在 `TorchesBecomeSunlight` 上（`NETWORK` 已随 `SimpleChannel` 移除）。
- 配置集中在单个 `ConfigHandler`，含 COMMON 与 CLIENT 两套 `ForgeConfigSpec`；战斗平衡数值由 `FreeFishEntity` 在生成时读取，因此改配置只影响之后新构造的怪物。
- 所有面向玩家的字符串都必须是语言键，且 `en_us.json` 与 `zh_cn.json` 都要补齐。

## 移植状态（feature/1.21.1-neoforge）

### 已完成：构建环境

Gradle 侧已整体切到 NeoForge/ModDevGradle 并验证通过：

- `./gradlew createMinecraftArtifacts` 成功（5364 个源文件重编译），即 **33 条 AT 条目全部有效**。
- `./gradlew dependencies --configuration compileClasspath` 全部解析成功，无 FAILED。
- 具体改动：ForgeGradle → ModDevGradle 2.0.144；Java 17 → 21；`mods.toml` → `neoforge.mods.toml`；AT 全量 SRG → Mojang 名；mixins.json 去 refmap；`pack_format` 15 → 34（`supported_formats` 补 48）；RosmontisLib 由私有 Nexus 改为 `libs/` 本地 jar。

**AT 的一个坑**：NeoForge 不会像 ForgeGradle 那样把 AT 自动传播到子类重写。放开 `LivingEntity.actuallyHurt` 后，必须同时放开 6 个 vanilla 重写者（`Player`/`LocalPlayer`/`Animal`/`Wolf`/`Armadillo`/`Camel`），否则重写"降低访问权限"会导致 MC 重编译失败。

### 进行中：Java 代码

编译错误从 **3518 降到 226**（约 -94%）。架构级重写已全部完成，剩余为分散的 API 细节。符号解析阶段已清零，当前错误全部来自其后的类型/访问检查阶段。

> ⚠️ **看错误数时务必留意 javac 的分阶段行为**：parse → 符号解析 → 类型检查，**前一阶段没通过时，后一阶段的错误根本不会打印**。所以错误数骤降往往不是"接近完成"，而是"卡在了更早的阶段"。本次移植中已两次踩到：清零符号阶段时从"剩 1 个"跳到 819；一个批量脚本引入 40 个语法错误时，759 个类型错误被整体隐藏，看上去像是"755 → 40"的大跃进，修好语法后立刻回到 739。判断真实进度要看**阶段**，不能只看数字。用 `./gradlew compileJava` 复现；`gradle.properties` 里已加 `-Duser.language=en` 让 javac 输出英文（否则 Windows 中文控制台是 GBK 乱码），`build.gradle` 里已放开 `-Xmaxerrs`。

### 这次移植踩过的坑（后续接手务必先看）

1. **javac 分阶段**：parse → 符号解析 → 类型检查。前一阶段没通过，后一阶段的错误**根本不会打印**。所以错误数骤降往往意味着"卡在更早的阶段"，而不是接近完成。本次已三次踩到（"剩 1 个"跳回 819；40 个语法错误遮蔽 739 个类型错误）。**只有 `EXIT=0` 才是真的通过。**
2. **文件是 CRLF 行尾**：正则写 `;\n` 匹配不到 `;\r\n`，会导致"脚本报告成功但实际没改"。一律用 `\r?\n`。
3. **别在 `bash -c` 里内联复杂正则**：`$`、反斜杠会被 shell 吃掉，已多次导致替换静默失效或生成畸形代码（如 `getHeadBone(getGeoModel())()`）。写成 `.js` 文件再执行。
4. **改名类要同时替换代码体里的简单名**，只改 import 会留下一大批 `cannot find symbol`。
5. **组件（DataComponents）取出的是副本**：`getTag()` 改完不写回就静默失效，见 `TBSItemData`。

**方法论**：所有批量改写都基于**从 jar 里提取的真实类索引**自动生成映射，不靠记忆猜包名 —— 解压 `neoforge-21.1.242-universal.jar` / `geckolib-*.jar` 建立 `简单名 → 全限定名` 索引，再比对项目里的 import；MC 侧的成员名和方法签名一律用 1.21.1 官方 mappings（`piston-meta` 下载的 `client.txt`）核对。改名类**必须同时替换代码体里的简单名**，只改 import 会留下一大批 `cannot find symbol`。

已完成：

| 批次 | 规模 |
| --- | --- |
| Forge → NeoForge import | 166 文件 / 377 处 |
| GeckoLib 包路径（`core.*` 已扁平化） | 106 文件 / 280 处 |
| `ResourceLocation` 构造器 → 静态工厂 | 137 文件 / 359 处 |
| 简单名改名 + `RegistryObject`→`DeferredHolder` + `ForgeRegistries`→`BuiltInRegistries` | 102 文件 / 444 处 |
| `VertexConsumer` API（1.21 全面改名） | 24 文件 / 832 处 |
| `defineSynchedData` / `BlockPathTypes` / `FrameType` | 44 文件 / 98 处 |
| **网络系统重写** | 16 封包 + ServerNetwork + 26 调用点 |

**网络已完成**：封包改为 `CustomPacketPayload` + `StreamCodec`，注册走 `RegisterPayloadHandlersEvent`。原 `serialize`/`deserialize` 签名被 `StreamCodec.of` 复用，故未改动序列化逻辑。**注意契约变化**：NeoForge 按 `Type` 里的 ResourceLocation 寻址，注册顺序不再是协议的一部分；但 `MessageUseAbility`/`AnimationActMessage` 传的仍是**数组下标**，`PLAYER_ABILITIES` 与 `getAnimations()` 的"只能追加"约束依然成立。

**Capability 已完成**：见上文「Capability —— 已迁到 Data Attachments」。`TickEvent` 也已按 1.21.1 的 Pre/Post 拆分改造（原代码用 `phase == START` 提前 return，语义即"只在 END 跑"，故一律对应 `Post`）。

### 移植期的权威参考：整合包里的 Mowzie's Mobs 1.21.1

本 Mod 的渲染层、粒子系统与技能系统大量源自 **Mowzie's Mobs**（`MowzieGeoArmorRenderer`、`MowzieGeoBone`、`MowzieGeoModel`、`MowzieAnimationController`、`GeckoPlayer`/`GeckoRenderPlayer`、`AdvancedParticleBase`/`ParticleComponent`、`Ability`/`AbilitySection` —— 部分文件的注释里直接写明了出处）。

整合包里就有 **`mowziesmobs-1.21.1-1.8.2.jar`**，即这些同源类**已经移植好的官方实现**。遇到签名对不上时，直接读它的签名比猜快得多：

```bash
J="D:/MC/snow/.minecraft/versions/最后的冬天/mods/mowziesmobs-1.21.1-1.8.2.jar"
G=$(find ~/.gradle/caches/modules-2/files-2.1/software.bernie.geckolib -name "*4.9.2*.jar" | grep -v sources | head -1)
mkdir -p /tmp/mm && unzip -o -q "$J" -d /tmp/mm
javap -cp "/tmp/mm;$G" com.bobmowzie.mowziesmobs.client.render.entity.MowzieGeoArmorRenderer
```

已据此确认的 1.21.1 GeckoLib 渲染签名（本项目对应类应与之一致）：

| 方法 | 1.21.1 签名 |
| --- | --- |
| `renderToBuffer` | `(PoseStack, VertexConsumer, int packedLight, int packedOverlay, int color)` |
| `renderRecursively` | `(PoseStack, T, GeoBone, RenderType, MultiBufferSource, VertexConsumer, boolean, float, int, int, int)` |
| `actuallyRender` | `(PoseStack, T, BakedGeoModel, RenderType, MultiBufferSource, VertexConsumer, boolean, float, int, int, int)` |

即末尾的 4 个 float 颜色分量统一合并为 1 个 ARGB `int`。Mowzie's Mobs 本身是开源的（github.com/BobMowzie/MowziesMobs），需要看实现而不只是签名时可去对应分支查阅。

### 剩余工作

- 分散 API：`does not override` 约 287、`Matrix3f`→`Pose` 39、double→float 窄化 36、`AttributeModifier` 改 record 19、`Holder<MobEffect>`/`Holder<ArmorMaterial>` 24、`saveAdditional`/`readBlockPos` 加 `HolderLookup.Provider` 20、进度 `addCriterion` 22。
- `INBTSerializable` 的实现已改为新签名（带 `HolderLookup.Provider`），但**调用点尚未全部补上该参数**（`ItemStackHandler` / `TeamMemberStorage` 等），通常可用 `level().registryAccess()` 取得。
- ItemStack NBT → DataComponents（`getOrCreateTag`/`hasTag` 等）。
- `RecipeBookType.create("STEW")` 已用原版值占位，NeoForge 1.21.1 需改用 `enumextender.json` 扩展枚举。

### 已定的决策

- **`compat/oculus` 已剥离**：整合包用 Sodium，不含 Oculus/Iris。`isUsingShaderPack()` 现返回 `false`，要接 Iris 就改这里。原文件可从 git 历史恢复。
- **`libs/*.jar` 入库**：RosmontisLib 无公开 maven，不入库则他人 clone 后无法构建。
- **许可证统一为 GPLv3**：以 `LICENSE.txt` 实际内容为准（1.20.1 分支的 `mods.toml` 写 LESSER GPL、`gradle.properties` 写 All Rights Reserved，三者原本不一致）。
