package org.jmc;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.*;
import org.jmc.Options.OffsetType;
import org.jmc.registry.NamespaceID;
import org.jmc.util.Filesystem;
import org.jmc.util.Log;


/**
 * Command line parser
 */
public class CmdLineParser {

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
	
	private static final Option optOutput = Option.builder("o").longOpt("output").hasArg().argName("DIR").desc("Output directory. Default is current directory.").build();
	private static final Option optArea = Option.builder("a").longOpt("area")
			.numberOfArgs(4).valueSeparator(',').argName("MINX,MINZ,MAXX,MAXZ")
			.desc("Area to export, in Minecraft units.").build();
	private static final Option optChunks = Option.builder("c").longOpt("chunks")
			.numberOfArgs(4).valueSeparator(',').argName("MINX,MINZ,MAXX,MAXZ")
			.desc("Area to export, in chunks (one chunk is 16x16 units).").build();
	private static final Option optHeight = Option.builder("h").longOpt("height")
			.numberOfArgs(2).valueSeparator(',').argName("MINY,MAXY")
			.desc("Minimum and maximum height to export. Default is -64,320.").build();
	private static final Option optDimension = Option.builder("d").longOpt("dimension").hasArg().argName("ID").desc("World dimension to export. Dimension ids are: 0 - Overworld; -1 - Nether; 1 - The End. Mods may add more dimensions. Default is 0.").build();
	private static final Option optTexExport = Option.builder().longOpt("tex-export")
			.numberOfArgs(4).valueSeparator(',').argName("base[,alpha][,norm][,spec]").optionalArg(true)
			.desc("What textures to export (any combination is valid): base - base textures; alpha - separate alphas; norm - normal maps. Default is base.").build();
	private static final Option optResourcePack = Option.builder("r").longOpt("resource-pack").hasArg().argName("FILE").desc("Resource pack to use for textures and models. Can be specified multiple times. If omitted, will attempt to use the default Minecraft pack.").build();
	private static final Option optTexScale = Option.builder().longOpt("texturescale").hasArg().argName("SCALE").desc("When exporting textures, scale the images by this factor. Default is 1 (no scaling).").build();
	private static final Option optObjFile = Option.builder().longOpt("objfile").hasArg().argName("NAME").desc("Name of geometry file to export. Default is minecraft.obj.").build();
	private static final Option optMtlFile = Option.builder().longOpt("mtlfile").hasArg().argName("NAME").desc("Name of materials file to export. Default is minecraft.mtl.").build();
	private static final Option optScale = Option.builder().longOpt("scale").hasArg().argName("SCALE").desc("Scale factor to apply to exported geometry. Default is 1.").build();
	private static final Option optOffset = Option.builder().longOpt("offset")
			.numberOfArgs(1).valueSeparator(',').argName("none|center|X,Z")
			.desc("How to offset the coordinates of the exported geometry: none - no offset; center - place the center of the exported area at the origin; X,Z - apply the given offset. Default is none.").build();
	private static final Option optRenderSides = new Option("s", "render-sides", false, "Render world sides and bottom.");
	private static final Option optRenderEntities = new Option(null, "render-entities", false, "Render entities.");
	private static final Option optIncludeUnknown = new Option(null, "include-unknown", false, "Include unknown blocks in the exported geometry.");
	private static final Option optBlacklistBlocks = Option.builder().longOpt("blacklist").hasArg().argName("BLOCK IDS").desc("Specify a comma separated list of block id's to exclude from the export.").build();
	private static final Option optWhitelistBlocks = Option.builder().longOpt("whitelist").hasArg().argName("BLOCK IDS").desc("Specify a comma separated list of block id's to include in the export, only these blocks will be exported.").build();
	private static final Option optIgnoreBiomes = new Option(null, "ignore-biomes", false, "Don't render biomes.");
	private static final Option optConvertOres = new Option(null, "convert-ores", false, "Convert ore blocks to stone.");
	private static final Option optObjectPerChunk = new Option(null, "object-per-chunk", false, "Export a separate object for each chunk.");
	private static final Option optObjectPerMaterial = new Option(null, "object-per-mat", false, "Export a separate object for each material.");
	private static final Option optObjectPerMaterialOcclusion = new Option(null, "object-per-mat-occl", false, "Enables occlusion testing for adjacent blocks with different materials. Only effective with object-per-mat.");
	private static final Option optObjectPerBlock = new Option(null, "object-per-block", false, "Export a separate object for each block. WARNING: will produce very large files.");
	private static final Option optObjectPerBlockOcclusion = new Option(null, "object-per-block-occl", false, "Enables occlusion testing for adjacent blocks. Only effective with object-per-block.");
	private static final Option optObjUseGroup = new Option(null, "obj-use-group", false, "Export objects as obj groups instead of objects (Maya compatible)");
	private static final Option optBlockRandomization = new Option(null, "block-randomization", false, "Allow resource pack models to randomly pick from blockstate models instead of always the first.");
	private static final Option optRemoveDuplicates = new Option(null, "remove-dup", false, "Try harder to merge vertexes that have the same coordinates.");
	private static final Option optOptimizeGeometry = new Option(null, "optimize-geometry", false, "Reduce size of exported files by joining adjacent faces together when possible.");
	private static final Option optThreads = Option.builder("t").longOpt("threads").hasArg().argName("NUM").desc("Number of threads to use. Default is 8.").build();
	private static final Option optHelp = new Option("?", "help", false, "Displays this help");
	
