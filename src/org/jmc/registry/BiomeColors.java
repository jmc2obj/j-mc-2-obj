package org.jmc.registry;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jmc.BlockData;
import org.jmc.Options;
import org.jmc.geom.BlockPos;
import org.jmc.threading.ThreadChunkDeligate;
import org.jmc.util.ResourcePackIO;

/** Read the selected game's biome definitions and resource-pack colormaps. */
public final class BiomeColors {
    private static final Map<NamespaceID, JsonObject> definitions = new HashMap<>();
    private static final Map<String, BufferedImage> maps = new HashMap<>();
    private static final int[] swampPermutation = new int[256];
    private static final int[][] gradients = {{1,1},{-1,1},{1,-1},{-1,-1},{1,0},{-1,0},{1,0},{-1,0},{0,1},{0,-1},{0,1},{0,-1}};
    static {
        Random random = new Random(2345L);
        random.nextDouble(); random.nextDouble(); random.nextDouble();
        for (int i = 0; i < 256; i++) swampPermutation[i] = i;
        for (int i = 0; i < 256; i++) {
            int j = i + random.nextInt(256 - i);
            int value = swampPermutation[i]; swampPermutation[i] = swampPermutation[j]; swampPermutation[j] = value;
        }
    }

    private BiomeColors() {}
    public static synchronized void clear() { definitions.clear(); maps.clear(); }

    public static Color forBlock(BlockData block, BlockPos pos, ThreadChunkDeligate chunks, NamespaceID biome) {
        String name = block.id.path;
        if (name.equals("birch_leaves")) return new Color(0x80a755);
        if (name.equals("spruce_leaves")) return new Color(0x619961);
        if (name.equals("mangrove_leaves")) return new Color(0x92c648);
        if (name.equals("lily_pad")) return new Color(0x208030);
        boolean foliage = name.endsWith("_leaves") || name.equals("vine");
        boolean dryFoliage = name.equals("leaf_litter");
        boolean grass = name.equals("grass") || name.equals("short_grass") || name.equals("tall_grass") || name.equals("fern") || name.equals("large_fern") || name.equals("grass_block") || name.equals("sugar_cane") || name.equals("potted_fern") || name.equals("bush") || name.equals("pink_petals") || name.equals("wildflowers");
        if (!grass && !foliage && !dryFoliage) return null;
        String palette = dryFoliage ? "dry_foliage" : foliage ? "foliage" : "grass";
        long red = 0, green = 0, blue = 0;
        int radius = Options.renderBiomes ? 2 : 0;
        int count = 0;
        for (int dz = -radius; dz <= radius; dz++) {
            for (int dx = -radius; dx <= radius; dx++) {
                NamespaceID sample = Options.renderBiomes ? biome : NamespaceID.fromString("plains");
                if (chunks != null && Options.renderBiomes) {
                    NamespaceID neighbor = chunks.getBlockBiome(pos.x + dx, pos.y, pos.z + dz);
                    if (neighbor != null && !neighbor.equals(NamespaceID.NULL) && !neighbor.equals(NamespaceID.UNKNOWN)) sample = neighbor;
                }
                int color = color(sample, palette, pos.x + dx, pos.z + dz);
                red += (color >> 16) & 255; green += (color >> 8) & 255; blue += color & 255; count++;
            }
        }
        return new Color((int)(red/count), (int)(green/count), (int)(blue/count));
    }

    public static int color(NamespaceID biome, boolean foliage, int x, int z) {
        return color(biome, foliage ? "foliage" : "grass", x, z);
    }

    public static int color(NamespaceID biome, String palette, int x, int z) {
        JsonObject definition = definition(biome);
        JsonObject effects = definition.has("effects") ? definition.getAsJsonObject("effects") : new JsonObject();
        String property = palette + "_color";
        int color;
        if (effects.has(property)) {
            JsonElement value = effects.get(property);
            String text = value.getAsString();
            color = text.startsWith("#") ? Integer.parseInt(text.substring(1), 16) : value.getAsInt();
        } else {
            // Biome climate fields are floats in Minecraft, promoted to doubles
            // only after clamping. Preserve that precision at colormap edges.
            double temperature = definition.has("temperature") ? definition.get("temperature").getAsFloat() : 0.8f;
            double rainfall = definition.has("downfall") ? definition.get("downfall").getAsFloat() : 0.4f;
            temperature = Math.max(0, Math.min(1, temperature));
            rainfall = Math.max(0, Math.min(1, rainfall)) * temperature;
            BufferedImage colormap = colormap(palette);
            if (colormap == null) return palette.equals("dry_foliage") ? 0x5c3c32 : palette.equals("foliage") ? 0x48b518 : 0x91bd59;
            int px = Math.min(colormap.getWidth()-1, (int)((1-temperature)*255) * colormap.getWidth()/256);
            int py = Math.min(colormap.getHeight()-1, (int)((1-rainfall)*255) * colormap.getHeight()/256);
            color = colormap.getRGB(px, py) & 0xffffff;
        }
        if (palette.equals("grass") && effects.has("grass_color_modifier")) {
            String modifier = effects.get("grass_color_modifier").getAsString();
            if (modifier.equals("dark_forest")) color = ((color & 0xfefefe) + 0x28340a) >> 1;
            if (modifier.equals("swamp")) color = swampNoise(x*0.0225, z*0.0225) < -0.1 ? 0x4c763c : 0x6a7039;
        }
        return color;
    }

    private static synchronized JsonObject definition(NamespaceID biome) {
        if (biome == null || biome.equals(NamespaceID.NULL) || biome.equals(NamespaceID.UNKNOWN)) biome = NamespaceID.fromString("plains");
        JsonObject result = definitions.get(biome);
        if (result != null) return result;
        try (Reader reader = ResourcePackIO.loadText("data/" + biome.namespace + "/worldgen/biome/" + biome.path + ".json")) {
            result = new Gson().fromJson(reader, JsonObject.class);
        } catch (IOException e) { result = new JsonObject(); }
        definitions.put(biome, result);
        return result;
    }

    private static synchronized BufferedImage colormap(String name) {
        if (maps.containsKey(name)) return maps.get(name);
        BufferedImage image = null;
        try { image = ResourcePackIO.loadImage("assets/minecraft/textures/colormap/" + name + ".png"); }
        catch (IOException e) { /* A texture-only pack can omit vanilla colormaps. */ }
        maps.put(name, image);
        return image;
    }

    private static int permutation(int index) { return swampPermutation[index & 255]; }
    private static double corner(int index, double x, double y) {
        double t = 0.5 - x*x - y*y;
        if (t < 0) return 0;
        t *= t;
        int[] g = gradients[index % 12];
        return t*t*(g[0]*x + g[1]*y);
    }
    private static double swampNoise(double x, double y) {
        double skew = (Math.sqrt(3)-1)/2, unskew = (3-Math.sqrt(3))/6;
        double s = (x+y)*skew;
        int i = (int)Math.floor(x+s), j = (int)Math.floor(y+s);
        double t = (i+j)*unskew, x0 = x-(i-t), y0 = y-(j-t);
        int ix = x0 > y0 ? 1 : 0, iy = 1-ix;
        double x1 = x0-ix+unskew, y1 = y0-iy+unskew;
        double x2 = x0-1+2*unskew, y2 = y0-1+2*unskew;
        return 70*(corner(permutation(i+permutation(j)),x0,y0)
            +corner(permutation(i+ix+permutation(j+iy)),x1,y1)
            +corner(permutation(i+1+permutation(j+1)),x2,y2));
    }
}
