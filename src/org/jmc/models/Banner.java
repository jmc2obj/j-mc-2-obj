package org.jmc.models;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.jmc.BlockData;
import org.jmc.OBJInputFile;
import org.jmc.OBJInputFile.OBJGroup;
import org.jmc.NBT.NBT_Tag;
import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_Int;
import org.jmc.NBT.TAG_List;
import org.jmc.NBT.TAG_String;
import org.jmc.geom.Direction;
import org.jmc.geom.Transform;
import org.jmc.registry.NamespaceID;
import org.jmc.registry.Registries;
import org.jmc.registry.TextureEntry;
import org.jmc.threading.ObjChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;
import org.jmc.util.Filesystem.JmcConfFile;
import org.jmc.util.Log;

public class Banner extends BlockModel {

    /**
     * Pattern Layer List
     * */

    private static Set<NamespaceID> exportedMaterials = new HashSet<>();

	private static boolean firstReadError = true;
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
    public void addModel(ObjChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, NamespaceID biome) {

        //Log.info("Banner ***************************");


        // get banner type from block config
        String bannerType = getConfigNodeValue("bannertype", 0);
        if (bannerType == null)
            bannerType = "standing";

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
                switch (data.state.getDirection("facing", Direction.NORTH)) {
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
            	
                int dataRot = Integer.parseInt(data.state.get("rotation"));

                rotation = 90 + (360.0f / 16.0f) * dataRot;

                offsetY = -0.48;

                if (rotation > 180) {
                    rotation = 0 - 180 + (rotation - 180);
                }
        };

        int baseColorIndex = -1;
        ArrayList<BannerPattern> patternList = new ArrayList<BannerPattern>();
        
    	String bid = data.id.path;
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
        String bannerTexName = "banner_";
        if (baseColorIndex > -1) {
            bannerTexName+= createPatternHash(baseColorIndex, patternList);
        }
        NamespaceID bannerTexId = new NamespaceID("jmc2obj", "banner/" + bannerTexName);



        // add the Banner
        addBanner(bannerType, bannerTexId, obj, x + offsetX, y + offsetY, z + offsetZ, 1 + offsetScale, rotation);

    	synchronized (exportedMaterials) {
            // already exported material?
            if (!exportedMaterials.contains(bannerTexId)) {
                if (generateBannerImage(bannerTexId, patternList)) {
                	exportedMaterials.add(bannerTexId);
                }
            }
		}
        // append the layout!

    }