	private static final org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
	
	static {
		options.addOption(optOutput);
		OptionGroup selectionGroup = new OptionGroup();
		selectionGroup.addOption(optArea);
		selectionGroup.addOption(optChunks);
		options.addOptionGroup(selectionGroup);
		options.addOption(optHeight);
		options.addOption(optDimension);
		options.addOption(optTexExport);
		options.addOption(optResourcePack);
		options.addOption(optTexScale);
		options.addOption(optObjFile);
		options.addOption(optMtlFile);
		options.addOption(optScale);
		options.addOption(optOffset);
		options.addOption(optRenderSides);
		options.addOption(optRenderEntities);
		options.addOption(optIncludeUnknown);
		OptionGroup excludeGroup = new OptionGroup();
		excludeGroup.addOption(optBlacklistBlocks);
		excludeGroup.addOption(optWhitelistBlocks);
		options.addOptionGroup(excludeGroup);
		options.addOption(optIgnoreBiomes);
		options.addOption(optConvertOres);
		options.addOption(optObjectPerChunk);
		options.addOption(optObjectPerMaterial);
		options.addOption(optObjectPerMaterialOcclusion);
		options.addOption(optObjectPerBlock);
		options.addOption(optObjectPerBlockOcclusion);
		options.addOption(optObjUseGroup);
		options.addOption(optBlockRandomization);
		options.addOption(optRemoveDuplicates);
		options.addOption(optOptimizeGeometry);
		options.addOption(optThreads);
		options.addOption(optHelp);
	}
	
	private static Option currentOpt = null;
	private static boolean checkOption(CommandLine cmd, Option opt) {
		currentOpt = opt;
		return cmd.hasOption(opt);
	}
	
