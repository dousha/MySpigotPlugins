package tech.dsstudio.minecraft.itemLotto;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class Help {
	public void getHelp(CommandSender player, String topic) {
		if (topic == null) {
			player.sendMessage("可用命令：");
			help.keySet().forEach(player::sendMessage);
		} else {
			HelpEntry entry = help.get(topic);
			if (entry == null) {
				player.sendMessage("没有这个命令");
			} else {
				entry.print(player);
			}
		}
	}

	private class HelpEntry {
		public HelpEntry(String usage, String func, String perm) {
			this.usage = usage;
			this.func = func;
			this.perm = perm;
		}

		public void print(CommandSender player) {
			ChatColor usageColor = player.hasPermission(perm) ? ChatColor.YELLOW : ChatColor.GRAY;
			player.sendMessage(new String[] {
					"用法: " + usageColor + usage,
					"功能: " + func,
					"权限: " + perm
			});
		}

		private String usage;
		private String func;
		private String perm;
	}

	private HashMap<String, HelpEntry> help = new HashMap<String, HelpEntry>() {
		{
			put("ilvo", new HelpEntry("/ilvo", "打开金币抽奖菜单", "itemlotto.use.vault"));
			put("ilpo", new HelpEntry("/ilpo", "打开点券抽奖菜单", "itemlotto.use.playerpoints"));
			put("ilr", new HelpEntry("/ilr", "重载配置文件", "itemlotto.reload"));
			put("ils", new HelpEntry("/ils <物品名>", "将手中物品保存为给定物品名用于奖池配置", "itemlotto.save"));
			put("ild", new HelpEntry("/ild <物品名>", "从奖池中删除给定物品", "itemlotto.delete"));
			put("ilp", new HelpEntry("/ilp", "打印当前奖池中的所有物品名", "itemlotto.print"));
			put("ilh", new HelpEntry("/ilh [命令名]", "显示帮助", "itemlotto.help"));
			put("ds", new HelpEntry("COPYRIGHT (C) dsStudio 2019", "你成功找到了这个插件的彩蛋！请将 ds-minecraft-plugin-l0tt0 发送至 dsstudio@qq.com", "itemlotto.easter"));
			put("ds0", new HelpEntry("拆插件是个蛋疼的工作，弃了吧", "该插件的授权协议是 LGPLv3, 如果你需要改这个插件，请至少发一封信，谢谢", "itemlotto.easter.reverse"));
		}
	};
}
