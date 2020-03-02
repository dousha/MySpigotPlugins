package tech.dsstudio.minecraft.shell.bouncer;

public abstract class Bouncer extends Thread {
	public Bouncer(String ip, String port) {
		this.ip = ip;
		this.port = port;
	}

	@Override
	public abstract void run();

	protected String ip;
	protected String port;
	protected boolean success;
}
