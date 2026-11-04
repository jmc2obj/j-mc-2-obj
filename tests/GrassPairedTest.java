import java.io.*;import java.util.*;import java.awt.Color;
import org.jmc.*;import org.jmc.geom.*;import org.jmc.models.Registry;import org.jmc.registry.*;import org.jmc.threading.*;
public class GrassPairedTest {
 static String texture(BlockData data,int x,int y){Registry renderer=new Registry();renderer.setBlockId(data.id);ChunkProcessor mesh=new ChunkProcessor();renderer.addModel(mesh,new GrassRegression.World(),x,y,0,data,NamespaceID.fromString("plains"));return Registries.getTexture(mesh.getAllFaces().get(0).texture).getFilePath();}
 public static void main(String[] a)throws Exception{
  Options.uiMode=Options.UIMode.CONSOLE;Options.renderBiomes=true;Options.randBlockVariations=true;
  for(String n:new String[]{"pack2.zip","pack1.zip","pack0.zip","pack3.zip","minecraft.jar"})Options.resourcePacks.add(new File(a[0],n));
  Registries.initialize();
  for(int x=-32;x<32;x++){
   BlockData lower=new BlockData(NamespaceID.fromString("large_fern"));lower.state.put("half","lower");BlockData upper=new BlockData(lower.id);upper.state.put("half","upper");
   String l=texture(lower,x,64).replace("bottom","top"),u=texture(upper,x,65);if(!l.equals(u))throw new AssertionError(l+" != "+u);
  }
  ThreadChunkDeligate mixed=new GrassRegression.World(){public NamespaceID getBlockBiome(int x,int y,int z){return NamespaceID.fromString(x<0?"forest":"plains");}};
  Color actual=BiomeColors.forBlock(new BlockData(NamespaceID.fromString("short_grass")),new BlockPos(0,64,0),mixed,NamespaceID.fromString("plains"));
  int p=BiomeColors.color(NamespaceID.fromString("plains"),false,0,0),f=BiomeColors.color(NamespaceID.fromString("forest"),false,0,0);
  for(int s:new int[]{0,8,16})if(((actual.getRGB()>>>s)&255)!=(((p>>>s)&255)*15+((f>>>s)&255)*10)/25)throw new AssertionError("Wrong biome boundary blend");
  System.out.println("PASS 64 paired tall plants use matching upper/lower variants; 5x5 biome boundary blend correct");
 }
}
