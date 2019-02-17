package tech.dsstudio.minecraft.portapack.internal.journal;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class Journal {
	public Journal(ConfigurationSection config) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String host = config.getString("host");
			String port = config.getString("port");
			String username = config.getString("username");
			String password = config.getString("password");
			String database = config.getString("name");
			Connection konn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port, username, password);
			konn.prepareCall("CREATE DATABASE IF NOT EXISTS " + database).executeUpdate();
			conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			init();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	public void logTransaction(Player who, JournalType type, int inventoryIndex, int itemIndex, ItemStack item) {
		try {
			JournalEntry entry = new JournalEntry(who, type, inventoryIndex, itemIndex, item);
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO `journal` (name, type, inventoryIndex, itemIndex, item) VALUES (?, ?, ?, ?, ?)");
			entry.prepareStatement(stmt).executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void printRecentJournals(CommandSender sender, String player, int count) {
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `journal` WHERE name = ? ORDER BY time DESC LIMIT ?");
			stmt.setString(1, player);
			stmt.setInt(2, count);
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				sender.sendMessage(Objects.requireNonNull(JournalEntry.fromRecord(result)).toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<JournalEntry> getJournalsWithin(String player, Timestamp time) {
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `journal` WHERE name = ? AND time <= ?");
			stmt.setString(1, player);
			stmt.setTimestamp(2, time);
			ResultSet result = stmt.executeQuery();
			ArrayList<JournalEntry> entries = new ArrayList<>();
			while (result.next()) {
				entries.add(JournalEntry.fromRecord(result));
			}
			return entries;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Future<Boolean> verifyJournal(String player) {
		return CompletableFuture.supplyAsync(() -> {
			ArrayList<JournalEntry> entries = getJournalsWithin(player, Timestamp.from(Instant.now()));
			if (entries == null) return false;
			entries.forEach(it -> {

			});
			return false;
		});
	}

	private void init() {
		try {
			PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `journal` (" +
					"name VARCHAR(128) NOT NULL," +
					"type INT NOT NULL," +
					"inventoryIndex INT NOT NULL," +
					"itemIndex INT NOT NULL," +
					"item TEXT NOT NULL," +
					"id INTEGER AUTO_INCREMENT NOT NULL," +
					"time DATETIME DEFAULT CURRENT_TIMESTAMP," +
					"PRIMARY KEY (id))");
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private Connection conn;
}
