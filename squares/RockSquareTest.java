package project.squares;

import static org.junit.Assert.*;

import org.junit.Test;

import project.dungeons.Dungeon;
import project.dungeons.Level;
import project.dungeons.Position;
import project.temperature.Temperature;

public class RockSquareTest {

	@Test
	public void getTemperature() {
		Dungeon<Square> dungeon = new Level<Square>();
		Square square = new RockSquare();
		dungeon.setSquareAt(new Position(3, 1, 0), square);
		
		assertEquals(new Temperature(0), square.getTemperature());
		
		dungeon.setSquareAt(new Position(2, 1, 0), new RockSquare());
		assertEquals(new Temperature(0), square.getTemperature());
		
		Square sq1 = new SquareImpl(); sq1.changeTemperature(new Temperature(30));
		dungeon.setSquareAt(new Position(3, 0, 0), sq1);
		assertEquals(new Temperature(30), square.getTemperature());
		
		Square sq2 = new SquareImpl(); sq2.changeTemperature(new Temperature(50));
		dungeon.setSquareAt(new Position(4, 1, 0), sq2);
		assertEquals(new Temperature(40), square.getTemperature());
		
		Square sq3 = new SquareImpl(); sq3.changeTemperature(new Temperature(100));
		dungeon.setSquareAt(new Position(3, 2, 0), sq3);
		assertEquals(new Temperature(60), square.getTemperature());
	}
}
