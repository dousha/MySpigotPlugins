package tech.dsstudio.minecraft.attributes.evaluator;

import org.jetbrains.annotations.NotNull;
import tech.dsstudio.RpnStack;

public abstract class Evaluator {
	public Evaluator(EvaluationType type) {
		this.type = type;
	}

	@NotNull
	public EvaluationType getType() {
		return type;
	}

	public abstract void evaluate(@NotNull RpnStack stack);

	private EvaluationType type;
}
