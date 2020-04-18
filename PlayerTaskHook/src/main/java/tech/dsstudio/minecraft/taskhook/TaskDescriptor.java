package tech.dsstudio.minecraft.taskhook;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.UUID;

public class TaskDescriptor {
	JavaPlugin owner;
	Runnable runnable;
	long delay;
	boolean repeat;
	long interval;
	UUID playerUuid;
	UUID taskId;

	long assignedTime;
	long executionTime;
	long remainingWaitTime;
	boolean needRemoval = false;
	boolean isSuspended = false;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TaskDescriptor that = (TaskDescriptor) o;
		return taskId.equals(that.taskId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(owner, runnable, delay, repeat, interval, playerUuid, taskId);
	}

	public void suspend() {
		if (!this.isSuspended) {
			PlayerTaskHookApi.suspend(this);
		}
	}

	public void resume() {
		if (this.isSuspended) {
			PlayerTaskHookApi.resume(this);
		}
	}

	public void cancel() {
		PlayerTaskHookApi.cancel(this);
	}

	public boolean isSuspended() {
		return this.isSuspended;
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
