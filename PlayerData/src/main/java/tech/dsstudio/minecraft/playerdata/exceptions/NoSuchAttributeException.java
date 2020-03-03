package tech.dsstudio.minecraft.playerdata.exceptions;

public class NoSuchAttributeException extends Error {
	public NoSuchAttributeException(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	private String key;
}
