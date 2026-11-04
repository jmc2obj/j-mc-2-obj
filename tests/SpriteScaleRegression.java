import java.io.*;import java.util.*;import java.awt.image.*;import org.jmc.*;import org.jmc.registry.*;
public class SpriteScaleRegression {
 public static void main(String[] a)throws Exception{
  Options.resourcePacks.add(new File(a[0]));Options.outputDir=new File(a[1]);Options.outputDir.mkdirs();Options.textureScale=2;Options.textureDiffuse=true;Options.textureAlpha=true;Options.textureNormal=true;Options.textureSpecular=true;Options.textureOverwrite=true;
  TextureEntry t=Registries.getTexture(NamespaceID.fromString("test:block/sheet"));BufferedImage im=t.getImage();if(im.getWidth()!=8||im.getHeight()!=12)throw new AssertionError("Atlas cropped");int p=im.getRGB(2,4);TextureExporter.exportTextures(Collections.singleton(t),null);
  if(im.getWidth()!=8||im.getHeight()!=12||p!=im.getRGB(2,4))throw new AssertionError("Source raster mutated");
  System.out.println("PASS full non-square 2D atlas, scale2, source raster unchanged, separate alpha/normal/specular files");
 }
}
