package tech.dsstudio.minecraft.playerdata.driver;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.dsstudio.minecraft.playerdata.PlayerData;
import tech.dsstudio.minecraft.playerdata.exceptions.CannotAccessMediumException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleFileStorage implements PlayerDataStorage {
	public SimpleFileStorage(File base) {
		File file = new File(base, "data");
		if (!file.exists()) {
			if (!file.mkdirs()) {
				throw new RuntimeException("Cannot create directory");
			}
		}
		if (!file.isDirectory()) {
			throw new RuntimeException("Given path is not a directory");
		}
		this.base = file;
	}

	@Override
	public boolean create(@NotNull UUID uuid) {
		File file = new File(base, uuid.toString());
		if (!file.exists()) {
			try {
				return file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	public @Nullable PlayerData find(@NotNull UUID uuid) {
		return activeData.get(uuid.toString());
	}

	@Override
	public PlayerData get(@NotNull UUID uuid) {
		PlayerData data;
		if (!activeData.containsKey(uuid.toString())) {
			data = new PlayerData(new ConcurrentHashMap<>());
			activeData.put(uuid.toString(), data);
		} else {
			data = activeData.get(uuid.toString());
		}
		return data;
	}

	@Override
	public boolean delete(@NotNull UUID uuid) {
		File file = new File(base, uuid.toString());
		return !file.exists() || file.delete();
	}

	@Override
	public boolean isPresent(@NotNull UUID uuid) {
		return new File(base, uuid.toString()).exists();
	}

	@Override
	public void purge() {
		activeData.keySet().forEach(key -> {
			File file = new File(base, key);
			if (!file.delete()) {
				throw new CannotAccessMediumException(file);
			}
		});
		activeData.clear();
	}

	@Override
	public boolean ready() {
		return true;
	}

	@Override
	public void load() {
		try {
			Files.walk(base.toPath(), 1).map(Path::toFile).filter(it -> !it.isDirectory()).forEach(file -> {
				try {
					ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
					String name = file.getName();
					PlayerData data = new PlayerData((ConcurrentHashMap<String, Serializable>) is.readObject());
					is.close();
					activeData.put(name, data);
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save() {
		activeData.forEach((k, v) -> {
			File file = new File(base, k);
			if (!file.exists()) {
				try {
					if (!file.createNewFile()) {
						throw new IOException("File creation failed");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file, false));
				os.writeObject(v.getEntries());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private File base;
	private ConcurrentHashMap<String, PlayerData> activeData = new ConcurrentHashMap<>();
}
