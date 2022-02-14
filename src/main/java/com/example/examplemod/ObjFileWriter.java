package com.example.examplemod;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Class for writing an Wavefront Object
 */
@SuppressWarnings("UnusedReturnValue")
public class ObjFileWriter {
    private BufferedWriter writer;
    int uvCounter;
    int vertexCounter;
    int faceCounter;


    /**
     * Opens a new file and resets the class
     * @param path
     * @throws IOException
     */
    public void openFile(String path) throws IOException {
        if(!path.contains(".")) {
            path = path+".obj";
        }
        vertexCounter = 0;
        uvCounter = 0;
        faceCounter = 0;
        writer = new BufferedWriter(new FileWriter(path));
    }

    /**
     * Write a new vertex coordinate to the file.
     * @param x X Coordinate
     * @param y Y Coordinate
     * @param z Z Coordinate
     * @return ID of the written Vertex. Keep for faces
     */
    public int writeVertex(float x, float y, float z) throws IOException {
        writer.write("v " + x + " " + y + " " + z +"\n");
        return ++vertexCounter;
    }

    /**
     * Write a new vertex coordinate to the file.
     * @param x X Coordinate
     * @param y Y Coordinate
     * @param z Z Coordinate
     * @return ID of the written Vertex. Keep for faces
     */
    public int writeVertex(double x, double y, double z) throws IOException {
        writer.write("v " + x + " " + y + " " + z +"\n");
        return ++vertexCounter;
    }



    public int writeUV(float u, float v) throws IOException{
        writer.write("vt " + u + " " + v + "\n");
        return ++uvCounter;
    }

    /**
     * Write a new face using the four given coordinates. Includes UV for texture
     * @param p1 ID of the first coordinate
     * @param uv1 ID of the first UV coordinate
     * @param p2 ID of the second coordinate
     * @param uv2 ID of the second UV coordinate
     * @param p3 ID of the third coordinate
     * @param uv3 ID of the third UV coordinate
     * @param p4 ID of the fourth coordinate
     * @param uv4 ID of the fourth UV coordinate
     * @return ID of the newly created
     */
    public int writeQuad(int p1, int uv1, int p2, int uv2, int p3, int uv3, int p4, int uv4) throws IOException {
        writer.write("f " + p1 + "/" + uv1 + " " + p2 + "/" + uv2 + " " + p3 + "/" + uv3 + " " + p4 + "/" + uv4 + "\n");
        return ++faceCounter;
    }

    public void closeFile() throws IOException {
        writer.close();
    }
}
