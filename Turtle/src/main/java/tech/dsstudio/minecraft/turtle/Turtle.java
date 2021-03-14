package tech.dsstudio.minecraft.turtle;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Turtle implements InventoryHolder {
	/**
	 * Create a turtle with default permissions.
	 */
	public Turtle(Location location) {
		this.location = location;
		inventory = new TurtleInventory(this);
		facing = TurtleFacing.fromYaw(location.getYaw());
		spawn();
	}

	/**
	 * Create a turtle with inherited permission.
	 *
	 * @param player Player, usually the owner.
	 */
	public Turtle(Location location, Player player) {
		Turtle entity = new Turtle(location);
		entity.inheritPermissions(player);
		entity.setOwner(player);
	}

	private Turtle() { }

	public void turnLeft() {
		facing = TurtleFacingJumpTable.turnLeft(facing);
		updatePosture();
	}

	public void turnRight() {
		facing = TurtleFacingJumpTable.turnRight(facing);
		updatePosture();
	}

	public boolean moveForward() {
		return teleportTo(puppet.getLocation().add(facing.toVector()));
	}

	public boolean moveBackward() {
		return teleportTo(puppet.getLocation().subtract(facing.toVector()));
	}

	public boolean ascend() {
		return teleportTo(puppet.getLocation().add(0, 1, 0));
	}

	public boolean descend() {
		return teleportTo(puppet.getLocation().add(0, -1, 0));
	}

	public boolean teleportTo(@NotNull Location location) {
		if (canMoveTo(location)) {
			puppet.teleport(location);
			return true;
		} else {
			return false;
		}
	}

	public boolean breakFacingBlock() {
		Block target = getTargetBlock(facing.getX(), facing.getY(), facing.getZ());
		return canBreakOrPutBlock(target) && breakBlock(target);
	}

	public boolean breakTopBlock() {
		Block target = getTargetBlock(0, 1, 0);
		return canBreakOrPutBlock(target) && breakBlock(target);
	}

	public boolean breakBottomBlock() {
		Block target = getTargetBlock(0, -1, 0);
		return canBreakOrPutBlock(target) && breakBlock(target);
	}

	public boolean placeFacingBlock() {
		Block target = getTargetBlock(facing.getX(), facing.getY(), facing.getZ());
		return canBreakOrPutBlock(target) && placeBlock(target);
	}

	public boolean placeTopBlock() {
		Block target = getTargetBlock(0, 1, 0);
		return canBreakOrPutBlock(target) && placeBlock(target);
	}

	public boolean placeBottomBlock() {
		Block target = getTargetBlock(0, -1, 0);
		return canBreakOrPutBlock(target) && placeBlock(target);
	}

	/**
	 * Collect dropped items in radius of a block, like a hopper.
	 *
	 * @return Items collected. If no items were collected, an empty set will be returned.
	 */
	public @NotNull Set<ItemStack> collectItemsAround() {
		HashSet<ItemStack> items = new HashSet<>();
		puppet.getWorld().getNearbyEntities(BoundingBox.of(puppet.getLocation(), 1.2, 1.2, 1.2), item -> item instanceof Item && canPickupItem((Item) item)).forEach(item -> {
			items.add(((Item) item).getItemStack());
			item.remove();
		});
		return items;
	}

	/**
	 * Drop item holding in hand.
	 *
	 * @return true if an item is cast, false otherwise.
	 */
	public boolean tossItemInHand() {
		if (equipment == -1 || !canLitterItem()) {
			return false;
		}
		ItemStack itemInHand = inventory.getInventory().getItem(equipment);
		if (itemInHand == null || itemInHand.getType().equals(Material.AIR)) {
			return false;
		}
		puppet.getWorld().dropItemNaturally(puppet.getLocation(), itemInHand);
		inventory.getInventory().setItem(equipment, new ItemStack(Material.AIR));
		equipment = -1;
		return true;
	}

	/**
	 * Equip the best tool currently available in turtle inventory.
	 *
	 * @return true if found an equipment, false otherwise.
	 */
	public boolean equip() {
		return false;
	}

	/**
	 * Equip the given material.
	 *
	 * @param material Item type to be hold in hand
	 * @return true if found given item, false otherwise.
	 */
	public boolean equip(Material material) {
		return false;
	}

	public boolean equip(int index) {
		return false;
	}

	public void saveToFile(File file) throws IOException {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("turtleOwner", owner.getUniqueId().toString());
		config.set("puppetName", puppet.getCustomName());
		config.set("turtleEquipment", equipment);
		inventory.saveInventory(config);
		config.save(file);
	}

	public static Turtle loadFromFile(ArmorStand puppet, File file) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		String turtleOwner = config.getString("turtleOwner");
		if (turtleOwner == null) {
			return null;
		}
		OfflinePlayer owner = Bukkit.getServer().getOfflinePlayer(UUID.fromString(turtleOwner));
		Turtle turtle = new Turtle();
		turtle.inventory = TurtleInventory.loadInventory(turtle, config);
		turtle.owner = owner;
		if (puppet.getCustomName() == null || !puppet.getCustomName().equals(config.getString("puppetName"))) {
			return null;
		}
		turtle.puppet = puppet;
		turtle.facing = TurtleFacing.fromYaw(puppet.getLocation().getYaw());
		turtle.equipment = config.getInt("turtleEquipment", -1);
		return turtle;
	}

	public void setOwner(OfflinePlayer who) {
		owner = who;
	}

	public OfflinePlayer getOwner() {
		return owner;
	}

	public ArmorStand getPuppet() {
		return puppet;
	}

	public String getName() {
		return puppet.getCustomName();
	}

	@NotNull
	@Override
	public Inventory getInventory() {
		return inventory.getInventory();
	}

	private void inheritPermissions(Player who) {
		Set<PermissionAttachmentInfo> permissions = who.getEffectivePermissions();
		permissions.forEach(it -> {
			PermissionAttachment attachment = it.getAttachment();
			if (attachment != null) {
				it.getAttachment().getPermissions().forEach((k, v) -> puppet.addAttachment(it.getAttachment().getPlugin(), k, v));
			}
		});
	}

	private void spawn() {
		World world = location.getWorld();
		if (world != null) {
			puppet = location.getWorld().spawn(location, ArmorStand.class);
			puppet.setMarker(true);
			puppet.setVisible(false);
			puppet.setInvulnerable(true);
			puppet.setCanPickupItems(false);
			puppet.setHealth(1.0);
			puppet.setSmall(true);
			puppet.setCollidable(false);
			puppet.setGravity(false);
			puppet.setBasePlate(false);
			puppet.setCustomNameVisible(false);
			puppet.setRemoveWhenFarAway(false);
			puppet.setChestplate(new ItemStack(Material.COMMAND_BLOCK, 1));
			puppet.setCustomName(UUID.randomUUID().toString());
		} else {
			throw new IllegalArgumentException("Location must be a real location!");
		}
	}

	private void despawn() {
		if (puppet != null) {
			puppet.damage(99999999);
		}
	}

	private void updatePosture() {
		puppet.setBodyPose(facing.toEulerAngle());
	}

	private Block getTargetBlock(double x, double y, double z) {
		return puppet.getWorld().getBlockAt(puppet.getLocation().add(x, y, z));
	}

	private boolean breakBlock(Block block) {
		if (block.isEmpty()) {
			return false;
		}
		if (block.isLiquid()) {
			if (equipment == -1) {
				return false;
			} else {
				ItemStack itemInHand = inventory.getInventory().getItem(equipment);
				if (itemInHand == null) {
					return false;
				} else {
					if (itemInHand.getType().equals(Material.BUCKET)) {
						ItemStack newItem;
						if (block.getType().equals(Material.LAVA)) {
							newItem = new ItemStack(Material.LAVA_BUCKET, 1);
						} else {
							newItem = new ItemStack(Material.WATER_BUCKET, 1);
						}
						inventory.getInventory().setItem(equipment, newItem);
						block.setType(Material.AIR);
						return true;
					} else {
						return false;
					}
				}
			}
		}
		if (equipment != -1) {
			ItemStack itemInHand = inventory.getInventory().getItem(equipment);
			if (itemInHand == null) {
				block.breakNaturally();
			} else {
				block.breakNaturally(itemInHand);
			}
		} else {
			block.breakNaturally();
		}
		return true;
	}

	private boolean placeBlock(Block block) {
		if (equipment == -1 || !block.getType().equals(Material.AIR)) {
			return false;
		}
		ItemStack itemInHand = inventory.getInventory().getItem(equipment);
		if (itemInHand == null || itemInHand.getType().equals(Material.AIR)) {
			return false;
		}
		if (itemInHand.getType().equals(Material.LAVA_BUCKET)) {
			itemInHand.setType(Material.BUCKET);
			block.setType(Material.LAVA);
		} else if (itemInHand.getType().equals(Material.WATER_BUCKET)) {
			itemInHand.setType(Material.BUCKET);
			block.setType(Material.WATER);
		} else {
			if (!itemInHand.getType().isBlock()) {
				return false;
			}
			itemInHand.setAmount(itemInHand.getAmount() - 1);
			block.setType(itemInHand.getType());
		}
		return true;
	}

	private boolean canMoveTo(@NotNull World world, double x, double y, double z) {
		ResidenceApi api = Main.getResidenceApi();
		if (api == null) {
			return true;
		} else {
			Location location = new Location(world, x, y, z);
			ClaimedResidence residence = Residence.getInstance().getResidenceManager().getByLoc(location);
			if (residence != null) {
				ResidencePermissions permissions = residence.getPermissions();
				String ownerName = owner.getName();
				return permissions.playerHas(ownerName, Flags.move, true);
			} else {
				return true;
			}
		}
	}

	private boolean canMoveTo(@NotNull Location location) {
		return location.getWorld() != null && canMoveTo(location.getWorld(), location.getX(), location.getY(), location.getZ());
	}

	private boolean canBreakOrPutBlock(@NotNull Block block) {
		ResidenceApi api = Main.getResidenceApi();
		if (api == null) {
			return true;
		} else {
			ClaimedResidence residence = Residence.getInstance().getResidenceManager().getByLoc(block.getLocation());
			if (residence == null) {
				return true;
			} else {
				ResidencePermissions permissions = residence.getPermissions();
				String ownerName = owner.getName();
				return permissions.playerHas(ownerName, Flags.build, true);
			}
		}
	}

	private boolean canLitterItem() {
		ResidenceApi api = Main.getResidenceApi();
		if (api == null) {
			return true;
		} else {
			ClaimedResidence residence = Residence.getInstance().getResidenceManager().getByLoc(puppet.getLocation());
			if (residence == null) {
				return true;
			} else {
				ResidencePermissions permissions = residence.getPermissions();
				String ownerName = owner.getName();
				return permissions.playerHas(ownerName, Flags.itemdrop, true);
			}
		}
	}

	private boolean canPickupItem(Item item) {
		if (item.getPickupDelay() > 0) {
			return false;
		}
		ResidenceApi api = Main.getResidenceApi();
		if (api == null) {
			return true;
		} else {
			ClaimedResidence residence = Residence.getInstance().getResidenceManager().getByLoc(item.getLocation());
			if (residence == null) {
				return true;
			} else {
				ResidencePermissions permissions = residence.getPermissions();
				String ownerName = owner.getName();
				return permissions.playerHas(ownerName, Flags.itempickup, true);
			}
		}
	}

	private Location location;
	private TurtleFacing facing;
	private ArmorStand puppet = null;
	private TurtleInventory inventory;
	private OfflinePlayer owner;
	private int equipment;
}
