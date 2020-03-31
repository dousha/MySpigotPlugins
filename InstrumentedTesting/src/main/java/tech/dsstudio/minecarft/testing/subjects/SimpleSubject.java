package tech.dsstudio.minecarft.testing.subjects;

import org.bukkit.event.EventHandler;
import tech.dsstudio.minecarft.testing.operators.Operator;

public abstract class SimpleSubject implements Subject {
	public abstract boolean test(Operator operator, Object object);

	@EventHandler
	public abstract void onSubjectRequest();
}
