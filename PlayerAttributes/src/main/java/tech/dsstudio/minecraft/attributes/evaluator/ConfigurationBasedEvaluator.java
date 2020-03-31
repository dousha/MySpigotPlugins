package tech.dsstudio.minecraft.attributes.evaluator;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import tech.dsstudio.RpnClosure;
import tech.dsstudio.RpnStack;
import tech.dsstudio.exceptions.CompilationFailedException;
import tech.dsstudio.minecraft.attributes.PlayerAttributeApi;
import tech.dsstudio.minecraft.attributes.utils.Log;

import java.util.List;

/**
 * Normally, you need to wrap this evaluator in {@link ProxyEvaluator}.
 * <p>
 * You can also pass a {@link org.bukkit.configuration.ConfigurationSection}
 * to {@link ProxyEvaluator} to let the proxy create this evaluator for you.
 * <p>
 * The proxy will handle the reload of the evaluator without messing
 * everything up.
 */
public class ConfigurationBasedEvaluator extends Evaluator {
	public ConfigurationBasedEvaluator(EvaluationType type, ConfigurationSection section) {
		super(type);
		// expr: str
		// params: list<str>
		expression = section.getString("expr");
		try {
			closure = PlayerAttributeApi.rpnCompiler.compile(expression);
		} catch (CompilationFailedException ex) {
			Log.error("Cannot compile expression " + expression);
			ex.printStackTrace();
		}
		parameters = section.getStringList("params");
	}

	@Override
	public void evaluate(@NotNull RpnStack stack) {
		if (closure == null) {
			return;
		}
		closure.process(stack);
	}

	public String getExpression() {
		return expression;
	}

	public RpnClosure getClosure() {
		return closure;
	}

	public List<String> getParameters() {
		return parameters;
	}

	private String expression;
	private List<String> parameters;
	private RpnClosure closure;
}
