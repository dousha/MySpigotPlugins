package tech.dsstudio.minecraft.mm.conditions;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.conditions.IEntityCondition;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConditionInventory extends SkillCondition implements IEntityCondition {
	public ConditionInventory(MythicLineConfig config, boolean debug) {
		super(config.getLine());
		this.mode = config.getString("mode", "nlt");
		this.pos = config.getInteger("pos", -1);
		this.material = Material.valueOf(config.getString("mat"));
		this.count = config.getInteger("count", 1);
		this.name = config.getString("name", null);
		this.debug = debug;
		if (this.debug) {
			System.err.println("Material is configured to " + this.material.toString());
		}
	}

	@Override
	public boolean check(AbstractEntity entity) {
		Entity adaptedEntity = BukkitAdapter.adapt(entity);
		if (adaptedEntity instanceof Player) {
			if (this.debug) {
				System.err.println("itemtest condition!");
			}
			AtomicBoolean found = new AtomicBoolean(false);
			ItemStack[] contents = ((Player) adaptedEntity).getInventory().getContents();
			if (this.pos == -1) {
				Arrays.stream(contents).forEach(item -> {
					if (!found.get()) {
						found.set(isMaterialMatch(item) && isAmountMatch(item) && isNameMatch(item));
					}
				});
				return found.get();
			} else {
				ItemStack item = contents[this.pos];
				if (item == null || item.getType().equals(Material.AIR)) {
					return false;
				} else {
					return item.getType().equals(this.material) && isAmountMatch(item) && isNameMatch(item);
				}
			}
		} else {
			return false;
		}
	}

	private boolean isMaterialMatch(ItemStack item) {
		if (this.material.equals(Material.AIR)) {
			return item == null || item.getType().equals(Material.AIR);
		} else {
			if (item == null || item.getType().equals(Material.AIR)) {
				return false;
			}
			return item.getType().equals(this.material);
		}
	}

	private boolean isAmountMatch(ItemStack item) {
		switch (this.mode) {
			case "eq":
				return item.getAmount() == this.count;
			case "ne":
				return item.getAmount() != this.count;
			case "gt":
				return item.getAmount() > this.count;
			case "lt":
				return item.getAmount() < this.count;
			case "ngt":
				return item.getAmount() <= this.count;
			case "nlt":
				return item.getAmount() >= this.count;
			default:
				throw new IllegalArgumentException("Modes can only be either 'eq', 'ne', 'gt', 'lt', 'ngt' or 'nlt'");
		}
	}

	private boolean isNameMatch(ItemStack item) {
		if (this.name == null) {
			return true;
		}
		ItemMeta meta = item.getItemMeta();
		if (meta == null) {
			return false;
		}
		return meta.hasDisplayName() && meta.getDisplayName().equals(this.name);
	}
	private String mode;
	private int pos;
	private Material material;
	private int count;
	private String name;
	private boolean debug;
}
