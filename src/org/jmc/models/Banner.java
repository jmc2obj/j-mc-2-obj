package org.jmc.models;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import org.jmc.NBT.*;
import org.jmc.OBJInputFile;
import org.jmc.OBJInputFile.OBJGroup;
import org.jmc.Options;
import org.jmc.geom.Transform;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;
import org.jmc.util.Log;

import javax.imageio.ImageIO;

public class Banner extends BlockModel {

    /**
     * Pattern Layer List
     * */
    ArrayList<BannerPattern> PatternList = new ArrayList<BannerPattern>();

    private Set<String> exportedMaterials = new HashSet<String>();
    /**
     * Class for Banner Pattern Layer
     */
    public static class BannerPattern {

        private int color = 0;
        private String pattern = "";

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public String toString() {
            return this.pattern + this.color;
        }
    }

    /**
     * Creates a uniq string for combination of patterns
     * @return
     */
    private String createPatternHash(int baseColorIndex) {
        String hashSource = "" + baseColorIndex + "";
        int count = 0;
        synchronized (PatternList) {
	        for (BannerPattern bp : PatternList) {
	            if (count++ != 0) {
	                hashSource += "-";
	            }
	            hashSource += bp.toString();
	        }
        }
        return hashSource;
    }

    @Override
    public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, byte data, byte biome) {

        //Log.info("Banner ***************************");


        // get banner type from block config
        String bannerType = getConfigNodeValue("bannertype", 0);

        // banner facing
        double rotation = 0;

        // obj correction
        byte rotationData = data;
        rotationData |= rotationData << 2;


        // scale the models down
        double offsetScale = -0.5;

        double offsetX = 0;
        double offsetY = 0;
        double offsetZ = 0;


        int facing = 0;
        switch(bannerType) {
            case "wall":
                facing = rotationData & 3;
                offsetY = -1.51;
                switch (facing)
                {
                    case 0:
                        rotation = 180;
                        offsetX = 0.52;
                        offsetZ = 0;
                        break;
                    case 1:
                        rotation = -90;
                        offsetX = -1;
                        offsetZ = -0.48;
                        break;
                    case 2:
                        rotation = 0;
                        offsetX = 0.486;
                        offsetZ = 1.0;
                        break;
                    case 3:
                        rotation = 90;
                        offsetX = 0;
                        offsetZ = -0.52;
                        break;
                }
                break;
            case "standing":

                // don't know if its correct - but it seams to work
                facing = (Math.abs(data)/2);

                rotation = 90 + facing * 45;

                offsetY = -0.48;

                if (rotation > 180) {
                    rotation = 0 - 180 + (rotation - 180);
                }
        };

        int baseColorIndex = -1;
        synchronized (PatternList) {
	        PatternList.clear();
	        // get banner layer information
	        TAG_Compound tag = chunks.getTileEntity(x, y, z);
	        if (tag != null) {
	            baseColorIndex = ((TAG_Int) tag.getElement("Base")).value;
	
	            // base Color
	            BannerPattern bpBase = new BannerPattern();
	            bpBase.setColor(baseColorIndex);
	            bpBase.setPattern("base");
	            PatternList.add(bpBase);
	
	            TAG_List patternList = (TAG_List) tag.getElement("Patterns");
	
	            if (patternList != null) {
	                for (NBT_Tag pattern : patternList.elements) {
	                    TAG_Compound c_pattern = (TAG_Compound) pattern;
	                    BannerPattern bp = new BannerPattern();
	                    bp.setColor((int) ((TAG_Int) c_pattern.getElement("Color")).value);
	                    bp.setPattern((String) ((TAG_String) c_pattern.getElement("Pattern")).value);
	                    PatternList.add(bp);
	                }
	            }
	        }
        }

        // use material hash (to generate unique material name and images)
        String bannerMaterial = "banner_" + bannerType + "_";
        if (baseColorIndex > -1) {
            bannerMaterial+= createPatternHash(baseColorIndex);
        }



        // add the Banner
        addBanner("conf/models/banner_"+bannerType+".obj", bannerMaterial, obj, x + offsetX, y + offsetY, z + offsetZ, 1 + offsetScale, rotation);

        try {
        	synchronized (exportedMaterials) {
	            boolean alreadyExported = exportedMaterials.contains(bannerMaterial);
	            // already exported material?
	            if (!alreadyExported) {
	                exportedMaterials.add(bannerMaterial);
	                generateBannerImage(bannerMaterial);
	                exportBannerMaterial(bannerMaterial, baseColorIndex);
	            }
			}
        }
        catch (IOException e) {
            Log.error("Cant write Banner Texture...", e, true);
        }
        // append the layout!

    }

    /**
     * Creates the Banner images
     *
     * @param materialImageName
     * @throws IOException
     */
    // TODO: user needs to export the textures first! - someone's might got a better idea for this!
    private void generateBannerImage(String materialImageName) throws IOException {

        // get the base material texture
        BufferedImage backgroundImage = null;
        try {
            backgroundImage = ImageIO.read(new File(Options.outputDir + "/tex/banner_base.png"));
        }
        catch (IOException e) {
            Log.error("Cant read banner_base - did you export Textures first?", e, true);
        }

        if (backgroundImage != null) {

            // todo - do something with the basecolor here...
            // Log.info(" - Base Color: " + baseColorIndex);

            int imageWidth = backgroundImage.getWidth();
            int imageHeight = backgroundImage.getHeight();

            // create a new image (this one is the target!)
            BufferedImage combined = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

            // get Graphics - to draw the layers
            Graphics combinedGraphics = combined.getGraphics();
            combinedGraphics.drawImage(backgroundImage, 0, 0, null);

            // each layer
            for (BannerPattern bp : PatternList) {

                // target of one layer
                BufferedImage patternImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

                // pattern source image
                BufferedImage patternSource = null;
                try {
                    patternSource = ImageIO.read(new File(Options.outputDir + "/tex/banner_pattern_" + bp.getPattern() + ".png"));
                }
                catch (IOException e) {
                    Log.error("Cant read banner_pattern_" + bp.getPattern() + " - did you export Textures first?", e, true);
                }

                // pattern source image
                BufferedImage patternAlpha = null;
                try {
                    patternAlpha = ImageIO.read(new File(Options.outputDir + "/tex/banner_pattern_" + bp.getPattern() + "_a.png"));
                }
                catch (IOException e) {
                    Log.error("Cant read banner_pattern_" + bp.getPattern() + "_a - you need to export Textures with seperate alpha!", e, true);
                }


                // draw into layer..
                Color patternColor = getColorById(bp.getColor());


                for(int x=0; x<imageWidth; x++) {
                    for(int y=0; y<imageHeight; y++) {
                        Color maskColor = new Color(patternSource.getRGB(x, y));
                        Color mainMaskColor = new Color(patternAlpha.getRGB(x, y));


                        int alpha = maskColor.getRed();
                        // mask the mask with the mainmask :) YEAH
                        if (alpha > mainMaskColor.getRed()) {
                            alpha = alpha * (mainMaskColor.getRed()/255);
                        }

                        Color currentColor = new Color(patternColor.getRed(), patternColor.getGreen(), patternColor.getBlue(), alpha);

                        //    Log.info(mainMaskColor.getRed()+", "+mainMaskColor.getRed()+", "+mainMaskColor.getRed()+", "+mainMaskColor.getAlpha());
                        patternImage.setRGB(x, y, currentColor.getRGB());
                    }
                }

                // draw this layer into the main image
                combinedGraphics.drawImage(patternImage, 0, 0, null);

                Log.info(" - Pattern: " + bp.getPattern() + " / " + bp.getColor() + "");
            }

            if (!ImageIO.write(combined, "PNG", new File(Options.outputDir+"/tex", materialImageName+".png"))) {
                throw new RuntimeException("Unexpected error writing image");
            }
        }



    }

    /**
     * Banner Pattern colorId to rgb
     * @param colorId
     * @return
     */
    private Color getColorById(int colorId) {
        Color mappedColor = new Color(0, 0, 0);

        switch(colorId) {
            case 0: mappedColor = new Color(0, 0, 0); break;
            case 1: mappedColor = new Color(153, 51, 51); break;
            case 2: mappedColor = new Color(102, 127, 51); break;
            case 3: mappedColor = new Color(102, 76, 51); break;
            case 4: mappedColor = new Color(51, 76, 178); break;
            case 5: mappedColor = new Color(127, 63, 178); break;
            case 6: mappedColor = new Color(76, 127, 153); break;
            case 7: mappedColor = new Color(153, 153, 153); break;
            case 8: mappedColor = new Color(76, 76, 76); break;
            case 9: mappedColor = new Color(242, 127, 165); break;
            case 10: mappedColor = new Color(127, 204, 25); break;
            case 11: mappedColor = new Color(229, 229, 51); break;
            case 12: mappedColor = new Color(102, 153, 216); break;
            case 13: mappedColor = new Color(178, 76, 216); break;
            case 14: mappedColor = new Color(216, 127, 51); break;
            case 15: mappedColor = new Color(255, 255, 255); break;
        }
        return mappedColor;
    }

    /**
     * Returns a formatet float string of a color int value
     * @param colorValue
     * @return
     */
    private String intChannel2Float(int colorValue) {

        double fColor = (double)colorValue/(double)255;
        String floatColor = String.format("%.4f", fColor);

        return floatColor;
    }

    /**
     * Append the current texture to material file
     * @param materialName
     */
    private void exportBannerMaterial(String materialName, int baseColorIndex) {


        File mtlfile = new File(Options.outputDir, Options.mtlFileName);

        Color baseColor = getColorById(baseColorIndex);

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(mtlfile, true)))) {
            out.println("");
            out.println("");
            out.println("newmtl " + materialName);
            out.println("Kd " + intChannel2Float(baseColor.getRed()) + " " + intChannel2Float(baseColor.getGreen()) + " " + intChannel2Float(baseColor.getBlue()));
            out.println("Ks 0.0000 0.0000 0.0000");
            out.println("map_Kd tex/" + materialName + ".png");
            out.print("map_Kd tex/banner_base_a.png");
        } catch (IOException e) {
            throw new RuntimeException("Unexpected error apending material file");
        }
    }

    /**
     * Add Banner to Outputfile
     *
     * @param objFileName
     * @param material
     * @param obj
     * @param x
     * @param y
     * @param z
     * @param scale
     * @param rotation
     */
    public void addBanner(String objFileName, String material, ChunkProcessor obj, double x, double y, double z, double scale, double rotation) {

        OBJInputFile objFile = new OBJInputFile();
        File objMeshFile = new File(objFileName);

        try {
            objFile.loadFile(objMeshFile, material);
        } catch (IOException e) {
            Log.error("Cant read Objectfile", e, true);
        }

        OBJGroup myObjGroup = objFile.getDefaultObject();
        objFile.overwriteMaterial(myObjGroup, material);
        // Log.info("myObjGroup: "+myObjGroup);

        // translate
        Transform translate = new Transform();
        translate.translate((float) x, (float) y, (float) z);

        // scale
        Transform tScale = new Transform();
        tScale.scale((float) scale, (float) scale, (float) scale);

        translate = translate.multiply(tScale);

        // rotate
        Transform tRotation = new Transform();
        tRotation.rotate(0, (float) rotation, 0);

        translate = translate.multiply(tRotation);

        // do it so!
        objFile.addObjectToOutput(myObjGroup, translate, obj);
    }

}
