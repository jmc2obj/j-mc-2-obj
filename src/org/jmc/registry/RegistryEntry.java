package org.jmc.registry;

public abstract class RegistryEntry {
	public NamespaceID name;
	
	public RegistryEntry(NamespaceID id) {
		this.name = id;
	}
	
	@Override
	public String toString() {
		return name.toString();
	}
}
