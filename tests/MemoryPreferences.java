package org.jmc.gui;
import java.util.*;import java.util.prefs.*;
public class MemoryPreferences extends AbstractPreferences {
 private final Map<String,String> values=new HashMap<>();private final Map<String,MemoryPreferences> children=new HashMap<>();
 public MemoryPreferences(){super(null,"");}private MemoryPreferences(MemoryPreferences p,String n){super(p,n);}
 protected void putSpi(String k,String v){values.put(k,v);}protected String getSpi(String k){return values.get(k);}protected void removeSpi(String k){values.remove(k);}protected void removeNodeSpi(){values.clear();children.clear();}protected String[] keysSpi(){return values.keySet().toArray(new String[0]);}protected String[] childrenNamesSpi(){return children.keySet().toArray(new String[0]);}protected AbstractPreferences childSpi(String n){return children.computeIfAbsent(n,k->new MemoryPreferences(this,k));}protected void syncSpi(){}protected void flushSpi(){}
}
