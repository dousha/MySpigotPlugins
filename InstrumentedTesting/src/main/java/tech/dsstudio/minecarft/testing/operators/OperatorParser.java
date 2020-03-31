package tech.dsstudio.minecarft.testing.operators;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.dsstudio.minecarft.testing.utils.Tuple;

public class OperatorParser {
	@Nullable
	public static Tuple<Operator, Integer> parseString(@NotNull String str) {
		if (str.length() < 1) {
			return null;
		}
		int i = 0, j;
		String guess;
		char c = str.charAt(i);
		while (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
			++i;
			c = str.charAt(i);
		}
		j = i + 1;
		do {
			guess = str.substring(i, j);
			++j;
		} while (Operator.ACCEPTED_OPERATORS.contains(guess));
		guess = str.substring(i, j - 1).trim();
		j = i + guess.length();
		Operator operator = Operator.fromExpression(guess);
		if (operator == null) {
			return null;
		}
		return new Tuple<>(operator, j);
	}
}
