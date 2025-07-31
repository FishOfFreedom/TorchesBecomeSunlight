package com.freefish.torchesbecomesunlight.server.init.generator;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.init.ItemHandle;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class ModItemModelProvider extends ItemModelProvider {
    public enum ItemType {
        DEFAULT,            // 普通物品
        HANDHELD,           // 手持物品
        BLOCK_ITEM,         // 方块物品
        SPAWN_EGG,          // 生成蛋
        TRIDENT,            // 三叉戟
        SHIELD,             // 盾牌
        COMPASS,            // 指南针/时钟
        BOW,                // 弓
        CROSSBOW,           // 弩
        FISHING_ROD,        // 钓鱼竿
        ELYTRA,             // 鞘翅
        GENERATED_3D        // 3D生成的物品
    }

    private final Map<Item, ItemType> itemTypes = new HashMap<>();
    
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TorchesBecomeSunlight.MOD_ID, existingFileHelper);
    }
    
    @Override
    protected void registerModels() {
        //registerItemModels();
        basicItem(ItemHandle.BORSCHT.get());
        basicItem(ItemHandle.POLAR_STEW_MEAT.get());
        basicItem(ItemHandle.BAKED_COD_POTATO.get());
        basicItem(ItemHandle.OIL_SALAD.get());
        basicItem(ItemHandle.MASHED_POTATO_STEAK.get());
        basicItem(ItemHandle.APPLE_PIE.get());
        basicItem(ItemHandle.BEEF_BEET_DUMPLING.get());
        basicItem(ItemHandle.URSUS_BREAD.get());
        basicItem(ItemHandle.BURDENBEAST_BURGER.get());
        basicItem(ItemHandle.DOUGH.get());
        basicItem(ItemHandle.SOUR_CREAM.get());
    }
    
    private void registerItemModels() {
        for (RegistryObject<Item> item : ItemHandle.ITEMS.getEntries()) {
            ItemType type = detectItemType(item.get());
            itemTypes.put(item.get(), type);
            registerModel(item.get(), type);
        }
        //registerCustomModels();
    }
    
    private void registerModel(Item item, ItemType type) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        if (id == null) return;
        
        String path = id.getPath();
        
        switch (type) {
            case DEFAULT:
                basicItem(item);
                break;
                
            case HANDHELD:
                withExistingParent(path, "item/handheld")
                    .texture("layer0", modLoc("item/" + path));
                break;
                
            case BLOCK_ITEM:
                withExistingParent(path, modLoc("block/" + path));
                break;
                
            case SPAWN_EGG:
                withExistingParent(path, "item/template_spawn_egg");
                break;
                
            case TRIDENT:
                withExistingParent(path, "item/template_trident");
                break;
                
            case SHIELD:
                withExistingParent(path, "item/shield")
                    .texture("particle", modLoc("item/" + path + "_particle"));
                break;
                
            case COMPASS:
            case BOW:
            case CROSSBOW:
            case FISHING_ROD:
            case ELYTRA:
            case GENERATED_3D:
                withExistingParent(path, "item/generated")
                    .texture("layer0", modLoc("item/" + path));
                break;
        }
    }

    private ItemType detectItemType(Item item) {
        String className = item.getClass().getName().toLowerCase();
        
        if (item instanceof BlockItem) {
            return ItemType.BLOCK_ITEM;
        }
        else if (className.contains("sword") || className.contains("pickaxe") || 
                 className.contains("axe") || className.contains("shovel") || 
                 className.contains("hoe") || className.contains("tool")) {
            return ItemType.HANDHELD;
        }
        else if (item instanceof SpawnEggItem) {
            return ItemType.SPAWN_EGG;
        }
        else if (item instanceof TridentItem) {
            return ItemType.TRIDENT;
        }
        else if (item instanceof ShieldItem) {
            return ItemType.SHIELD;
        }
        else if (item instanceof CompassItem ) {
            return ItemType.COMPASS;
        }
        else if (item instanceof BowItem) {
            return ItemType.BOW;
        }
        else if (item instanceof CrossbowItem) {
            return ItemType.CROSSBOW;
        }
        else if (item instanceof FishingRodItem) {
            return ItemType.FISHING_ROD;
        }
        else if (item instanceof ElytraItem) {
            return ItemType.ELYTRA;
        }
        
        return ItemType.DEFAULT;
    }

    public void layeredItem(Item item, String... layers) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        ItemModelBuilder builder = withExistingParent(id.getPath(), "item/generated");
        
        for (int i = 0; i < layers.length; i++) {
            builder.texture("layer" + i, modLoc(layers[i]));
        }
    }

    public void bowModel(Item bow) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(bow);
        
        withExistingParent(id.getPath(), "item/bow")
            .texture("layer0", modLoc("item/" + id.getPath()))
            .override().predicate(new ResourceLocation("pulling"), 1)
                .model(getExistingFile(modLoc("item/" + id.getPath() + "_pulling_0")))
            .end()
            .override().predicate(new ResourceLocation("pulling"), 1)
                .predicate(new ResourceLocation("pull"), 0.65f)
                .model(getExistingFile(modLoc("item/" + id.getPath() + "_pulling_1")))
            .end()
            .override().predicate(new ResourceLocation("pulling"), 1)
                .predicate(new ResourceLocation("pull"), 0.9f)
                .model(getExistingFile(modLoc("item/" + id.getPath() + "_pulling_2")))
            .end();
    }

    public void customParentItem(Item item, String parent) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        withExistingParent(id.getPath(), parent)
            .texture("layer0", modLoc("item/" + id.getPath()));
    }

    public void shieldModel(Item shield, boolean hasBanner) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(shield);
        
        ModelFile baseModel = withExistingParent(id.getPath(), "item/shield")
            .texture("particle", modLoc("item/" + id.getPath() + "_particle"));
        
        if (hasBanner) {
            withExistingParent(id.getPath() + "_banner", "item/shield")
                .texture("particle", modLoc("item/" + id.getPath() + "_particle"))
                .override().predicate(new ResourceLocation("blocking"), 1)
                    .model(getExistingFile(modLoc("item/" + id.getPath() + "_blocking")))
                .end();
        }
    }
}