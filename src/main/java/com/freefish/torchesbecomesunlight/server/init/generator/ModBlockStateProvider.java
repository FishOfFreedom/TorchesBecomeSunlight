package com.freefish.torchesbecomesunlight.server.init.generator;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.block.furniture.EightDirectionBlock;
import com.freefish.torchesbecomesunlight.server.block.furniture.VerticalSlabBlock;
import com.freefish.torchesbecomesunlight.server.block.furniture.furniturenum.EightDirection;
import com.freefish.torchesbecomesunlight.server.init.BlockHandle;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, TorchesBecomeSunlight.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        //simpleBlockWithItem(BlockHandle.STEW_POT.get(),
        //        new ModelFile.UncheckedModelFile(modLoc("block/stew_pot")));
        simpleBlockWithItem(BlockHandle.ROUND_STOOL.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/round_stool")));
        //horizontalBlock(BlockHandle.CUTTING_BOARD.get(),
        //        new ModelFile.UncheckedModelFile(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"block/cutting_board")));
        //horizontalBlock(BlockHandle.PATH_SIGN.get(),
        //        new ModelFile.UncheckedModelFile(mcLoc("block/path_sign")));
        //horizontalBlock(BlockHandle.PATH_SIGN_2.get(),
        //        new ModelFile.UncheckedModelFile(mcLoc("block/path_sign_2")));
        //horizontalBlock(BlockHandle.ELEVATOR_CONTROLLER.get(),
        //        new ModelFile.UncheckedModelFile(mcLoc("block/elevator_controller")));
        //horizontalBlock(BlockHandle.THERMAL_KETTLE.get(),
        //        models().cubeAll("directional_block", mcLoc("block/oak_planks")));
        //simpleBlockWithItem(BlockHandle.THERMAL_KETTLE.get(),
        //        new ModelFile.UncheckedModelFile(modLoc("block/thermal_kettle")));
        //simpleBlockWithItem(BlockHandle.CARTON.get(),
        //        new ModelFile.UncheckedModelFile(modLoc("block/carton")));
        //simpleBlockWithItem(BlockHandle.BIG_CARTON.get(),
        //        new ModelFile.UncheckedModelFile(modLoc("block/big_carton")));
        //eightDirectionWithItem((EightDirectionBlock) BlockHandle.BIG_CARTON.get(),
        //        "block/big_carton");
        //simpleBlockWithItem(BlockHandle.CARGO_RACK.get(),
        //        new ModelFile.UncheckedModelFile(modLoc("block/cargo_rack")));


        verticalSlabBlockWithItem(BlockHandle.GRAY_CONCRETE_VERTICAL_SLAB.get(),new ResourceLocation("block/gray_concrete"));
        verticalSlabBlockWithItem(BlockHandle.WHITE_CONCRETE_VERTICAL_SLAB.get(),new ResourceLocation("block/white_concrete"));
        verticalSlabBlockWithItem(BlockHandle.YELLOW_CONCRETE_VERTICAL_SLAB.get(),new ResourceLocation("block/yellow_concrete"));
        verticalSlabBlockWithItem(BlockHandle.LIGHT_GRAY_CONCRETE_VERTICAL_SLAB.get(),new ResourceLocation("block/light_gray_concrete"));

        slabBlockWithItem(BlockHandle.GRAY_CONCRETE_SLAB.get(),new ResourceLocation("block/gray_concrete"));
        slabBlockWithItem(BlockHandle.WHITE_CONCRETE_SLAB.get(),new ResourceLocation("block/white_concrete"));
        slabBlockWithItem(BlockHandle.YELLOW_CONCRETE_SLAB.get(),new ResourceLocation("block/yellow_concrete"));
        slabBlockWithItem(BlockHandle.LIGHT_GRAY_CONCRETE_SLAB.get(),new ResourceLocation("block/light_gray_concrete"));

        stairsBlockWithItem(BlockHandle.GRAY_CONCRETE_STAIRS.get(),new ResourceLocation("block/gray_concrete"));
        stairsBlockWithItem(BlockHandle.WHITE_CONCRETE_STAIRS.get(),new ResourceLocation("block/white_concrete"));
        stairsBlockWithItem(BlockHandle.YELLOW_CONCRETE_STAIRS.get(),new ResourceLocation("block/yellow_concrete"));
        stairsBlockWithItem(BlockHandle.LIGHT_GRAY_CONCRETE_STAIRS.get(),new ResourceLocation("block/light_gray_concrete"));
    }

    public void simpleBlockWithItem(Block block) {
        simpleBlock(block, cubeAll(block));
        simpleBlockItem(block, cubeAll(block));
    }

    public void slabBlockWithItem(SlabBlock block, ResourceLocation texture){
        slabBlock(block, texture);
        String baseName = key(block).toString();
        simpleBlockItem(block, models().slab(baseName, texture, texture, texture));
    }

    public void slabBlockWithItem(SlabBlock block, ResourceLocation doubleSlab, ResourceLocation texture){
        slabBlock(block, doubleSlab, texture);
        String baseName = key(block).toString();
        simpleBlockItem(block, models().slab(baseName, texture, texture, texture));
    }

    public void slabBlock(SlabBlock block, ResourceLocation texture) {
        slabBlock(block, texture, texture, texture, texture);
    }

    public void stairsBlockWithItem(StairBlock block, ResourceLocation texture) {
        stairsBlock(block, texture, texture, texture);
        String baseName = key(block).toString();
        simpleBlockItem(block, models().stairs(baseName, texture, texture, texture));
    }

    private void verticalSlabBlockWithItem(VerticalSlabBlock block, ResourceLocation texture) {
        verticalSlabBlock(block, texture);
        String baseName = key(block).toString();
        simpleBlockItem(block, models().withExistingParent(baseName,new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"block/vertical_slab")));
    }

    private void eightDirectionWithItem(EightDirectionBlock block,String model) {
        ResourceLocation resourceLocation = modLoc(model);
        getVariantBuilder(block)
                .forAllStatesExcept(state -> {
                    EightDirection eightDirection = state.getValue(EightDirectionBlock.EIGHT_DIRECTION);
                    return ConfiguredModel.builder()
                            .modelFile(new ModelFile.UncheckedModelFile(resourceLocation))
                            .rotationY(90)
                            .build();
                }, StairBlock.WATERLOGGED);


        String baseName = key(block).toString();
        simpleBlockItem(block, models().withExistingParent(baseName,resourceLocation));
    }


    //verticalSlab
    public void verticalSlabBlock(VerticalSlabBlock block, ResourceLocation all) {
        verticalSlabBlockInternal(block, key(block).toString(), all, all, all);
    }

    private void verticalSlabBlockInternal(VerticalSlabBlock block, String baseName, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile stairs = models().withExistingParent(baseName,new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"vertical_slab")).texture("side",side).texture("bottom",bottom).texture("top",top);
        ModelFile stairsInner = models().withExistingParent(baseName+"_inner",new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"inner_vertical_slab")).texture("side",side).texture("bottom",bottom).texture("top",top);
        ModelFile stairsOuter = models().withExistingParent(baseName+"_outer",new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"outer_vertical_slab")).texture("side",side).texture("bottom",bottom).texture("top",top);
        verticalSlabBlock(block, stairs, stairsInner, stairsOuter);
    }

    public void verticalSlabBlock(VerticalSlabBlock block, ModelFile stairs, ModelFile stairsInner, ModelFile stairsOuter) {
        getVariantBuilder(block)
                .forAllStatesExcept(state -> {
                    Direction facing = state.getValue(StairBlock.FACING);
                    StairsShape shape = state.getValue(StairBlock.SHAPE);
                    int yRot = (int) facing.getClockWise().toYRot(); // Stairs model is rotated 90 degrees clockwise for some reason
                    if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) {
                        yRot += 270; // Left facing stairs are rotated 90 degrees clockwise
                    }
                    if (shape != StairsShape.STRAIGHT) {
                        yRot += 90; // Top stairs are rotated 90 degrees clockwise
                    }
                    yRot %= 360;
                    boolean uvlock = yRot != 0; // Don't set uvlock for states that have no rotation
                    return ConfiguredModel.builder()
                            .modelFile(shape == StairsShape.STRAIGHT ? stairs : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? stairsInner : stairsOuter)
                            .rotationY(yRot)
                            .uvLock(uvlock)
                            .build();
                }, StairBlock.WATERLOGGED);
    }

    public void buttonBlockWithItem(ButtonBlock block, ResourceLocation texture){
        buttonBlock(block, texture);
        buttonBlockInventory(block, texture);
        simpleBlockItem(block, buttonBlockInventory(block, texture));
    }

    public ModelFile buttonBlockInventory(ButtonBlock block, ResourceLocation texture){
        String baseName = key(block).toString();
        return models().buttonInventory(baseName + "_inventory", texture);
    }

    public void logBlockWithItem(RotatedPillarBlock block){
        logBlock(block);
        String baseName = key(block).toString();
        simpleBlockItem(block, models().cubeColumn(baseName, blockTexture(block), extend(blockTexture(block), "_top")));
    }

    public void columnBlockWithItem(RotatedPillarBlock block, ResourceLocation texture){
        columnBlock(block, texture);
        String baseName = key(block).toString();
        simpleBlockItem(block, models().cubeColumn(baseName, texture, texture));
    }

    public void columnBlock(RotatedPillarBlock block, ResourceLocation texture){
        axisBlock(block,
                models().cubeColumn(name(block), texture, texture),
                models().cubeColumn(name(block), texture, texture));
    }

    public void fenceGateWithItem(FenceGateBlock block, ResourceLocation texture){
        fenceGateBlock(block, texture);
        String baseName = key(block).toString();
        simpleBlockItem(block, models().fenceGate(baseName, texture));
    }

    public void fenceBlockWithItem(FenceBlock block, ResourceLocation texture){
        fenceBlock(block, texture);
        fenceInventory(block, texture);
        simpleBlockItem(block, fenceInventory(block, texture));
    }

    public ModelFile fenceInventory(FenceBlock block, ResourceLocation texture){
        String baseName = key(block).toString();
        return models().fenceInventory(baseName + "_inventory", texture);
    }

    public void fenceBlock(FenceBlock block, ResourceLocation texture) {
        String baseName = key(block).toString();
        fourWayBlock(block,
                models().fencePost(baseName + "_post", texture),
                models().fenceSide(baseName + "_side", texture));
        fenceInventory(block, texture);
    }

    public void pressurePlateWithItem(PressurePlateBlock block, ResourceLocation texture){
        pressurePlateBlock(block, texture);
        String baseName = key(block).toString();
        simpleBlockItem(block, models().pressurePlate(baseName, texture));
    }

    public void wallBlockWithItem(WallBlock block, ResourceLocation texture){
        wallBlock(block, texture);
        wallBlockInventory(block, texture);
        simpleBlockItem(block, wallBlockInventory(block, texture));
    }

    public void wallBlock(WallBlock block, ResourceLocation texture) {
        wallBlockInternal(block, key(block).toString(), texture);
    }

    public ModelFile wallBlockInventory(WallBlock block, ResourceLocation texture){
        String baseName = key(block).toString();
        return models().wallInventory(baseName + "_inventory", texture);
    }

    private void wallBlockInternal(WallBlock block, String baseName, ResourceLocation texture) {
        wallBlock(block, models().wallPost(baseName + "_post", texture),
                models().wallSide(baseName + "_side", texture),
                models().wallSideTall(baseName + "_side_tall", texture));
    }

    protected void builtinEntity(Block b, String particle) {
        simpleBlock(b, models().getBuilder(name(b))
                .parent(new ModelFile.UncheckedModelFile("builtin/entity"))
                .texture("particle", particle));
    }

    private ResourceLocation extend(ResourceLocation rl, String suffix) {
        return new ResourceLocation(rl.getNamespace(), rl.getPath() + suffix);
    }

    private String name(Block block) {
        return key(block).getPath();
    }

    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }
}
