package tech.dsstudio.minecraft.worldtimer.objects;

import org.bukkit.boss.BarColor;

import java.util.List;

public class WorldLimitDescriptor {
	public String title;
	public BarColor color;
	public String name;
	public long limit;
	public boolean lingering;
	public List<WorldEffectDescriptor> debuff;
}
