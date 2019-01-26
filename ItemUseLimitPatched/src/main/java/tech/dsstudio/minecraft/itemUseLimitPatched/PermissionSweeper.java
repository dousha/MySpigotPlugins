package tech.dsstudio.minecraft.itemUseLimitPatched;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PermissionSweeper implements Runnable {
	public PermissionSweeper(Main father) {
		this.father = father;
	}

	@Override
	public void run() {
		if (lock.readLock().tryLock()) {
			AtomicReference<HashSet<String>> dirty = new AtomicReference<>(new HashSet<>());
			record.forEach((name, map) -> sweepTable.forEach(perm -> {
				Boolean cachedValue = map.getOrDefault(perm, false);
				Boolean testValue = father.getServer().getPlayer(name).hasPermission(perm);
				if (cachedValue != testValue) {
					dirty.get().add(name);
					map.put(perm, testValue);
				}
			}));
			dirty.get().forEach(it -> {
				Player player = father.getServer().getPlayer(it);
				father.getMonitor().updatePlayerPermission(player);
			});
			lock.readLock().unlock();
		}
	}

	public void addPlayer(Player player) {
		lock.writeLock().lock();
		record.put(player.getName(), new HashMap<>());
		lock.writeLock().unlock();
	}

	public void deletePlayer(Player player) {
		lock.writeLock().lock();
		record.remove(player.getName());
		lock.writeLock().lock();
	}

	public void notifyUpdate() {
		lock.writeLock().lock();
		HashSet<String> permSet = Config.getPermSet();
		sweepTable.addAll(permSet);
		lock.writeLock().unlock();
	}

	private Main father;
	private ArrayList<String> sweepTable = new ArrayList<>();
	private HashMap<String, HashMap<String, Boolean>> record = new HashMap<>();
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
}
