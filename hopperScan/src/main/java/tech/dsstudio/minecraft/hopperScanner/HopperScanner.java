package tech.dsstudio.minecraft.hopperScanner;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HopperScanner extends JavaPlugin {
	@Override
	public void onEnable() {
		saveDefaultConfig();
		Config.loadConfig(getConfig());
		HopperCluster.notifyUpdate();
		LatticeCluster.notifyUpdate();
		Lattice.notifyUpdate();
	}

	@Override
	public void onDisable() {

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equals("hscan")) {
			if (annoyee != null) {
				sender.sendMessage("当前正在运行一个扫描，由于性能限制不能进行多个扫描");
				sender.sendMessage("请等待当前扫描结束");
				return true;
			}
			World target;
			if (sender instanceof Player && sender.hasPermission("hscan.do")) {
				target = ((Player) sender).getWorld();
			} else {
				if (args.length != 1) {
					sender.sendMessage("用法：hscan <世界名>");
					return true;
				} else {
					target = Bukkit.getWorld(args[0]);
					if (target == null) {
						sender.sendMessage("世界 " + args[0] + " 不存在！");
						return true;
					}
				}
			}
			annoyee = sender;
			annoy(String.format("准备在世界 %s 扫描漏斗群...", target.getName()));
			annoy("正在统计区块...");

			Pattern pattern = Pattern.compile("r\\.([0-9-]+)\\.([0-9]+)\\.mca");
			File worldDir = new File(Bukkit.getWorldContainer(), target.getName());
			File regionDir = new File(worldDir, "region");
			File[] regionFiles = regionDir.listFiles((file, name) -> pattern.matcher(name).matches());
			if (regionFiles == null || regionFiles.length == 0) {
				annoy("无法读取世界文件！无法运行统计");
				done();
				return true;
			}
			annoy(String.format("当前世界生成了 %d 个区域文件，预计最多需要扫描 %d 个区块", regionFiles.length, regionFiles.length << 10));
			lattice.clearAll();
			lattice.setEstimatedChunkCount(regionFiles.length << 10);

			Thread workerThread = new Thread(() -> {
				while (true) {
					if (workers.isEmpty()) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							break;
						}
					} else {
						Objects.requireNonNull(workers.poll()).run();
					}
				}
			});
			BukkitRunnable boss = new BukkitRunnable() {
				@Override
				public void run() {
					if (filePtr == regionFiles.length) {
						if (workers.isEmpty()) {
							workerThread.interrupt();
							annoy(String.format("共扫描 %d 个区块", lattice.getScannedChunkCount()));
							annoy("扫描完成，准备启动分析线程...");
							Bukkit.getScheduler().cancelTask(scannerHandle);
							Bukkit.getScheduler().cancelTask(tellerHandle);
							lattice.analyze();
						}
					} else {
						File current = regionFiles[filePtr];
						Matcher matcher = pattern.matcher(current.getName());
						if (!matcher.matches()) {
							annoy("跳过文件 " + current.getName());
							++filePtr;
							return;
						}
						int mcaX = Integer.parseInt(matcher.group(1));
						int mcaZ = Integer.parseInt(matcher.group(2));
						int x = chunkPtr;
						int qx = (mcaX << 5) + x;
						for (int z = 0; z < 32; z++) {
							int qz = (mcaZ << 5) + z;
							if (target.isChunkLoaded(qx, qz) || target.loadChunk(qx, qz, false)) {
								workers.add(new Worker(qx, qz, target.getChunkAt(qx, qz).getTileEntities(), lattice));
							}
						}
						if (++chunkPtr == 32) {
							++filePtr;
							chunkPtr = 0;
						}
					}
				}
			};
			annoy("正在启动扫描线程...");
			scannerHandle = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, boss, 0, 1);
			workerThread.start();
			tellerHandle = Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, teller, 0, 30);
			return true;
		} else if (command.getName().equals("hlast")) {
			if (sender.hasPermission("hscan.see")) {
				if (lattice.getEstimatedChunkCount() == 0) {
					sender.sendMessage("尚未进行扫描，使用 /hscan 开始扫描");
				} else {
					sender.sendMessage(String.format("最后扫描了 %d 个区块", lattice.getScannedChunkCount()));
					lattice.getLastResult().forEach(sender::sendMessage);
					dumpLog(lattice.getLastResult());
				}
			}
			return true;
		} else if (command.getName().equals("hreload") && sender.hasPermission("hscan.reload")) {
			Config.loadConfig(getConfig());
			HopperCluster.notifyUpdate();
			LatticeCluster.notifyUpdate();
			Lattice.notifyUpdate();
			sender.sendMessage("配置已重载");
			return true;
		} else {
			return false;
		}
	}

	public static void annoy(String msg) {
		try {
			if (annoyee == null) return;
			annoyee.sendMessage(msg);
		} catch (Exception ignored) {

		}
	}

	public static void done() {
		annoyee = null;
	}

	public void dumpLog(ArrayList<String> str) {
		File log = new File(this.getDataFolder(), "last.log");
		if (!log.exists()) {
			try {
				log.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				annoy("日志写入失败：不能创建文件");
				return;
			}
		}
		if (!log.canWrite()) {
			annoy("日志写入失败：没有写入权限");
			return;
		}
		try {
			FileOutputStream out = new FileOutputStream(log);
			OutputStreamWriter writer = new OutputStreamWriter(out);
			str.forEach(it -> {
				try {
					writer.append(it);
					writer.append('\n');
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static CommandSender annoyee = null;
	private Lattice lattice = new Lattice(this);
	private Teller teller = new Teller(lattice);
	private int tellerHandle = 0;
	private int scannerHandle = 0;
	private ConcurrentLinkedQueue<Worker> workers = new ConcurrentLinkedQueue<>();
	private int filePtr = 0;
	private int chunkPtr = 0;
}
