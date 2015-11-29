package org.jmc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import org.jmc.geom.FaceUtils.Face;
import org.jmc.geom.Vertex;
import org.jmc.threading.ThreadOutputQueue;
import org.jmc.threading.ThreadOutputQueue.ChunkOutput;
import org.jmc.threading.WriterRunnable;
import org.jmc.util.Filesystem;
import org.jmc.util.Log;


/**
 * Class for exporting Minecraft cloud textures to 3D files.
 */
public class CloudsExporter {

	private static boolean isCloud(BufferedImage image, int x, int y) {
		int image_w = image.getWidth();
		int image_h = image.getHeight();
		
		// wrap coordinates around
		if (x < 0)
			x += image_w;
		else if (x >= image_w)
			x -= image_w;
		if (y < 0)
			y += image_h;
		else if (y >= image_h)
			y -= image_h;
		
		// get alpha value
		int a = image.getRGB(x, y) >>> 24;
		
		// The default texture pack uses alpha=1 (almost transparent) to indicate
		// clear sky and alpha=255 (fully opaque) to indicate a cloud. Here we
		// assume anything above 127 is a cloud.
		return a > 127;
	}
	
	private static void renderClouds(BufferedImage image, ThreadOutputQueue queue) {
		int image_w = image.getWidth();
		int image_h = image.getHeight();
		
		ArrayList<Face> faces = new ArrayList<Face>();
		
		for (int z = 0; z < image_h; z++) {
			for (int x = 0; x < image_w; x++) {
				if (isCloud(image, x, z)) {
					Vertex[] verts = new Vertex[4];

					// bottom
					verts[0] = new Vertex(x+1, 0, z+1);
					verts[1] = new Vertex(x, 0, z+1);
					verts[2] = new Vertex(x, 0, z);
					verts[3] = new Vertex(x+1, 0, z);
					faces.add(new Face(verts.clone(), null, null, "cloud"));
					
					// top
					verts[0] = new Vertex(x, 0.3333f, z+1);
					verts[1] = new Vertex(x+1, 0.3333f, z+1);
					verts[2] = new Vertex(x+1, 0.3333f, z);
					verts[3] = new Vertex(x, 0.3333f, z);
					faces.add(new Face(verts.clone(), null, null, "cloud"));
					
					// left (W)
					if (!isCloud(image, x-1, z)) {
						verts[0] = new Vertex(x, 0, z);
						verts[1] = new Vertex(x, 0, z+1);
						verts[2] = new Vertex(x, 0.3333f, z+1);
						verts[3] = new Vertex(x, 0.3333f, z);
						faces.add(new Face(verts.clone(), null, null, "cloud"));
					}

					// right (E)
					if (!isCloud(image, x+1, z)) {
						verts[0] = new Vertex(x+1, 0, z+1);
						verts[1] = new Vertex(x+1, 0, z);
						verts[2] = new Vertex(x+1, 0.3333f, z);
						verts[3] = new Vertex(x+1, 0.3333f, z+1);
						faces.add(new Face(verts.clone(), null, null, "cloud"));
					}
					
					// front (N)
					if (!isCloud(image, x, z-1)) {
						verts[0] = new Vertex(x+1, 0, z);
						verts[1] = new Vertex(x, 0, z);
						verts[2] = new Vertex(x, 0.3333f, z);
						verts[3] = new Vertex(x+1, 0.3333f, z);
						faces.add(new Face(verts.clone(), null, null, "cloud"));
					}

					// back (S)
					if (!isCloud(image, x, z+1)) {
						verts[0] = new Vertex(x, 0, z+1);
						verts[1] = new Vertex(x+1, 0, z+1);
						verts[2] = new Vertex(x+1, 0.3333f, z+1);
						verts[3] = new Vertex(x, 0.3333f, z+1);
						faces.add(new Face(verts.clone(), null, null, "cloud"));
					}
				}
			}
		}

		queue.add(new ChunkOutput(null, faces));
	}
	

	/**
	 * Reads a Minecraft texture pack and converts the clouds texture to an OBJ file.
	 * 
	 * @param destination
	 *            Directory to place the output file.
	 * @param texturePack
	 *            A Minecraft texture pack file. If null, will use minecraft's
	 *            default textures.
	 * @param outputFileName
	 *            The name of the OBJ file to write.
	 * @throws Exception 
	 *             if there is an error.
	 */
	public static void exportClouds(File destination, File texturePack, String outputFileName) throws Exception {
		if (destination == null)
			throw new IllegalArgumentException("destination cannot be null");
		if (outputFileName == null)
			throw new IllegalArgumentException("outputFileName cannot be null");

		if (!destination.exists() || !destination.isDirectory()) {
			if (destination.exists())
				throw new RuntimeException("Cannot create texture directory! File is in the way!");
			if (!destination.mkdir())
				throw new RuntimeException("Cannot create texture directory!");
		}

		File zipfile;
		if (texturePack == null)
			zipfile = Filesystem.getMinecraftJar();
		else
			zipfile = texturePack;
		if (!zipfile.canRead())
			throw new Exception("Cannot open " + zipfile.getName());

		ZipInputStream zis = null;
		PrintWriter writer = null;
		try {
			// Clouds texture will be in one of these locations:
			// MC 1.6 and later: assets/minecraft/textures/environment/clouds.png 
			// MC 1.5 and earlier: environment/clouds.png
			zis = new ZipInputStream(new FileInputStream(zipfile));
	
			ZipEntry entry = null;
			while ((entry = zis.getNextEntry()) != null) {
				if (entry.isDirectory())
					continue;
				if (entry.getName().equals("assets/minecraft/textures/environment/clouds.png"))
					break;
				if (entry.getName().equals("environment/clouds.png"))
					break;
			}
			if (entry == null)
				throw new Exception(zipfile.toString() + " does not contain a clouds texture.");

			BufferedImage image = ImageIO.read(zis);

			Log.info("Exporting clouds to " + outputFileName);
			
			writer = new PrintWriter(new FileWriter(new File(destination, outputFileName)));
			
			ThreadOutputQueue outputQueue = new ThreadOutputQueue();
			WriterRunnable writeRunner = new WriterRunnable(outputQueue, writer, null, null, 1);
			writeRunner.setPrintUseMTL(false);
			writeRunner.setOffset(-image.getWidth()/2, 128f/12f, -image.getHeight()/2);
			writeRunner.setScale(12.0f);
			
			writer.println("g clouds");
			writer.println();
			
			Thread writeThread = new Thread(writeRunner);
			writeThread.start();
			
			renderClouds(image, outputQueue);
			outputQueue.finish();
			writeThread.join();
			
			Log.info("Done.");
		}
		finally {
			if (zis != null)
				zis.close();
			if (writer != null)
				writer.close();
		}
	}

}
