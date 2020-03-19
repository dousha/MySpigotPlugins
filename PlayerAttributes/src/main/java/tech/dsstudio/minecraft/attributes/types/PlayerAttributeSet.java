package tech.dsstudio.minecraft.attributes;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class PlayerAttributeSet implements Serializable {
	public String friendlyName;
	public PlayerAttributes attributes;

	@NotNull
	@Contract("null -> fail; _ -> new")
	public static PlayerAttributeSet fromConfigurationSection(@NotNull ConfigurationSection section) {
		PlayerAttributeSet set = new PlayerAttributeSet();
		set.friendlyName = section.getString("friendlyName");
		set.attributes = PlayerAttributes.fromConfigurationSection(section);
		return set;
	}
}
