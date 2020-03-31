package tech.dsstudio.minecraft.attributes.types;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Arrays;

public class PlayerAttributes implements Serializable {
	public double health; // max health
	public double magic; // max magic
	public double stamina; // max stamina
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

	@Override
	public String toString() {
		return "PlayerAttributes{" +
				"health=" + health +
				", magic=" + magic +
				", stamina=" + stamina +
				", attack=" + attack +
				", defense=" + defense +
				", durability=" + durability +
				", dexterity=" + dexterity +
				", intelligence=" + intelligence +
				", charisma=" + charisma +
				'}';
	}
}
