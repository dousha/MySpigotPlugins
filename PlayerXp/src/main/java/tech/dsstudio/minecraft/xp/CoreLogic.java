package tech.dsstudio.minecraft.xp;

import com.comphenix.packetwrapper.WrapperPlayServerExperience;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.jetbrains.annotations.NotNull;
import tech.dsstudio.dingbats.Concealed;
import tech.dsstudio.minecraft.playerdata.PlayerData;
import tech.dsstudio.minecraft.playerdata.driver.PlayerDataStorage;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CoreLogic implements Listener {
	public CoreLogic(@NotNull PlayerDataStorage storage) {
		this.storage = storage;
	}

	public void addPlayer(@NotNull Player player) {
		if (activePlayers.contains(player.getUniqueId())) {
			return;
		}
		PlayerData data = storage.get(player.getUniqueId());
		if (!data.hasKey(PLAYER_XP_KEY)) {
			data.set(PLAYER_XP_KEY, 1.0f);
		}
		sendPacket(player, data.getFloat(PLAYER_XP_KEY));
		if (!data.hasKey(PLAYER_LEVEL_KEY)) {
			data.set(PLAYER_LEVEL_KEY, 1);
		}
		sendPacket(player, data.getInt(PLAYER_LEVEL_KEY));
	}

	public void removePlayer(@NotNull Player player) {
		activePlayers.remove(player.getUniqueId());
		int level = player.getExpToLevel();
		float xp = player.getExp();
		sendPacket(player, level, xp);
	}

	/**
	 * This will set the fullness of the bar.
	 *
	 * @param player Player
	 * @param value A value between 0.0 (empty) to 1.0 (full). Clamping is applied.
	 */
	public void setPlayerXpPercentage(@NotNull Player player, float value) {
		PlayerData data = storage.get(player.getUniqueId());
		float newXp = (float) Math.max(0.0, Math.min(1.0, value));
		data.set(PLAYER_XP_KEY, newXp);
		sendPacket(player, newXp);
	}

	/**
	 * Set the player level.
	 *
	 * @param player Player
	 * @param value A value between 1 to 255. Clamping is applied.
	 */
	public void setPlayerLevel(@NotNull Player player, int value) {
		PlayerData data = storage.get(player.getUniqueId());
		data.set(PLAYER_LEVEL_KEY, Math.max(1, Math.min(255, value)));
		sendPacket(player, value);
	}

	@EventHandler
	public void onPlayerXpChange(@NotNull PlayerExpChangeEvent event) {
		Player player = event.getPlayer();
		if (activePlayers.contains(player.getUniqueId())) {
			PlayerXpApi.getInstance().getServer().getScheduler().runTaskLater(PlayerXpApi.getInstance(), () -> {
				// send packet
				PlayerData data = storage.get(player.getUniqueId());
				sendPacket(player, data.getInt(PLAYER_LEVEL_KEY), data.getFloat(PLAYER_XP_KEY));
			}, 1L);
		}
	}

	private void sendPacket(@NotNull Player player, float percentage) {
		WrapperPlayServerExperience packet = new WrapperPlayServerExperience();
		packet.setExperienceBar(percentage);
		try {
			PlayerXpApi.getInstance().getProtocolManager().sendServerPacket(player, PacketContainer.fromPacket(packet));
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void sendPacket(@NotNull Player player, int level) {
		WrapperPlayServerExperience packet = new WrapperPlayServerExperience();
		packet.setLevel(level);
		try {
			PlayerXpApi.getInstance().getProtocolManager().sendServerPacket(player, PacketContainer.fromPacket(packet));
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void sendPacket(@NotNull Player player, int level, float percentage) {
		WrapperPlayServerExperience packet = new WrapperPlayServerExperience();
		packet.setExperienceBar(percentage);
		packet.setLevel(level);
		try {
			PlayerXpApi.getInstance().getProtocolManager().sendServerPacket(player, PacketContainer.fromPacket(packet));
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private PlayerDataStorage storage;
	private Set<UUID> activePlayers = ConcurrentHashMap.newKeySet();
	public static final String PLAYER_XP_KEY = "pxpXpValue";
	public static final String PLAYER_LEVEL_KEY = "pxpLevelValue";
}
