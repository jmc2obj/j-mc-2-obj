package org.jmc;

import java.io.File;

import org.jmc.Options.OffsetType;
import org.jmc.util.Filesystem;
import org.jmc.util.Log;


/**
 * Command line parser
 */
public class CmdLineParser
{

	/**
	 * Exception to signal an invalid command line.
	 */
	@SuppressWarnings("serial")
	public static class CmdLineException extends Exception
	{
		public CmdLineException(String message) {
			super(message);
		}
	}
	
	
	/**
	 * Parses the command line and sets the corresponding Options values.
	 * @param args
	 * @throws CmdLineException if there are invalid arguments
	 */
	public static void parse(String[] args) throws CmdLineException
	{
		for (int i = 0; i < args.length; i++)
		{
			String a = args[i];
			
			try {
				if (a.equals("-o")) {
					Options.outputDir = new File(args[i+1]);
					i++;
				}
				else if (a.startsWith("--output=")) {
					Options.outputDir = new File(a.substring(9));
				}
				else if (a.equals("-a")) {
					String[] parts = args[i+1].split(",");
					Options.minX = Integer.parseInt(parts[0]);
					Options.minZ = Integer.parseInt(parts[1]);
					Options.maxX = Integer.parseInt(parts[2]);
					Options.maxZ = Integer.parseInt(parts[3]);
					i++;
				}
				else if (a.startsWith("--area=")) {
					String[] parts = a.substring(7).split(",");
					Options.minX = Integer.parseInt(parts[0]);
					Options.minZ = Integer.parseInt(parts[1]);
					Options.maxX = Integer.parseInt(parts[2]);
					Options.maxZ = Integer.parseInt(parts[3]);
				}
				else if (a.equals("-c")) {
					String[] parts = args[i+1].split(",");
					Options.minX = Integer.parseInt(parts[0]) * 16;
					Options.minZ = Integer.parseInt(parts[1]) * 16;
					Options.maxX = Integer.parseInt(parts[2]) * 16;
					Options.maxZ = Integer.parseInt(parts[3]) * 16;
					i++;
				}
				else if (a.startsWith("--chunks=")) {
					String[] parts = a.substring(9).split(",");
					Options.minX = Integer.parseInt(parts[0]) * 16;
					Options.minZ = Integer.parseInt(parts[1]) * 16;
					Options.maxX = Integer.parseInt(parts[2]) * 16;
					Options.maxZ = Integer.parseInt(parts[3]) * 16;
				}
				else if (a.equals("-h")) {
					String[] parts = args[i+1].split(",");
					Options.minY = Integer.parseInt(parts[0]);
					Options.maxY = Integer.parseInt(parts[1]);
					i++;
				}
				else if (a.startsWith("--height=")) {
					String[] parts = a.substring(9).split(",");
					Options.minY = Integer.parseInt(parts[0]);
					Options.maxY = Integer.parseInt(parts[1]);
				}
				else if (a.equals("-d")) {
					Options.dimension = Integer.parseInt(args[i+1]);
					i++;
				}
				else if (a.startsWith("--dimension=")) {
					Options.dimension = Integer.parseInt(a.substring(12));
				}
				else if (a.equals("-e")) {
					Options.exportWorld = true;
					Options.exportObj = false;
					Options.exportMtl = false;
					for (String part : args[i+1].split(",")) {
						if (part.equals("obj")) Options.exportObj = true;
						else if (part.equals("mtl")) Options.exportMtl = true;
						else throw new CmdLineException("Invalid argument to option -e: " + part);
					}
					i++;
				}
				else if (a.startsWith("--export=")) {
					Options.exportWorld = true;
					Options.exportObj = false;
					Options.exportMtl = false;
					for (String part : a.substring(9).split(",")) {
						if (part.equals("obj")) Options.exportObj = true;
						else if (part.equals("mtl")) Options.exportMtl = true;
						else throw new CmdLineException("Invalid argument to option --export: " + part);
					}
				}
				else if (a.startsWith("-t")) {
					Options.exportTex = true;
					Options.textureDiffuse = false;
					Options.textureAlpha = false;
					Options.textureNormal = false;
					Options.textureSpecular = false;
					for (String part : args[i+1].split(",")) {
						if (part.equals("base")) Options.textureDiffuse = true;
						else if (part.equals("alpha")) Options.textureAlpha = true;
						else if (part.equals("norm")) Options.textureNormal = true;
						else if (part.equals("spec")) Options.textureSpecular = true;
						else throw new CmdLineException("Invalid argument to option -t: " + part);
					}
					i++;
				}
				else if (a.startsWith("--tex_export=")) {
					Options.exportTex = true;
					Options.textureDiffuse = false;
					Options.textureAlpha = false;
					Options.textureNormal = false;
					Options.textureSpecular = false;
					for (String part : a.substring(13).split(",")) {
						if (part.equals("base")) Options.textureDiffuse = true;
						else if (part.equals("alpha")) Options.textureAlpha = true;
						else if (part.equals("norm")) Options.textureNormal = true;
						else if (part.equals("spec")) Options.textureSpecular = true;
						else throw new CmdLineException("Invalid argument to option --tex_export: " + part);
					}
				}
				else if (a.startsWith("--resourcepack=")) {
					Options.resourcePacks.add(new File(a.substring(15)));
				}
				else if (a.startsWith("--texturescale=")) {
					Options.textureScale = Float.parseFloat(a.substring(15));
				}
				else if (a.startsWith("--objfile=")) {
					Options.objFileName = a.substring(10);
				}
				else if (a.startsWith("--mtlfile=")) {
					Options.mtlFileName = a.substring(10);
				}
				else if (a.startsWith("--scale=")) {
					Options.scale = Float.parseFloat(a.substring(8));
				}
				else if (a.startsWith("--offset=")) {
					String aux = a.substring(9);
					if (aux.equals("none")) {
						Options.offsetType = OffsetType.NONE;
					}
					else if (aux.equals("center")) {
						Options.offsetType = OffsetType.CENTER;
					}
					else {
						Options.offsetType = OffsetType.CUSTOM;
						String[] parts = aux.split(",");
						Options.offsetX = Integer.parseInt(parts[0]);
						Options.offsetZ = Integer.parseInt(parts[1]);
					}
				}
				else if (a.equals("-s")) {
					Options.renderSides = true;
				}
				else if (a.equals("--render-sides")) {
					Options.renderSides = true;
				}
				else if (a.equals("--include-unknown")) {
					Options.renderUnknown = true;
				}
				else if (a.equals("--ignore-biomes")) {
					Options.renderBiomes = false;
				}
				else if (a.equals("--render-entities")) {
					Options.renderEntities = true;
				}
				else if (a.equals("--object-per-chunk")) {
					Options.objectPerChunk = true;
				}
				else if (a.equals("--object-per-mat")) {
					Options.objectPerMaterial = true;
				}
				else if (a.equals("--object-per-block")) {
					Options.objectPerBlock = true;
				}
				else if (a.equals("--remove-dup")) {
					Options.removeDuplicates = true;
				}
				else if (a.equals("--optimize-geometry")) {
					Options.optimiseGeometry = true;
				}
				else if (a.equals("--help")) {
					printUsage();
					System.exit(-1);
				}
				else if (a.startsWith("-")) {
					throw new CmdLineException("Unrecognized option: " + a);
				}
				else {
					Options.exportWorld = true;
					if (Options.worldDir == null)
						Options.worldDir = new File(a);
					else
						throw new CmdLineException("Only one world directory must be given.");
				}
			}
			catch (CmdLineException ex) {
				throw ex;
			}
			catch (IndexOutOfBoundsException ex) {
				throw new CmdLineException("Missing argument to option " + a);
			}
			catch (Exception ex) {
				throw new CmdLineException("Invalid option: " + a);
			}
		}

		// basic validations
		if (Options.worldDir != null && !Options.worldDir.isDirectory())
			throw new CmdLineException(Options.worldDir + " is not a valid directory.");
		if (!Options.outputDir.isDirectory())
			throw new CmdLineException(Options.outputDir + " is not a valid directory.");
		
		if (Options.resourcePacks.isEmpty()) {
			Log.info("No resource pack specified, attempting to find & use latest minecraft .jar");
			File jar = Filesystem.getMinecraftJar();
			if (jar != null) {
				Options.resourcePacks.add(jar);
			}
		}
	}
	

