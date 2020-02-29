package tech.dsstudio.minecraft.shell.reflector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicReference;

public class DirectReflector implements Reflector {
	@Override
	public boolean test() {
		try {
			Runtime runtime = Runtime.getRuntime();
			if (runtime == null) {
				return false;
			}
			Process process = runtime.exec("echo 1");
			process.waitFor();
			int exitCode = process.exitValue();
			return exitCode == 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String execute(String[] cmd) {
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			InputStreamReader reader = new InputStreamReader(p.getInputStream());
			BufferedReader br = new BufferedReader(reader);
			StringBuilder sb = new StringBuilder();
			AtomicReference<StringBuilder> ref = new AtomicReference<>(sb);
			br.lines().forEach(it -> ref.get().append(it).append("\r\n"));
			return sb.toString();
		} catch (IOException ex) {
			return ex.getCause().toString();
		}
	}
}
