import org.junit.Test;
import tech.dsstudio.minecraft.flow.Flow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Tests {
	@Test
	public void chainOfCommands() {
		new Flow<Integer, Integer>(param -> param + 1)
				.<Integer, Integer>then(param -> param + 1)
				.<Integer, Integer>finishSync(param -> {
					assertEquals(Integer.valueOf(2), param);
				}, 0);
	}

	@Test
	public void errorHandling() {
		new Flow<Integer, Integer>(param -> param)
				.<Integer, String>then(String::valueOf)
				.orElse(ex -> assertTrue(ex instanceof IndexOutOfBoundsException))
				.<String, Character>then(param -> param.charAt(2))
				.runSync(1);
	}
}
