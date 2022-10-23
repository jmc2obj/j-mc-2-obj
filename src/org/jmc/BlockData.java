package org.jmc;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
	}
	
	@Nonnull
	public NamespaceID id;
	@Nonnull
	public Blockstate state;
	
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
	
	public String toFullIdString() {
		StringBuilder blockId = new StringBuilder(id.getExportSafeString());
		for (Map.Entry<String, String> entry : state.entrySet()) {
			assert ((!entry.getValue().contains("-")) && (!entry.getKey().contains("-")));
			blockId.append(String.format("-%s=%s", entry.getKey(), entry.getValue()));
		}
		return blockId.toString();
	}
	
	public String toHashedId() {
		String fullId = toFullIdString();
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		byte[] hash = digest.digest(fullId.getBytes(StandardCharsets.UTF_8));
		
		BigInteger hashNum = new BigInteger(1, hash);
		String hashStr = hashNum.toString(36).substring(0, 10);
		return id.getExportSafeString() + "_" + hashStr;
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
