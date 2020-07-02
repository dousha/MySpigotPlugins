package tech.dsstudio.minecraft.tests;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class Tests {
	@Test
	public void materialTest() {
		assertSame(Material.valueOf("SMITHING_TABLE"), Material.SMITHING_TABLE);
	}
}