	/**
	 * Parses the command line and sets the corresponding Options values.
	 * @param args command line arguments
	 * @throws CmdLineException if there are invalid arguments
	 */
	public static void parse(String[] args) throws CmdLineException {
		
		try {
			CommandLine cmdLine = new DefaultParser().parse(options, args);
			if (checkOption(cmdLine, optHelp)) {
				printHelp();
				System.exit(-1);
			}
			if (checkOption(cmdLine, optOutput)) {
				Options.outputDir = new File(cmdLine.getOptionValue(optOutput));
			}
			if (checkOption(cmdLine, optArea)) {
				String[] parts = cmdLine.getOptionValues(optArea);
				if (parts.length != 4) throw new CmdLineException(String.format("Option '%s' must have exactly 4 arguments", optArea.hasLongOpt() ? optArea.getLongOpt() : optArea.getOpt()));
				Options.minX = Integer.parseInt(parts[0]);
				Options.minZ = Integer.parseInt(parts[1]);
				Options.maxX = Integer.parseInt(parts[2]);
				Options.maxZ = Integer.parseInt(parts[3]);
			}
			if (checkOption(cmdLine, optChunks)) {
				String[] parts = cmdLine.getOptionValues(optChunks);
				if (parts.length != 4) throw new CmdLineException(String.format("Option '%s' must have exactly 4 arguments", optChunks.hasLongOpt() ? optChunks.getLongOpt() : optChunks.getOpt()));
				Options.minX = Integer.parseInt(parts[0]) * 16;
				Options.minZ = Integer.parseInt(parts[1]) * 16;
				Options.maxX = Integer.parseInt(parts[2]) * 16;
				Options.maxZ = Integer.parseInt(parts[3]) * 16;
			}
			if (checkOption(cmdLine, optHeight)) {
				String[] parts = cmdLine.getOptionValues(optHeight);
				if (parts.length != 2) throw new CmdLineException(String.format("Option '%s' must have exactly 2 arguments", optHeight.hasLongOpt() ? optHeight.getLongOpt() : optHeight.getOpt()));
				Options.minY = Integer.parseInt(parts[0]);
				Options.maxY = Integer.parseInt(parts[1]);
			}
			if (checkOption(cmdLine, optDimension)) {
				Options.dimension = NamespaceID.fromString(cmdLine.getOptionValue(optDimension));
			}
			if (checkOption(cmdLine, optTexExport)) {
				Options.exportTex = true;
				Options.textureDiffuse = false;
				Options.textureAlpha = false;
				Options.textureNormal = false;
				Options.textureSpecular = false;
				for (String part : cmdLine.getOptionValues(optTexExport)) {
					switch (part) {
						case "base":
							Options.textureDiffuse = true;
							break;
						case "alpha":
							Options.textureAlpha = true;
							break;
						case "norm":
							Options.textureNormal = true;
							break;
						case "spec":
							Options.textureSpecular = true;
							break;
						default:
							throw new CmdLineException("Invalid argument to option -t: " + part);
					}
				}
			}
			if (checkOption(cmdLine, optResourcePack)) {
				for (String pack : cmdLine.getOptionValues(optResourcePack)) {
					Options.resourcePacks.add(new File(pack));
				}
			}
			if (checkOption(cmdLine, optTexScale)) {
				Options.textureScale = Float.parseFloat(cmdLine.getOptionValue(optTexScale));
			}
			if (checkOption(cmdLine, optObjFile)) {
				Options.objFileName = cmdLine.getOptionValue(optObjFile);
			}
			if (checkOption(cmdLine, optMtlFile)) {
				Options.mtlFileName = cmdLine.getOptionValue(optMtlFile);
			}
			if (checkOption(cmdLine, optScale)) {
				Options.scale = Float.parseFloat(cmdLine.getOptionValue(optScale));
			}
			if (checkOption(cmdLine, optOffset)) {
				String arg = cmdLine.getOptionValue(optOffset);
				switch (arg) {
					case "none":
						Options.offsetType = OffsetType.NONE;
						break;
					case "center":
						Options.offsetType = OffsetType.CENTER;
						break;
					default:
						String[] parts = arg.split(",");
						if (parts.length == 2) {
							Options.offsetType = OffsetType.CUSTOM;
							Options.offsetX = Integer.parseInt(parts[0]);
							Options.offsetZ = Integer.parseInt(parts[1]);
						} else {
							throw new CmdLineException("Invalid argument to option -offset: " + arg);
						}
				}
			}
			if (checkOption(cmdLine, optRenderSides)) {
				Options.renderSides = true;
			}
			if (checkOption(cmdLine, optIncludeUnknown)) {
				Options.renderUnknown = true;
			}
			if (cmdLine.hasOption(optBlacklistBlocks) || cmdLine.hasOption(optWhitelistBlocks)) {
				String arg;
				if (checkOption(cmdLine, optWhitelistBlocks)) {
					arg = cmdLine.getOptionValue(optWhitelistBlocks);
					Options.excludeBlocksIsWhitelist = true;
				} else {
					checkOption(cmdLine, optBlacklistBlocks);
					arg = cmdLine.getOptionValue(optBlacklistBlocks);
				}
				
				Set<NamespaceID> blocks = new HashSet<>();
				for (String str : arg.split(",")) {
					blocks.add(NamespaceID.fromString(str));
				}
				Options.excludeBlocks = blocks;
			}
			if (checkOption(cmdLine, optIgnoreBiomes)) {
				Options.renderBiomes = false;
			}
			if (checkOption(cmdLine, optConvertOres)) {
				Options.convertOres = true;
			}
			if (checkOption(cmdLine, optRenderEntities)) {
				Options.renderEntities = true;
			}
			if (checkOption(cmdLine, optObjectPerChunk)) {
				Options.objectPerChunk = true;
			}
			if (checkOption(cmdLine, optObjectPerMaterial)) {
				Options.objectPerMaterial = true;
			}
			if (checkOption(cmdLine, optObjectPerMaterialOcclusion)) {
				Options.objectPerMaterialOcclusion = true;
			}
			if (checkOption(cmdLine, optObjectPerBlock)) {
				Options.objectPerBlock = true;
			}
			if (checkOption(cmdLine, optObjectPerBlockOcclusion)) {
				Options.objectPerBlockOcclusion = true;
			}
			if (checkOption(cmdLine, optObjUseGroup)) {
				Options.objUseGroup = true;
			}
			if (checkOption(cmdLine, optBlockRandomization)) {
				Options.randBlockVariations = true;
			}
			if (checkOption(cmdLine, optRemoveDuplicates)) {
				Options.removeDuplicates = true;
			}
			if (checkOption(cmdLine, optOptimizeGeometry)) {
				Options.optimiseGeometry = true;
			}
			if (checkOption(cmdLine, optThreads)) {
				Options.exportThreads = Integer.parseInt(cmdLine.getOptionValue(optThreads));
			}
			Options.exportWorld = true;
			List<String> remainingArgs = cmdLine.getArgList();
			if (remainingArgs.size() == 1) {
				Options.worldDir = new File(remainingArgs.get(0));
			} else if (remainingArgs.size() > 1) {
				throw new CmdLineException("Only one world directory must be given.");
			} else {
				printHelp();
				throw new CmdLineException("No world directory specified!");
			}
		} catch (AlreadySelectedException e) {
			Option opt = e.getOption();
			OptionGroup group = e.getOptionGroup();
			throw new CmdLineException(String.format("Option '%s' has been used, can't also use '%s'", group.getSelected(), opt.hasLongOpt() ? opt.getLongOpt() : opt.getOpt()));
		} catch (ParseException e) {
			throw new CmdLineException(e.getMessage());
		} catch (IndexOutOfBoundsException e) {
			if (currentOpt != null) {
				throw new CmdLineException(String.format("Missing argument to option '%s'! %s", currentOpt.hasLongOpt() ? currentOpt.getLongOpt() : currentOpt.getOpt(), e.toString()));
			} else {
				throw new CmdLineException("Missing argument to option! " + e.toString());
			}
		} catch (NumberFormatException e) {
			if (currentOpt != null) {
				throw new CmdLineException(String.format("Invalid argument passed to option '%s'! %s", currentOpt.hasLongOpt() ? currentOpt.getLongOpt() : currentOpt.getOpt(), e.toString()));
			} else {
				throw new CmdLineException("Invalid argument passed to option! " + e.toString());
			}
		}
		
		// basic validations
		if (!Options.worldDir.isDirectory())
			throw new CmdLineException(Options.worldDir + " is not a valid directory.");
		if (!Options.outputDir.isDirectory())
			throw new CmdLineException(Options.outputDir + " is not a valid directory.");
		
		if (Options.resourcePacks.isEmpty()) {
			Log.info("No resource pack specified, attempting to find & use latest minecraft .jar");
			File jar = Filesystem.getMinecraftJar();
			if (jar != null) {
				Log.info("Using minecraft %s as default resource pack.");
				Options.resourcePacks.add(jar);
			} else {
				Log.info("No minecraft .jar found to use as default resource pack!");
			}
		}
	}


	/**
	 * Prints the command line usage help.
	 */
	private static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setLongOptSeparator("=");
		formatter.setOptionComparator(null);
		formatter.setWidth(110);
		formatter.printHelp("jmc2obj [OPTIONS] WORLD_DIRECTORY", "Running with no arguments launches the GUI", options, null);
	}

}
