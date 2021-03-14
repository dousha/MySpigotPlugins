package tech.dsstudio.minecraft.turtle;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class TurtleRegistry {
	TurtleRegistry() {
		turtles = new HashSet<>();
	}

	public Turtle spawnNewTurtle(Player player) {
		Block block = player.getTargetBlock(null, 10);
		Location location = block.getLocation().add(0, 1.1, 0); // XXX
		Turtle turtle = new Turtle(location, player);
		turtles.add(turtle);
		return turtle;
	}

	public void loadTurtles(World world, File base) {
		world.getEntities().stream()
				.filter(entity -> entity instanceof ArmorStand)
				.map(entity -> (ArmorStand) entity)
				.filter(armorStand -> armorStand.getCustomName() != null)
				.forEach(armorStand -> {
					File in = new File(base, armorStand.getCustomName());
					if (in.exists()) {
						Turtle turtle = Turtle.loadFromFile(armorStand, in);
						if (turtle != null) {
							turtles.add(turtle);
						}
					}
				});
	}

	public void saveTurtles(File base) {
		turtles.forEach(turtle -> {
			File out = new File(base, turtle.getName());
			try {
				turtle.saveToFile(out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private final HashSet<Turtle> turtles;
}
