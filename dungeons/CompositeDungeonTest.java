package project.dungeons;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import project.misc.Direction;
import project.squares.RockSquare;
import project.squares.Square;
import project.squares.SquareImpl;
import project.squares.TransparentSquareImpl;
/**
 * A test class for the composite dungeon class.
 * 
 * @version	1.0
 * @author 	Stef Noten & Jasper Hilven
 */
public class CompositeDungeonTest {
	private CompositeDungeon<Square> compositeDungeon;
	private Dungeon<Square> subShaft, subLevel;
	private Set<Square> squareSet;

	@Before
	public void setUpMutableTextFixture() {
		compositeDungeon = new CompositeDungeon<Square>();
		squareSet = new HashSet<Square>();
		CompositeDungeon<Square> subCompositeDungeon = new CompositeDungeon<Square>();
		compositeDungeon.setSubDungeonAt(subCompositeDungeon, new Position());
		subCompositeDungeon.setSubDungeonAt(subShaft = new Shaft<Square>(Direction.CEILING,20), new Position(0,0,1));
		subCompositeDungeon.setSubDungeonAt(subLevel = new Level<Square>(20, 20), new Position());
		insertSquares();
	}

	private void insertSquares(){
		for (int i = 1; i < 21; i++) {

			compositeDungeon.setSquareAt(new Position(0,0,i),new TransparentSquareImpl());
			squareSet.add(compositeDungeon.getSquareAt(new Position(0,0,i)));
			compositeDungeon.setSquareAt(new Position(0,i,0),new SquareImpl());
			squareSet.add(compositeDungeon.getSquareAt(new Position(0,i,0)));
			compositeDungeon.setSquareAt(new Position(i,0,0),new RockSquare());
			squareSet.add(compositeDungeon.getSquareAt(new Position(i,0,0)));
		}
	}

	@Test
	public void Iterator_LegalCase() {
		Iterator<Square> compositeDungeonIterator =  compositeDungeon.iterator();	 
		for (int i = 0; i < 60; i++) {
			assertTrue(compositeDungeonIterator.hasNext());
			Square next = compositeDungeonIterator.next();
			assertTrue(squareSet.contains(next));
			squareSet.remove(next);
		}
		assertEquals(0, squareSet.size());
		assertFalse(compositeDungeonIterator.hasNext());
	}

	@Test(expected = IllegalStateException.class)
	public void Iterator_IllegalState() {
		compositeDungeon.terminate();
		compositeDungeon.iterator();
	}

	@Test
	public void iteratorWithPredicate_LegalCase() {
		SquarePredicate<Square> isTransParentSquareImplPredicate = new SquarePredicate<Square>() {

			@Override
			public boolean satisfies(Square square,
					Dungeon<? extends Square> parentDungeon) {

				return square instanceof TransparentSquareImpl;
			}
		};
		Iterator<Square>transparentIterator = compositeDungeon.iterator(isTransParentSquareImplPredicate);
		for (int i = 0; i < 20; i++) {
			assertTrue(transparentIterator.hasNext());
			transparentIterator.next();
		}
		assertFalse(transparentIterator.hasNext());
	}



	@Test(expected = IllegalStateException.class)
	public void iteratorWithPredicate_IllegalState() {
		compositeDungeon.terminate();
		compositeDungeon.iterator(null);
	}
	
	@Test
	public void getAllSingularDungeons_LegalCase() {
		Set<SingularDungeon<Square>> singularDungeons = compositeDungeon.getAllSingularDungeons();
		assertTrue(singularDungeons.contains(subLevel));
		assertTrue(singularDungeons.contains(subShaft));
		assertTrue(singularDungeons.size() == 2);
	}
	
	@Test(expected = IllegalStateException.class)
	public void getAllSingularDungeons_IllegalState() {
		compositeDungeon.terminate();
		compositeDungeon.getAllSingularDungeons();
	}
}
