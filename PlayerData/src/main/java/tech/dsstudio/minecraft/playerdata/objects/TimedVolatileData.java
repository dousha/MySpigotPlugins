package tech.dsstudio.minecraft.playerdata.objects;

public class TimedVolatileData<T> {
	public TimedVolatileData(T data, long expire) {
		this.data = data;
		this.expire = expire;
	}

	public long getExpire() {
		return expire;
	}

	public T getData() {
		return data;
	}

	private T data;
	private long expire;
}
