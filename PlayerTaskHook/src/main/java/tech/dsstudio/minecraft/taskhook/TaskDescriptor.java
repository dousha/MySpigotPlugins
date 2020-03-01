package tech.dsstudio.minecraft.taskhook;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class TaskDescriptor implements Comparable<TaskDescriptor> {
	JavaPlugin owner;
	Runnable runnable;
	long delay;
	boolean repeat;
	long interval;
	UUID playerUuid;

	long assignedTime;
	long executionTime;
	long remainingWaitTime;
	boolean needRemoval = false;
	boolean isRunning = false;

	@Override
	public int compareTo(@NotNull TaskDescriptor descriptor) {
		return (int) (this.assignedTime - descriptor.assignedTime);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TaskDescriptor that = (TaskDescriptor) o;
		return delay == that.delay &&
				repeat == that.repeat &&
				interval == that.interval &&
				assignedTime == that.assignedTime &&
				owner.equals(that.owner) &&
				runnable.equals(that.runnable) &&
				playerUuid.equals(that.playerUuid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(owner, runnable, delay, repeat, interval, assignedTime, playerUuid);
	}

	public void suspend() {
		PlayerTaskHookApi.suspend(this);
	}

	public void resume() {
		PlayerTaskHookApi.resume(this);
	}

	public void cancel() {
		PlayerTaskHookApi.cancel(this);
	}

	public boolean isSuspended() {
		return false;
	}

	public JavaPlugin getOwner() {
		return owner;
	}

	public long getAssignedTime() {
		return assignedTime;
	}

	public long getDelay() {
		return delay;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public long getInterval() {
		return interval;
	}

	public long getRemainingWaitTime() {
		return remainingWaitTime;
	}

	public UUID getPlayerUuid() {
		return playerUuid;
	}

	public Runnable getRunnable() {
		return runnable;
	}
}
