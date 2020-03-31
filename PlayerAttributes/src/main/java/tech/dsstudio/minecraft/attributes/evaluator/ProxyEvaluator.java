package tech.dsstudio.minecraft.attributes.evaluator;

import jdk.internal.jline.internal.Nullable;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import tech.dsstudio.RpnStack;

public class ProxyEvaluator extends Evaluator {
	/**
	 * Create a proxy for a evaluator that could be reloaded
	 * during the game.
	 *
	 * @param id       Evaluator ID
	 * @param instance Evaluator instance
	 */
	public ProxyEvaluator(@NotNull String id, @NotNull Evaluator instance) {
		super(instance.getType());
		this.id = id;
		this.evaluator = instance;
	}

	/**
	 * Create a proxy for a evaluator that is based on configuration
	 * files.
	 *
	 * @param section Evaluator configuration
	 */
	public ProxyEvaluator(@NotNull ConfigurationSection section) {
		super(EvaluationType.valueOf(section.getString("type").toUpperCase()));
		this.id = section.getName();
		this.evaluator = new ConfigurationBasedEvaluator(this.getType(), section);
	}

	/**
	 * Attach a new evaluator.
	 * <p>
	 * The old evaluator (if present) will be discarded.
	 * If you want to preserve the old instance, call {@link #detachEvaluator()}
	 * first.
	 *
	 * @param instance New evaluator instance
	 * @see #detachEvaluator()
	 */
	public void attachEvaluator(@NotNull Evaluator instance) {
		this.evaluator = instance;
	}

	/**
	 * Detach the old evaluator. If there is no evaluator attached,
	 * <pre>null</pre> will be returned.
	 *
	 * @return Current evaluator instance
	 */
	@Nullable
	public Evaluator detachEvaluator() {
		Evaluator evaluator = this.evaluator;
		this.evaluator = null;
		return evaluator;
	}

	/**
	 * Evaluate a value.
	 * <p>
	 * If no evaluator is attached, the stack is left intact
	 *
	 * @param stack Initial stack
	 */
	@Override
	public void evaluate(@NotNull RpnStack stack) {
		if (this.evaluator != null) {
			this.evaluator.evaluate(stack);
		}
	}

	public String getId() {
		return id;
	}

	public Evaluator getEvaluator() {
		return evaluator;
	}

	private String id;
	private Evaluator evaluator;
}
