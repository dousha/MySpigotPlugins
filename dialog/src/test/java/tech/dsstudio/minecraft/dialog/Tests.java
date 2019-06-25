package tech.dsstudio.minecraft.dialog;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tech.dsstudio.minecraft.dialog.formatters.AlphaNumericFormatter;
import tech.dsstudio.minecraft.dialog.formatters.BooleanFormatter;
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
	public void manualSession() {
		PlayerMock playerMock = mock.addPlayer();
		SessionManager manager = main.getSessionManager();
		OneShotSessionContext context = new OneShotSessionContext((msg) -> assertSame("test", msg));
		manager.registerContext(playerMock, context);
		assertTrue(manager.isOccupied(playerMock));
		assertFalse(manager.registerContext(playerMock, context));
		manager.playerTalked(playerMock, "test");
		assertFalse(manager.isOccupied(playerMock));
		OneShotSessionContext anotherContext = new OneShotSessionContext((msg) -> {});
		manager.registerContext(playerMock, context);
		manager.forceRegisterContext(playerMock, anotherContext);
		assertSame(anotherContext, manager.getCurrentContext(playerMock));
	}

	@Test
	public void formatterTest1() {
		AlphaNumericFormatter formatter = new AlphaNumericFormatter();
		assertTrue(formatter.test("0123"));
		assertTrue(formatter.test("abc"));
		assertTrue(formatter.test("a00b11"));
		assertFalse(formatter.test(" "));
		assertFalse(formatter.test(""));
	}

	@Test
	public void formatterTest2() {
		BooleanFormatter formatter = new BooleanFormatter();
		assertTrue(formatter.test("t"));
		assertTrue(formatter.test("f"));
		assertTrue(formatter.test("Yes"));
		assertFalse(formatter.test("q"));
		assertFalse(formatter.test("yes?"));
		assertFalse(formatter.test(""));
	}

	@After
	public void cleanUp() {
		MockBukkit.unload();
	}

	private ServerMock mock;
	private Main main;
}
