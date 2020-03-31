package tech.dsstudio.minecarft.testing.operators;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public enum Operator {
	EQUAL("="),
	NOT_EQUAL("!="),
	GREATER_THAN(">"),
	LESSER_THAN("<"),
	NOT_GREATER_THAN("<="),
	NOT_LESSER_THAN(">="),
	IS_NULL_OR_EMPTY("?"),
	NOT_NULL_OR_EMPTY("!"),
	MATCH("~"),
	NOT_MATCH("!~");

	Operator(String expr) {
		this.expr = expr;
	}

	public String getExpression() {
		return expr;
	}

	public static Operator fromExpression(String expr) {
		return OPERATOR_MAP.get(expr);
	}

	public static final List<Operator> OPERATORS = Arrays.stream(Operator.values()).collect(Collectors.toList());
	public static final List<String> ACCEPTED_OPERATORS = Arrays.stream(Operator.values()).map(it -> it.expr).collect(Collectors.toList());
	public static final Map<String, Operator> OPERATOR_MAP = IntStream.range(0, ACCEPTED_OPERATORS.size()).boxed().collect(Collectors.toMap(ACCEPTED_OPERATORS::get, OPERATORS::get));

	private String expr;
}
