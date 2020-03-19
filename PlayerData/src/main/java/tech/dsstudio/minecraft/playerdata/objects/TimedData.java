package tech.dsstudio.minecraft.playerdata.objects;

import java.io.Serializable;

public class TimedData<T extends Serializable> implements Serializable {
	public TimedData(T data, long expire) {
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
