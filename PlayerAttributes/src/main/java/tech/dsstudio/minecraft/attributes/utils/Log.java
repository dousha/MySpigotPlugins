package tech.dsstudio.minecraft.attributes.utils;

public class Log {
	public static void debug(String msg) {
		if (allowDebug) {
			System.out.println(msg);
		}
	}

	public static void error(String msg) {
		System.err.println(msg);
	}

	public static void setDebug(boolean b) {
		allowDebug = b;
	}

	private static boolean allowDebug = true;
}
