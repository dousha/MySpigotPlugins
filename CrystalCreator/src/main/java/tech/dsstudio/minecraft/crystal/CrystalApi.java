package tech.dsstudio.minecraft.crystal;

import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import tech.dsstudio.minecraft.crystal.events.EnderCrystalBrokenEvent;
import tech.dsstudio.minecraft.playerdata.PlayerData;
import tech.dsstudio.minecraft.playerdata.driver.PlayerDataStorage;
import tech.dsstudio.minecraft.playerdata.events.RequestForStorageEvent;
import tech.dsstudio.minecraft.playerdata.events.StorageReadyEvent;

public class CrystalApi extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		CrystalApi.instance = this;
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		pm.registerEvents(new MasterListener(), this);
		pm.callEvent(new RequestForStorageEvent());
	}

	@EventHandler
	public void onStorageEngineReady(@NotNull StorageReadyEvent event) {
		CrystalApi.storage = event.getStorage();
	}

	public EnderCrystal spawnEnderCrystalAt(@NotNull Location location, double health) {
		location.checkFinite();
		if (location.getWorld() != null) {
			EnderCrystal crystal = (EnderCrystal) location.getWorld().spawnEntity(location, EntityType.ENDER_CRYSTAL);
			PlayerData data = storage.get(crystal.getUniqueId());
			data.set(CRYSTAL_HEALTH_KEY, health);
			data.set(CRYSTAL_OWNER_KEY, null);
			getServer().getPluginManager().callEvent(new EnderCrystalBrokenEvent(crystal));
			return crystal;
		} else {
			throw new IllegalArgumentException("Trying to spawn a crystal at nowhere!");
		}
	}

	public void setCrystalOwner(@NotNull EnderCrystal crysta, @NotNull String ownerId) {
		PlayerData data = storage.get(crysta.getUniqueId());
		data.set(CRYSTAL_OWNER_KEY, ownerId);
	}

	public static CrystalApi getInstance() {
		return instance;
	}

	public static CrystalApi instance;
	public static PlayerDataStorage storage;
	public static final String CRYSTAL_HEALTH_KEY = "crystal_creator_health";
	public static final String CRYSTAL_OWNER_KEY = "crystal_creator_owner";
}
