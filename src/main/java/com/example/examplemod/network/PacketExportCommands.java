package com.example.examplemod.network;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketExportCommands {
    protected final static int PACKET_ACTION_OFFEST = 1;

    public enum Action {
        NONE(0), SAVE_POS_1(1), SAVE_POS_2(2), EXPORT(3);
        int id;

        public int toInt() {
            return id;
        }

        Action(int id) {
            this.id = id;
        }
    };

    public Action action;

    public PacketExportCommands() {
        action = Action.NONE;
    }

    public PacketExportCommands(Action action) {
        this.action = action;
    }

    public static void encode(PacketExportCommands packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(packet.action.toInt());
    }

    public static PacketExportCommands decode(FriendlyByteBuf friendlyByteBuf) {
        PacketExportCommands packet = new PacketExportCommands();
        packet.action = Action.NONE;
        int receivedInt = friendlyByteBuf.getInt(PACKET_ACTION_OFFEST);
        if(friendlyByteBuf.capacity() >= 4) {

            for(Action action : Action.values()) {
                if(action.toInt() == receivedInt) {
                    packet.action = action;
                    break;
                }
            }
        }
        return packet;
    }

    public static void handle(PacketExportCommands packet, Supplier<NetworkEvent.Context> contextSupplier) {

        if(contextSupplier.get().getDirection().getReceptionSide().isClient()) {
            contextSupplier.get().enqueueWork(()-> {
                ClientHandler.INSTANCE.handleSimpleCommand(contextSupplier, packet.action);
            });
        }
        contextSupplier.get().setPacketHandled(true);
    }
}
