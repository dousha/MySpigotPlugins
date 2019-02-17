package tech.dsstudio.minecraft.arsenal;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import tech.dsstudio.minecraft.abstractRepository.RepositoryManager;

public class Main extends JavaPlugin {
	@Override
	public void onEnable() {
		saveDefaultConfig();
		repository = new ArsenalRepository();
		itemRepository = repository.getRepository();
		RepositoryManager.registerRepository(repository, this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch (command.getName()) {
			case "aro":
				if (sender instanceof Player) {
					Player player = (Player) sender;
					player.openInventory(repository.getInventory(player.getUniqueId()));
				} else {
					sender.sendMessage("Cannot open inventory: you are not a player");
				}
				break;
			case "arr":
				repository.notifyUpdate(getConfig());
				itemRepository.reload();
				sender.sendMessage("Reloaded configuration");
				break;
			case "ara":
				if (sender instanceof Player) {
					Player player = (Player) sender;
					ItemStack item = player.getInventory().getItemInMainHand();
					if (item == null || item.getType().equals(Material.AIR)) {
						sender.sendMessage("Hold something in your hand please");
					} else {
						if (args.length != 1) {
							return false;
						}
						String perm = args[0];
						itemRepository.addItem(perm, item);
						sender.sendMessage("Item added");
					}
				} else {
					sender.sendMessage("Cannot add item: you are not a player");
				}
				break;
			case "ard":
				if (args.length != 1) {
					return false;
				} else {
					itemRepository.deleteItem(args[0]);
					sender.sendMessage("Item deleted");
				}
				break;
			default:
				return false;
		}
		return true;
	}

	private ArsenalRepository repository;
	private ItemRepository itemRepository;
}
