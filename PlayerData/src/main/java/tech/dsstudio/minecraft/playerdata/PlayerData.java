package tech.dsstudio.minecraft.playerdata;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.dsstudio.minecraft.playerdata.exceptions.NoSuchAttributeException;
import tech.dsstudio.minecraft.playerdata.objects.TimedData;
import tech.dsstudio.minecraft.playerdata.objects.TimedVolatileData;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class PlayerData implements Serializable {
	/**
	 * This constructor shall not be called outside of this project.
	 *
	 * If you are trying to create a PlayerData instance for a player,
	 * use {@link tech.dsstudio.minecraft.playerdata.driver.PlayerDataStorage#get(UUID)}
	 * or {@link tech.dsstudio.minecraft.playerdata.driver.PlayerDataStorage#create(UUID)}
	 *
	 * @param entries Initial entries
	 */
	public PlayerData(ConcurrentHashMap<String, Serializable> entries) {
		this.entries = entries;
		this.volatileEntries = new ConcurrentHashMap<>();
		this.timedEntries = new ConcurrentHashMap<>();
		this.timedVolatileEntries = new ConcurrentHashMap<>();
	}

	/**
	 * Get a data entry.
	 *
	 * @param key Key
	 * @return Serializable data
	 */
	@Nullable
	public Serializable get(@NotNull String key) {
		return entries.get(key);
	}

	/**
	 * Get a data entry. If given key is not found, a
	 * {@link NoSuchAttributeException} will be thrown.
	 *
	 * @param key Key
	 * @return Serializable data
	 */
	@NotNull
	public Serializable getOrFail(@NotNull String key) {
		Serializable obj = entries.get(key);
		if (obj == null) {
			throw new NoSuchAttributeException(key);
		}
		return obj;
	}

	/**
	 * Get a data entry. If given key is not found,
	 * the default value provided will be returned.
	 *
	 * @param key Key
	 * @param def Default value
	 * @return Serializable data
	 */
	@NotNull
	public Serializable getOrDefault(@NotNull String key, @NotNull Serializable def) {
		return entries.getOrDefault(key, def);
	}

	/**
	 * Get a data entry.
	 *
	 * @param key Key
	 * @return Optional of serializable data
	 */
	@NotNull
	public Optional<Serializable> getThen(@NotNull String key) {
		return Optional.ofNullable(entries.get(key));
	}

	/**
	 * Get a data entry. If given key is not found, the generator
	 * function provided will be called. The return value of the
	 * function will be inserted into the set and return to caller.
	 *
	 * @param key Key
	 * @param generator Generator function
	 * @return Serializable data
	 */
	@NotNull
	public Serializable computeIfAbsent(@NotNull String key, @NotNull Function<String, Serializable> generator) {
		if (entries.containsKey(key)) {
			return entries.get(key);
		} else {
			Serializable value = generator.apply(key);
			entries.put(key, value);
			return value;
		}
	}

	/**
	 * Get a volatile entry.
	 *
	 * @param key Key
	 * @return Volatile data
	 */
	@Nullable
	public Object getVolatile(@NotNull String key) {
		return volatileEntries.get(key);
	}

	/**
	 * Get a volatile entry. If given key is not found, a
	 * {@link NoSuchAttributeException} will be thrown.
	 *
	 * @param key Key
	 * @return Volatile data
	 */
	@NotNull
	public Object getVolatileOrFail(@NotNull String key) {
		Object obj = volatileEntries.get(key);
		if (obj == null) {
			throw new NoSuchAttributeException(key);
		}
		return obj;
	}

	/**
	 * Get a volatile entry. If given key is not found,
	 * the default value provided will be returned.
	 *
	 * @param key Key
	 * @param def Default value
	 * @return Volatile data
	 */
	@NotNull
	public Object getVolatileOrDefault(@NotNull String key, @NotNull Object def) {
		return volatileEntries.getOrDefault(key, def);
	}

	/**
	 * Get a volatile entry.
	 *
	 * @param key Key
	 * @return Optional of volatile data
	 */
	@NotNull
	public Optional<Object> getVolatileThen(@NotNull String key) {
		return Optional.ofNullable(volatileEntries.get(key));
	}

	/**
	 * Set a data. If given key exists, the old value will be
	 * overwritten. If given value is null, the key is deleted.
	 *
	 * @param key Key
	 * @param value Value
	 */
	public void set(@NotNull String key, @Nullable Serializable value) {
		if (value == null) {
			entries.remove(key);
		} else {
			entries.put(key, value);
		}
	}

	/**
	 * Set a data. If given key exists, the old value will be
	 * overwritten. If given value is null, the key is deleted.
	 *
	 * @param key Key
	 * @param value Value
	 */
	public void setVolatile(@NotNull String key, @Nullable Object value) {
		if (value == null) {
			volatileEntries.remove(key);
		} else {
			volatileEntries.put(key, value);
		}
	}

	/**
	 * Get a string.
	 * If given key is not found, a
	 * {@link NoSuchAttributeException} will be thrown.
	 *
	 * @param key Key
	 * @return String
	 */
	public String getString(@NotNull String key) {
		return (String) getOrFail(key);
	}

	public String getString(@NotNull String key, @NotNull String def) {
		return (String) getOrDefault(key, def);
	}

	/**
	 * Get an integer.
	 * If given key is not found, a
	 * {@link NoSuchAttributeException} will be thrown.
	 *
	 * @param key Key
	 * @return integer
	 */
	public int getInt(@NotNull String key) {
		return (Integer) getOrFail(key);
	}

	/**
	 * Get a long.
	 * If given key is not found, a
	 * {@link NoSuchAttributeException} will be thrown.
	 *
	 * @param key Key
	 * @return long
	 */
	public long getLong(@NotNull String key) {
		return (Long) getOrFail(key);
	}

	/**
	 * Get a float.
	 * If given key is not found, a
	 * {@link NoSuchAttributeException} will be thrown.
	 *
	 * @param key Key
	 * @return float
	 */
	public float getFloat(@NotNull String key) {
		return (Float) getOrFail(key);
	}

	/**
	 * Get a double.
	 * If given key is not found, a
	 * {@link NoSuchAttributeException} will be thrown.
	 *
	 * @param key Key
	 * @return double
	 */
	public double getDouble(@NotNull String key) {
		return (Double) getOrFail(key);
	}

	/**
	 * Get a boolean.
	 * If given key is not found, a
	 * {@link NoSuchAttributeException} will be thrown.
	 *
	 * @param key Key
	 * @return boolean
	 */
	public boolean getBool(@NotNull String key) {
		return (Boolean) getOrFail(key);
	}

	/**
	 * Test if two data sets are isomorphic.
	 * i.e. has the same key set.
	 *
	 * Only non-volatile data is compared.
	 *
	 * @param data Data set to be compared
	 * @return Whether two sets are isomorphic
	 */
	public boolean isIsomorphic(@NotNull PlayerData data) {
		return data.entries.keySet().equals(this.entries.keySet());
	}

	/**
	 * Test if a key is present in non-volatile set.
	 *
	 * @param key Key
	 * @return Self explanatory
	 */
	public boolean hasKey(@NotNull String key) {
		return entries.containsKey(key);
	}

	/**
	 * Test if a key is present in volatile set.
	 *
	 * @param key Key
	 * @return Self explanatory
	 */
	public boolean hasVolatileKey(@NotNull String key) {
		return volatileEntries.containsKey(key);
	}

	/**
	 * Negate a non-volatile boolean value.
	 * If given key is not present,
	 * a {@link NoSuchAttributeException} will be thrown.
	 * If given key is not a boolean,
	 * a {@link ClassCastException} will be thrown.
	 *
	 * @param key Key
	 */
	public void negate(@NotNull String key) {
		set(key, !getBool(key));
	}

	/**
	 * Add one (1) to a non-volatile integer.
	 * If given key is not present,
	 * a {@link NoSuchAttributeException} will be thrown.
	 * If given key is not a integer,
	 * a {@link ClassCastException} will be thrown.
	 *
	 * @param key Key
	 */
	public void increaseInt(@NotNull String key) {
		set(key, getInt(key) + 1);
	}

	/**
	 * Add one (1) to a non-volatile long.
	 * If given key is not present,
	 * a {@link NoSuchAttributeException} will be thrown.
	 * If given key is not a boolean,
	 * a {@link ClassCastException} will be thrown.
	 *
	 * @param key Key
	 */
	public void increaseLong(@NotNull String key) {
		set(key, getLong(key) + 1L);
	}

	/**
	 * Delete all saved data. Both volatile and non-volatile data.
	 */
	public void clear() {
		entries.clear();
		volatileEntries.clear();
	}

	/**
	 * Get non-volatile set.
	 *
	 * @return Non-volatile set
	 */
	public ConcurrentHashMap<String, Serializable> getEntries() {
		return entries;
	}

	/**
	 * Get volatile set.
	 *
	 * @return Volatile set
	 */
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
	private ConcurrentHashMap<String, TimedData<? extends Serializable>> timedEntries;
	private ConcurrentHashMap<String, TimedVolatileData<?>> timedVolatileEntries;
}
