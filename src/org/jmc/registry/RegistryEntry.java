package org.jmc.registry;

public abstract class RegistryEntry {
	public NamespaceID id;
	
	public RegistryEntry(NamespaceID id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return id.toString();
	}
}
