package tech.dsstudio.minecraft.portapack.internal.journal;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import tech.dsstudio.minecraft.portapack.internal.Instances;
import tech.dsstudio.minecraft.portapack.internal.pack.PortablePackSnapshot;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class JournalEntry implements Serializable {
	public JournalEntry(OfflinePlayer owner, JournalType type, int inventoryIndex, int itemIndex, ItemStack item) {
		this.who = owner;
		this.type = type;
		this.inventoryIndex = inventoryIndex;
		this.itemIndex = itemIndex;
		this.item = item;
	}

	public PreparedStatement prepareStatement(PreparedStatement statement) {
		YamlConfiguration configuration = new YamlConfiguration();
		configuration.set("item", item);
		try {
			statement.setString(1, who.getName());
			statement.setInt(2, type.getValue());
			statement.setInt(3, inventoryIndex);
			statement.setInt(4, itemIndex);
			statement.setString(5, configuration.saveToString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return statement;
	}

	public String toString() {
		return String.format("#%d %s@%s: %s, %d.%d: %s x %d", id, time.toString(), who.getName(), type == JournalType.DEPOSIT ? "IN" : "OUT", inventoryIndex, itemIndex, item.getType().toString(), item.getAmount());
	}

	public static JournalEntry fromRecord(ResultSet record) {
		try {
			OfflinePlayer player = Instances.main.getServer().getOfflinePlayer(record.getString(1));
			JournalType type = JournalType.fromInt(record.getInt(2));
			int inventoryIndex = record.getInt(3);
			int itemIndex = record.getInt(4);
			YamlConfiguration config = new YamlConfiguration();
			config.loadFromString(record.getString(5));
			ItemStack item = config.getItemStack("item");
			JournalEntry entry = new JournalEntry(player, type, inventoryIndex, itemIndex, item);
			entry.id = record.getInt(6);
			entry.time = record.getTimestamp(7);
			return entry;
		} catch (SQLException | InvalidConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void applyToPack(PortablePackSnapshot pack) {
		try {
			if (this.type == JournalType.DEPOSIT) {
				pack.getInventory(inventoryIndex).setItem(itemIndex, item);
			} else {
				pack.getInventory(inventoryIndex).setItem(itemIndex, null);
			}
		} catch (NullPointerException npt) {
			System.err.println("[WARN] MAX PAGE WAS LESSER THAN A HISTORY VALUE! Tried to get page " + inventoryIndex + " but it was not possible");
		}
	}

	private OfflinePlayer who;
	private JournalType type;
	private int inventoryIndex, itemIndex;
	private ItemStack item;

	private int id;
	private Timestamp time;
}
