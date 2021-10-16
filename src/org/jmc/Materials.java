package org.jmc;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.annotation.CheckForNull;

import org.jmc.registry.Registries;
import org.jmc.registry.TextureEntry;
import org.jmc.util.Filesystem;
import org.jmc.util.Filesystem.JmcConfFile;
import org.jmc.util.Log;
import org.jmc.util.Messages;


/**
 * This class reads the default materials file (default.mtl).
 * Currently the only needed information is the material color.
 */
public class Materials
{
	private static final String SINGLE_TEXTURE_MTLS_FILE = "conf/singletex.mtl";
	private static final String SINGLE_MTL_FILE = "conf/single.mtl";


	/**
	 * Copies the .mtl file to the given location.
	 * 
	 * @param dest Destination file
	 * @param progress 
	 */
	public static void writeMTLFile(File dest, ProgressCallback progress) throws IOException
	{
		if (progress != null)
			progress.setMessage(Messages.getString("Progress.MTL"));
		/*if(Options.singleMaterial) { TODO fix single tex export
			try (JmcConfFile mtlFile = new JmcConfFile(SINGLE_MTL_FILE)) {
				Filesystem.writeFile(mtlFile.getInputStream(), dest);
			}
		} else if(Options.useUVFile) {
			try (JmcConfFile mtlFile = new JmcConfFile(SINGLE_TEXTURE_MTLS_FILE)) {
				Filesystem.writeFile(mtlFile.getInputStream(), dest);
			}
		} else*/ {
			ByteArrayOutputStream matBuffer = new ByteArrayOutputStream();
			int count = 0;
			synchronized (Registries.objTextures) {
				for (TextureEntry textureEntry : Registries.objTextures) {
					try {
						String alphaTex = null;
						if (textureEntry.hasAlpha()) {
							if (Options.textureAlpha) {
								alphaTex = textureEntry.getExportFilePathAlpha();
							} else {
								alphaTex = textureEntry.getExportFilePath();
							}
						}
						writeMaterial(matBuffer, textureEntry.getMatName(), textureEntry.getAverageColour(), null, textureEntry.getExportFilePath(), alphaTex);
					} catch (IOException e) {
						Log.error("Error writing material definition " + textureEntry.id, e);
					}
					if (progress != null)
						progress.setProgress((float)++count / Registries.objTextures.size());
				}
			}
			Files.copy(new ByteArrayInputStream(matBuffer.toByteArray()), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	private static void writeMaterial(ByteArrayOutputStream matBuffer, String matName, Color color, Color spec, @CheckForNull String diffTex, @CheckForNull String alphaTex) {
		if (color == null)
			color = Color.WHITE;
		if (spec == null)
			spec = Color.BLACK;
		
		float[] colorComps = color.getRGBComponents(null);
		float[] specComps = spec.getRGBComponents(null);
		
		PrintWriter out = new PrintWriter(matBuffer);
		out.println();
		out.printf("newmtl %s", matName).println();
		out.printf("Kd %.4f %.4f %.4f", colorComps[0], colorComps[1], colorComps[2]).println();
		out.printf("Ks %.4f %.4f %.4f", specComps[0], specComps[1], specComps[2]).println();
		if (diffTex != null) {
			out.printf("map_Kd %s", diffTex).println();
		}
		if (alphaTex != null) {
			out.printf("map_d %s", alphaTex).println();
		}
		
		out.close();
	}


}
