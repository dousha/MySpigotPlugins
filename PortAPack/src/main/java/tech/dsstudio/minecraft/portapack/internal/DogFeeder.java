package tech.dsstudio.minecraft.portapack.internal;

public class DogFeeder implements Runnable {
	public DogFeeder() {
		lastRespondTime = System.currentTimeMillis();
	}

	@Override
	public void run() {
		long now = System.currentTimeMillis();
		if (now - lastRespondTime > tolerance) {
			System.out.println("[PortAPack:DogFeeder] Server lagging!");
		}
		lastRespondTime = now;
	}

	public boolean check() {
		return System.currentTimeMillis() - lastRespondTime > tolerance;
	}

	public void setTolerance(long tolerance) {
		this.tolerance = tolerance;
	}

	private long lastRespondTime;
	private long tolerance = 1000;
}
