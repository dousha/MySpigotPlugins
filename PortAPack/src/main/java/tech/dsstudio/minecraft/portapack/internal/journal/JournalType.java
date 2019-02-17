package tech.dsstudio.minecraft.portapack.internal.journal;

public enum JournalType {
	DEPOSIT(1),
	WITHDRAW(2);

	JournalType(int i) {
		this.i = i;
	}

	public static JournalType fromInt(int i) {
		switch (i) {
			case 1:
				return DEPOSIT;
			case 2:
				return WITHDRAW;
			default:
				throw new IllegalArgumentException();
		}
	}

	public int getValue() {
		return i;
	}

	private int i;
}
