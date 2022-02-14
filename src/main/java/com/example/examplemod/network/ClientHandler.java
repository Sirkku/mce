package com.example.examplemod.network;

import com.example.examplemod.render.WorldExporter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientHandler {

    BlockPos corner1;
    BlockPos corner2;

    private ClientHandler() {
        corner1 = null;
        corner2 = null;
    }


    public void handleSimpleCommand(Supplier<NetworkEvent.Context> contextSupplier, PacketExportCommands.Action action) {
        switch(action) {
            case NONE -> {
                return;
            }
            case SAVE_POS_1 -> {
                corner1 = Minecraft.getInstance().player.getOnPos();
            }
            case SAVE_POS_2 -> {
                corner2 = Minecraft.getInstance().player.getOnPos();
            }
            case EXPORT -> {
                doExport();
            }
        }
    }

    private void doExport() {
        Minecraft.getInstance().gui.getChat().addMessage(new TextComponent("Trying to export"));
        WorldExporter worldExporter = new WorldExporter();
        worldExporter.export(corner1, corner2);
    }

    final public static ClientHandler INSTANCE = new ClientHandler();
}
