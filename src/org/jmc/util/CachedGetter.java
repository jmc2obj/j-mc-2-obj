package org.jmc.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.CheckForNull;

public abstract class CachedGetter <K, V> {
	private final Map<K, V> entries = new ConcurrentHashMap<>();
	private final Map<K, AtomicBoolean> creatingStates = new HashMap<>();
	
	/**Gets the cached value for {@code key}.
	 * Calls {@link #make(K)} to create and cache if it doesn't have a mapping
	 * @param key
	 * @return
	 */
	@CheckForNull
	public V get(K key) {
		AtomicBoolean creatingState;
		synchronized (this) {
			if (entries.containsKey(key)) {
				return entries.get(key);// if it exists then just return
			} else {
				creatingState = creatingStates.get(key);
				if (creatingState == null) {
					creatingState = new AtomicBoolean(false);
					creatingStates.put(key, creatingState);
				}
				if (!creatingState.compareAndSet(false, true)) {// try and become creator
					while (creatingState.get()) {
						try {
							this.wait();// wait until created
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
					}
					return entries.get(key);
				}
			}
		}
		// we are the creator, add to map
		V entry = make(key);
		synchronized (this) {
			entries.put(key, entry);
			creatingStates.remove(key);
			creatingState.set(false);
			this.notifyAll();
			return entry;
		}
	}
	
	/** @return Unmodifiable map of the entries */
	public Map<K,V> getAll() {
		return Collections.unmodifiableMap(entries);
	}
	
	public synchronized void clear() {
		entries.clear();
		creatingStates.clear();
	}
	
	/** Waits if key is being created then puts value into the map*/
	public synchronized void put(K key, V value) {
		AtomicBoolean creatingState = creatingStates.get(key);
		if (creatingState != null) {
			while (creatingState.get()) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
		entries.put(key, value);
	}
	
	public int size() {
		return entries.size();
	}
	
	/** creates a {@code value} for the given {@code key} */
	public abstract V make(K key);
}
