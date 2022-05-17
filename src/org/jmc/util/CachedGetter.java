package org.jmc.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.CheckForNull;

public abstract class CachedGetter <K, V> {
	private final Map<K, Optional<V>> entries = new ConcurrentHashMap<>(new HashMap<>());
	private final Map<K, AtomicBoolean> creatingStates = new HashMap<>();
	
	/**Gets the cached value for {@code key}.
	 * Calls {@link #make(K)} to create and cache if it doesn't have a mapping
	 * @param key
	 * @return
	 */
	@CheckForNull
	public V get(K key) {
		Optional<V> entryOpt = entries.get(key);
		if (entryOpt != null) {// fast path
			return entryOpt.orElse(null);// if it exists then just return
		} 
		AtomicBoolean creatingState;
		synchronized (this) {
			if (entries.containsKey(key)) {// check again, in case
				return entries.get(key).orElse(null);// if it exists then just return
			}
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
						Thread.currentThread().interrupt();
						return null;
					}
				}
				return entries.get(key).orElse(null);
			}
		}
		// we are the creator, add to map
		V entry = null;
		try {
			entry = make(key);
		} finally {
			synchronized (this) {
				entries.put(key, Optional.ofNullable(entry));
				creatingStates.remove(key);
				creatingState.set(false);
				this.notifyAll();
			}
		}
		return entry;
	}
	
	/** @return Unmodifiable map of the entries */
	public Map<K,V> getAll() {
		HashMap<K, V> map = new HashMap<>();
		synchronized (this) {
			for (Entry<K, Optional<V>> e : entries.entrySet()) {
				map.put(e.getKey(), e.getValue().orElse(null));
			}
		}
		return Collections.unmodifiableMap(map);
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
		entries.put(key, Optional.ofNullable(value));
	}
	
	public synchronized int size() {
		return entries.size();
	}
	
	/** creates a {@code value} for the given {@code key} */
	public abstract V make(K key);
}
