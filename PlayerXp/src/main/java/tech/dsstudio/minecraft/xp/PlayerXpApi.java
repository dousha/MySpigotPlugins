package tech.dsstudio.minecraft.xp;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import tech.dsstudio.dingbats.Concealed;
import tech.dsstudio.minecraft.playerdata.driver.PlayerDataStorage;
import tech.dsstudio.minecraft.playerdata.events.RequestForStorageEvent;
import tech.dsstudio.minecraft.playerdata.events.StorageReadyEvent;

public class PlayerXpApi extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		handler = new CommandHandler(this);
		pm = ProtocolLibrary.getProtocolManager();
		instance = this;
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().callEvent(new RequestForStorageEvent());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch (command.getName()) {
			case "pxp":
				return handler.handleToggle(sender, args);
			case "pxpr":
				return handler.handleGet(sender, args);
			case "pxpw":
				return handler.handleSet(sender, args);
			default:
				return false;
		}
	}

	@EventHandler
	public void onStorageReady(@NotNull StorageReadyEvent event) {
		this.storage = event.getStorage();
		logic = new CoreLogic(this.storage);
		getServer().getPluginManager().registerEvents(logic, this);
	}

	@Concealed
	public ProtocolManager getProtocolManager() {
		return this.pm;
	}

	@Concealed
	public PlayerDataStorage getStorage() {
		return this.storage;
	}

	@Concealed
	public CoreLogic getLogic() {
		return this.logic;
	}

	public static PlayerXpApi getInstance() {
		return instance;
	}

	private ProtocolManager pm;
	private CommandHandler handler;
	private PlayerDataStorage storage;
	private CoreLogic logic;
	private static PlayerXpApi instance;
}
