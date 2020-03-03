package tech.dsstudio.minecraft.playerdata;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.dsstudio.minecraft.playerdata.exceptions.NoSuchAttributeException;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData implements Serializable {
	public PlayerData(ConcurrentHashMap<String, Serializable> entries) {
		this.entries = entries;
		this.volatileEntries = new ConcurrentHashMap<>();
	}

	@Nullable
	public Serializable get(@NotNull String key) {
		return entries.get(key);
	}

	@NotNull
	public Serializable getOrFail(@NotNull String key) {
		Serializable obj = entries.get(key);
		if (obj == null) {
			throw new NoSuchAttributeException(key);
		}
		return obj;
	}

	@NotNull
	public Serializable getOrDefault(@NotNull String key, @NotNull Serializable def) {
		return entries.getOrDefault(key, def);
	}

	@Nullable
	public Object getVolatile(@NotNull String key) {
		return volatileEntries.get(key);
	}

	@NotNull
	public Object getVolatileOrFail(@NotNull String key) {
		Object obj = volatileEntries.get(key);
		if (obj == null) {
			throw new NoSuchAttributeException(key);
		}
		return obj;
	}

	@NotNull
	public Object getVolatileOrDefault(@NotNull String key, @NotNull Object def) {
		return volatileEntries.getOrDefault(key, def);
	}

	public void set(@NotNull String key, @Nullable Serializable value) {
		if (value == null) {
			entries.remove(key);
		} else {
			entries.put(key, value);
		}
	}

	public void setVolatile(@NotNull String key, @Nullable Object value) {
		if (value == null) {
			volatileEntries.remove(key);
		} else {
			volatileEntries.put(key, value);
		}
	}

	public String getString(@NotNull String key) {
		return (String) getOrFail(key);
	}

	public String getString(@NotNull String key, @NotNull String def) {
		return (String) getOrDefault(key, def);
	}

	public int getInt(@NotNull String key) {
		return (Integer) getOrFail(key);
	}

	public long getLong(@NotNull String key) {
		return (Long) getOrFail(key);
	}

	public boolean getBool(@NotNull String key) {
		return (Boolean) getOrFail(key);
	}

	public boolean isIsomorphic(@NotNull PlayerData data) {
		return data.entries.keySet().equals(this.entries.keySet());
	}

	public boolean hasKey(@NotNull String key) {
		return entries.containsKey(key);
	}

	public boolean hasVolatileKey(@NotNull String key) {
		return volatileEntries.containsKey(key);
	}

	public void negate(@NotNull String key) {
		set(key, !getBool(key));
	}

	public void increaseInt(@NotNull String key) {
		set(key, getInt(key) + 1);
	}

	public void increaseLong(@NotNull String key) {
		set(key, getLong(key) + 1L);
	}

	public void clear() {
		entries.clear();
		volatileEntries.clear();
	}

	public ConcurrentHashMap<String, Serializable> getEntries() {
		return entries;
	}

	public ConcurrentHashMap<String, Object> getVolatileEntries() {
		return volatileEntries;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PlayerData) {
			return entries.equals(((PlayerData) obj).entries);
		}
		return false;
	}

	// XXX: Better typing?
	private ConcurrentHashMap<String, Serializable> entries;
	private ConcurrentHashMap<String, Object> volatileEntries;
}
