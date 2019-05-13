package org.jmc.geom;

/**
 * A small enum for describing sides of a cube.
 * @author danijel
 *
 */
public enum Side
{
	TOP,
	BOTTOM,
	LEFT,
	RIGHT,
	FRONT,
	BACK;
	
	public Side getOpposite() {
		switch(this) {
		case TOP: return Side.BOTTOM;
		case BOTTOM: return Side.TOP;
		case BACK: return Side.FRONT;
		case FRONT: return Side.BACK;
		case LEFT: return Side.RIGHT;
		case RIGHT: return Side.LEFT;
		}
		throw new RuntimeException("Unknown side " + this.name());
	}
}