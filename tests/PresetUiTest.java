package org.jmc.gui;
import java.io.*;import java.nio.file.*;import java.util.*;import java.lang.reflect.*;import javax.swing.*;import java.awt.*;import java.awt.image.*;import javax.imageio.*;import com.google.gson.*;import org.jmc.*;import org.jmc.gui.SettingsPresetStore.*;
public class PresetUiTest {
 static void select(Settings w,String name){for(int i=1;i<w.cbPreset.getItemCount();i++)if(w.cbPreset.getItemAt(i).toString().equals(name)){w.cbPreset.setSelectedIndex(i);return;}throw new AssertionError("Missing preset "+name);}
 static void check(boolean b,String m){if(!b)throw new AssertionError(m);}static void invoke(Settings w,String method)throws Exception{Method m=Settings.class.getDeclaredMethod(method);m.setAccessible(true);m.invoke(w);}
 public static void main(String[] args)throws Exception{
  Options.uiMode=Options.UIMode.CONSOLE;final Throwable[] error={null};
  SwingUtilities.invokeAndWait(()->{Settings w=null,reopened=null;try{
   Path dir=Paths.get(args[0]);Files.createDirectories(dir);MemoryPreferences prefs=new MemoryPreferences();prefs.put("RESOURCE_PACKS","[]");prefs.putBoolean("USE_DEFAULT_RESOURCE_PACK",false);SettingsPresetStore store=new SettingsPresetStore(dir.resolve("ui-presets.json"));w=new Settings(prefs,store);
   check(w.cbPreset.getItemCount()==1,"Empty dropdown wrong");check(w.btnSavePreset.getActionListeners().length==1,"Save button disconnected");
   File game=new File(args[1]),pack=new File(args[2]);w.listPacks.getModel().addElement(pack);w.listPacks.getModel().addElement(game);w.spPrevThreads.setValue(12);w.savePreset("Grass — Трава");check(w.cbPreset.getSelectedIndex()==1,"Saved preset not selected");
   w.listPacks.reset();w.listPacks.getModel().addElement(game);w.spPrevThreads.setValue(3);w.cbMove.setSelectedIndex(2);w.chckbxUsePackDefault.setSelected(false);w.savePreset("Vanilla");check(store.load().size()==2,"Second preset missing");
   select(w,"Grass — Трава");check(w.spPrevThreads.getValue().equals(12),"Threads not restored");check(w.cbMove.getSelectedIndex()==1&&w.cbSelect.getSelectedIndex()==0,"Controls not restored");check(w.listPacks.getList().equals(Arrays.asList(pack,game)),"Pack order changed");check(Options.resourcePacks.equals(Arrays.asList(pack,game)),"Runtime pack order not applied");
   check(prefs.get("SETTINGS_PRESET","").equals("Grass — Трава"),"Selection not persisted");check(prefs.getInt("PREVIEW_THREADS",0)==12,"Prefs not saved");
   reopened=new Settings(prefs,store);check(reopened.cbPreset.getSelectedItem().toString().equals("Grass — Трава"),"Reopened selection lost");check(reopened.listPacks.getList().equals(Arrays.asList(pack,game)),"Reopen settings lost");
   reopened.spPrevThreads.setValue(10);check(reopened.cbPreset.getSelectedIndex()==0,"Modified settings still labeled as saved");check(store.load().get(0).settings.previewThreads==12,"Autosave changed preset");reopened.savePreset("Grass — Трава");check(store.load().size()==2&&store.load().get(0).settings.previewThreads==10,"Overwrite failed");
   invoke(reopened,"resetSettings");check(store.load().size()==2,"Factory reset deleted presets");check(reopened.cbPreset.getSelectedIndex()==0,"Reset selection not cleared");
   select(reopened,"Grass — Трава");reopened.setSize(Math.max(reopened.getWidth(),460),480);reopened.validate();BufferedImage im=new BufferedImage(reopened.getWidth(),reopened.getHeight(),BufferedImage.TYPE_INT_RGB);Graphics2D g=im.createGraphics();reopened.getContentPane().printAll(g);g.dispose();ImageIO.write(im,"png",dir.resolve("settings-presets.png").toFile());
   reopened.savePreset(org.jmc.util.Messages.getString("Settings.Preset.CURRENT"));reopened.cbPreset.setSelectedIndex(0);select(reopened,org.jmc.util.Messages.getString("Settings.Preset.CURRENT"));check(reopened.cbPreset.getSelectedIndex()>0,"Preset name collides with placeholder");
   System.out.println("PASS Settings dropdown save/apply/reopen/overwrite/reset, no autosave loops, ordered runtime packs, isolated preferences");
  }catch(Throwable t){error[0]=t;}finally{if(w!=null)w.dispose();if(reopened!=null)reopened.dispose();}});
  if(error[0]!=null)throw new AssertionError(error[0]);System.exit(0);
 }
}
