package org.jmc;

import java.awt.Point;
import java.awt.Rectangle;
import java.lang.ref.WeakReference;

import org.jmc.Chunk.Blocks;
import org.jmc.util.CachedGetter;

public class ChunkDataBuffer {

	private final Rectangle xzBoundaries;
	private final Rectangle xyBoundaries;
	private final CachedGetter<Point, WeakeningReference<Blocks>> chunks;

	public ChunkDataBuffer(int xmin, int xmax, int ymin, int ymax, int zmin, int zmax)
	{
		xzBoundaries = new Rectangle(xmin, zmin, xmax-xmin, zmax-zmin);
		xyBoundaries = new Rectangle(xmin, ymin, xmax-xmin, ymax-ymin);
		
		chunks = new CachedGetter<Point, WeakeningReference<Blocks>>() {
			@Override
			public WeakeningReference<Blocks> make(Point p) {
				Chunk chunk;
				try {// if chunk exists
					Region region = Region.findRegion(Options.worldDir, Options.dimension, p.x, p.y);
					if (region == null)
						return null;
					
					chunk = region.getChunk(p.x, p.y);
					if (chunk == null)
						return null;
				} catch (Exception e) {
					return null;
				}
				
				return new WeakeningReference<>(chunk.getBlocks());
			}
		};
	}
	
	public synchronized void removeAllChunks()
	{
		chunks.clear();
	}
	
	public synchronized int getChunkCount()
	{
		return chunks.size();
	}

	public Blocks getBlocks(Point p)
	{
		WeakeningReference<Blocks> ref = chunks.get(p);
		if (ref == null)
			return null;
		Blocks blocks = ref.get();
		if (blocks == null) { // there was a reference, but it was garbage collected
			chunks.remove(p); // remove the dead ref and generate a new one
			ref = chunks.get(p);
			blocks = ref.get();
		}
		return blocks;
	}
	
	public Rectangle getXZBoundaries()
	{
		return xzBoundaries;
	}
	
	public Rectangle getXYBoundaries()
	{
		return xyBoundaries;
	}
	
	/** Weak ref that holds a strong reference until {@link #get()} is called for the first time
	 * stops it being gc'd until use */
	static private class WeakeningReference<T> extends WeakReference<T> {
		private T strongRef;
		public WeakeningReference(T b) {
			super(b);
			strongRef = b;
		}
		
		@Override
		public T get() {
			T ref = super.get();
			strongRef = null;
			return ref;
		}
	}
}
