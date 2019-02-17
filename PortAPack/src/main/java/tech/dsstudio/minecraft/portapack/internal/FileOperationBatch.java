package tech.dsstudio.minecraft.portapack.internal;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FileOperationBatch {
	public FileOperationBatch() {
	}

	private interface FileOperation {
		void doOperation(File file);
	}

	private class FileCreation implements FileOperation {
		public FileCreation(File file) {

		}

		@Override
		public void doOperation(File file) {

		}
	}

	private ConcurrentLinkedQueue<FileOperation> operations = new ConcurrentLinkedQueue<>();
}
