package tech.dsstudio.minecraft.redpacket;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class RedPacketEntry {
	public RedPacketEntry(String who, String name, RedPacketType type, int amount, int playerCount) {
		this.who = who;
		this.name = name;
		this.type = type;
		this.amount = new AtomicInteger(amount);
		this.playerCount = new AtomicInteger(playerCount);
		this.random = new Random();
		this.drawnPlayers = new HashSet<>();
		this.announced = new AtomicBoolean(false);
	}

	public synchronized int draw(Player who) {
		int out;
		drawnPlayers.add(who.getUniqueId());
		if (playerCount.get() == 0) {
			return 0;
		} else {
			if (playerCount.get() == 1) {
				out = amount.get();
			} else {
				out = 1 + random.nextInt(getUpperBound());
			}
		}
		playerCount.decrementAndGet();
		amount.addAndGet(-out);
		if (out > winnerValue) {
			winner = who.getDisplayName();
			winnerValue = out;
		}
		return out;
	}

	public synchronized boolean isDrawn(Player who) {
		return drawnPlayers.contains(who.getUniqueId());
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public String getCommand() {
		return command;
	}

	public String getWho() {
		return who;
	}

	public String getName() {
		return name;
	}

	public int getAmount() {
		return amount.get();
	}

	public synchronized int getPlayerCount() {
		return playerCount.get();
	}

	public RedPacketType getType() {
		return type;
	}

	public String getWinner() {
		return winner;
	}

	public int getWinnerValue() {
		return winnerValue;
	}

	public synchronized boolean isAnnounced() {
		return announced.get();
	}

	public void setAnnounced() {
		this.announced.set(true);
	}

	private int getUpperBound() {
		return Math.max(1, this.amount.get() / playerCount.get() * 2);
	}

	private String who;
	private AtomicInteger amount;
	private AtomicInteger playerCount;
	private RedPacketType type;
	private Random random;
	private String name;
	private HashSet<UUID> drawnPlayers;
	private String winner = "";
	private int winnerValue = 0;
	private AtomicBoolean announced;

	private String token = "";
	private String command;
}
