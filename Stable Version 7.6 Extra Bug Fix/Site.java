public class Site {

    public final int production;
    public int owner, strength;
    public int d1, d2, d3, d4, min;
    public boolean dangerous, brim, safe;
    public Direction preferedMove = Direction.STILL;
    public int futureStrength;
    public double value;
    public Direction finalMove;
    public boolean decided;
    public boolean waitForMerge = false;
    public Site(int production) {
        d1 = 2000;
        d2 = 2000;
        d3 = 2000;
        d4 = 2000;
        min = 2000;
        dangerous = false;
        decided = false;
        waitForMerge = false;

        this.production = production;
    }
    boolean getwaitForMerge() {
        return waitForMerge;
    }
    void reset()
    {
        d1 = 2000;
        d2 = 2000;
        d3 = 2000;
        d4 = 2000;
        min = 2000;
        preferedMove = Direction.STILL;
        futureStrength = 0;
        dangerous = false;
        decided = false;
        waitForMerge = false;
        value = 1.0*production/strength;
        
    }
    void editDirection(Direction d, int turn){
        if (d == Direction.WEST & turn < d1) d1 = turn;
        else if (d == Direction.NORTH & turn < d2) d2 = turn;
        else if (d == Direction.EAST & turn < d3) d3 = turn;
        else if (d == Direction.SOUTH & turn < d4) d4 = turn;
        changeMin();
    }
    void chooseAlternative (Direction d) {
        if (d == Direction.WEST) d1 += 1;
        else if (d == Direction.NORTH) d2 += 1;
        else if (d == Direction.EAST) d3 += 1;
        else if (d == Direction.SOUTH) d4 += 1;
        changeMin();
    }

    void changeMin() {
        if (d1 <= d2 & d1 <= d3 & d1 <= d4) {
            preferedMove = Direction.WEST;
            min = d1;
        }
        else if (d2 <= d1 & d2 <= d3 & d2 <= d4) {
            preferedMove = Direction.NORTH;
            min = d2;
        }
        else if (d3 <= d1 & d3 <= d2 & d3 <= d4) {
            preferedMove = Direction.EAST;
            min = d3;
        }
        else if (d4 <= d1 & d4 <= d2 & d4 <= d3) {
            preferedMove = Direction.SOUTH;
            min = d4;
        }
    }
    Direction move(int myID, Site tempSite) {
        if (tempSite.owner == myID)
        {
            if (strength > 5*production) preferedMove = preferedMove;
            else preferedMove = Direction.STILL;
        }
        else if (tempSite.owner != myID & tempSite.owner !=0)
        {
            preferedMove = preferedMove;
        }
        else 
        {
            if (strength < tempSite.strength) preferedMove = Direction.STILL;
        }
        if (strength > 200 & preferedMove == Direction.STILL) return Direction.NORTH; //Temporary
        else return preferedMove;
        // if (pmStrength >= strength) return Direction.STILL;
        // else return preferedMove;
    }
}