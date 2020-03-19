package tech.dsstudio.minecraft.stamina;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.jetbrains.annotations.NotNull;
import tech.dsstudio.dingbats.ThreadSafe;
import tech.dsstudio.minecraft.playerdata.PlayerData;
import tech.dsstudio.minecraft.playerdata.driver.PlayerDataStorage;
import tech.dsstudio.minecraft.stamina.events.PlayerExhaustedEvent;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CoreLogic implements Listener {
	public CoreLogic(@NotNull PlayerStaminaApi main, @NotNull PlayerDataStorage storage, @NotNull List<String> enabledWorlds) {
		this.main = main;
		this.storage = storage;
		this.enabledWorlds = enabledWorlds;
		setStaminaBarUpdater();
	}

	@EventHandler
	public void onPlayerFoodLevelChange(@NotNull FoodLevelChangeEvent event) {
		HumanEntity entity = event.getEntity();
		if (entity instanceof Player) {
			// usu this means player is exhausted if stamina is enabled
			Player player = (Player) entity;
			if (isPlayerOnStamina(player)) {
				event.setCancelled(true);
				if (player.getFoodLevel() > event.getFoodLevel()) {
					// player exhausted
					main.getServer().getPluginManager().callEvent(new PlayerExhaustedEvent(player));
				} else {
					// player ate food
					// XXX: this will discard excess
					float saturation = 4f * (event.getFoodLevel() - player.getFoodLevel());
					player.setSaturation(player.getSaturation() + saturation);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerChangeWorld(@NotNull PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		String nowWorldName = player.getWorld().getName();
		if (!enabledWorlds.contains(nowWorldName)) {
			// recover
			if (automaticResetOnExit) {
				clearStaminaMode(player);
			}
		} else {
			// set
			if (automaticSetOnEnter) {
				setStaminaMode(player);
			}
		}
	}

	public void setStaminaBarUpdater() {
		// XXX: Performance overhead? ConcurrentModification?
		main.getServer().getScheduler().runTaskTimer(main, () ->
						main.getServer().getOnlinePlayers().parallelStream().filter(this::isPlayerOnStamina).forEach(it -> {
							float top = getPlayerStaminaTop(it.getUniqueId());
							int value = (int) (20 * (it.getSaturation() / top));
							it.setFoodLevel(value);
						}),
				20L,
				20L);
	}

	public void setStaminaMode(@NotNull Player player) {
		PlayerData data = storage.get(player.getUniqueId());
		float saturation;
		if (data.hasKey(STAMINA_KEY)) {
			saturation = (float) data.getOrFail(STAMINA_KEY);
		} else {
			saturation = player.getFoodLevel() * 4.0f;
		}
		int currentFoodLevel = player.getFoodLevel();
		float currentSaturation = player.getSaturation();
		data.set(ORIGINAL_FOOD_LEVEL_KEY, currentFoodLevel);
		data.set(ORIGINAL_SATURATION_LEVEL_KEY, currentSaturation);
		player.setFoodLevel(20); // FIXME: This line will be no longer needed
		player.setSaturation(saturation);
		data.set(STAMINA_KEY, null);
	}

	public void clearStaminaMode(@NotNull Player player) {
		PlayerData data = storage.get(player.getUniqueId());
		if (data.hasKey(ORIGINAL_FOOD_LEVEL_KEY)) {
			float currentSaturation = player.getSaturation();
			data.set(STAMINA_KEY, currentSaturation);
			int originalFoodLevel = data.getInt(ORIGINAL_FOOD_LEVEL_KEY);
			float originalSaturation = data.getFloat(ORIGINAL_SATURATION_LEVEL_KEY);
			player.setFoodLevel(originalFoodLevel);
			player.setSaturation(originalSaturation);
			data.set(ORIGINAL_FOOD_LEVEL_KEY, null);
			data.set(ORIGINAL_SATURATION_LEVEL_KEY, null);
		}
	}

	public boolean isPlayerOnStamina(@NotNull Player player) {
		return storage.get(player.getUniqueId()).hasKey(ORIGINAL_SATURATION_LEVEL_KEY);
	}

	public void setAutomaticResetOnExit(boolean automaticResetOnExit) {
		this.automaticResetOnExit = automaticResetOnExit;
	}

	public void setAutomaticSetOnEnter(boolean automaticSetOnEnter) {
		this.automaticSetOnEnter = automaticSetOnEnter;
	}

	@ThreadSafe
	public void setPlayerStaminaTop(@NotNull UUID uuid, float top) {
		PlayerData playerData = storage.get(uuid);
		playerData.set(STAMINA_TOP_KEY, top);
		playerStaminaTop.put(uuid, top);
	}

	@ThreadSafe
	public float getPlayerStaminaTop(@NotNull UUID uuid) {
		return playerStaminaTop.computeIfAbsent(uuid, (k) -> 80.0f);
	}

	private PlayerStaminaApi main;
	private PlayerDataStorage storage;
	private List<String> enabledWorlds;
	private boolean automaticSetOnEnter = true;
	private boolean automaticResetOnExit = true;
	private ConcurrentHashMap<UUID, Float> playerStaminaTop = new ConcurrentHashMap<>();
	private static final String ORIGINAL_FOOD_LEVEL_KEY = "pstFoodLevel";
	private static final String ORIGINAL_SATURATION_LEVEL_KEY = "pstSaturation";
	private static final String STAMINA_KEY = "pstStamina";
	private static final String STAMINA_TOP_KEY = "pstStaminaTop";
}
