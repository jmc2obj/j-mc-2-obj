package org.jmc.geom;

import javax.annotation.Nonnull;

/**
 * A small enum for describing sides of a cube.
 * @author danijel
 *
 */
public enum Direction
{
	/**Top*/
	UP,
	/**Bottom*/
	DOWN,
	/**Left*/
	WEST,
	/**Right*/
	EAST,
	/**Front*/
	NORTH,
	/**Back*/
	SOUTH;
	
	public int getArrIndex() {
		switch(this) {
		case UP: return 0;
		case DOWN: return 5;
		case SOUTH: return 2;
		case NORTH: return 1;
		case WEST: return 3;
		case EAST: return 4;
		}
		throw new RuntimeException("Unknown side " + this.name());
	}
	
	@Nonnull
	public Direction getOpposite() {
		switch(this) {
		case UP: return Direction.DOWN;
		case DOWN: return Direction.UP;
		case SOUTH: return Direction.NORTH;
		case NORTH: return Direction.SOUTH;
		case WEST: return Direction.EAST;
		case EAST: return Direction.WEST;
		}
		throw new RuntimeException("Unknown side " + this.name());
	}
	
	public Direction rotate(Transform trans) {
		Vertex vec = trans.multiply(getVector());
		return fromVector(vec);
	}
	
	public Vertex getVector() {
		switch(this) {
		case UP: return new Vertex(0, 1, 0);
		case DOWN: return new Vertex(0, -1, 0);
		case SOUTH: return new Vertex(0, 0, 1);
		case NORTH: return new Vertex(0, 0, -1);
		case WEST: return new Vertex(-1, 0, 0);
		case EAST: return new Vertex(1, 0, 0);
		}
		throw new RuntimeException("Unknown side " + this.name());
	}
	
	public static Direction fromVector(Vertex vector) {
		float ax,ay,az;
		ax = Math.abs(vector.x);
		ay = Math.abs(vector.y);
		az = Math.abs(vector.z);
		if (ax > ay && ax > az) {
			if (vector.x < 0) {
				return WEST;
			} else {
				return EAST;
			}
		} else if (ay > ax && ay > az) {
			if (vector.y < 0) {
				return DOWN;
			} else {
				return UP;
			}
		} else {
			if (vector.z <= 0) {
				return NORTH;
			} else {
				return SOUTH;
			}
		}
	}
}