import java.util.Random;

public enum Direction {
    STILL, NORTH, EAST, SOUTH, WEST;

    public static final Direction[] DIRECTIONS = new Direction[]{STILL, NORTH, EAST, SOUTH, WEST};
    public static final Direction[] CARDINALS = new Direction[]{NORTH, EAST, SOUTH, WEST};

    public static Direction randomDirection() {
        Direction[] values = values();
        return values[new Random().nextInt(values.length)];
    }
    public static Direction random() {
Random rand = new Random();
	int n = rand.nextInt(100) + 0;
	if (n >= 50) return Direction.SOUTH;
	else return Direction.EAST;
	}
}
