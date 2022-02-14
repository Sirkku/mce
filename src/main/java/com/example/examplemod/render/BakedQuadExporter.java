package com.example.examplemod.render;

import com.example.examplemod.ObjFileWriter;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Can exports lists of BakedQuads
 */
public class BakedQuadExporter {
    private final Map<ResourceLocation, OBJUnit> meshes;

    public BakedQuadExporter() {
        meshes = new HashMap<>();
    }

    public void render(List<BakedQuad> bakedQuads) {
        render(bakedQuads, BlockPos.ZERO);
    }

    public void render(List<BakedQuad> bakedQuads, BlockPos offset) {

        for (BakedQuad bakedQuad : bakedQuads) {
            renderQuad(bakedQuad, offset);
        }
    }

    public void renderQuad(BakedQuad bakedQuad, BlockPos offset) {
        ResourceLocation atlasID = bakedQuad.getSprite().atlas().location();
        if (!meshes.containsKey(atlasID)) {
            meshes.put(atlasID, new OBJUnit(atlasID));
        }

        OBJUnit target = meshes.get(atlasID);
        target.renderQuad(bakedQuad, offset);
    }

    public void writeToFile(String path) {
        meshes.forEach((ResourceLocation rl, OBJUnit obj) -> {
            try {
                obj.writeToFile(path + rl.getPath().replaceAll("\\W", "_"));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    /**
     * Store all mesh for a single texture atlas
     */
    public static class OBJUnit {
        public final TextureAtlas textureAtlas;
        public final List<Vertex> vertices;
        public final List<QuadFace> quadFaces;

        public OBJUnit(ResourceLocation atlasID) {
            vertices = new ArrayList<Vertex>();
            quadFaces = new ArrayList<QuadFace>();
            textureAtlas = Minecraft.getInstance().getModelManager()
                    .getAtlas(atlasID);
        }


        public void renderQuad(BakedQuad bakedQuad, BlockPos blockPos) {

            int[] vertices = bakedQuad.getVertices();
            Vertex v1, v2, v3, v4;
            v1 = new Vertex(vertices, 0, blockPos);
            v2 = new Vertex(vertices, 8, blockPos);
            v3 = new Vertex(vertices, 16, blockPos);
            v4 = new Vertex(vertices, 24, blockPos);
            this.vertices.add(v1);
            this.vertices.add(v2);
            this.vertices.add(v3);
            this.vertices.add(v4);
            this.quadFaces.add(new QuadFace(v1, v2, v3, v4));
        }

        //TODO: Should this also write the Object file?

        /**
         * Write the obj and the texture as mesh_name.obj and mesh_name.png
         *
         * @param mesh_name Without file extensions
         */
        public void writeToFile(String mesh_name) throws IOException {
            MyTextureUtil.writeAsPNG(mesh_name, textureAtlas.getId(), 0);

            ObjFileWriter objWriter = new ObjFileWriter();
            objWriter.openFile(mesh_name + ".obj");

            for (Vertex vertex : vertices) {
                vertex.id = 0;
            }

            int id = 1;

            for (Vertex vertex : vertices) {
                if (vertex.id == 0) {
                    vertex.id = objWriter.writeVertex(vertex.x, vertex.y, vertex.z);
                    objWriter.writeUV(vertex.u, vertex.v);
                }
            }

            for (QuadFace qf : quadFaces) {
                objWriter.writeQuad(qf.v1.id, qf.v1.id, qf.v2.id, qf.v2.id,qf.v3.id, qf.v3.id, qf.v4.id, qf.v4.id);
            }

            objWriter.closeFile();
        }
    }

    public static class Vertex {
        public float x, y, z, u, v, normal;
        int id;

        /**
         * @param source The int[] of the BakedQuad Vertices
         * @param offset There are 4 vertices in a quad. To get each, call
         *               this function 4 times with offset = 0, 8, 16, 24
         */
        public Vertex(int[] source, int offset, BlockPos blockPos) {
            this.x = Float.intBitsToFloat(source[offset]) + blockPos.getX();
            this.y = Float.intBitsToFloat(source[1 + offset]) + blockPos.getY();
            this.z = Float.intBitsToFloat(source[2 + offset]) + blockPos.getZ();
            // this.shadeColor = source[3+offset];
            this.u = Float.intBitsToFloat(source[4 + offset]);
            this.v = 1.0f - Float.intBitsToFloat(source[5 + offset]);
            // this.light = source[6+offset];
            this.normal = 1.0f - Float.intBitsToFloat(source[7 + offset]);
            System.out.println(this.normal);
        }
    }

    public static class QuadFace {
        Vertex v1, v2, v3, v4;

        public QuadFace(Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
            this.v4 = v4;
        }
    }
}
