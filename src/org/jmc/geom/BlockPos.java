package org.jmc.geom;

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
