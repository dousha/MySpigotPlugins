package tech.dsstudio.minecraft.playerdata.exceptions;

import java.io.File;

public class CannotAccessMediumException extends Error {
	public CannotAccessMediumException(File path) {
		this.file = path;
	}

	public File getPath() {
		return file;
	}

	private File file;
}
