package tech.dsstudio.minecraft.armorHide;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.UUID;

public class Main extends JavaPlugin {
	@Override
	public void onEnable() {
		pm = ProtocolLibrary.getProtocolManager();
		saveDefaultConfig();
		lore = getConfig().getString("lore").replace('&', '\u00a7');
		pipe = new PacketPipe(this);
		pm.addPacketListener(pipe);
	}

	@Override
	public void onDisable() {

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch (command.getName()) {
			case "ahr":
				lore = getConfig().getString("lore");
				sender.sendMessage("已重载");
				break;
			case "ahon":
				if (sender instanceof Player) {
					active.add(((Player) sender).getUniqueId());
					sender.sendMessage("盔甲已隐藏");
				} else {
					sender.sendMessage("该命令仅限玩家使用");
				}
				break;
			case "ahoff":
				if (sender instanceof Player) {
					active.remove(((Player) sender).getUniqueId());
					sender.sendMessage("盔甲已显示");
				} else {
					sender.sendMessage("该命令仅限玩家使用");
				}
				break;
			default:
				return false;
		}
		return true;
	}

	private class PacketPipe extends PacketAdapter {
		public PacketPipe(JavaPlugin plugin) {
			super(plugin, ListenerPriority.HIGH, PacketType.Play.Server.ENTITY_EQUIPMENT);
		}

		@Override
		public void onPacketSending(PacketEvent event) {
			if (active.contains(event.getPlayer().getUniqueId())) {
				event.setCancelled(true);
			} else {
				PacketContainer container = event.getPacket();
				StructureModifier<ItemStack> items = container.getItemModifier();
				ItemStack item = items.read(0);
				if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().getLore().contains(lore)) {
					event.setCancelled(true);
				}
			}
		}
	}

	private ProtocolManager pm;
	private PacketPipe pipe;
	private String lore;
	private HashSet<UUID> active = new HashSet<>();
}
