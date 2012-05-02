package org.jmc;


/**
 * Holds the global options for the program.
 * 
 * Some options are only used in GUI mode or command line mode, but most apply to both.
 */
public class Options
{

	public enum OffsetType
	{
		NONE,
		CENTER,
		CUSTOM
	}
	
	public enum OverwriteAction
	{
		ASK,
		ALWAYS,
		NEVER
	}


	/**
	 * How to scale the coordinates of the exported region.
	 */
	public static float scale = 1.0f;
	
	/**
	 * How to offset the coordinates of the exported region.
	 */
	public static OffsetType offsetType = OffsetType.NONE;

	/**
	 * Custom offset X
	 */
	public static int offsetX = 0;
	
	/**
	 * Custom offset Z
	 */
	public static int offsetZ = 0;
	
	/**
	 * If true, will export a separate object for each material.
	 */
	public static boolean objectPerMaterial = false;
	
	/**
	 * If true, will try harder to merge vertices that have the same coordinates.
	 */
	public static boolean removeDuplicates = false;

	/**
	 * Whether to overwrite .OBJ files on export.
	 */
	public static OverwriteAction objOverwriteAction = OverwriteAction.ASK;
	
	/**
	 * Whether to overwrite .MTL files on export.
	 */
	public static OverwriteAction mtlOverwriteAction = OverwriteAction.ASK;

	/**
	 * Name of .OBJ file to export.
	 */
	public static String objFileName = "minecraft.obj";

	/**
	 * Name of .MTL file to export.
	 */
	public static String mtlFileName = "minecraft.mtl";
	
}
