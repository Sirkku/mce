package com.example.examplemod.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Stand-in for MultiBUfferSource that saves mesh information on the Heap instead of VRAM, so it can be exported later
 */
public class RamMultibufferSource implements MultiBufferSource {
    Map<RenderType, OBJVertexConsumer> vertexConsumerMap;

    public RamMultibufferSource() {
        vertexConsumerMap = new HashMap<>();
    }

    @Override
    public @NotNull VertexConsumer getBuffer(@NotNull RenderType renderType) {

        if(vertexConsumerMap.containsKey(renderType)) {
            return vertexConsumerMap.get(renderType);
        } else {
            OBJVertexConsumer objVertexConsumer = new OBJVertexConsumer(renderType);
            vertexConsumerMap.put(renderType, objVertexConsumer);
            return objVertexConsumer;
        }
    }

    public void writeToFile(String path) {
        for(OBJVertexConsumer objVertexConsumer : vertexConsumerMap.values()) {
            objVertexConsumer.writeToFile(path);
        }
    }
}
