package tech.dsstudio.minecraft.tests;

import org.junit.Test;
import tech.dsstudio.minecarft.testing.operators.Operator;
import tech.dsstudio.minecarft.testing.utils.Tuple;

import static org.junit.Assert.*;
import static tech.dsstudio.minecarft.testing.operators.OperatorParser.*;

public class StringTests {
	@Test
	public void dummy() {
		assertTrue(true);
	}

	@Test
	public void operatorTest() {
		assertNull(parseString(""));
		Tuple<Operator, Integer> out = parseString("= 1");
		assertNotNull(out);
		assertEquals(1, out.getRight().intValue());
		out = parseString(" >= 5");
		assertNotNull(out);
		assertEquals(3, out.getRight().intValue());
	}
}
