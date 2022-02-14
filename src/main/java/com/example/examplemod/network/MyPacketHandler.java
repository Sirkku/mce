package com.example.examplemod.network;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.network.TriggerExportPackage;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class MyPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ExampleMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        int id = 0;
        INSTANCE.registerMessage(id++, TriggerExportPackage.class, TriggerExportPackage::encode, TriggerExportPackage::decode, TriggerExportPackage::handle);
        INSTANCE.registerMessage(id++, PacketExportCommands.class, PacketExportCommands::encode, PacketExportCommands::decode, PacketExportCommands::handle);
    }
}
