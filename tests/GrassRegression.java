import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import com.google.gson.*;
import org.jmc.*;
import org.jmc.geom.*;
import org.jmc.geom.FaceUtils.Face;
import org.jmc.models.Registry;
import org.jmc.registry.*;
import org.jmc.threading.*;
import org.jmc.util.CachedGetter;

public class GrassRegression {
 static final NamespaceID PLAINS=NamespaceID.fromString("plains");
 static class World extends ThreadChunkDeligate {
  World(){super(new ChunkDataBuffer(-512,512,-64,320,-512,512));}
  public BlockData getBlockData(int x,int y,int z){return new BlockData(NamespaceID.fromString("air"));}
  public NamespaceID getBlockBiome(int x,int y,int z){return PLAINS;}
 }
 static void check(boolean b,String m){if(!b)throw new AssertionError(m);}
 @SuppressWarnings("unchecked") static void force(String block,String model)throws Exception {
  java.lang.reflect.Field field=Registries.class.getDeclaredField("blockstates");field.setAccessible(true);
  CachedGetter<NamespaceID,BlockstateEntry> states=(CachedGetter<NamespaceID,BlockstateEntry>)field.get(null);
  NamespaceID id=NamespaceID.fromString(block);
  JsonObject json=new Gson().fromJson("{\"variants\":{\"\":{\"model\":\"minecraft:block/"+model+"\"}}}",JsonObject.class);
  states.put(id,BlockstateEntry.parseJson(id,json));
 }
 static List<Face> model(String block,String name,int x,int y,int z,String half)throws Exception {
  force(block,name);BlockData data=new BlockData(NamespaceID.fromString(block));
  if(half!=null)data.state.put("half",half);
  Registry registry=new Registry();registry.setBlockId(data.id);ChunkProcessor processor=new ChunkProcessor();
  registry.addModel(processor,new World(),x,y,z,data,PLAINS);
  return processor.getAllFaces();
 }
 static void add(List<Map<String,Object>> out,Set<TextureEntry> textures,String label,List<Face> faces)throws Exception {
  check(!faces.isEmpty(),"No faces: "+label);
  Map<String,Object> object=new LinkedHashMap<>();object.put("name",label);
  List<Map<String,Object>> encoded=new ArrayList<>();
  for(Face face:faces){
   TextureEntry texture=Registries.getTexture(face.texture);texture.getImage();textures.add(texture);
   Map<String,Object> f=new LinkedHashMap<>();f.put("vertices",face.vertices);f.put("uv",face.uvs);f.put("texture",texture.getExportFilePath());encoded.add(f);
  }
  object.put("faces",encoded);out.add(object);
 }
 public static void main(String[] args)throws Exception {
  Options.uiMode=Options.UIMode.CONSOLE;Options.renderBiomes=true;Options.randBlockVariations=true;
  Options.doubleSidedFaces=false;Options.optimiseGeometry=false;
  for(String n:Arrays.asList("pack2.zip","pack1.zip","pack0.zip","pack3.zip","minecraft.jar"))Options.resourcePacks.add(new File(args[0],n));
  Registries.initialize();BlockTypes.initialize();
  File output=new File(args[1]);output.mkdirs();Options.outputDir=output;
  List<Map<String,Object>> objects=new ArrayList<>();Set<TextureEntry> textures=new HashSet<>();
  for(int i=1;i<=4;i++){
   String suffix=i==1?"":""+i;
   add(objects,textures,"Short grass "+i,model("short_grass","grass"+suffix,(i-1)*3,0,0,null));
   add(objects,textures,"Fern "+i,model("fern","fern"+suffix,(i-1)*3,0,3,null));
   List<Face> tall=new ArrayList<>();tall.addAll(model("tall_grass","tall_grass_bottom",(i-1)*3,0,6,"lower"));
   tall.addAll(model("tall_grass","tall_grass_top"+suffix,(i-1)*3,1,6,"upper"));
   add(objects,textures,"Tall grass "+i,tall);
   List<Face> fern=new ArrayList<>();fern.addAll(model("large_fern","large_fern_bottom"+suffix,(i-1)*3,0,9,"lower"));
   fern.addAll(model("large_fern","large_fern_top"+suffix,(i-1)*3,1,9,"upper"));
   add(objects,textures,"Large fern "+i,fern);
  }
  add(objects,textures,"Untinted shrub",model("red_shrub","red_shrub_blue",0,0,12,null));
  for(TextureEntry entry:textures){File p=new File(output,entry.getExportFilePath());p.getParentFile().mkdirs();ImageIO.write(entry.getImage(),"png",p);}
  Registries.objTextures.addAll(textures);Materials.writeMTLFile(new File(output,"materials.mtl"),null);
  Files.write(new File(output,"scene.json").toPath(),new GsonBuilder().setPrettyPrinting().create().toJson(objects).getBytes("UTF-8"));
  System.out.println("EXPORTED "+objects.size()+" plant fixtures and "+textures.size()+" textures");
  if(args.length>2&&args[2].equals("verify")){
   Class<?> colors=Class.forName("org.jmc.registry.BiomeColors");
   int grass=(Integer)colors.getMethod("color",NamespaceID.class,boolean.class,int.class,int.class).invoke(null,PLAINS,false,0,0);
   NamespaceID source=NamespaceID.fromString("block/grass2");
   Class<?> helper=Class.forName("org.jmc.registry.ModelTextures");
   NamespaceID tinted=(NamespaceID)helper.getMethod("forFace",NamespaceID.class,java.awt.Color.class,boolean.class).invoke(null,source,new java.awt.Color(grass),true);
   BufferedImage a=ImageIO.read(new ByteArrayInputStream(org.jmc.util.ResourcePackIO.loadResource("assets/minecraft/textures/block/grass2.png")));
   BufferedImage b=Registries.getTexture(tinted).getImage();
   for(int y=0;y<a.getHeight();y++)for(int x=0;x<a.getWidth();x++){
    int p=a.getRGB(x,y),q=b.getRGB(x,y);check((p>>>24)==(q>>>24),"Alpha changed");
    for(int s:new int[]{0,8,16})check(((q>>>s)&255)==((p>>>s)&255)*((grass>>>s)&255)/255,"Wrong tint pixel");
   }
   List<Face> overlay=model("tall_grass","tall_grass_top3",0,1,0,"upper");
   check(overlay.stream().anyMatch(f->f.texture.path.contains("layers_")),"Color layer lost");
   check(!overlay.stream().anyMatch(f->f.texture.equals(NamespaceID.UNKNOWN)),"Unknown model texture");
   String mtl=new String(Files.readAllBytes(new File(output,"materials.mtl").toPath()),"UTF-8");
   for(String line:mtl.split("\\R"))if(line.startsWith("Kd "))check(line.equals("Kd 1.0000 1.0000 1.0000"),"Texture colored twice by MTL");
   for(String[] sample:new String[][]{{"wheat","wheat_stage7"},{"carrots","carrots_stage3"},{"sugar_cane","sugar_cane"},{"oak_leaves","oak_leaves1"},{"birch_leaves","birch_leaves1"}}) {
    List<Face> smoke=model(sample[0],sample[1],0,0,0,null);
    check(!smoke.isEmpty(),"Empty crop/leaf model "+sample[1]);
    for(Face face:smoke)check(Registries.getTexture(face.texture).getImage()!=null,"Missing crop/leaf texture "+face.texture);
   }
   Registries.initialize();
   Map<String,Integer> counts=new TreeMap<>();
   BlockstateEntry state=Registries.getBlockstate(NamespaceID.fromString("short_grass"));
   BlockstateEntry.ModelListWeighted variants=state.getModelsFor(new Blockstate()).get(0);
   for(int x=0;x<512;x++){
    String name=variants.getRandomModel(new BlockPos(x,64,0)).id.path;
    counts.put(name,counts.getOrDefault(name,0)+1);
    check(name.equals(variants.getRandomModel(new BlockPos(x,64,0)).id.path),"Nondeterministic variant");
   }
   check(counts.size()==4,"Missing grass variants: "+counts);
   System.out.printf("PASS pixel tint #%06x, original alpha, retained color layers, white MTL multiplier, deterministic 4 variants %s%n",grass,counts);
  }
 }
}
