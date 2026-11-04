package org.jmc.gui;
import java.nio.file.*;import java.util.*;import java.io.*;import org.jmc.gui.SettingsPresetStore.*;
public class PresetStoreTest {
 static void check(boolean b,String m){if(!b)throw new AssertionError(m);}
 public static void main(String[] args)throws Exception{
  Path dir=Paths.get(args[0]);Files.createDirectories(dir);Path file=dir.resolve("presets.json");SettingsPresetStore s=new SettingsPresetStore(file);check(s.load().isEmpty(),"New store not empty");check(!Files.exists(file),"Reading created file");
  Snapshot a=new Snapshot(1,0,0,8,new String[]{"C:/Паки/Трава.zip","C:/Minecraft/1.21.11.jar"},false),b=new Snapshot(2,1,1,16,new String[]{"D:/Другой пак.zip"},true);
  s.save("Трава и листва",a);s.save("Vanilla",b);s=new SettingsPresetStore(file);check(s.load().size()==2,"Lost presets after reopen");check(s.load().get(1).settings.equals(a),"Changed settings/order/Unicode");
  s.save("vanilla",a);check(s.load().size()==2,"Duplicate on overwrite");check(s.load().get(0).settings.equals(a),"Overwrite not saved");byte[] before=Files.readAllBytes(file);
  try{s.save("  ",b);throw new AssertionError("Blank accepted");}catch(IllegalArgumentException good){}check(Arrays.equals(before,Files.readAllBytes(file)),"Blank changed file");
  try{s.save("Bad",new Snapshot(0,0,0,8,new String[0],false));throw new AssertionError("Conflicting controls accepted");}catch(IllegalArgumentException good){}
  String[] many=new String[180];for(int i=0;i<many.length;i++)many[i]="C:/Длинный путь к ресурс-пакам/пак-"+i+".zip";s.save("Большой список",new Snapshot(1,0,0,512,many,false));check(s.load().get(1).settings.resourcePacks.length==180,"Long preset truncated");
  Files.write(file,"{bad json".getBytes("UTF-8"));before=Files.readAllBytes(file);try{s.save("No clobber",a);throw new AssertionError("Bad file overwritten");}catch(IOException good){}check(Arrays.equals(before,Files.readAllBytes(file)),"Corrupt file lost");
  Files.write(file,"{\"version\":2,\"presets\":[]}".getBytes("UTF-8"));try{s.load();throw new AssertionError("Future version accepted");}catch(IOException good){}
  check(Files.list(dir).noneMatch(p->p.toString().endsWith(".tmp")),"Leaked temporary files");System.out.println("PASS persistence, Unicode, pack order, overwrite, validation, long lists, corrupt/future file preservation, atomic-write cleanup");
 }
}
