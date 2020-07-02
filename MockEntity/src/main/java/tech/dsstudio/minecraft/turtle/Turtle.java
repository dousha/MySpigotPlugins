package tech.dsstudio.minecraft.mockentity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class MockEntity {
	/**
	 * Create a mock entity with default permissions.
	 */
	public MockEntity(Location location) {
		this.location = location;
		spawn();
	}

	/**
	 * Create a mock entity with inherited permission.
	 *
	 * @param player Player, usually the owner.
	 */
	public MockEntity(Location location, Player player) {
		MockEntity entity = new MockEntity(location);
		entity.inheritPermissions(player);
	}

	public boolean moveForward() {
		return false;
	}

	public void turnLeft() {

	}

	public void turnRight() {

	}

	public boolean moveBackward() {
		return false;
	}

	public boolean breakFacingBlock() {
		return false;
	}

	public boolean collectItemsAround() {
		return false;
	}

	public ArmorStand getPuppet() {
		return puppet;
	}

	private void inheritPermissions(Player who) {

	}

	private void spawn() {
		World world = location.getWorld();
		if (world != null) {
			puppet = location.getWorld().spawn(location, ArmorStand.class);
		} else {
			throw new IllegalArgumentException("Location must be a real location!");
		}
	}

	private void despawn() {
		if (puppet != null) {
			puppet.damage(99999999);
		}
	}

	private Location location;
	private Vector facing;
	private ArmorStand puppet = null;
}
