package org.jmc;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jmc.util.Filesystem;
import org.jmc.util.Filesystem.JmcConfFile;
import org.jmc.util.Log;


/**
 * This class reads the default materials file (default.mtl).
 * Currently the only needed information is the material color.
 */
public class Materials
{

	private static final String CONFIG_FILE = "conf/default.mtl";
	private static final String SINGLE_TEXTURE_MTLS_FILE = "conf/singletex.mtl";
	private static final String SINGLE_MTL_FILE = "conf/single.mtl";

	private static ByteArrayOutputStream matBuffer;

	private static HashMap<String, Color> mtlColors;
	private static HashMap<String, String> mtlTextures = new HashMap<>();


	private static void readConfig(HashMap<String, Color> mtlColors) throws Exception
	{
		
		try (JmcConfFile mtlFile = new JmcConfFile(CONFIG_FILE)) {
			if (!mtlFile.hasStream())
				throw new Exception("Cannot open configuration file " + CONFIG_FILE);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(mtlFile.getInputStream()));
			String currMtl = null;
			Pattern rxNewmtl = Pattern.compile("^\\s*newmtl\\s+(.*?)\\s*$");
			Pattern rxKd = Pattern.compile("^\\s*Kd\\s+([0-9.]+)\\s+([0-9.]+)\\s+([0-9.]+)\\s*$");
			Pattern rxMapKd = Pattern.compile("^\\s*map_Kd\\s+tex\\/(.*?)\\.png\\s*$");

			String line;
			while ((line = reader.readLine()) != null)
			{
				Matcher mNewmtl = rxNewmtl.matcher(line);
				Matcher mKd = rxKd.matcher(line);
				Matcher mMapKd = rxMapKd.matcher(line);
				if (mNewmtl.matches())
				{
					currMtl = mNewmtl.group(1);
				}
				else if (mKd.matches() && currMtl != null)
				{
					float r = Float.parseFloat(mKd.group(1));
					float g = Float.parseFloat(mKd.group(2));
					float b = Float.parseFloat(mKd.group(3));

					mtlColors.put(currMtl.toLowerCase(), new Color(r,g,b,1));
				}
				else if (mMapKd.matches() && currMtl != null)
				{
					mtlTextures.put(currMtl.toLowerCase(), mMapKd.group(1));
				}
			}
		}
	}


	/**
	 * Reads the configuration file.
	 * Must be called once at the start of the program.
	 * 
	 * @throws Exception if reading the configuration failed. In this case the program should abort.
	 */
	public static void initialize() throws Exception
	{
		// create the colors table
		Log.info("Reading materials file...");
		
		mtlColors = new HashMap<String, Color>();
		readConfig(mtlColors);
		
		
		try (JmcConfFile mtlFile = new JmcConfFile(CONFIG_FILE)) {
			if (!mtlFile.hasStream())
				throw new Exception("Cannot open configuration file " + CONFIG_FILE);
			
			matBuffer = new ByteArrayOutputStream();
			
			Filesystem.copyStream(mtlFile.getInputStream(), matBuffer);
		}
		
		Log.info("Loaded " + mtlColors.size() + " materials.");
	}


	/**
	 * Copies the .mtl file to the given location.
	 * 
	 * @param dest Destination file
	 */
	public static void copyMTLFile(File dest) throws IOException
	{
		if(Options.singleMaterial)
		{
			try (JmcConfFile mtlFile = new JmcConfFile(SINGLE_MTL_FILE)) {
				Filesystem.writeFile(mtlFile.getInputStream(), dest);
			}
		}
		else if(Options.useUVFile)
		{
			try (JmcConfFile mtlFile = new JmcConfFile(SINGLE_TEXTURE_MTLS_FILE)) {
				Filesystem.writeFile(mtlFile.getInputStream(), dest);
			}
		}
		else
		{
			Files.copy(new ByteArrayInputStream(matBuffer.toByteArray()), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}


	/**
	 * Gets the diffuse color defined for a material.
	 * If the material name is not found, returns a default color.
	 * 
	 * @param mtlName Material name
	 * @return Material color
	 */
	public static Color getColor(String mtlName)
	{
		Color c = mtlColors.get(mtlName.toLowerCase());
		return c != null ? c : new Color(0,0,0);
	}
	
	/**
	 * Gets the texture defined for a material.
	 * If the material name is not found or it has no texture, returns null.
	 * 
	 * @param mtlName Material name
	 * @return Material texture
	 */
	public static String getTexture(String mtlName)
	{
		return mtlTextures.get(mtlName.toLowerCase());
	}
	
	public synchronized static void addMaterial(String matName, Color color, Color spec, String diffTex, String alphaTex) {
		if (color == null) 
			color = Color.WHITE;
		if (spec == null) 
			spec = Color.BLACK;
		
		float[] colorComps = color.getRGBComponents(null);
		float[] specComps = spec.getRGBComponents(null);
		
        PrintWriter out = new PrintWriter(Materials.getMaterialFileBuffer());
        out.println();
        out.println();
        out.printf("newmtl %s", matName).println();
        out.printf("Kd %.4f %.4f %.4f", colorComps[0], colorComps[1], colorComps[2]).println();
        out.printf("Ks %.4f %.4f %.4f", specComps[0], specComps[1], specComps[2]);
        if (diffTex != null) {
        	out.println();
        	out.printf("map_Kd %s", diffTex);
        }
        if (alphaTex != null) {
        	out.println();
        	out.printf("map_d %s", alphaTex);
        }
        
        out.close();
	}

	public static ByteArrayOutputStream getMaterialFileBuffer() {
		return matBuffer;
	}


}
