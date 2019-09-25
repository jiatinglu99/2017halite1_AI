public class Site {

    public final int production;
    public int owner, strength;
    public int d1, d2, d3, d4, min;
    public double m1, m2, m3, m4;
    public boolean dangerous, brim, safe;
    public Direction preferedMove = Direction.STILL;
    public int futureStrength;
    public double value, mapValue;
    public Direction finalMove;
    public boolean decided, analyzed;
    public double initValue; //strength / production
    public Site(int production) {
        d1 = 200;
        d2 = 200;
        d3 = 200;
        d4 = 200;
        min = 200;
        dangerous = false;
        decided = false;

        this.production = production;
    }
    void reset()
    {
        d1 = 200;
        d2 = 200;
        d3 = 200;
        d4 = 200;
        min = 200;
        preferedMove = Direction.STILL;
        futureStrength = 0;
        dangerous = false;
        decided = false;
        value = 1.0 * production / strength;
    }
    void initialize() {
        m1 = 2000.0;
        m2 = 2000.0;
        m3 = 2000.0;
        m4 = 2000.0;
        if (production != 0) initValue = 1.0 * strength / production;
        else initValue = 1.0 * strength / 0.1;
        mapValue = 1.0 * initValue;
        analyzed = false;
    }
    void editValue(double tempValue, Direction fromDirection) {
        switch (fromDirection) {
            case WEST: if (tempValue < m1) m1 = tempValue;
            case NORTH: if (tempValue < m2) m2 = tempValue;
            case EAST: if (tempValue < m3) m3 = tempValue;
            case SOUTH: if (tempValue < m4) m4 = tempValue;
        }
    }
    // void decideValue() {
    //     if (!analyzed) {
    //         if (m1 <= 1000.0) mapValue += m1;
    //         if (m2 <= 1000.0) mapValue += m2;
    //         if (m3 <= 1000.0) mapValue += m3;
    //         if (m4 <= 1000.0) mapValue += m4;
    //         analyzed = true;
    //     }
    // }
    void changeMin() {
        if (m1 <= m2 & m1 <= m3 & m1 <= m4) {
            preferedMove = Direction.WEST;
            min = d1;
        }
        else if (2 <= m1 & m2 <= m3 & m2 <= m4) {
            preferedMove = Direction.NORTH;
            min = d2;
        }
        else if (m3 <= m1 & m3 <= d2 & d3 <= d4) {
            preferedMove = Direction.EAST;
            min = d3;
        }
        else if (d4 <= d1 & d4 <= d2 & d4 <= d3) {
            preferedMove = Direction.SOUTH;
            min = d4;
        }
    }
    void editDirection(Direction d, int turn){
        if (d == Direction.WEST & turn < d1) d1 = turn;
        else if (d == Direction.NORTH & turn < d2) d2 = turn;
        else if (d == Direction.EAST & turn < d3) d3 = turn;
        else if (d == Direction.SOUTH & turn < d4) d4 = turn;
        changeMin();
    }

    double getMapValue() {
        if (d1 <= d2 & d1 <= d3 & d1 <= d4) {
            mapValue = initValue + d1;
        }
        else if (d2 <= d1 & d2 <= d3 & d2 <= d4) {
            mapValue = initValue + d2;
        }
        else if (d3 <= d1 & d3 <= m2 & m3 <= m4) {
            mapValue = initValue + d3;
        }
        else if (m4 <= m1 & m4 <= m2 & m4 <= m3) {
            mapValue = initValue + d4;
        }
        return mapValue;
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
