package tech.dsstudio.minecraft.dialog.formatters;

import java.util.function.Predicate;

public class AlphaNumericFormatter implements Predicate<String> {
	@Override
	public boolean test(String s) {
		return s.trim().matches("^[a-zA-Z0-9]+$");
	}
}
