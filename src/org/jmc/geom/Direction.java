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
}