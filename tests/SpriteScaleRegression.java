import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import javax.imageio.ImageIO;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jmc.Options;
import org.jmc.TextureExporter;
import org.jmc.registry.NamespaceID;
import org.jmc.registry.Registries;
import org.jmc.registry.TextureEntry;

public class SpriteScaleRegression {
    public static void main(String[] args) throws Exception {
        Path root = Paths.get(args[0]);
        Path pack = root.resolve("pack");
        Path textures = pack.resolve("assets/test/textures/block");
        Files.createDirectories(textures);
        BufferedImage sheet = new BufferedImage(8, 12, BufferedImage.TYPE_INT_ARGB);
        sheet.setRGB(2, 4, 0x7f204080);
        for (String filename : new String[] {"sheet.png", "sheet_n.png", "sheet_s.png"}) {
            ImageIO.write(sheet, "png", textures.resolve(filename).toFile());
        }
        String metadata = "{\"animation\":{\"width\":4,\"height\":6,\"frametime\":3,"
                + "\"frames\":[0,{\"index\":2,\"time\":5}],\"interpolate\":true}}";
        Files.write(textures.resolve("sheet.png.mcmeta"), metadata.getBytes(StandardCharsets.UTF_8));

        Options.resourcePacks.add(pack.toFile());
        Options.outputDir = root.resolve("export").toFile();
        Options.textureScale = 2;
        Options.textureDiffuse = false;
        Options.textureAlpha = true;
        Options.textureNormal = true;
        Options.textureSpecular = true;
        Options.textureOverwrite = true;
        TextureEntry entry = Registries.getTexture(NamespaceID.fromString("test:block/sheet"));
        BufferedImage source = entry.getImage();
        TextureExporter.exportTextures(Collections.singleton(entry), null);

        if (source.getWidth() != 8 || source.getHeight() != 12 || source.getRGB(2, 4) != 0x7f204080) {
            throw new AssertionError("Source spritesheet cropped or mutated");
        }
        File color = new File(Options.outputDir, entry.getExportFilePath());
        for (String path : new String[] {entry.getExportFilePath(), entry.getExportFilePathNormal(),
                entry.getExportFilePathSpecular()}) {
            BufferedImage scaled = ImageIO.read(new File(Options.outputDir, path));
            if (scaled.getWidth() != 16 || scaled.getHeight() != 24) {
                throw new AssertionError("Scaled spritesheet dimensions: " + path);
            }
        }
        if (ImageIO.read(color).getRGB(4, 8) != 0x7f204080
                || new File(color.getParentFile(), "sheet_a.png").exists()) {
            throw new AssertionError("The legacy alpha option must preserve alpha in the color PNG");
        }
        try (Reader reader = Files.newBufferedReader(Paths.get(color + ".mcmeta"), StandardCharsets.UTF_8)) {
            JsonObject expected = JsonParser.parseString(metadata).getAsJsonObject();
            expected.getAsJsonObject("animation").addProperty("width", 8);
            expected.getAsJsonObject("animation").addProperty("height", 12);
            if (!expected.equals(JsonParser.parseReader(reader))) {
                throw new AssertionError("Incorrect scaled animation metadata");
            }
        }
        System.out.println("PASS non-square spritesheet, shared alpha, normal/specular sheets and scaled frame metadata");
    }
}
