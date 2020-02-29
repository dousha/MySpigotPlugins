package tech.dsstudio.minecraft.shell.reflector;

public interface Reflector {
	boolean test();
	String execute(String[] cmd);
}
