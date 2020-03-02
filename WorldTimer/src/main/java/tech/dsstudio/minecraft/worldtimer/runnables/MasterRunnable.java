package tech.dsstudio.minecraft.worldtimer.runnables;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tech.dsstudio.minecraft.playerdata.PlayerData;
import tech.dsstudio.minecraft.taskhook.PlayerTaskHookApi;
import tech.dsstudio.minecraft.taskhook.TaskDescriptor;
import tech.dsstudio.minecraft.worldtimer.Main;
import tech.dsstudio.minecraft.worldtimer.objects.WorldEffectDescriptor;
import tech.dsstudio.minecraft.worldtimer.objects.WorldLimitDescriptor;

import java.util.ArrayList;

public class MasterRunnable implements Runnable {
	public MasterRunnable(@NotNull Main plugin, @NotNull Player player, @NotNull PlayerData data, @NotNull WorldLimitDescriptor descriptor) {
		this.plugin = plugin;
		this.player = player;
		this.descriptor = descriptor;
		this.data = data;
	}

	@Override
	public void run() {
		ArrayList<TaskDescriptor> childTasks = new ArrayList<>();
		for (WorldEffectDescriptor debuff : descriptor.debuff) {
			TaskDescriptor childTask = PlayerTaskHookApi.runTaskTimer(plugin, player, () -> {
				player.addPotionEffect(debuff.effect);
			}, debuff.offset, debuff.interval);
			childTasks.add(childTask);
		}
		data.setVolatile(Main.CHILD_TASK_KEY_NAME, childTasks);
	}

	private Main plugin;
	private Player player;
	private WorldLimitDescriptor descriptor;
	private PlayerData data;
}
