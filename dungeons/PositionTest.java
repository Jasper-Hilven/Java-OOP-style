package project.dungeons;
import org.junit.*;

import project.misc.Direction;




import static org.junit.Assert.*;

/**
 * A test class for the position class.
 * 
 * @version	1.0
 * @author 	Stef Noten & Jasper Hilven
 *
 */
public class PositionTest {
	/**
	 * A variable referencing a default position, that is, constructed with the default 
	 * constructor.
	 */
	private static Position zeroPosition = new Position();
	
	/**
	 * A variable referencing a position, with coordinates (1,1,1).
	 * 
	 */
	private static Position onePosition = new Position(1, 1, 1);;
	
	
	/**
	 * A variable referencing the north direction.
	 */
	private static final Direction north =Direction.NORTH;
	
	/**
	 * A variable referencing the south direction.
	 */
	private static final Direction south = Direction.SOUTH;
	
	@Test
	public void extendedConstructor_LegalCase() {
		Position normalPosition = new Position(25,23,26);
		Position borderPosition = new Position(25,23,Long.MAX_VALUE);
		assert(normalPosition.getX() == 25 && normalPosition.getY() == 23 && normalPosition.getZ()==26);
		assert(borderPosition.getX() == 25 && borderPosition.getY() == 23 && borderPosition.getZ()==Long.MAX_VALUE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void extendedConstructor_NegativeValue(){
		@SuppressWarnings("unused")
		Position illegalPosition = new Position(-25,23,26);
	}
	
	@Test
	public void defaultConstructor(){
		assert(zeroPosition.getX() == 0 && zeroPosition.getY() == 0 && zeroPosition.getZ() == 0 );
	}
	
	@Test
	public void isIdenticalTo_LegalCase(){ 
		Position firstPosition = new Position(1,2,3);
		Position secondPosition= new Position(1,2,3);
		Position thirdPosition = new Position(2,2,3);
		assertTrue(firstPosition.isIdenticalTo(secondPosition));
		assertFalse(secondPosition.isIdenticalTo(thirdPosition));
	}

	@Test(expected=NullPointerException.class)
	public void isIdenticalTo_NullPointer(){
		onePosition.isIdenticalTo(null);
	}
	
	@Test
	public void isAdjacentTo_LegalCase(){ 
		Position firstPosition = new Position(1,2,3);
		Position secondPosition= new Position(1,2,2);
		Position thirdPosition = new Position(2,2,3);
		assertTrue(firstPosition.isAdjacentTo(secondPosition));
		assertTrue(firstPosition.isAdjacentTo(thirdPosition));
		assertFalse(firstPosition.isAdjacentTo(firstPosition));
		assertFalse(secondPosition.isAdjacentTo(thirdPosition));
	}

	@Test(expected=NullPointerException.class)
	public void isAdjacentTo_NullPointer(){
		onePosition.isAdjacentTo(null);
	}
	
	@Test
	public void getAdjecentPosition_LegalCase(){
		onePosition.getAdjacentPosition(north).isIdenticalTo(new Position(1, 2, 1));
		onePosition.getAdjacentPosition(south).isIdenticalTo(new Position(1, 0, 1));
	}
	
	@Test(expected=NullPointerException.class)
	public void getAdjecentPosition_NullPointer(){
		onePosition.getAdjacentPosition(null);
	}
	@Test
	public void getAdjacentCoordinateDirection_LegalCase(){
		assert(onePosition.getAdjacentCoordinateDirection(new Position(1, 2, 1)) == north);
		assert(onePosition.getAdjacentCoordinateDirection(new Position(1, 0, 1)) == south);
	}
	
	@Test(expected=IllegalStateException.class)
	public void getAdjacentCoordinateDirection_IllegalState(){
		onePosition.getAdjacentCoordinateDirection(zeroPosition);
	}
	
	@Test(expected=NullPointerException.class)
	public void getAdjacentCoordinateDirection_NullPointer(){
		onePosition.getAdjacentCoordinateDirection(null);
	}
	
	@Test
	public void offset_LegalCase(){
		assert(onePosition.offset(3, 4, 5).isIdenticalTo(new Position(4, 5, 6)));
	}
	@Test
	public void equals_LegalCase(){
		assert(new Position(1,1,1).equals(onePosition));
		assert(!new Position(0,1,1).equals(onePosition));
		assert(!new Position(0,1,1).equals(null));
	}
	@Test
	public void hashCode_LegalCase() {
		assert(onePosition.hashCode() == new Position(1,1,1).hashCode());
		assert(onePosition.hashCode() != new Position(1,2,1).hashCode());
	}	
}