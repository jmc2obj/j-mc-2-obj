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
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;
import org.jmc.util.Filesystem.JmcConfFile;
import org.jmc.util.Log;

public class Banner extends BlockModel {

    /**
     * Pattern Layer List
     * */

    private static final Set<NamespaceID> exportedMaterials = new HashSet<>();

	private static boolean firstReadError = true;
    /**
     * Class for Banner Pattern Layer
     */
    public static class BannerPattern {

        private String color = "white";
        private NamespaceID pattern = NamespaceID.fromString("base");

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public NamespaceID getPattern() {
            return pattern;
        }

        public void setPattern(NamespaceID pattern) {
            this.pattern = pattern;
        }

        public String toString() {
            return this.pattern.path + "_" + this.color;
        }
    }

    /**
     * Creates a uniq string for combination of patterns
     * @return
     */
    private String createPatternHash(ArrayList<BannerPattern> patternList) {
        StringBuilder hashSource = new StringBuilder();
        int count = 0;
        for (BannerPattern bp : patternList) {
            if (count++ != 0) {
                hashSource.append("-");
            }
            hashSource.append(bp.toString());
        }
        return hashSource.toString();
    }

    @Override
    public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, NamespaceID biome) {

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

        ArrayList<BannerPattern> patternList = new ArrayList<BannerPattern>();
		
		String baseColor;
		String bid = data.id.path;
		if (bid.endsWith("_standing_banner")) {
			baseColor = bid.replace("_standing_banner", "");
		} else if (bid.endsWith("_wall_banner")) {
			baseColor = bid.replace("_wall_banner", "");
		} else if (bid.endsWith("_banner")) {
			baseColor = bid.replace("_banner", "");
		} else {
			Log.debugOnce("Unable to get base banner color from id! " + bid);
			baseColor = "white";
		}
		
		// base Color
        BannerPattern bpBase = new BannerPattern();
        bpBase.setColor(baseColor);
        bpBase.setPattern(NamespaceID.fromString("base"));
        patternList.add(bpBase);

        // get banner layer information
        TAG_Compound tag = chunks.getTileEntity(x, y, z);
		if (tag != null) {
			TAG_List patternsTag = (TAG_List) tag.getElement("patterns");
			if (patternsTag == null) {
				patternsTag = (TAG_List) tag.getElement("Patterns");
			}
			if (patternsTag != null) {
				for (NBT_Tag pattern : patternsTag.elements) {
					TAG_Compound c_pattern = (TAG_Compound) pattern;
					BannerPattern bp = new BannerPattern();
					
					NBT_Tag colorTag = c_pattern.getElement("color");
					if (colorTag != null) {
						bp.setColor(((TAG_String) colorTag).value);
					} else {
						int colorId = ((TAG_Int) c_pattern.getElement("Color")).value;
						bp.setColor(colorIdToName(colorId));
					}
					
					TAG_String patternTag = (TAG_String) c_pattern.getElement("pattern");
					if (patternTag != null) {
						bp.setPattern(NamespaceID.fromString(patternTag.value));
					} else {
						patternTag = (TAG_String) c_pattern.getElement("Pattern");
						// put old name patterns under jmc to be picked up later
						bp.setPattern(new NamespaceID("jmc2obj", patternTag.value));
					}
					
					patternList.add(bp);
				}
			}
		}

        // use material hash (to generate unique material name and images)
        String bannerTexName = "banner_" + createPatternHash(patternList);
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
		if (backgroundImage == null) return false;

        // create a new image (this one is the target!)
        BufferedImage combined = new BufferedImage(backgroundImage.getWidth(), backgroundImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // get Graphics - to draw the layers
        combined.getGraphics().drawImage(backgroundImage, 0, 0, null);
        
        // each layer
        for (BannerPattern bp : patternList) {

            // pattern source image
            BufferedImage patternSource;
            try {
				TextureEntry te;
				NamespaceID patternName = bp.getPattern();
				if (patternName.namespace.equals("jmc2obj")) {
					// old style pattern names get found by textures.json source id override
					te = Registries.getTexture(new NamespaceID("jmc2obj", "banner/pattern_" + patternName.path));
				} else {
					te = Registries.getTexture(new NamespaceID("minecraft", "entity/banner/" + patternName.path));
				}
				if (te == null) return false;
            	patternSource = te.getImage();
            } catch (IOException e) {
            	Log.error("Cant read banner pattern " + bp.getPattern(), e, showReadErrorPopup());
                return false;
            }
            
            // target of one layer
            BufferedImage patternImage = new BufferedImage(patternSource.getWidth(), patternSource.getHeight(), BufferedImage.TYPE_INT_ARGB);

            // draw into layer..
            Color layerColor = getColorByName(bp.getColor());
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
		if (texEntry == null) return false;
        texEntry.setImage(combined);
        return true;
    }

    /**
     * Banner Pattern colorName to rgb
     * @param colorName
     * @return the Color for the color name
     */
    private Color getColorByName(String colorName) {
        switch(colorName) {
			case "white": return new Color(255, 255, 255);
			case "orange": return new Color(216, 127, 51);
			case "magenta": return new Color(178, 76, 216);
			case "light_blue": return new Color(80, 153, 216);
			case "yellow": return new Color(229, 200, 51);
			case "lime": return new Color(120, 190, 23);
			case "pink": return new Color(242, 127, 165);
			case "gray": return new Color(76, 76, 76);
			case "light_gray": return new Color(153, 153, 153);
			case "cyan": return new Color(76, 127, 153);
			case "purple": return new Color(127, 63, 178);
			case "blue": return new Color(51, 76, 178);
			case "brown": return new Color(102, 76, 51);
			case "green": return new Color(102, 127, 51);
			case "red": return new Color(153, 51, 51);
			case "black": return new Color(0, 0, 0);
			default:
				Log.errorOnce("Unknown banner color " + colorName, null, false);
				return new Color(255, 0, 0);
		}
	}
	
	/**
	 * Banner Pattern colorId to name
	 * @param colorId
	 * @return name of the color id
	 */
	private String colorIdToName(int colorId) {
		switch(colorId) {
			case 0: return "white";
			case 1: return "orange";
			case 2: return "magenta";
			case 3: return "light_blue";
			case 4: return "yellow";
			case 5: return "lime";
			case 6: return "pink";
			case 7: return "gray";
			case 8: return "light_gray";
			case 9: return "cyan";
			case 10: return "purple";
			case 11: return "blue";
			case 12: return "brown";
			case 13: return "green";
			case 14: return "red";
			case 15: return "black";
			default:
				Log.errorOnce("Unknown banner colorID " + colorId, null, false);
				return "black";
		}
	}

    /**
     * Add Banner to Outputfile
     */
    public void addBanner(String bannerType, NamespaceID material, ChunkProcessor obj, double x, double y, double z, double scale, double rotation) {
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
