package tech.dsstudio.minecraft.dialog;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tech.dsstudio.minecraft.dialog.sessions.OneShotSessionContext;

import static org.junit.Assert.*;

public class Tests {
	@Test
	public void junitTest() {
		// this test should never fail
		// unless JUnit is not configured correctly
		assertTrue(true);
	}

	@Before
	public void setup() {
		mock = MockBukkit.mock();
		main = MockBukkit.load(Main.class);
	}

	@Test
	public void session() {
		PlayerMock player = mock.addPlayer();
		SessionManager manager = main.getSessionManager();
		OneShotSessionContext context = new OneShotSessionContext((msg) -> assertSame(msg, "test"));
		manager.registerContext(player, context);
		assertFalse(manager.registerContext(player, context));
		assertTrue(manager.isOccupied(player));
		player.chat("test");
		assertFalse(manager.isOccupied(player));
	}

	@After
	public void cleanUp() {
		MockBukkit.unload();
	}

	private ServerMock mock;
	private Main main;
}
