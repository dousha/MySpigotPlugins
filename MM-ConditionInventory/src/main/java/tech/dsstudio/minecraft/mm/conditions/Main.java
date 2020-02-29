package tech.dsstudio.minecraft.mm.conditions;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicConditionLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		saveConfig();
		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onSkillRegistration(MythicConditionLoadEvent e) {
		if (e.getConditionName().equals("itemtest")) {
			e.register(new ConditionInventory(e.getConfig(), getConfig().getBoolean("debug", true)));
		}
	}
}
