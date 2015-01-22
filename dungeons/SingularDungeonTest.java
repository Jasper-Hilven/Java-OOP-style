package project.dungeons;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import project.misc.Direction;
import project.squares.Square;
import project.squares.SquareImpl;
import project.squares.TransparentSquareImpl;
/**
 * A test class for the singular dungeon class.
 * 
 * @version	1.0
 * @author 	Stef Noten & Jasper Hilven
 */
public class SingularDungeonTest {
	private SingularDungeon<Square> singularDungeon;
	private Set<Square> squareSet;
	@Before
	public void setUpMutableTextFixture() {
		singularDungeon = new Level<Square>( 100,100);
		squareSet = new HashSet<Square>();
		insertSquares();
	}

	private void insertSquares(){
		for (int i = 1; i < 21; i++) {
			for(int j =1;j < 6; j++){
				if(j == 1)
					singularDungeon.setSquareAt(new Position(i,j,0), new SquareImpl(Direction.SOUTH));
				else 
					singularDungeon.setSquareAt(new Position(i,j,0), new TransparentSquareImpl(Direction.SOUTH));

				squareSet.add(singularDungeon.getSquareAt(new Position(i,j,0)));
			}
		}
	}

	@Test
	public void Iterator_LegalCase() {
		Iterator<Square> singularDungeonIterator = singularDungeon.iterator();	 
		for (int i = 0; i < 100; i++) {
			assertTrue(singularDungeonIterator.hasNext());
			Square next = singularDungeonIterator.next();
			assertTrue(squareSet.contains(next));
			squareSet.remove(next);
		}
		assertEquals(0, squareSet.size());
		assertFalse(singularDungeonIterator.hasNext());
	}

	@Test(expected = IllegalStateException.class)
	public void Iterator_IllegalState() {
		singularDungeon.terminate();
		singularDungeon.iterator();
	}

	@Test
	public void iteratorWithPredicate_LegalCase() {
		SquarePredicate<Square> isTransparentSquareImplPredicate = new SquarePredicate<Square>() {
			@Override
			public boolean satisfies(Square square,
					Dungeon<? extends Square> parentDungeon) {

				return square instanceof TransparentSquareImpl;
			}
		};
		Iterator<Square> transparentIterator = singularDungeon.iterator(isTransparentSquareImplPredicate);
		for (int i = 0; i < 80; i++) {
			assertTrue(transparentIterator.hasNext());
			transparentIterator.next();
		}
		assertFalse(transparentIterator.hasNext());
	}



	@Test(expected = IllegalStateException.class)
	public void iteratorWithPredicate_IllegalState() {
		singularDungeon.terminate();
		singularDungeon.iterator(null);
	}
}