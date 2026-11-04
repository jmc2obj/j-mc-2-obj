package org.jmc.gui;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import com.google.gson.*;
import org.jmc.Options;

/** Named snapshots of the Settings window. No world/export paths are captured. */
public final class SettingsPresetStore {
    private final Path file;
    public SettingsPresetStore(Path file) { this.file = file; }
    public static SettingsPresetStore forUser() {
        return new SettingsPresetStore(Paths.get(System.getProperty("user.home"), ".jmc2obj", "settings-presets.json"));
    }
    public Path getFile() { return file; }

    public static final class Snapshot {
        public int moveAction, selectAction, language, previewThreads;
        public String[] resourcePacks;
        public boolean useDefaultResourcePack;
        public Snapshot(int move, int select, int lang, int threads, String[] packs, boolean useDefault) {
            moveAction=move; selectAction=select; language=lang; previewThreads=threads;
            resourcePacks=packs.clone(); useDefaultResourcePack=useDefault;
        }
        void validate() {
            if (moveAction<0 || moveAction>5 || selectAction<0 || selectAction>5 || moveAction==selectAction
                    || language<0 || language>=Options.availableLocales.length || previewThreads<1 || previewThreads>512 || resourcePacks==null)
                throw new IllegalArgumentException("Invalid preset settings");
            for (String path:resourcePacks) if (path==null || path.trim().isEmpty()) throw new IllegalArgumentException("Invalid pack path");
        }
        @Override public boolean equals(Object other) {
            if (!(other instanceof Snapshot)) return false;
            Snapshot s=(Snapshot)other;
            return moveAction==s.moveAction && selectAction==s.selectAction && language==s.language && previewThreads==s.previewThreads
                    && useDefaultResourcePack==s.useDefaultResourcePack && Arrays.equals(resourcePacks,s.resourcePacks);
        }
        @Override public int hashCode() { return Objects.hash(moveAction,selectAction,language,previewThreads,useDefaultResourcePack,Arrays.hashCode(resourcePacks)); }
    }
    public static final class Preset {
        public String name;
        public Snapshot settings;
        Preset(String name, Snapshot settings) { this.name=name; this.settings=settings; }
    }
    private static final class Document {
        int version=1;
        List<Preset> presets=new ArrayList<>();
    }
    private static String validName(String name) {
        if (name==null || name.trim().isEmpty() || name.trim().length()>100) throw new IllegalArgumentException("Preset name must have 1-100 characters");
        return name.trim();
    }
    public synchronized List<Preset> load() throws IOException {
        if (!Files.exists(file)) return new ArrayList<>();
        try {
            Document doc=new Gson().fromJson(new String(Files.readAllBytes(file),StandardCharsets.UTF_8),Document.class);
            if (doc==null || doc.version!=1 || doc.presets==null) throw new IllegalArgumentException("Unsupported preset file");
            Set<String> names=new HashSet<>();
            for (Preset p:doc.presets) {
                if (p==null || p.settings==null) throw new IllegalArgumentException("Incomplete preset");
                p.name=validName(p.name); p.settings.validate();
                if (!names.add(p.name.toLowerCase(Locale.ROOT))) throw new IllegalArgumentException("Duplicate preset name");
            }
            doc.presets.sort((a,b)->a.name.compareToIgnoreCase(b.name));
            return new ArrayList<>(doc.presets);
        } catch (JsonParseException | IllegalArgumentException e) { throw new IOException("Cannot read settings presets: "+file,e); }
    }
    public synchronized void save(String name, Snapshot snapshot) throws IOException {
        name=validName(name); snapshot.validate();
        List<Preset> presets=load();
        for (Iterator<Preset> i=presets.iterator();i.hasNext();) if(i.next().name.equalsIgnoreCase(name)) i.remove();
        presets.add(new Preset(name,snapshot));presets.sort((a,b)->a.name.compareToIgnoreCase(b.name));
        Document doc=new Document();doc.presets=presets;
        Path target=file.toAbsolutePath();Files.createDirectories(target.getParent());
        Path temp=Files.createTempFile(target.getParent(),"settings-presets-",".tmp");
        try {
            Files.write(temp,new GsonBuilder().setPrettyPrinting().create().toJson(doc).getBytes(StandardCharsets.UTF_8));
            try { Files.move(temp,target,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING); }
            catch (AtomicMoveNotSupportedException noAtomic) { Files.move(temp,target,StandardCopyOption.REPLACE_EXISTING); }
        } finally { Files.deleteIfExists(temp); }
    }
}
