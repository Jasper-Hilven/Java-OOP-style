package project.dungeons;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import project.misc.Direction;
import project.squares.RockSquare;
import project.squares.Square;
import project.squares.SquareImpl;
import project.squares.TransparentSquareImpl;

public class ShaftTest {

	private Shaft<Square> shaft;
	private static Position pos1 = new Position(10, 0, 0);
	private static Position pos2 = new Position(20, 0, 0);
	private static Position pos3 = new Position(30, 0, 0);
	private static Position invalidPos1 = new Position(0, 10, 0);
	
	@Before
	public void setUpMutableTextFixture() {
		this.shaft = new Shaft<Square>(Direction.EAST);
	}

	@Test
	public void canSetSquareAt_TrueCase() {
		assertTrue(shaft.canSetSquareAt(pos1, new SquareImpl()));
		assertTrue(shaft.canSetSquareAt(pos2, new SquareImpl()));
		assertTrue(shaft.canSetSquareAt(pos3, new SquareImpl()));
		assertTrue(shaft.canSetSquareAt(new Position(2, 0, 0), new TransparentSquareImpl(Direction.EAST)));
	}
	
	@Test
	public void canSetSquareAt_FalseCase() {
		assertFalse(shaft.canSetSquareAt(new Position(0, 10, 10), new SquareImpl()));
		assertFalse(shaft.canSetSquareAt(new Position(10, 10, 9), new SquareImpl()));
		assertFalse(shaft.canSetSquareAt(invalidPos1, new SquareImpl()));
		assertFalse(shaft.canSetSquareAt(pos1, new RockSquare()));
		
		shaft.setSquareAt(new Position(1, 0, 0), new SquareImpl());
		assertFalse(shaft.canSetSquareAt(new Position(2, 0, 0), new TransparentSquareImpl(Direction.WEST)));
	}
	
	@Test
	public void canHaveAsMaximumPosition_TrueCase() {
		assertTrue(shaft.canHaveAsMaximumPosition(new Position(200, 0, 0)));
		assertTrue(shaft.canHaveAsMaximumPosition(new Position(300, 0, 0)));
	}
	
	@Test
	public void canHaveAsMaximumPosition_FalseCase() {
		assertFalse(shaft.canHaveAsMaximumPosition(new Position(0, 200, 0)));
		assertFalse(shaft.canHaveAsMaximumPosition(new Position(0, 0, 200)));
		assertFalse(shaft.canHaveAsMaximumPosition(new Position(0, 0, 0)));
	}
}