    /**
     * Creates the Banner images
     *
     * @param materialImageName
     * @throws IOException
     */
    private boolean generateBannerImage(NamespaceID materialImageName, ArrayList<BannerPattern> patternList) {

        // get the base material texture
        BufferedImage backgroundImage;
        try {
        	backgroundImage = Registries.getTexture(NamespaceID.fromString("entity/banner_base")).getImage();
        } catch (IOException e) {
            Log.error("Cant read banner base image!", e, showReadErrorPopup());
            return false;
        }
        // todo - do something with the basecolor here...
        // Log.info(" - Base Color: " + baseColorIndex);

        // create a new image (this one is the target!)
        BufferedImage combined = new BufferedImage(backgroundImage.getWidth(), backgroundImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // get Graphics - to draw the layers
        combined.getGraphics().drawImage(backgroundImage, 0, 0, null);
        
        // each layer
        for (BannerPattern bp : patternList) {

            // pattern source image
            BufferedImage patternSource;
            try {
            	TextureEntry te = Registries.getTexture(new NamespaceID("jmc2obj", "banner/pattern_" + bp.getPattern()));
            	patternSource = te.getImage();
            } catch (IOException e) {
            	Log.error("Cant read banner pattern " + bp.getPattern(), e, showReadErrorPopup());
                return false;
            }
            
            // target of one layer
            BufferedImage patternImage = new BufferedImage(patternSource.getWidth(), patternSource.getHeight(), BufferedImage.TYPE_INT_ARGB);

            // draw into layer..
            Color layerColor = getColorById(bp.getColor());
            float layerComps[] = layerColor.getComponents(null);
            
            for(int x=0; x<patternSource.getWidth(); x++) {
                for(int y=0; y<patternSource.getHeight(); y++) {
                    Color patternColor = new Color(patternSource.getRGB(x, y), true);
                    float patternComps[] = patternColor.getComponents(null);
                    
                    Color currentColor = new Color(layerComps[0] * patternComps[0], layerComps[1] * patternComps[1], layerComps[2] * patternComps[2], patternComps[3]);
                    
                    //Log.debug(mainMaskColor.getRed()+", "+mainMaskColor.getRed()+", "+mainMaskColor.getRed()+", "+mainMaskColor.getAlpha());
                    patternImage.setRGB(x, y, currentColor.getRGB());
                }
            }
            
            if (patternImage.getWidth() > combined.getWidth()) {
            	AffineTransformOp scaleOp = new AffineTransformOp(AffineTransform.getScaleInstance((float)patternImage.getWidth()/combined.getWidth(), 1),
            			AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            	combined = scaleOp.filter(combined, null);
            }
            if (patternImage.getHeight() > combined.getHeight()) {
            	AffineTransformOp scaleOp = new AffineTransformOp(AffineTransform.getScaleInstance(1, (float)patternImage.getHeight()/combined.getHeight()),
            			AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            	combined = scaleOp.filter(combined, null);
            }
            if (patternImage.getHeight() != combined.getHeight() || patternImage.getWidth() != combined.getWidth()) {
            	AffineTransformOp scaleOp = new AffineTransformOp(
            			AffineTransform.getScaleInstance((float)combined.getWidth()/patternImage.getWidth(), (float)combined.getHeight()/patternImage.getHeight()),
            			AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            	patternImage = scaleOp.filter(patternImage, null);
            }
            
            // draw this layer into the main image
            combined.getGraphics().drawImage(patternImage, 0, 0, null);
            
            Log.debug(" - Pattern: " + bp.getPattern() + " / " + bp.getColor() + "");
        }
        
        TextureEntry texEntry = Registries.getTexture(materialImageName);
        texEntry.setImage(combined);
        return true;
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
     * Add Banner to Outputfile
     */
    public void addBanner(String bannerType, NamespaceID material, ObjChunkProcessor obj, double x, double y, double z, double scale, double rotation) {
    	String objFileName = "conf/models/banner_"+bannerType+".obj";
    	
        OBJInputFile objFile = new OBJInputFile();

        try (JmcConfFile objFileStream = new JmcConfFile(objFileName)) {
            objFile.loadFile(objFileStream, material.getExportSafeString());
        } catch (IOException e) {
            Log.error("Can't read banner obj file!", e, true);
        }

        OBJGroup myObjGroup = objFile.getDefaultObject();
        myObjGroup = objFile.overwriteMaterial(myObjGroup, material);
        // Log.info("myObjGroup: "+myObjGroup);

        // translate
        Transform translate = Transform.translation((float) x, (float) y, (float) z);

        // scale
        Transform tScale = Transform.scale((float) scale, (float) scale, (float) scale);

        translate = translate.multiply(tScale);

        // rotate
        Transform tRotation = Transform.rotation(0, rotation, 0);

        translate = translate.multiply(tRotation);

        // do it so!
        objFile.addObjectToOutput(myObjGroup, translate, obj, false);
    }
    
    private static synchronized boolean showReadErrorPopup() {
		boolean wasFirst = firstReadError;
		firstReadError = false;
		return wasFirst;
	}
    
    public static synchronized void resetReadError(){
    	firstReadError = true;
    }
    
    public static void clearExported() {
    	synchronized (exportedMaterials) {
			exportedMaterials.clear();
		}
    }

}
