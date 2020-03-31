package tech.dsstudio.minecarft.testing.subjects;

import org.bukkit.event.Listener;
import tech.dsstudio.minecarft.testing.operators.Operator;

public interface Subject extends Listener {
	boolean test(Operator operator, Object value);
}
