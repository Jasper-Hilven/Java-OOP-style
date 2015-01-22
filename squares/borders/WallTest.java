package project.squares.borders;
import project.squares.borders.Wall;
import static org.junit.Assert.*;

import org.junit.*;

/**
 * A test class for the square class.
 * 
 * @version	1.0
 * @author 	Stef Noten & Jasper Hilven
 *
 */
public class WallTest {
	
	@Test
	public void Default_Constructor_LegalCase() {
		Wall wall1 = new Wall(false, false);
		Wall wall2 = new Wall(true, false);
		Wall wall3 = new Wall(false, true);
		Wall wall4 = new Wall(true, true);

		assertFalse(wall1.hasDoor());
		assertTrue(wall2.hasDoor());
		assertFalse(wall3.hasDoor());
		assertTrue(wall4.hasDoor());

		assertFalse(wall1.isSlippery());
		assertFalse(wall2.isSlippery());
		assertTrue(wall3.isSlippery());
		assertFalse(wall4.isSlippery());
	}
		
	@Test
	public void openDoor_LegalCase() {
		Wall wall1 = new Wall(true, false);
		wall1.closeDoor();
		wall1.openDoor();
		assertTrue(wall1.isDoorOpen());
	}
		 
	@Test
	public void closeDoor_LegalCase() {
		Wall wall1 = new Wall(true, false);
		wall1.openDoor();
		wall1.closeDoor();
		assertFalse(wall1.isDoorOpen());
	}
	
	@Test
	public void canHaveAsIsolating_LegalCase() {
		Wall wall1 = new Wall(true, false);
		assertTrue(wall1.canHaveAsIsolating(true));
		assertTrue(wall1.canHaveAsIsolating(false));
		Wall wall2 = new Wall(false, false);
		assertFalse(wall2.canHaveAsIsolating(false));
		assertTrue(wall2.canHaveAsIsolating(true));
	}
	
	@Test
	public void getDominantBorder_LegalCase() {
		Wall door = new Wall(true, false);
		Wall wall = new Wall(false,false);
		OpenBorder openBorder = new OpenBorder();
		assertEquals(door.getDominantBorder(door), door);
		assertEquals(door.getDominantBorder(wall), wall);
		assertEquals(door.getDominantBorder(openBorder), door);
		assertEquals(wall.getDominantBorder(door), wall);
		assertEquals(wall.getDominantBorder(wall), wall);
		assertEquals(wall.getDominantBorder(openBorder),wall);
		assertEquals(openBorder.getDominantBorder(door), door);
		assertEquals(openBorder.getDominantBorder(wall), wall);
		assertEquals(openBorder.getDominantBorder(openBorder), openBorder);
	}
}