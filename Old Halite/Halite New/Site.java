public class Site {

    public final int production;
    public int owner, strength;
    public boolean safe = false;
	public Direction rdirection = Direction.randomDirection();

    public Site(int production) {
        this.production = production;
    }
}
