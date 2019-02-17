package tech.dsstudio.minecraft.abstractRepository.repository;

public enum ItemOperationResult {
	CANCEL_EVENT(0),
	REVERT_CONTENT(1),
	ACCEPT_CHANGE(2);

	ItemOperationResult(int i) {
		this.i = i;
	}

	public ItemOperationResult cascade(ItemOperationResult result) {
		if (i < result.i) return this;
		else return result;
	}

	private int i;
}
