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
	private final Map<K, Optional<V>> entries = new ConcurrentHashMap<>();
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
			entryOpt = entries.get(key);
			if (entryOpt != null) {// check again, in case
				return entryOpt.orElse(null);// if it exists then just return
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
				entryOpt = entries.get(key);
				if (entryOpt != null) {
					return entryOpt.orElse(null);
				} else {// entry was removed just after creation, we still want to get it.
					assert (creatingStates.get(key) == null);
					creatingState = new AtomicBoolean(true);
					creatingStates.put(key, creatingState);// become new creator
				}
			}
		}
		// we are the creator, add to map
		V entry = null;
		try {
			entry = make(key);
		} finally {
			synchronized (this) {
				if (!creatingState.get()) { // someone else created or put while we were making, use theirs.
					entry = entries.get(key).orElse(null);
				} else {
					entries.put(key, Optional.ofNullable(entry));
					creatingStates.remove(key);
					creatingState.set(false);
					this.notifyAll();
				}
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
	
	/** Waits for any keys to be created then removes all values from the cache.
	 * Other threads may still be waiting in {@link #get(K)} after this and immediately create the key again.*/
	public synchronized void clear() {
		for (K key : creatingStates.keySet()) {
			waitForCreating(key);
		}
		entries.clear();
		creatingStates.clear();
	}
	
	/** Waits if key is being created then puts value into the map.
	 * Will override a key even if it is mid creation*/
	public synchronized void put(K key, V value) {
		AtomicBoolean creatingState = creatingStates.get(key);
		if (creatingState != null) { // If it is being created, set as done and notify.
			creatingStates.remove(key);
			creatingState.set(false);
			this.notifyAll();
		}
		entries.put(key, Optional.ofNullable(value));
	}
	
	public synchronized int size() {
		return entries.size();
	}
	
	protected synchronized void waitForCreating(K key) {
		AtomicBoolean creatingState = creatingStates.get(key);
		if (creatingState != null) {
			waitForCreating(creatingState);
		}
	}
	protected synchronized void waitForCreating(AtomicBoolean creatingState) {
		while (creatingState.get()) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
	
	/** Creates a {@code value} for the given {@code key} */
	public abstract V make(K key);
}
