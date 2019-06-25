package tech.dsstudio.minecraft.dialog.formatters;

import java.util.function.Predicate;

public class BooleanFormatter implements Predicate<String> {
	@Override
	public boolean test(String s) {
		return s.trim().matches("[yYnNtTfF]") || s.trim().matches("[Yy]es|[Nn]o") || s.trim().matches("[Tt]rue|[Ff]alse");
	}
}
