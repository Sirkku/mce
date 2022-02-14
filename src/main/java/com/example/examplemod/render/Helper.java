package com.example.examplemod.render;

import net.minecraft.resources.ResourceLocation;

public class Helper {

    public static String resourceLocationToFilename(ResourceLocation resourceLocation) {
        return resourceLocation.getPath().replaceAll("\\W", "_");
    }
}
