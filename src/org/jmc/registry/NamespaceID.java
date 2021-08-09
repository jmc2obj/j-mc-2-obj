package org.jmc.registry;

import java.lang.reflect.Type;
import java.util.Objects;

import javax.annotation.Nonnull;

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

	public final String namespace;
	public final String path;
	
	public NamespaceID(String namespace, String path) {
		if (namespace == null) throw new IllegalArgumentException("namespace can't be null!");
		if (path == null) throw new IllegalArgumentException("path can't be null!");
		this.namespace = namespace;
		this.path = path;
	}
	
	@Nonnull
	public static NamespaceID fromString(String name) {
		if (name == null) throw new IllegalArgumentException("name can't be null!");
		
		String[] splitName = name.split(":");
		
		if (splitName.length > 2) {
			throw new IllegalArgumentException("name can't contain more than 1 colon!");
		} else if (splitName.length == 1) {
			splitName = new String[] {"minecraft", name};
		}
		
		return new NamespaceID(splitName[0], splitName[1]);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(namespace, path);
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
		return Objects.equals(namespace, other.namespace) && Objects.equals(path, other.path);
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

		@Override
		public NamespaceID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return NamespaceID.fromString(json.getAsString());
		}
		
	}
}
