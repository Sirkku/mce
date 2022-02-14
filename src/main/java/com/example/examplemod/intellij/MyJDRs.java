package com.example.examplemod.intellij;

import net.minecraft.client.renderer.block.model.BakedQuad;

/**
 * Contains methods for custom Java Data Renderer.
 */
public class MyJDRs {
    public static String renderBakedQuad(BakedQuad bakedQuad) {
        return "Baked Quad";
    }

    public static Object[] renderBakedQuadChildren(BakedQuad bakedQuad) {
        Object[] children = new Object[1];

        children[0] = new Vertices(bakedQuad);

        return children;
    }

    private static class Vertices {
        Vertex[] vertices;

        public Vertices(BakedQuad bakedQuad) {
            int[] vertices = bakedQuad.getVertices();
            int size = vertices.length/8;
            this.vertices = new Vertex[size];

            for(int i = 0; i < size; i++) {
                this.vertices[i] = new Vertex(vertices, i*8);
            }
        }
    }

    private static class Vertex {
        float x;
        float y;
        float z;
        int shadeColor;
        float u;
        float v;
        int light;
        int normal;

        public Vertex(int[] source, int offset) {
            this.x = Float.intBitsToFloat(source[offset]);
            this.y = Float.intBitsToFloat(source[1+offset]);
            this.z = Float.intBitsToFloat(source[2+offset]);
            this.shadeColor = source[3+offset];
            this.u = Float.intBitsToFloat(source[4+offset]);
            this.v = Float.intBitsToFloat(source[5+offset]);
            this.light = source[6+offset];
            this.normal = source[7+offset];
        }

    }
}
