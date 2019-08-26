package tech.dsstudio.minecraft.flow;

public interface FlowTerminalCallable<InType> {
	void call(InType param) throws Exception;
}
