package org.jmc;

import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import org.jmc.registry.NamespaceID;

@JsonAdapter(BlockData.BlockDataAdapter.class)
@ParametersAreNonnullByDefault
public class BlockData {
	
	/**
	 * Sets id to NamespaceID.NULL.
	 */
	public BlockData() {
		this(NamespaceID.NULL);
	}
	
	public BlockData(NamespaceID id) {
		this(id, new Blockstate());
	}
	
	public BlockData(NamespaceID id, Blockstate state) {
		if (id == null || state == null) {
			throw new NullPointerException("id and state can't be null!");
		}
		this.id = id;
		this.state = state;
	}
	
	public BlockData(BlockData other) {
		if (other == null) {
			throw new NullPointerException("other BlockData can't be null!");
		}
		this.id = other.id;
		this.state = (Blockstate) other.state.clone();
		this.info = other.info;
	}
	
	@Nonnull
	public NamespaceID id;
	@Nonnull
	public Blockstate state;
	
	private BlockInfo info;
	
	public BlockInfo getInfo() {
		if (info == null || !id.equals(info.id)) {
			info = BlockTypes.get(this);
		}
		return info;
	}
	
	@Override
	public boolean equals(@CheckForNull Object o) {
		if (super.equals(o) && o instanceof BlockData) {
			BlockData bd = (BlockData)o;
			return id.equals(bd.id) && state.equals(bd.state);
		}
		return false;
	}
	
	/**
	 * @param other
	 * @return result of {@link AbstractMap#equals(Object)} ignoring block id
	 */
	public boolean equalData(BlockData other) {
		return state.equals(other.state);
	}
	
	@Override
	public String toString() {
		return String.format("id=%s %s", id, state.toString());
	}
	
	public String toIdString() {
		StringBuilder blockId = new StringBuilder(id.toString());
		for (Map.Entry<String, String> entry : state.entrySet()) {
			assert ((!entry.getValue().contains("-")) && (!entry.getKey().contains("-")));
			blockId.append(String.format("-%s=%s", entry.getKey(), entry.getValue()));
		}
		return blockId.toString();
	}
	
	static class BlockDataAdapter implements JsonDeserializer<BlockData> {
		@Override
		public BlockData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			BlockData bd = new BlockData();
			if (json.isJsonPrimitive()) {
				bd.id = context.deserialize(json, NamespaceID.class);
			} else if (json.isJsonObject()) {
				JsonObject jo = json.getAsJsonObject();
				JsonElement id = jo.get("id");
				if (id != null) {
					bd.id = context.deserialize(id, NamespaceID.class);
				}
				JsonElement state = jo.get("state");
				if (state != null) {
					bd.state = context.deserialize(state, Blockstate.class);
				}
			} else {
				throw new JsonParseException("BlockData element is not a string or object!");
			}
			return bd;
		}
	}
}
