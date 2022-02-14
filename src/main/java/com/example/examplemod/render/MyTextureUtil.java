package com.example.examplemod.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.b3d.B3DModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.IOException;


public class MyTextureUtil extends TextureUtil {
    private static final Logger LOGGER = LogManager.getLogger();


    /**
     * Copied from base class to avoid AT...
     * @param textureID AbstractTexture.getID() I hope
     */
    private static void bind(int textureID) {
        GlStateManager._bindTexture(textureID);
    }

    /**
     * Exports a texture using it's ID to a file with all mip maps.
     * Guesses the file size using glGetTexLevelParameter
     * @param texture Texture to be exported.
     * @param filenameStart Start of the filename of the exported images
     * @param maxMipmapLevel max mip zoom factor
     */
    public static void writeAsPNG(AbstractTexture texture, String filenameStart, int maxMipmapLevel) {
        writeAsPNG(filenameStart, texture.getId(), maxMipmapLevel);
    }

    /**
     * Exports a texture using it's ID to a file with all mip maps.
     * Guesses the file size using glGetTexLevelParameter
     * @param filenameStart Start of the filename of the exported images
     * @param textureID AbstractTexture.getID() I hope
     * @param maxMipmapLevel max mip zoom factor
     */
    public static void writeAsPNG(String filenameStart, int textureID, int maxMipmapLevel) {
        RenderSystem.assertOnRenderThread();
        bind(textureID);

        int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        for(int i = 0; i <= maxMipmapLevel; ++i) {
            String s = filenameStart + "_" + i + ".png";
            int scaledWidth = width >> i;
            int scaledHeight = height >> i;

            try {
                NativeImage nativeimage = new NativeImage(scaledWidth, scaledHeight, false);

                try {
                    nativeimage.downloadTexture(i, false);
                    nativeimage.writeToFile(s);
                    LOGGER.debug("Exported png to: {}", (Object)(new File(s)).getAbsolutePath());
                } catch (Throwable throwable1) {
                    try {
                        nativeimage.close();
                    } catch (Throwable throwable) {
                        throwable1.addSuppressed(throwable);
                    }

                    throw throwable1;
                }

                nativeimage.close();
            } catch (IOException ioexception) {
                LOGGER.debug("Unable to write: ", (Throwable)ioexception);
            }
        }

    }
}
