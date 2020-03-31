package tech.dsstudio.minecraft.attributes.utils;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tech.dsstudio.RpnStack;
import tech.dsstudio.minecraft.attributes.evaluator.ConfigurationBasedEvaluator;
import tech.dsstudio.minecraft.attributes.types.ParameterType;
import tech.dsstudio.minecraft.attributes.types.PlayerAttributes;

import java.util.List;

public class RpnStackPrepare {
	public static RpnStack prepare(@NotNull ConfigurationBasedEvaluator evaluator, @NotNull Player player, @NotNull RpnStack stack) {
		PlayerAttributes attributes = StorageProxy.getPlayerAttributes(player);
		List<String> parameters = evaluator.getParameters();
		parameters.stream().map(String::toUpperCase).map(ParameterType::valueOf).forEach(it -> {
			switch (it) {
				case HEALTH:
					stack.push(player.getHealth());
					break;
				case MAX_HEALTH:
					stack.push(attributes.health);
					break;
				case MAGIC:
					stack.push((double) player.getExp()); // FIXME
					break;
				case MAX_MAGIC:
					stack.push(attributes.magic);
					break;
				case STAMINA:
					stack.push((double) player.getSaturation());
					break;
				case MAX_STAMINA:
					stack.push(attributes.stamina);
					break;
				case ATTACK:
					stack.push(attributes.attack);
					break;
				case DEFENSE:
					stack.push(attributes.defense);
					break;
				case DURABILITY:
					stack.push(attributes.durability);
					break;
				case DEXTERITY:
					stack.push(attributes.dexterity);
					break;
				case INTELLIGENCE:
					stack.push(attributes.intelligence);
					break;
				case CHARISMA:
					stack.push(attributes.charisma);
					break;
				case LUCK:
					player.getAttribute(Attribute.GENERIC_LUCK);
					break;
			}
		});
		return stack;
	}

	public static RpnStack prepare(@NotNull ConfigurationBasedEvaluator evaluator, @NotNull Player player, double initialValue) {
		RpnStack result = new RpnStack();
		result.push(initialValue);
		return prepare(evaluator, player, result);
	}

	public static RpnStack prepare(@NotNull ConfigurationBasedEvaluator evaluator, @NotNull Player player) {
		RpnStack result = new RpnStack();
		return prepare(evaluator, player, result);
	}
}
