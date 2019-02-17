package tech.dsstudio.minecraft.itemLotto;

import org.bukkit.configuration.ConfigurationSection;

public class LottoEntry implements Comparable<LottoEntry> {
	public LottoEntry(int offset, ConfigurationSection section) {
		this.offset = offset;
		name = section.getString("name");
		rate = section.getDouble("rate");
	}

	public String getName() {
		return name;
	}

	public double getRate() {
		return rate;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public int compareTo(LottoEntry lottoEntry) {
		return lottoEntry == null ? 1 : Double.compare(rate, lottoEntry.rate);
	}

	private int offset;
	private String name;
	private double rate;
}
