package tech.dsstudio.minecraft.attributes;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.io.Serializable;
import java.util.Arrays;

public class PlayerAttributes implements Serializable {
	public double health;
	public double magic;
	public double stamina;
	public double attack;
	public double defense;
	public double durability;
	public double dexterity;
	public double intelligence;
	public double charisma;

	@NotNull
	@Contract("null -> fail; _ -> new")
	public static PlayerAttributes fromConfigurationSection(@NotNull ConfigurationSection section) {
		PlayerAttributes attributes = new PlayerAttributes();
		Arrays.stream(attributes.getClass().getDeclaredFields()).forEach(it -> {
			try {
				it.setDouble(attributes, section.getDouble(it.getName(), 0.0));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		});
		return attributes;
	}
}
