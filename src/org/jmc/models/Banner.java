package org.jmc.models;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import org.jmc.BlockData;
import org.jmc.Materials;
import org.jmc.OBJInputFile;
import org.jmc.OBJInputFile.OBJGroup;
import org.jmc.Options;
import org.jmc.NBT.NBT_Tag;
import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_Int;
import org.jmc.NBT.TAG_List;
import org.jmc.NBT.TAG_String;
import org.jmc.geom.Direction;
import org.jmc.geom.Transform;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;
import org.jmc.util.Log;

public class Banner extends BlockModel {

    /**
     * Pattern Layer List
     * */

    private static Set<String> exportedMaterials = new HashSet<String>();

	private static boolean firstBaseReadError = true;
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
    private String createPatternHash(int baseColorIndex, ArrayList<BannerPattern> patternList) {
        String hashSource = "";// + baseColorIndex + "";
        int count = 0;
        for (BannerPattern bp : patternList) {
            if (count++ != 0) {
                hashSource += "-";
            }
            hashSource += bp.toString();
        }
        return hashSource;
    }

    @Override
    public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome) {

        //Log.info("Banner ***************************");


        // get banner type from block config
        String bannerType = getConfigNodeValue("bannertype", 0);

        // banner facing
        double rotation = 0;

        // scale the models down
        double offsetScale = -0.5;

        double offsetX = 0;
        double offsetY = 0;
        double offsetZ = 0;

        switch(bannerType) {
            case "wall":
                offsetY = -1.51;
                switch (data.getDirection("facing", Direction.NORTH)) {
                default:
				case NORTH:
                    rotation = -90;
                    offsetX = 0;
                    offsetZ = 0.514;
					break;
				case EAST:
                    rotation = 0;
                    offsetX = -0.514;
                    offsetZ = 0.0;
					break;
				case SOUTH:
                    rotation = 90;
                    offsetX = 0;
                    offsetZ = -0.52;
					break;
				case WEST:
                    rotation = 180;
                    offsetX = 0.52;
                    offsetZ = 0;
					break;
				}
                break;
            case "standing":
            	
                int dataRot = Integer.parseInt(data.get("rotation"));

                rotation = 90 + (360.0f / 16.0f) * dataRot;

                offsetY = -0.48;

                if (rotation > 180) {
                    rotation = 0 - 180 + (rotation - 180);
                }
        };

        int baseColorIndex = -1;
        ArrayList<BannerPattern> patternList = new ArrayList<BannerPattern>();
        
    	String bid = chunks.getBlockID(x, y, z);
    	bid = bid.split(":", 2)[1];
    	if (bid.startsWith("white"))
    		baseColorIndex = 0;
    	else if (bid.startsWith("orange"))
    		baseColorIndex = 1;
    	else if (bid.startsWith("magenta"))
    		baseColorIndex = 2;
    	else if (bid.startsWith("light_blue"))
    		baseColorIndex = 3;
    	else if (bid.startsWith("yellow"))
    		baseColorIndex = 4;
    	else if (bid.startsWith("lime"))
    		baseColorIndex = 5;
    	else if (bid.startsWith("pink"))
    		baseColorIndex = 6;
    	else if (bid.startsWith("gray"))
    		baseColorIndex = 7;
    	else if (bid.startsWith("light_gray"))
    		baseColorIndex = 8;
    	else if (bid.startsWith("cyan"))
    		baseColorIndex = 9;
    	else if (bid.startsWith("purple"))
    		baseColorIndex = 10;
    	else if (bid.startsWith("blue"))
    		baseColorIndex = 11;
    	else if (bid.startsWith("brown"))
    		baseColorIndex = 12;
    	else if (bid.startsWith("green"))
    		baseColorIndex = 13;
    	else if (bid.startsWith("red"))
    		baseColorIndex = 14;
    	else if (bid.startsWith("black"))
    		baseColorIndex = 15;

        // base Color
        BannerPattern bpBase = new BannerPattern();
        bpBase.setColor(baseColorIndex);
        bpBase.setPattern("base");
        patternList.add(bpBase);

        // get banner layer information
        TAG_Compound tag = chunks.getTileEntity(x, y, z);
	    if (tag != null) {
            TAG_List patternsTag = (TAG_List) tag.getElement("Patterns");

            if (patternsTag != null) {
                for (NBT_Tag pattern : patternsTag.elements) {
                    TAG_Compound c_pattern = (TAG_Compound) pattern;
                    BannerPattern bp = new BannerPattern();
                    bp.setColor((int) ((TAG_Int) c_pattern.getElement("Color")).value);
                    bp.setPattern((String) ((TAG_String) c_pattern.getElement("Pattern")).value);
                    patternList.add(bp);
                }
            }
        }

        // use material hash (to generate unique material name and images)
        String bannerMaterial = "banner_";
        if (baseColorIndex > -1) {
            bannerMaterial+= createPatternHash(baseColorIndex, patternList);
        }



        // add the Banner
        addBanner(bannerType, bannerMaterial, obj, x + offsetX, y + offsetY, z + offsetZ, 1 + offsetScale, rotation);

        try {
        	synchronized (exportedMaterials) {
	            // already exported material?
	            if (exportedMaterials.add(bannerMaterial)) {
	                generateBannerImage(bannerMaterial, patternList);
	                addBannerMaterial(bannerMaterial, baseColorIndex);
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
    private void generateBannerImage(String materialImageName, ArrayList<BannerPattern> patternList) throws IOException {

        // get the base material texture
        BufferedImage backgroundImage = null;
        try {
            backgroundImage = ImageIO.read(new File(Options.outputDir + "/tex/banner_base.png"));
        }
        catch (IOException e) {
        	synchronized (Banner.class) {
                Log.error("Cant read banner_base - did you export Textures first?", e, firstBaseReadError);
                firstBaseReadError = false;
			}
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
            for (BannerPattern bp : patternList) {

                // target of one layer
                BufferedImage patternImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

                // pattern source image
                BufferedImage patternSource = null;
                try {
                    patternSource = ImageIO.read(new File(Options.outputDir + "/tex/banner_pattern_" + bp.getPattern() + ".png"));
                }
                catch (IOException e) {
                    Log.error("Cant read banner_pattern_" + bp.getPattern() + " - did you export Textures first?", e, true);
                    return;
                }

                // pattern source image
                BufferedImage patternAlpha = null;
                try {
                    patternAlpha = ImageIO.read(new File(Options.outputDir + "/tex/banner_pattern_" + bp.getPattern() + "_a.png"));
                }
                catch (IOException e) {
                    Log.error("Cant read banner_pattern_" + bp.getPattern() + "_a - you need to export Textures with seperate alpha!", e, true);
                    return;
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
                        
                        //Log.debug(mainMaskColor.getRed()+", "+mainMaskColor.getRed()+", "+mainMaskColor.getRed()+", "+mainMaskColor.getAlpha());
                        patternImage.setRGB(x, y, currentColor.getRGB());
                    }
                }
                
                // draw this layer into the main image
                combinedGraphics.drawImage(patternImage, 0, 0, null);
                
                Log.debug(" - Pattern: " + bp.getPattern() + " / " + bp.getColor() + "");
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
			case 0: mappedColor = new Color(255, 255, 255); break;//White
			case 1: mappedColor = new Color(216, 127, 51); break;//Orange
			case 2: mappedColor = new Color(178, 76, 216); break;//Magenta
			case 3: mappedColor = new Color(80, 153, 216); break;//Light Blue
			case 4: mappedColor = new Color(229, 200, 51); break;//Yellow
			case 5: mappedColor = new Color(120, 190, 23); break;//Lime
			case 6: mappedColor = new Color(242, 127, 165); break;//Pink
			case 7: mappedColor = new Color(76, 76, 76); break;//Grey
			case 8: mappedColor = new Color(153, 153, 153); break;//Light Grey
			case 9: mappedColor = new Color(76, 127, 153); break;//Cyan
			case 10: mappedColor = new Color(127, 63, 178); break;//Purple
			case 11: mappedColor = new Color(51, 76, 178); break;//Blue
			case 12: mappedColor = new Color(102, 76, 51); break;//Brown
			case 13: mappedColor = new Color(102, 127, 51); break;//Green
			case 14: mappedColor = new Color(153, 51, 51); break;//Red
			case 15: mappedColor = new Color(0, 0, 0); break;//Blacke
        }
        return mappedColor;
    }

    /**
     * Append the current texture to material file
     * @param materialName
     */
    private void addBannerMaterial(String materialName, int baseColorIndex) {
    	
        Color baseColor = getColorById(baseColorIndex);
        
        Materials.addMaterial(materialName, baseColor, null, "tex/" + materialName + ".png", null);
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
    public void addBanner(String bannerType, String material, ChunkProcessor obj, double x, double y, double z, double scale, double rotation) {
    	String objFileName = "conf/models/banner_"+bannerType+".obj";
    	
        OBJInputFile objFile = new OBJInputFile();
        File objMeshFile = new File(objFileName);

        try {
            objFile.loadFile(objMeshFile, material);
        } catch (IOException e) {
            Log.error("Can't read banner obj file!", e, true);
        }

        OBJGroup myObjGroup = objFile.getDefaultObject();
        myObjGroup = objFile.overwriteMaterial(myObjGroup, material);
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
        tRotation.rotate(0, rotation, 0);

        translate = translate.multiply(tRotation);

        // do it so!
        objFile.addObjectToOutput(myObjGroup, translate, obj);
    }
    
    public static synchronized void resetReadError(){
    	firstBaseReadError = true;
    }

}
