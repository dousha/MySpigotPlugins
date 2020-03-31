package tech.dsstudio.minecraft.attributes.evaluator;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tech.dsstudio.RpnStack;
import tech.dsstudio.minecraft.attributes.utils.RpnStackPrepare;

import java.util.ArrayList;
import java.util.HashMap;

public class MasterEvaluator {
	public void addEvaluator(@NotNull Evaluator evaluator) {
		evaluators.computeIfAbsent(evaluator.getType(), (k) -> new ArrayList<>()).add(evaluator);
	}

	public void removeEvaluator(@NotNull Evaluator evaluator) {
		evaluators.computeIfAbsent(evaluator.getType(), (k) -> new ArrayList<>()).remove(evaluator);
	}

	public double evaluate(@NotNull EvaluationType type, double initialValue, @NotNull Player player) {
		RpnStack stack = new RpnStack();
		stack.push(initialValue);
		evaluators.computeIfAbsent(type, (k) -> new ArrayList<>()).forEach(evaluator -> {
			if (evaluator instanceof ProxyEvaluator) {
				Evaluator innerEvaluator = ((ProxyEvaluator) evaluator).getEvaluator();
				if (innerEvaluator instanceof ConfigurationBasedEvaluator) {
					// config based
					RpnStackPrepare.prepare((ConfigurationBasedEvaluator) innerEvaluator, player, stack);
					innerEvaluator.evaluate(stack);
				} else {
					// self managed
					evaluator.evaluate(stack);
				}
			} else if (evaluator instanceof ConfigurationBasedEvaluator) {
				// wried flex, but ok
				RpnStackPrepare.prepare((ConfigurationBasedEvaluator) evaluator, player, stack);
				evaluator.evaluate(stack);
			} else {
				// self managed
				evaluator.evaluate(stack);
			}
		});
		return stack.pop();
	}

	private HashMap<EvaluationType, ArrayList<Evaluator>> evaluators = new HashMap<>();
}
