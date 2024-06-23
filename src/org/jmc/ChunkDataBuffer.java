package org.jmc;

import java.awt.Point;
import java.awt.Rectangle;
import java.lang.ref.WeakReference;

import org.jmc.Chunk.Blocks;
import org.jmc.util.CachedGetter;
import org.jmc.util.Log;

import javax.annotation.CheckForNull;

public class ChunkDataBuffer {

	private final Rectangle xzBoundaries;
	private final Rectangle xyBoundaries;
	private final CachedGetter<Point, WeakeningReference<Blocks>> chunks;
	private final CachedGetter<Point, Region> regions;

	public ChunkDataBuffer(int xmin, int xmax, int ymin, int ymax, int zmin, int zmax)
	{
		xzBoundaries = new Rectangle(xmin, zmin, xmax-xmin, zmax-zmin);
		xyBoundaries = new Rectangle(xmin, ymin, xmax-xmin, ymax-ymin);
		
		chunks = new CachedGetter<Point, WeakeningReference<Blocks>>() {
			@Override
			public WeakeningReference<Blocks> make(Point p) {
				return new WeakeningReference<>(makeBlocks(p));
			}
		};
		regions = new CachedGetter<Point, Region>() {
			@Override
			public Region make(Point p) {
				try {
					return Region.findRegion(Options.worldDir, Options.dimension, p);
				} catch (Exception e) {
					return null;
				}
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
		synchronized (ref) {
			Blocks blocks = ref.get();
			if (blocks == null) {
				blocks = makeBlocks(p);
				ref.set(blocks);
			}
			return blocks;
		}
	}
	
	@CheckForNull
	public Chunk getChunk(Point p) {
		try {// if chunk exists
			Point regionCoord = Region.getRegionCoord(p);
			Region region = regions.get(regionCoord);
			if (region == null)
				return null;
			
			return region.getChunk(p.x, p.y);
		} catch (Exception e) {
			Log.errorOnce("Error reading from chunk", e, false);
			return null;
		}
	}
	
	private Blocks makeBlocks(Point p) {
		Chunk chunk = getChunk(p);
		return chunk == null ? null : chunk.getBlocks();
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
	static private class WeakeningReference<T> {
		private T strongRef;
		private WeakReference<T> weakRef;
		public WeakeningReference(T b) {
			strongRef = b;
			weakRef = new WeakReference<>(b);
		}
		
		public T get() {
			T ref = weakRef.get();
			strongRef = null;
			return ref;
		}
		
		public void set(T b, boolean strengthen) {
			if (strengthen || strongRef != null) strongRef = b;
			weakRef = new WeakReference<>(b);
		}
		
		public void set(T b) {
			set(b, false);
		}
	}
}
