package org.jmc;

import java.io.File;


/**
 * Holds the global options for the program.
 * 
 * Some options are only used in GUI mode or command line mode, but most apply to both.
 */
public class Options
{

	public enum UIMode
	{
		GUI,
		CONSOLE
	}

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
	 * User interface mode.
	 */
	public static UIMode uiMode = UIMode.GUI; 
	
	/**
	 * Output directory.
	 */
	public static File outputDir = new File(".");
	
	/**
	 * Path to the Minecraft world save directory.
	 */
	public static File worldDir = null;

	/**
	 * Path to the texture pack to extract.
	 */
	public static File texturePack = null;

	/**
	 * Id of the world dimension to export.
	 */
	public static int dimension = 0;
	
	/**
	 * Lower bound of the volume to export.
	 */
	public static int minX=-32, minY=0, minZ=-32;
	
	/**
	 * Upper bound of the volume to export.
	 */
	public static int maxX=32, maxY=256, maxZ=32;
	
	/**
	 * How to scale the exported geometry.
	 */
	public static float scale = 1.0f;
	
	/**
	 * How to offset the coordinates of the exported geometry.
	 */
	public static OffsetType offsetType = OffsetType.NONE;

	/**
	 * Custom offset.
	 */
	public static int offsetX=0, offsetZ=0;
	
	/**
	 * If true, will export a separate object for each material.
	 */
	public static boolean objectPerMaterial = false;
	
	/**
	 * If true, will try harder to merge vertices that have the same coordinates.
	 */
	public static boolean removeDuplicates = false;

	/**
	 * Whether to export the .obj file.
	 * Only used in console mode.
	 */
	public static boolean exportObj = true;

	/**
	 * Whether to export the .mtl file.
	 * Only used in console mode.
	 */
	public static boolean exportMtl = true;

	/**
	 * Whether to export the textures.
	 * Only used in console mode.
	 */
	public static boolean exportTex = false;

	/**
	 * Whether to overwrite .OBJ files on export.
	 * Only used in GUI mode.
	 */
	public static OverwriteAction objOverwriteAction = OverwriteAction.ASK;
	
	/**
	 * Whether to overwrite .MTL files on export.
	 * Only used in GUI mode.
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
