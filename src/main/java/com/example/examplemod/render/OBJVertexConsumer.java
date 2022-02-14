package com.example.examplemod.render;

import com.example.examplemod.ObjFileWriter;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

class OBJVertexConsumer implements VertexConsumer {
    RenderType renderType;
    Vertex currentVertex;
    List<Vertex> vertices;

    boolean useDefaultColor;
    int defaultRed;
    int defaultGreen;
    int defaultBlue;
    int defaultAlpha;

    public OBJVertexConsumer(RenderType renderType) {
        this.renderType = renderType;
        vertices = new ArrayList<>();
        currentVertex = new Vertex();
    }


    @Override
    public @NotNull VertexConsumer vertex(double x, double y, double z) {
        currentVertex.x = x;
        currentVertex.y = y;
        currentVertex.z = z;
        return this;
    }

    @Override
    public @NotNull VertexConsumer color(int r, int g, int b, int a) {
        currentVertex.colorRed = r;
        currentVertex.colorGreen = g;
        currentVertex.colorBlue = b;
        currentVertex.colorAlpha = a;
        return this;
    }

    @Override
    public @NotNull VertexConsumer uv(float u, float v) {
        currentVertex.u = u;
        currentVertex.v = 1.0f-v;
        return this;
    }

    @Override
    public @NotNull VertexConsumer overlayCoords(int x, int y) {
        return this;
    }

    @Override
    public @NotNull VertexConsumer uv2(int u, int v) {
        currentVertex.u2 = u;
        currentVertex.v2 = v;
        return this;
    }

    @Override
    public @NotNull VertexConsumer normal(float x, float y, float z) {
        currentVertex.normalX = x;
        currentVertex.normalY = y;
        currentVertex.normalZ = z;
        return this;
    }

    @Override
    public void endVertex() {
        vertices.add(currentVertex);
        currentVertex = new Vertex();
    }

    @Override
    public void defaultColor(int colorRed, int colorGreen, int colorBlue, int colorAlpha) {
        useDefaultColor = true;
        defaultRed = colorRed;
        defaultGreen = colorGreen;
        defaultBlue = colorBlue;
        defaultAlpha = colorAlpha;
    }

    @Override
    public void unsetDefaultColor() {
        useDefaultColor = false;

    }

    public void writeToFile(String path) {
        if(renderType instanceof RenderType.CompositeRenderType crt) {
            if(crt.state.textureState instanceof RenderStateShard.TextureStateShard tss) {
                if(tss.texture.isPresent()) {
                    ResourceLocation textureLocation = tss.texture.get();
                    String preppedPath = path + Helper.resourceLocationToFilename(textureLocation);
                    AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(textureLocation);
                    MyTextureUtil.writeAsPNG(texture, preppedPath, 0);

                    try {
                        ObjFileWriter objWriter = new ObjFileWriter();
                        objWriter.openFile(preppedPath);

                        int verticesAmount = vertices.size();
                        int facesAmount = verticesAmount / 4;
                        Iterator<Vertex> iterator = vertices.iterator();
                        Vertex vertex;

                        for(int i = 0; i<facesAmount; i++) {
                            for(int j = 0; j<4; j++) {
                                vertex = iterator.next();
                                objWriter.writeVertex(vertex.x, vertex.y, vertex.z);
                                objWriter.writeUV(vertex.u, vertex.v);
                            }

                            int k = 4*i+1;
                            objWriter.writeQuad(k, k, k+1, k+1, k+2, k+2, k+3, k+3);
                        }

                        objWriter.closeFile();


                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }

                }
                else {
                    System.out.println("TextureStateShade.texture (Optional<ResourceLocation>) is empty");
                }
            }
        } else {
            System.out.println("RenderType not instanceof CompositeRenderType?" + renderType.toString());
        }
    }


    public static class Vertex {
        // position
        double x;
        double y;
        double z;
        // color??
        int colorRed;
        int colorGreen;
        int colorBlue;
        int colorAlpha;
        // uv
        float u;
        float v;
        // uv2
        int u2;
        int v2;
        // normal
        float normalX;
        float normalY;
        float normalZ;
    }
}
