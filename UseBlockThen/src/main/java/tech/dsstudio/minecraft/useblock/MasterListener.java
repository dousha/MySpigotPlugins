package tech.dsstudio.minecraft.useblock;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class MasterListener implements Listener {
	@EventHandler
	public void onPlayerRightClickBlock(PlayerInteractEvent event) {
		if (event.hasBlock()) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Block block = event.getClickedBlock();
				BlockFace face = event.getBlockFace();
				Bukkit.getPluginManager().callEvent(new PlayerUsedBlockEvent(event.getPlayer(), block, face));
			}
		}
	}
}
