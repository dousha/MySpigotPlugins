package tech.dsstudio.minecraft.flow;

public interface FlowCallable<InType, OutType> {
	OutType call(InType param) throws Exception;
}
