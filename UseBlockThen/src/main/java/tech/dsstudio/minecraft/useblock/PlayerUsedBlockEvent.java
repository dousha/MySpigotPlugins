package tech.dsstudio.minecraft.useblock;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerUsedBlockEvent extends PlayerEvent {
	public PlayerUsedBlockEvent(Player who, Block block, BlockFace face) {
		super(who);
		this.block = block;
		this.face = face;
	}

	public Block getBlock() {
		return block;
	}

	public BlockFace getFace() {
		return face;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	private static final HandlerList HANDLERS = new HandlerList();
	private final Block block;
	private final BlockFace face;
}
