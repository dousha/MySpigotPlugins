package tech.dsstudio.minecraft.flow;

import java.util.function.Consumer;

public class Flow<InType, OutType> {
	public Flow(FlowCallable<InType, OutType> callable) {
		this.callable = callable;
	}

	private Flow(FlowCallable<InType, OutType> callable, Flow flowSource) {
		this.callable = callable;
		this.source = flowSource;
	}

	@SuppressWarnings({"unchecked"})
	public <ParameterType, ReturnType> Flow<ParameterType, ReturnType> then(FlowCallable<ParameterType, ReturnType> callable) {
		Flow<ParameterType, ReturnType> f = new Flow<>(callable, source == null ? this : source);
		f.errorHandler = this.errorHandler;
		this.next = f;
		if (this.state == FlowState.EVALUATED) {
			// pick up execution
			this.next.execute(this.result);
		}
		return f;
	}

	public Flow<InType, OutType> orElse(Consumer<Exception> handler) {
		errorHandler = handler;
		if (next != null) {
			next.orElse(handler);
		}
		return this;
	}

	@SuppressWarnings({"unchecked"})
	public <InitType> void runSync(InitType item) {
		source.executeSync(item);
	}

	@SuppressWarnings({"unchecked"})
	public <InitType> Thread run(InitType item) {
		return source.execute(item);
	}

	@SuppressWarnings({"unchecked"})
	public <ParameterType, InitType> void finish(FlowTerminalCallable<ParameterType> callable, InitType initParam) {
		this.<ParameterType, Void>then(param -> {
			callable.call(param);
			return null;
		});
		source.execute(initParam);
	}

	@SuppressWarnings({"unchecked"})
	public <ParameterType, InitType> void finishSync(FlowTerminalCallable<ParameterType> callable, InitType initParam) {
		this.<ParameterType, Void>then(param -> {
			callable.call(param);
			return null;
		});
		source.executeSync(initParam);
	}

	@SuppressWarnings({"unchecked"})
	private void executeSync(InType param) {
		try {
			FlowCallable<InType, OutType> callable = this.callable;
			this.result = callable.call(param);
			if (this.next != null) {
				this.next.executeSync(result);
			}
		} catch (Exception ex) {
			this.errorHandler.accept(ex);
		}
	}

	private Thread execute(InType param) {
		Flow<InType, OutType> me = this;
		Thread worker = new Thread(() -> me.executeSync(param));
		worker.start();
		return worker;
	}

	private FlowCallable<InType, OutType> callable;
	private OutType result = null;
	private Flow source = null;
	private Flow next = null;
	private FlowState state = FlowState.PENDING;
	private Consumer<Exception> errorHandler = Throwable::printStackTrace;
}
