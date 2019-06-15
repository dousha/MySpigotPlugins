package tech.dsstudio.minecraft.dsrt;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main extends JavaPlugin {
	@Override
	public void onEnable() {
		saveDefaultConfig();
		FileConfiguration config = getConfig();
		try {
			hub = new FileHub(config);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		if (hub != null)
			hub.closeAll();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
			sender.sendMessage("Need 1 arg");
			return true;
		}
		Optional<RuntimeFileHandler> handler = hub.open(sender, args[0]);
		if (!handler.isPresent()) {
			sender.sendMessage("Cannot get handler, check console or the file is occupied");
			return true;
		}
		switch (command.getName()) {
			case "ls":
				handler.ifPresent(it -> {
					try {
						Files.walk(Paths.get(it.getFile().toURI())).forEach(entry -> sender.sendMessage(entry.getFileName().toString()));
					} catch (IOException e) {
						sender.sendMessage("Cannot walk file. You should see the console!");
						e.printStackTrace();
					}
					hub.close(it);
				});
				break;
			case "rm":
				handler.ifPresent(it -> {
					hub.delete(it);
					sender.sendMessage("Deleted");
				});
				break;
			case "touch":
				handler.ifPresent(it -> {
					hub.close(it);
					sender.sendMessage("Touched");
				});
				break;
		}
		return super.onCommand(sender, command, label, args);
	}

	public static FileHub getHub() {
		return hub;
	}

	private static FileHub hub = null;

	public class FileHub {
		FileHub(FileConfiguration config) throws IOException {
			String jailPath = config.getString("jail");
			File jail = new File(jailPath);
			this.jailPath = Paths.get(jailPath);
			if (!jail.exists()) {
				if (!jail.mkdirs()) {
					throw new IOException();
				}
			}
			this.config = config;
		}

		public Optional<RuntimeFileHandler> open(CommandSender who, String relativePath) {
			Path jail = getJailPathOf(who);
			if (jail == null) {
				return Optional.empty();
			}
			if (who instanceof ConsoleCommandSender) {
				if (this.config.getBoolean("console.canLeaveJail") || isInJail(jail, relativePath)) {
					return Optional.ofNullable(openWorker(who, jail, relativePath));
				} else {
					return Optional.empty();
				}
			} else if (who instanceof Player) {
				if (isInJail(jail, relativePath) || who.hasPermission(getPermission("operator")) && config.getBoolean("operator.canLeaveJail")) {
					return Optional.ofNullable(openWorker(who, jail, relativePath));
				} else {
					return Optional.empty();
				}
			}
			return Optional.empty();
		}

		public void close(RuntimeFileHandler handler) {
			if (handler == null) {
				return;
			}
			ReentrantReadWriteLock.WriteLock l = lock.writeLock();
			l.lock();
			handler.close();
			openedFiles.remove(handler);
			l.unlock();
		}

		public void delete(RuntimeFileHandler handler) {
			if (handler == null) {
				return;
			}
			ReentrantReadWriteLock.WriteLock l = lock.writeLock();
			l.lock();
			openedFiles.remove(handler);
			try {
				handler.delete();
				handler.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				l.unlock();
			}
		}

		void closeAll() {
			ReentrantReadWriteLock.WriteLock l = lock.writeLock();
			l.lock();
			openedFiles.forEach(RuntimeFileHandler::close);
			l.unlock();
		}

		private RuntimeFileHandler openWorker(CommandSender who, Path jail, String relativePath) {
			ReentrantReadWriteLock.WriteLock l = lock.writeLock();
			l.lock();
			RuntimeFileHandler handler = null;
			try {
				handler = new RuntimeFileHandler(who, jail.toFile(), relativePath);
				if (openedFiles.contains(handler)) {
					return null;
				} else {
					openedFiles.add(handler);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				l.unlock();
			}
			return handler;
		}

		private Path getJailPathOf(CommandSender who) {
			if (who instanceof ConsoleCommandSender) {
				return Paths.get(getJailPath("console"));
			} else if (who instanceof Player) {
				if (who.hasPermission(getPermission("operator"))) {
					String subPath = getJailPath("operator").replaceAll("~", who.getName());
					return Paths.get(jailPath.toUri().toString(), subPath);
				} else if (who.hasPermission(getPermission("user"))) {
					String subPath = getJailPath("user").replaceAll("~", who.getName());
					return Paths.get(jailPath.toUri().toString(), subPath);
				} else if (who.hasPermission(getPermission("basic"))) {
					return Paths.get(jailPath.toUri().toString(), getJailPath("basic"));
				} else {
					return null; // no permission
				}
			} else {
				// Command blocks and execute as are not allowed
				return null;
			}
		}

		private boolean isInJail(Path jail, String relativePath) {
			Path targetPath = Paths.get(jail.toUri().toString(), relativePath).normalize();
			return targetPath.startsWith(jail);
		}

		private String getJailPath(String klass) {
			return config.getString(klass + ".jail");
		}

		private String getPermission(String klass) {
			return config.getString(klass + ".permission");
		}

		private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		private HashSet<RuntimeFileHandler> openedFiles = new HashSet<>();
		private Path jailPath;
		private FileConfiguration config;
	}
}
