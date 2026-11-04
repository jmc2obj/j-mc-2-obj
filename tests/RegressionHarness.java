import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.util.*;
import org.jmc.*;
import org.jmc.registry.*;
import org.jmc.models.Banner;
import org.jmc.models.Banner.BannerPattern;
import org.jmc.util.*;

public class RegressionHarness {
 static void check(boolean ok, String why) {if(!ok) throw new AssertionError(why);}
 public static void main(String[] args) throws Exception {
  Options.uiMode=Options.UIMode.CONSOLE;
  if(args[0].equals("config")) {
   Configuration.initialize();
   Path conf=Filesystem.getDatafilesDir().toPath().resolve("conf");
   check(Files.size(conf.resolve("hash"))>0,"configuration hash not written beside JAR");
   Path blocks=conf.resolve("blocks.conf");
   byte[] bytes=Files.readAllBytes(blocks);
   check(bytes.length>1000,"configuration extraction incomplete");
   Files.write(blocks,"\n<!-- local customization -->\n".getBytes("UTF-8"),StandardOpenOption.APPEND);
   byte[] customized=Files.readAllBytes(blocks);
   Configuration.initialize();
   check(Arrays.equals(customized,Files.readAllBytes(blocks)),"local customization overwritten");
   check(Files.isRegularFile(Paths.get("conf")),"working-directory conf file changed");
   System.out.println("PASS configuration beside JAR, unrelated working directory, local edits preserved");
   return;
  }
  for(String pack:args) {
   Options.resourcePacks.clear();Options.resourcePacks.add(new File(pack));Registries.initialize();
   for(String name:Arrays.asList("glass","glass_pane_post","glass_pane_side","glass_pane_side_alt","glass_pane_noside","glass_pane_noside_alt","chain")) {
    ModelEntry m=Registries.getModel(NamespaceID.fromString("block/"+name));
    check(m!=null,"missing model "+name);
    ModelEntry.RegistryModel g=m.generateModel();
    check(g.elements!=null&&!g.elements.isEmpty(),"missing model elements "+name);
    for(String tex:g.textures.values())if(tex!=null&&!tex.startsWith("#"))check(Registries.getTexture(NamespaceID.fromString(tex)).getImage()!=null,"missing model texture "+tex);
   }
   check(Registries.getBlockstate(NamespaceID.fromString("chain"))!=null,"missing legacy chain blockstate");
   BufferedImage portal=Registries.getTexture(NamespaceID.fromString("entity/end_portal")).getImage();
   String portalPath="assets/minecraft/textures/entity/"+(pack.contains("mc-26.")?"end_portal/end_portal":"end_portal")+".png";
   BufferedImage exactPortal=javax.imageio.ImageIO.read(new ByteArrayInputStream(ResourcePackIO.loadResource(new File(pack),portalPath)));
   check(portal.getWidth()==exactPortal.getWidth()&&portal.getHeight()==exactPortal.getHeight(),"portal dimensions changed");
   check(Arrays.equals(portal.getRGB(0,0,portal.getWidth(),portal.getHeight(),null,0,portal.getWidth()),exactPortal.getRGB(0,0,exactPortal.getWidth(),exactPortal.getHeight(),null,0,exactPortal.getWidth())),"portal pixels changed");
   BufferedImage bannerBase=Registries.getTexture(NamespaceID.fromString("entity/banner_base")).getImage();
   check(bannerBase.getWidth()>0,"missing banner base");
   ArrayList<BannerPattern> patterns=new ArrayList<>();
   BannerPattern base=new BannerPattern();base.setColor("red");patterns.add(base);
   BannerPattern emblem=new BannerPattern();emblem.setColor("yellow");emblem.setPattern(NamespaceID.fromString("mojang"));patterns.add(emblem);
   NamespaceID banner=NamespaceID.fromString("jmc2obj:banner/regression");
   Method generate=Banner.class.getDeclaredMethod("generateBannerImage",NamespaceID.class,ArrayList.class);generate.setAccessible(true);
   check((Boolean)generate.invoke(new Banner(),banner,patterns),"banner composition failed");
   check(Registries.getTexture(banner).getImage().getWidth()==bannerBase.getWidth(),"banner size changed");
   BlockTypes.initialize();
   for(String block:Arrays.asList("white_bed","birch_sign","birch_wall_sign","bamboo_hanging_sign","bamboo_wall_hanging_sign")) {
    BlockInfo info=BlockTypes.getAll().get(NamespaceID.fromString(block));
    check(info!=null,"missing configured block "+block);
    check((info.getModel() instanceof org.jmc.models.Registry)==pack.contains("mc-26."),"wrong model handler "+block);
   }
   System.out.println("PASS "+new File(pack).getName()+": glass, all pane variants, chain geometry/textures, banner composition, end portal pixels, bed and sign model selection");
  }
 }
}

