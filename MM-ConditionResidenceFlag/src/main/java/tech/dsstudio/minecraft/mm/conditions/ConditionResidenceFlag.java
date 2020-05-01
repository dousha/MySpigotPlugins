package tech.dsstudio.minecraft.mm.conditions;

import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.conditions.IEntityCondition;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ConditionResidenceFlag extends SkillCondition implements IEntityCondition {
	public ConditionResidenceFlag(MythicLineConfig config, boolean debug) {
		super(config.getLine());
		this.flag = config.getString("flag", null);
		this.flag = config.getString("f", this.flag);
	}

	@Override
	public boolean check(AbstractEntity abstractEntity) {
		if (flag == null) {
			return false;
		}
		if (abstractEntity instanceof Player) {
			Player player = (Player) abstractEntity;
			Location location = player.getLocation();
			ClaimedResidence residence = ResidenceApi.getResidenceManager().getByLoc(location);
			if (residence == null) {
				return false;
			}
			ResidencePermissions permissions = residence.getPermissions();
			return permissions.playerHas(player, flag, false);
		}
		return false;
	}

	private String flag;
}
