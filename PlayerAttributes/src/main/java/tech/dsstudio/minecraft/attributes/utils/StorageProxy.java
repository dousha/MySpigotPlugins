package tech.dsstudio.minecraft.attributes.utils;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.dsstudio.minecraft.attributes.PlayerAttributeApi;
import tech.dsstudio.minecraft.attributes.types.PlayerAttributes;
import tech.dsstudio.minecraft.playerdata.driver.PlayerDataStorage;

public class StorageProxy {
	@NotNull
	public static PlayerDataStorage getStorage() {
		return storage;
	}

	@NotNull
	public static PlayerAttributes getPlayerAttributes(@NotNull Player player) {
		return (PlayerAttributes) storage.get(player.getUniqueId()).computeIfAbsent(PlayerAttributeApi.ATTRIBUTE_KEY, (k) -> new PlayerAttributes());
	}

	@Nullable
	public static String getPlayerAttributeSet(@NotNull Player player) {
		return (String) storage.get(player.getUniqueId()).get(PlayerAttributeApi.ATTRIBUTE_SET_KEY);
	}

	public static void setPlayerAttributeSet(@NotNull Player player, @NotNull String set) {
		storage.get(player.getUniqueId()).set(PlayerAttributeApi.ATTRIBUTE_SET_KEY, set);
	}

	public static void setStorage(@NotNull PlayerDataStorage storage) {
		StorageProxy.storage = storage;
	}

	private static PlayerDataStorage storage = null;
}
