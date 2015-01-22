package project.misc;
import org.junit.*;

/**
 * A test class for the position class.
 * 
 * @version	1.0
 * @author 	Stef Noten & Jasper Hilven
 *
 */
public class DirectionTest {
	/**
	 * A variable referencing the north direction.
	 */
	private static final Direction north =Direction.NORTH;
	
	/**
	 * A variable referencing the south direction.
	 */
	private static final Direction south = Direction.SOUTH;
	
	/**
	 * A variable referencing the east direction.
	 */
	private static final Direction east =Direction.EAST;
	
	/**
	 * A variable referencing the west direction.
	 */
	private static final Direction west = Direction.WEST;
	/**
	 * A variable referencing the ceiling direction.
	 */
	private static final Direction ceiling =Direction.CEILING;
	
	/**
	 * A variable referencing the floor direction.
	 */
	private static final Direction floor = Direction.FLOOR;
	
	@Test
	public void getOppositeDirection_legalCase(){
		assert(floor.getOppositeDirection() == ceiling);
		assert(ceiling.getOppositeDirection() == floor);
		assert(south.getOppositeDirection() == north);
		assert(north.getOppositeDirection() == south);
		assert(east.getOppositeDirection() == west);
		assert(west.getOppositeDirection() == east);
		
	}
}