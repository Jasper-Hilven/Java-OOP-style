package project.misc;
import java.util.ArrayList;
import java.util.List;

import project.dungeons.*;
import project.squares.*;

/**
 * A demo class that demonstrates the correctness of the classes of this package.
 *
 * @version 3.1
 * @author Stef Noten & Jasper Hilven
 */
public class Demo {
	/**
	 * The application entry point.
	 * 
	 * @param 	args
	 * 				The application arguments.
	 */
	public static void main(String[] args) {
		int n = 3;
		CompositeDungeon<Square> mainDungeon = getMainDungeon(n);
		setBorderSquares(mainDungeon,n);
		
		Position squarePosition = new Position(1,n,0);
		System.out.println("\r\n*************WITH TELEPORTATIONSQUARE****************\r\n");
		checkForAccessibility(squarePosition, n, mainDungeon);
		mainDungeon.removeSquareAt(squarePosition);
		mainDungeon.setSquareAt(squarePosition, new SquareImpl());
		System.out.println("\r\n*************WITHOUT TELEPORTATIONSQUARE*************\r\n");
		checkForAccessibility(squarePosition, n, mainDungeon);
		
	}


	private static void checkForAccessibility(Position squarePosition,int n,
			CompositeDungeon<Square> mainDungeon) {
		Square teleSquare = mainDungeon.getSquareAt(squarePosition);
		System.out.println("	canNavigateTo from square at " + squarePosition.toString());
		for (int y = 0; y <= n; y++) {
			for (int x = 0; x <= n; x++) {
				Position position = new Position(x, y, 0);
				if (!mainDungeon.hasSquareAt(position))
					continue;
				Square square = mainDungeon.getSquareAt(position);
				System.out.println(	"		to square at position: " + position.toString() + 
				" results in: " + teleSquare.canNavigateTo(square));
			}
		}
	}
	
	static Level<Square> level; 
	private static CompositeDungeon<Square> getMainDungeon(int size){
		CompositeDungeon<Square> mainDungeon= new CompositeDungeon<Square>(new Position(size,size,size));
		Level<Square> groundLevel = new Level<Square>(size-1,size-1);
		level = groundLevel;
		Shaft<Square> groundShaft = new Shaft<Square>(Direction.EAST, size-1);
		mainDungeon.setSubDungeonAt(groundLevel, new Position());
		mainDungeon.setSubDungeonAt(groundShaft, new Position(0, size, 0));
		return mainDungeon;
	}
	private static void setBorderSquares(CompositeDungeon<Square> mainDungeon, int size){
	
		for (int i = 0; i < size; i++) {
			for (int j = 1; j < size; j++) {
				//Set a square in the level, next to the square of the shaft, with a wall to the shaft.
				mainDungeon.setSquareAt(new Position(i,j,0), new SquareImpl(Direction.FLOOR));
			}
			//Set a square in the shaft,  a wall should be between both squares.
			if (i == 1) {
				List<Square> teleportTargets = new ArrayList<Square>();
				teleportTargets.add(mainDungeon.getSquareAt(new Position(0,size-1,0)));
				teleportTargets.add(mainDungeon.getSquareAt(new Position(1,size-1,0)));
				mainDungeon.setSquareAt(new Position(i,size,0),  new TeleportationSquareImpl(teleportTargets, Direction.FLOOR, Direction.SOUTH));
			}
			else
				mainDungeon.setSquareAt(new Position(i,size,0), new SquareImpl(Direction.SOUTH));
			
		}
	}
}
