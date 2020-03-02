package tech.dsstudio.minecraft.playerdata;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData implements Serializable {
	public PlayerData(ConcurrentHashMap<String, Serializable> entries) {
		this.entries = entries;
		this.volatileEntries = new ConcurrentHashMap<>();
	}

	@Nullable public Serializable get(String key) {
		return entries.get(key);
	}

	public Serializable getOrDefault(String key, Serializable def) {
		return entries.getOrDefault(key, def);
	}

	@Nullable public Object getVolatile(String key) {
		return volatileEntries.get(key);
	}

	public Object getVolatileOrDefault(String key, Object def) {
		return volatileEntries.getOrDefault(key, def);
	}

	public void set(String key, Serializable value) {
		if (value == null) {
			entries.remove(key);
		} else {
			entries.put(key, value);
		}
	}

	public void setVolatile(String key, Object value) {
		if (value == null) {
			volatileEntries.remove(key);
		} else {
			volatileEntries.put(key, value);
		}
	}

	public String getString(String key) {
		return (String) get(key);
	}

	public int getInt(String key) {
		return (Integer) get(key);
	}

	public long getLong(String key) {
		return (Long) get(key);
	}

	public boolean isIsomorphic(PlayerData data) {
		return data.entries.keySet().equals(this.entries.keySet());
	}

	public boolean hasKey(String key) {
		return entries.containsKey(key);
	}

	public boolean hasVolatileKey(String key) {
		return volatileEntries.containsKey(key);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PlayerData) {
			return entries.equals(((PlayerData) obj).entries);
		}
		return false;
	}

	public ConcurrentHashMap<String, Serializable> getEntries() {
		return entries;
	}

	public ConcurrentHashMap<String, Object> getVolatileEntries() {
		return volatileEntries;
	}

	// XXX: Better typing?
	private ConcurrentHashMap<String, Serializable> entries;
	private ConcurrentHashMap<String, Object> volatileEntries;
}
