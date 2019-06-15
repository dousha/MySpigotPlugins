package tech.dsstudio.minecraft.dsrt;

import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;

public class RuntimeFileHandler implements Comparable<RuntimeFileHandler> {
	RuntimeFileHandler(CommandSender who, File base, String file) throws IOException {
		this.who = who;
		this.file = new File(base, file);
		if (!this.file.exists()) {
			if (!this.file.createNewFile()) {
				throw new IOException();
			}
		}
	}

	void delete() throws IOException {
		if (isClosed) return;
		if (!file.delete()) {
			throw new IOException();
		}
	}

	void close() {
		this.isClosed = true;
	}

	protected void finalize() {
		Main.getHub().close(this);
	}

	public File getFile() {
		return isClosed ? null : file;
	}

	@Override
	public int compareTo(RuntimeFileHandler handler) {
		return handler == null || isClosed ? 1 : handler.getFile().toURI().compareTo(file.toURI());
	}

	public CommandSender getWho() {
		return who;
	}

	private File file;
	private CommandSender who;

	private boolean isClosed = false;
}
