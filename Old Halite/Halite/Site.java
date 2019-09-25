public class Site {

    public final int production;
    public int owner, strength;
    public int d1, d2, d3, d4, min;
    public boolean dangerous, brim, safe;
    public Direction preferedMove = Direction.STILL;
    public int futureStrength;

    public Site(int production) {
        dangerous = false;
        brim = false;
        safe= false;
        d1 = 200;
        d2 = 200;
        d3 = 200;
        d4 = 200;
        min = 200;

        this.production = production;
    }
    void reset()
    {
        dangerous = false;
        brim = false;
        safe= false;
        d1 = 200;
        d2 = 200;
        d3 = 200;
        d4 = 200;
        min = 200;
        preferedMove = Direction.STILL;
    }

    void setDangerous(){
        dangerous = true;
        brim = false;
        safe = false;
    }
    void setBrim(){
        dangerous = false;
        brim = true;
        safe = false;
    }
    void setSafe(){
        dangerous = false;
        brim = false;
        safe = true;
    }
    void editDirection(Direction d, int turn){
        if (d == Direction.WEST & turn < d1) d1 = turn;
        else if (d == Direction.NORTH & turn < d2) d2 = turn;
        else if (d == Direction.EAST & turn < d3) d3 = turn;
        else if (d == Direction.SOUTH & turn < d4) d4 = turn;
        changeMin();
    }

    void changeMin() {
        if (d1 < d2 & d1 < d3 & d1 < d4) {
            preferedMove = Direction.WEST;
            min = d1;
        }
        else if (d2 < d1 & d2 < d3 & d2 < d4) {
            preferedMove = Direction.NORTH;
            min = d2;
        }
        else if (d3 < d1 & d3 < d2 & d3 < d4) {
            preferedMove = Direction.EAST;
            min = d3;
        }
        else if (d4 < d1 & d4 < d2 & d4 < d3) {
            preferedMove = Direction.SOUTH;
            min = d4;
        }
    }
    Direction move(int myID, Site tempSite) {
        if (tempSite.owner == myID)
        {
            if (strength > 40) return preferedMove;
            else return Direction.STILL;
        }
        else if (tempSite.owner != myID & tempSite.owner !=0)
        {
            return preferedMove;
        }
        else 
        {
            if (strength >= tempSite.strength) return preferedMove;
            else return Direction.STILL;
        }
        // if (pmStrength >= strength) return Direction.STILL;
        // else return preferedMove;
    }
}
