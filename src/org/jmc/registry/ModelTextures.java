package org.jmc.registry;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.google.gson.JsonObject;

import org.jmc.geom.UV;
import org.jmc.util.Log;

/** Texture variants belong to faces, not texture filenames. Bake the result
 * into PNGs because OBJ/MTL cannot represent Minecraft vertex tint or layers. */
public final class ModelTextures {
    private static final Map<String, NamespaceID> variants = new HashMap<>();

    private ModelTextures() {}

    public static synchronized void clear() { variants.clear(); }

    public static synchronized NamespaceID forFace(NamespaceID source, Color biomeTint, boolean tinted) {
        if (source.equals(NamespaceID.UNKNOWN)) return source;
        TextureEntry entry = Registries.getTexture(source);
        Color color = tinted ? (biomeTint == null ? entry.getConfiguredTint() : biomeTint) : null;
        if (color == null && entry.getConfiguredTint() == null) return source;
        String suffix = color == null ? "raw" : String.format("%06x", color.getRGB() & 0xffffff);
        String key = source + "|" + suffix;
        NamespaceID cached = variants.get(key);
        if (cached != null) return cached;
        try {
            BufferedImage input = entry.getUntintedImage();
            BufferedImage output = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
            int tint = color == null ? 0xffffff : color.getRGB();
            for (int y = 0; y < input.getHeight(); y++) {
                for (int x = 0; x < input.getWidth(); x++) {
                    int p = input.getRGB(x, y);
                    int red = ((p >>> 16) & 255) * ((tint >>> 16) & 255) / 255;
                    int green = ((p >>> 8) & 255) * ((tint >>> 8) & 255) / 255;
                    int blue = (p & 255) * (tint & 255) / 255;
                    output.setRGB(x, y, (p & 0xff000000) | (red << 16) | (green << 8) | blue);
                }
            }
            NamespaceID id = new NamespaceID("jmc2obj", "model/" + source.namespace + "/" + source.path + "_" + suffix);
            Registries.registerTexture(entry.derived(id, output));
            variants.put(key, id);
            return id;
        } catch (IOException e) {
            Log.errorOnce("Cannot bake model tint for " + source, e, false);
            return source;
        }
    }

    /** Merge coplanar layers, including their UV flips, into the first layer's
     * UV space. Retaining separate coplanar OBJ faces would introduce z-fighting. */
    public static synchronized NamespaceID overlay(NamespaceID baseId, NamespaceID overlayId, UV[] baseUv, UV[] overlayUv) {
        if (baseUv == null || overlayUv == null) return baseId;
        StringBuilder key = new StringBuilder(baseId + "|" + overlayId);
        for (UV uv : baseUv) key.append('|').append(uv.u).append(',').append(uv.v);
        for (UV uv : overlayUv) key.append('|').append(uv.u).append(',').append(uv.v);
        NamespaceID cached = variants.get(key.toString());
        if (cached != null) return cached;
        double ax = baseUv[1].u - baseUv[0].u, ay = baseUv[1].v - baseUv[0].v;
        double bx = baseUv[3].u - baseUv[0].u, by = baseUv[3].v - baseUv[0].v;
        double det = ax * by - ay * bx;
        if (Math.abs(det) < 1e-12) return baseId;
        try {
            TextureEntry baseEntry = Registries.getTexture(baseId);
            BufferedImage base = baseEntry.getImage();
            BufferedImage top = Registries.getTexture(overlayId).getImage();
            JsonObject baseMeta = baseEntry.getMetadata();
            JsonObject topMeta = Registries.getTexture(overlayId).getMetadata();
            Atlas baseAtlas = new Atlas(base, baseMeta), topAtlas = new Atlas(top, topMeta);
            Atlas driver = topAtlas.count > baseAtlas.count ? topAtlas : baseAtlas;
            JsonObject meta = topAtlas.count > baseAtlas.count ? topMeta : baseMeta;
            int frameWidth = Math.max(baseAtlas.width, topAtlas.width);
            int frameHeight = Math.max(baseAtlas.height, topAtlas.height);
            int width = frameWidth * driver.columns;
            int height = frameHeight * driver.rows;
            BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int frame = (y / frameHeight) * driver.columns + x / frameWidth;
                    double u = ((x % frameWidth) + 0.5) / frameWidth;
                    double v = 1 - ((y % frameHeight) + 0.5) / frameHeight;
                    double du = u - baseUv[0].u, dv = v - baseUv[0].v;
                    double a = (du * by - dv * bx) / det;
                    double b = (ax * dv - ay * du) / det;
                    double tu = overlayUv[0].u + a * (overlayUv[1].u - overlayUv[0].u) + b * (overlayUv[3].u - overlayUv[0].u);
                    double tv = overlayUv[0].v + a * (overlayUv[1].v - overlayUv[0].v) + b * (overlayUv[3].v - overlayUv[0].v);
                    result.setRGB(x, y, over(baseAtlas.sample(u, v, frame), topAtlas.sample(tu, tv, frame)));
                }
            }
            NamespaceID id = new NamespaceID("jmc2obj", "model/layers_" + UUID.nameUUIDFromBytes(key.toString().getBytes(StandardCharsets.UTF_8)).toString().replace("-", ""));
            TextureEntry combined = baseEntry.derived(id, result);
            if (meta != null && meta.has("animation")) {
                meta = meta.deepCopy();
                meta.getAsJsonObject("animation").addProperty("width", frameWidth);
                meta.getAsJsonObject("animation").addProperty("height", frameHeight);
                combined.setMetadata(meta);
            }
            Registries.registerTexture(combined);
            variants.put(key.toString(), id);
            return id;
        } catch (IOException e) {
            Log.errorOnce("Cannot compose model layers " + baseId + " and " + overlayId, e, false);
            return baseId;
        }
    }

    private static final class Atlas {
        final BufferedImage image;
        final int width, height, columns, rows, count;
        Atlas(BufferedImage image, JsonObject meta) {
            this.image = image;
            int w = image.getWidth(), h = image.getHeight();
            if (meta != null && meta.has("animation") && meta.get("animation").isJsonObject()) {
                JsonObject animation = meta.getAsJsonObject("animation");
                if (animation.has("width")) {
                    w = animation.get("width").getAsInt();
                    if (animation.has("height")) h = animation.get("height").getAsInt();
                } else if (animation.has("height")) h = animation.get("height").getAsInt();
                else w = h = Math.min(w, h);
            }
            if (w <= 0 || h <= 0 || image.getWidth() % w != 0 || image.getHeight() % h != 0)
                throw new IllegalArgumentException("Invalid animation frame dimensions");
            width = w; height = h;
            columns = image.getWidth() / w; rows = image.getHeight() / h; count = columns * rows;
        }
        int sample(double u, double v, int frame) {
            frame %= count;
            int x = Math.max(0, Math.min(width - 1, (int)Math.floor(u * width)));
            int y = Math.max(0, Math.min(height - 1, (int)Math.floor((1 - v) * height)));
            return image.getRGB((frame % columns) * width + x, (frame / columns) * height + y);
        }
    }

    private static int over(int base, int top) {
        int a = top >>> 24, b = base >>> 24;
        if (a == 0) return base;
        if (a == 255) return top;
        int alpha = a * 255 + b * (255 - a);
        if (alpha == 0) return 0;
        int result = ((alpha + 127) / 255) << 24;
        for (int shift : new int[] {16, 8, 0}) {
            int channel = (((top >>> shift) & 255) * a * 255 + ((base >>> shift) & 255) * b * (255 - a) + alpha / 2) / alpha;
            result |= channel << shift;
        }
        return result;
    }
}
