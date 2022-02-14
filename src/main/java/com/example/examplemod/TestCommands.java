package com.example.examplemod;

import com.example.examplemod.network.MyPacketHandler;
import com.example.examplemod.network.PacketExportCommands;
import com.example.examplemod.network.TriggerExportPackage;
import com.example.examplemod.render.RamMultibufferSource;
import com.example.examplemod.render.BakedQuadExporter;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;
import java.util.function.Supplier;

public class TestCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("test").executes(ctx -> test(ctx.getSource())));
        dispatcher.register(Commands.literal("wit").executes(ctx -> wit(ctx.getSource())));
        dispatcher.register(Commands.literal("pos1").executes(ctx -> sendPos1(ctx.getSource())));
        dispatcher.register(Commands.literal("pos2").executes(ctx -> sendPos2(ctx.getSource())));;
        dispatcher.register(Commands.literal("exportregion").executes(ctx -> exportRegion(ctx.getSource())));
    }

    private static int test(CommandSourceStack source) {
        source.sendSuccess(new TextComponent("Test"), true);
        return 1;
    }

    private static int sendPos1(CommandSourceStack source) {
        MyPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new PacketExportCommands(PacketExportCommands.Action.SAVE_POS_1));
        return 1;
    }

    private static int sendPos2(CommandSourceStack source) {
        MyPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new PacketExportCommands(PacketExportCommands.Action.SAVE_POS_2));
        return 1;
    }

    private static int exportRegion(CommandSourceStack source) {
        MyPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new PacketExportCommands(PacketExportCommands.Action.EXPORT));
        return 1;
    }



    private static int wit(CommandSourceStack source) {

        MyPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(),
                new TriggerExportPackage());

        return 0;
    }


    public static <E extends BlockEntity> void blah(Supplier<NetworkEvent.Context> contextSupplier) {

        Minecraft minecraft = Minecraft.getInstance();
        BlockPos pos = minecraft.level.players().get(0).getOnPos();

        BlockState blockState = minecraft.level.getBlockState(pos);

        BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
        if(blockEntity != null) {
            IModelData modelData = blockEntity.getModelData();
            blahfoo(blockEntity);
        }


        ModelManager modelManager = minecraft.getModelManager();
        BakedModel bakedModel =
                modelManager.getModel(BlockModelShaper.stateToModelLocation(blockState));

        List<BakedQuad> quads = bakedModel.getQuads(blockState,
                Direction.EAST,
                new Random(), EmptyModelData.INSTANCE);

        quads.addAll(bakedModel.getQuads(blockState,
                Direction.SOUTH,
                new Random(), EmptyModelData.INSTANCE));
        quads.addAll(bakedModel.getQuads(blockState,
                Direction.WEST,
                new Random(), EmptyModelData.INSTANCE));
        quads.addAll(bakedModel.getQuads(blockState,
                Direction.NORTH,
                new Random(), EmptyModelData.INSTANCE));
        quads.addAll(bakedModel.getQuads(blockState,
                Direction.UP,
                new Random(), EmptyModelData.INSTANCE));
        quads.addAll(bakedModel.getQuads(blockState,
                Direction.DOWN,
                new Random(), EmptyModelData.INSTANCE));
        quads.addAll(bakedModel.getQuads(blockState,
                null,
                new Random(), EmptyModelData.INSTANCE));

        BakedQuadExporter bakedQuadExporter = new BakedQuadExporter();

        bakedQuadExporter.render(quads);

        bakedQuadExporter.writeToFile("");

        /*TextureAtlas textureAtlas =
                modelManager.getAtlas(TextureAtlas.LOCATION_BLOCKS);

        TextureUtil.writeAsPNG("Test",
                textureAtlas.getId(),
                0, 1024, 512);*/
    }

    private static <T extends BlockEntity> void blahfoo(T blockEntity) {
        BlockEntityRenderer<T> blockentityrenderer =
                Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity);
        RamMultibufferSource ramMultibufferSource = new RamMultibufferSource();
        PoseStack poseStack = new PoseStack();
        blockentityrenderer.render(blockEntity, 0.0f, poseStack, ramMultibufferSource, 0, 0);
        int i = 1;
    }
}
