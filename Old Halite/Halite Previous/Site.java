public class Site {

    public final int production;
    public int owner, strength, need, direction = 5; 
    public boolean wait = false;

    public Site(int production) {
        this.production = production;
    }
}
