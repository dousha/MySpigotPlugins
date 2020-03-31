package tech.dsstudio.minecarft.testing.subjects;

import tech.dsstudio.minecarft.testing.operators.Operator;

import java.util.List;
import java.util.Map;

public abstract class ComplexSubject implements Subject {
	public abstract boolean test(Operator operator, List<Map<String, Object>> value);
}
