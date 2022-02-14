package com.example.examplemod.network;

import com.example.examplemod.TestCommands;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TriggerExportPackage {
    public TriggerExportPackage() {}

    public static <TriggerExportPackage> void encode(TriggerExportPackage pkt,
                                                     FriendlyByteBuf friendlyByteBuf) {

    }

    public static TriggerExportPackage decode(FriendlyByteBuf friendlyByteBuf) {
        return new TriggerExportPackage();
    }

    public static <TriggerExportPackage> void handle(TriggerExportPackage TriggerExportPackage, Supplier<NetworkEvent.Context> contextSupplier) {

        if(contextSupplier.get().getDirection().getReceptionSide().isClient()) {
            contextSupplier.get().enqueueWork(()-> {
                TestCommands.blah(contextSupplier);
            });
        }
        contextSupplier.get().setPacketHandled(true);
    }
}
