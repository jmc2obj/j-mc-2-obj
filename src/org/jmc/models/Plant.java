package org.jmc.models;
 
import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;

import java.util.Random;
 
/**
 * Updated model for plants rendered as 2 crossed polygons, like grass, dead bushes, small flowers.
 */
public class Plant extends BlockModel
{
    @Override
    public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
    {       
        // Generates a random number to offset the grass in the x and y.
        Random rX = new Random();
        rX.setSeed((x+z)*1000);
        float randomX = -0.2f + rX.nextFloat() * 0.4f;
        
        Random rZ = new Random();
        rZ.setSeed((x+z)*2000);       
        float randomZ = -0.2f + rZ.nextFloat() * 0.4f;	
       
        // Flip half of the faces horizontally to avoid repeating patterns.
        Random rF = new Random();
        rF.setSeed((x+z)*3000); 
        boolean flip = rF.nextBoolean();
        float flipValue = 1.0f;
        if (flip){
            flipValue = -1.0f; }
        
        Transform t = Transform.translation(x+randomX, y, z+randomZ);
        final String material = materials.get(data, biome)[0];
 
        Vertex[] vertices = new Vertex[4];
        vertices[0] = new Vertex((+0.43f)*flipValue,-0.5f,(-0.43f)*flipValue);
        vertices[1] = new Vertex((-0.43f)*flipValue,-0.5f,(+0.43f)*flipValue);
        vertices[2] = new Vertex((-0.43f)*flipValue,+0.45f,(+0.43f)*flipValue);
        vertices[3] = new Vertex((+0.43f)*flipValue,+0.45f,(-0.43f)*flipValue);
        obj.addDoubleSidedFace(vertices, null, t, material);
       
        vertices[0] = new Vertex((-0.43f)*flipValue,-0.5f,(-0.43f)*flipValue);
        vertices[1] = new Vertex((+0.43f)*flipValue,-0.5f,(+0.43f)*flipValue);
        vertices[2] = new Vertex((+0.43f)*flipValue,+0.45f,(+0.43f)*flipValue);
        vertices[3] = new Vertex((-0.43f)*flipValue,+0.45f,(-0.43f)*flipValue);
        obj.addDoubleSidedFace(vertices, null, t, material);
    }
}