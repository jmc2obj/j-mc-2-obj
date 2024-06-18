package org.jmc.registry;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

@JsonAdapter(NamespaceID.NamespaceIDAdapter.class)
public class NamespaceID implements Comparable<NamespaceID> {
	private static final Map<String, String> nsStringPool = new ConcurrentHashMap<>();
	private static final Map<String, String> pathStringPool = new ConcurrentHashMap<>();
	
	@Nonnull
	public final static NamespaceID NULL = new NamespaceID("jmc2obj", "null");
	@Nonnull
	public final static NamespaceID UNKNOWN = new NamespaceID("jmc2obj", "unknown");
	@Nonnull
	public final static NamespaceID EXPORTEDGE = new NamespaceID("jmc2obj", "export_edge");

	public final String namespace;
	public final String path;
	
	public NamespaceID(String namespace, String path) {
		if (namespace == null) throw new IllegalArgumentException("NamespaceID namespace can't be null!");
		if (path == null) throw new IllegalArgumentException("NamespaceID path can't be null!");
		String ns = nsStringPool.get(namespace);
		if (ns == null) {
			ns = namespace;
			nsStringPool.put(namespace, namespace);
		}
		this.namespace = ns;
		String pth = pathStringPool.get(path);
		if (pth == null) {
			pth = path;
			pathStringPool.put(path, path);
		}
		this.path = pth;
	}
	
	@Nonnull
	@ParametersAreNonnullByDefault
	public static NamespaceID fromString(String name) {
		int colonInd = name.indexOf(':');
		if (colonInd == -1) {
			return new NamespaceID("minecraft", name);
		}
		String namespace = name.substring(0, colonInd);
		String path = name.substring(colonInd + 1);
		if (path.indexOf(':') != -1) {
			throw new IllegalArgumentException("NamespaceID name can't contain more than 1 colon!");
		}
		return new NamespaceID(namespace, path);
	}
	
	@Override
	public int hashCode() {
		return 31 * path.hashCode() + namespace.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NamespaceID other = (NamespaceID) obj;
		// because of our string pool, we know equal strings are the same object
		//noinspection StringEquality
		return path == other.path && namespace == other.namespace;
	}
	
	public String getExportSafeString() {
		return toString().replace(':', '_').replace('/', '-');
	}

	@Override
	@Nonnull
	public String toString() {
		return namespace + ":" + path;
	}
	
	@Override
	public int compareTo(NamespaceID o) {
		int namespaceCmp = namespace.compareTo(o.namespace);
		return (namespaceCmp != 0 ? namespaceCmp : path.compareTo(o.path));
	}
	
	
	static class NamespaceIDAdapter implements JsonDeserializer<NamespaceID>, JsonSerializer<NamespaceID> {
		@Override
		public JsonElement serialize(NamespaceID src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.toString());
		}

		@SuppressWarnings("null")
		@Override
		public NamespaceID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return NamespaceID.fromString(json.getAsString());
		}
		
	}
}