	/**
	 * Prints the command line usage help.
	 */
	public static void printUsage()
	{
		String usage =
			"\n"+
			"Usage:\n" +
			"\n" +
			"jmc2obj                             Run program in GUI mode\n" +
			"jmc2obj [OPTIONS] WORLD_DIRECTORY   Run program in command line mode\n" +
			"\n" +
			"Options:\n" +
			"  -o --output=DIR                   Output directory. Default is current\n" +
			"                                    directory.\n" +
			"  -a --area=MINX,MINZ,MAXX,MAXZ     Area to export, in Minecraft units.\n" +
			"  -c --chunks=MINX,MINZ,MAXX,MAXZ   Area to export, in chunks (one chunk is\n" +
			"                                    16x16 units).\n" +
			"  -h --height=MINY,MAXY             Minimum and maximum height to export.\n" +
			"                                    World bottom is 0, ocean level is 63,\n" +
			"                                    world top is 256. Default is 0,256.\n" +
			"  -d --dimension=ID                 World dimension to export. Dimension ids\n" +
			"                                    are: 0 - Overworld; -1 - Nether; 1 - The\n" +
			"                                    End. Mods may add more dimensions. Default\n" +
			"                                    is 0.\n" +
			"  -e --export=obj[,mtl]             What files to export (any combination is\n" +
			"                                    valid): obj - geometry file (.obj); mtl -\n" +
			"                                    materials file (.mtl). Default is obj,mtl.\n" +
			"  -t --tex_export=base[,alpha][,norm][,spec]\n" +
			"                                    What textures to export (any combination is\n" +
			"                                    valid): base - base textures; alpha -\n" +
			"                                    seperate alphas; norm - normal maps.\n" +
			"     --resourcepack=FILE            When exporting, use this resource\n" +
			"                                    pack. If omitted will attempt to use the\n" +
			"                                    default Minecraft models and textures.\n" +
			"     --texturescale=SCALE           When exporting textures, scale the images\n" +
			"                                    by this factor. Default is 1 (no scaling).\n" +
			"     --objfile=NAME                 Name of geometry file to export. Default\n" +
			"                                    is minecraft.obj\n" +
			"     --mtlfile=NAME                 Name of materials file to export. Default\n" +
			"                                    is minecraft.mtl\n" +
			"     --scale=SCALE                  How to scale the exported geometry. Default\n" +
			"                                    is 1 (no scaling).\n" +
			"     --offset=none|center|X,Z       How to offset the coordinates of the\n" +
			"                                    exported geometry: none - no offset;\n" +
			"                                    center - place the center of the exported\n" +
			"                                    area at the origin; X,Z - apply the given\n" +
			"                                    offset. Default is none.\n" +
			"  -s --render-sides                 Render world sides and bottom.\n" +
			"     --render-entities              Render entities.\n" +
			"     --include-unknown              Include blocks with unknown block ids.\n" +
			"     --ignore-biomes                Don't render biomes.\n" +
			"     --object-per-chunk             Export a separate object for each chunk.\n" +
			"     --object-per-mat               Export a separate object for each material.\n" +
			"     --object-per-block             Export a separate object for each block.\n" +
			"                                    WARNING: Will produce very large files.\n" +
			"     --remove-dup                   Try harder to merge vertexes that have the\n" +
			"                                    same coordinates.\n" +
			"     --optimize-geometry            Reduce size of exported files by joining\n" +
			"                                    adjacent faces together when possible.\n" +
			"     --help                         Display this help.\n";

		System.out.println(usage);
	}

}
