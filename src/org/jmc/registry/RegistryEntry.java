package org.jmc.registry;

import com.google.gson.annotations.Expose;

public abstract class RegistryEntry {
	@Expose
	public NamespaceID id;
	
	public RegistryEntry(NamespaceID id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return id.toString();
	}
}
