package org.jmc.models;
 
import java.util.HashMap;

import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;
 
 
/**
 * Model for path block.
 */
public class Path extends BlockModel
{
   
    @Override
    public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, HashMap<String, String> data, int biome)
    {
        boolean[] drawSides = drawSides(chunks, x, y, z);
       
        UV[] uvSide = new UV[] { new UV(0,0), new UV(1,0), new UV(1,15/16f), new UV(0,15/16f) };
        UV[][] uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };
 
        addBox(obj,
                x-0.5f, y-0.5f, z-0.5f,
                x+0.5f, y+0.4375f, z+0.5f,
                null,
                getMtlSides(data,biome),
                uvSides,
                drawSides);
    }
 
}