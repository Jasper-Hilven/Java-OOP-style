package project.squares;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import project.misc.Direction;
import project.squares.borders.Border;
import project.squares.borders.OpenBorder;
import project.squares.borders.Wall;

public class TransparentSquareImplTest {

	@Test
	public void canHaveAsBordersIfNotTerminated_TrueCase() {
		TransparentSquare square = new TransparentSquareImpl();
		Map<Direction, Border> borders = square.getBorders();
		assertTrue(square.canHaveAsBordersIfNotTerminated(borders));
		borders.put(Direction.SOUTH, new Wall(true, false));
		assertTrue(square.canHaveAsBordersIfNotTerminated(borders));
	}
	
	@Test
	public void canHaveAsBordersIfNotTerminated_FalseCase() {
		TransparentSquare square = new TransparentSquareImpl();
		Map<Direction, Border> borders = square.getBorders();
		borders.put(Direction.EAST, new Wall(true, false));
		assertFalse(square.canHaveAsBordersIfNotTerminated(borders));
		borders.put(Direction.EAST, new OpenBorder());
		borders.put(Direction.SOUTH, new Wall(false, false));
		assertFalse(square.canHaveAsBordersIfNotTerminated(borders));
	}
}
