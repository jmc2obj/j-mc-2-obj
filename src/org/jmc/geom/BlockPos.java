package org.jmc.geom;

import java.util.Random;

public class BlockPos {
	public int x;
	public int y;
	public int z;
	
	public BlockPos(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Transform getTransform() {
		return Transform.translation(x, y, z);
	}
	
	public Random getRandom() {
		long seed = new Random(x).nextLong() + new Random(y).nextLong() + new Random(z).nextLong();
		return new Random(seed);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BlockPos) {
			BlockPos other = (BlockPos)obj;
			if (other.x == this.x && other.y == this.y && other.z == other.z) {
				return true;
			} else {
				return false;
			}
		} else {
			return super.equals(obj);
		}
	}
	
	@Override
	public String toString() {
		return String.format("[%d, %d, %d]", x, y, z);
	}
}
