package tech.dsstudio.minecraft.worldtimer.objects;

import org.bukkit.potion.PotionEffect;

public class WorldEffectDescriptor {
	public PotionEffect effect;
	public int interval = 20;
	public int offset = 0;

	@Override
	public String toString() {
		return "WorldEffectDescriptor(effect=" + effect.toString() + ", interval=" + interval + ", offset=" + offset + ")";
	}
}
