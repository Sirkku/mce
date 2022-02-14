package com.example.examplemod.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import java.util.List;
import java.util.Random;

public class WorldExporter {
    final private RamMultibufferSource ramMultibufferSource;
    final private BakedQuadExporter bakedQuadExporter;
    final private Random random;
    PoseStack poseStack = new PoseStack();

    public WorldExporter() {
        this.ramMultibufferSource = new RamMultibufferSource();
        this.bakedQuadExporter = new BakedQuadExporter();
        random = new Random();
    }


    public void export(BlockPos p1, BlockPos p2) {
        for(BlockPos p : BlockPos.betweenClosed(p1, p2)) {
            System.out.println(p.toString());
            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(p);
            // Normal blocks do not have an entity AFAIK
            if(blockEntity != null) {
                exportBlockEntity(p, blockEntity);
            }
            exportBlock(p);
        }

        bakedQuadExporter.writeToFile("export/");
        ramMultibufferSource.writeToFile("export/");
    }


    private void exportBlock(BlockPos offset) {
        Minecraft minecraft = Minecraft.getInstance();
        ModelManager modelManager = minecraft.getModelManager();
        BlockState blockState = minecraft.level.getBlockState(offset);

        BakedModel bakedModel = modelManager.getModel(BlockModelShaper.stateToModelLocation(blockState));

        List<BakedQuad> quads = bakedModel.getQuads(blockState,null, new Random(), EmptyModelData.INSTANCE);

        quads.addAll(bakedModel.getQuads(blockState, Direction.SOUTH, random, EmptyModelData.INSTANCE));
        quads.addAll(bakedModel.getQuads(blockState, Direction.WEST, random, EmptyModelData.INSTANCE));
        quads.addAll(bakedModel.getQuads(blockState, Direction.NORTH, random, EmptyModelData.INSTANCE));
        quads.addAll(bakedModel.getQuads(blockState, Direction.UP, random, EmptyModelData.INSTANCE));
        quads.addAll(bakedModel.getQuads(blockState, Direction.DOWN, random, EmptyModelData.INSTANCE));
        quads.addAll(bakedModel.getQuads(blockState, Direction.EAST, random, EmptyModelData.INSTANCE));

        bakedQuadExporter.render(quads, offset);
    }


    private <T extends BlockEntity> void exportBlockEntity(BlockPos offset, T blockEntity) {
        poseStack.pushPose();
        poseStack.translate(offset.getX()*1.0, offset.getY()*1.0, offset.getZ()*1.0);
        BlockEntityRenderer<T> blockentityrenderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity);
        blockentityrenderer.render(blockEntity, 0.0f, poseStack, ramMultibufferSource, 0, 0);
        poseStack.popPose();
    }
}
