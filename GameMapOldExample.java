import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
//Log 02/05/2017: Need to seperate SetDirection for Enemy and Non-Agression sites,  -- Solvable
//Error Log: Bot Crashes when myHighest level decreases and too many sites send request for help. -- Not Solved

//Log 02/06/2017: I have deleted the iterators from this version (Need to change the iterator limit value, )
//Need to Improve: also need to stop the small strength grids from attacking
//Need to Improve: Find out the crash problem source
//Need to Improve: Each grid choose alternative route 

//Log 02/07/2017: This version is working very well (around 30 points)
//Need to Improve: stop the big guys from overemerging (WASTE!!!) -- Run through every tile, given postFinalMove as well as futureStrength -- then stop some guys or swith them;// In Progress
public class GameMap{

    private final Site[][] contents;
    private final Location[][] locations;
    public final int width, height;
    public int highest, highestcount, totalStrength;
    public int myID;
    public int myHighest = 1;
    public int total, myTotal;
    public boolean enemyNear;
    public int length;
    public int frame;
    public boolean attackStarted = false;
    public int[] upDown = new int[50];
    public int[] leftRight = new int[50]; 
    public double highestValue, averageValue;
    public int firstTargetLength;

    public GameMap(int width, int height, int[][] productions) {
        highest = 0;
        this.width = width;
        this.height = height;
        this.contents = new Site[width][height];
        this.locations = new Location[width][height];
        total = width * height;
        length = 26;
        frame = 0;
        highestValue = 0.0;
        averageValue = 0.0;
        firstTargetLength = 5;
        totalStrength = 0;
        //attackStarted = false;

        for (int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                final Site site = new Site(productions[x][y]);
                contents[x][y] = site;
                locations[x][y] = new Location(x, y, site);
                //mapAnalyze
                if (productions[x][y] > highest) highest = productions[x][y];
                averageValue += productions[x][y];
                totalStrength += locations[x][y].site.strength;
            }
        }

    }
    void mapAnalyze() {
        averageValue = 1.0*averageValue / totalStrength;
        //if (averageValue > 80)
        //system.out.print(averageValue);
    }

    public boolean inBounds(Location loc) {
        return loc.x < width && loc.x >= 0 && loc.y < height && loc.y >= 0;
    }

    public double getDistance(Location loc1, Location loc2) {
        int dx = Math.abs(loc1.x - loc2.x);
        int dy = Math.abs(loc1.y - loc2.y);

        if(dx > width / 2.0) dx = width - dx;
        if(dy > height / 2.0) dy = height - dy;

        return dx + dy;
    }

    public double getAngle(Location loc1, Location loc2) {
        int dx = loc1.x - loc2.x;

        // Flip order because 0,0 is top left
        // and want atan2 to look as it would on the unit circle
        int dy = loc2.y - loc1.y;

        if(dx > width - dx) dx -= width;
        if(-dx > width + dx) dx += width;

        if(dy > height - dy) dy -= height;
        if(-dy > height + dy) dy += height;

        return Math.atan2(dy, dx);
    }

    public Location getLocation(Location location, Direction direction) {
        switch (direction) {
            case STILL:
                return location;
            case NORTH:
                return locations[location.x][(location.y == 0 ? height : location.y) -1];
            case EAST:
                return locations[location.x == width - 1 ? 0 : location.x + 1][location.y];
            case SOUTH:
                return locations[location.x][location.getY() == height - 1 ? 0 : location.y + 1];
            case WEST:
                return locations[(location.x == 0 ? width : location.x) - 1][location.y];
            default:
                throw new IllegalArgumentException(String.format("Unknown direction %s encountered", direction));
        }
    }

    public Site getSite(Location loc, Direction dir) {
        return getLocation(loc, dir).getSite();
    }

    public Site getSite(int x, int y) {
        Location loc = getLocation(x, y);
        return loc.getSite();
    }

    public Location getLocation(int x, int y) {
        return locations[x][y];
    }

    void reset() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final Site site = contents[x][y];
                site.owner = 0;
                site.strength = 0;
            }
        }
    }
    void determineMyHighest() {//Basically Preparing for the real game: going through the whole map without give 
        myHighest = 0;
        highestValue = 0.0;
        myTotal = 0;
        enemyNear = false;
        for (int y = 0; y < height; y++) leftRight[y] = 0;
        for (int x = 0; x < width; x++) upDown[x] = 0;
        for (int y = 0; y < height; y++) 
            for (int x = 0; x < width; x++) 
                locations[x][y].site.reset();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //locations[x][y].site.reset();
                if (locations[x][y].site.owner == myID)
                {
                    myTotal++;
                    final Site s1 = getSite(locations[x][y], Direction.WEST);
                    final Site s2 = getSite(locations[x][y], Direction.NORTH);
                    final Site s3 = getSite(locations[x][y], Direction.EAST);
                    final Site s4 = getSite(locations[x][y], Direction.SOUTH);
                    if (s1.owner == 0 & s1.value > highestValue) highestValue = 1.0*s1.value;
                    if (s2.owner == 0 & s2.value > highestValue) highestValue = 1.0*s2.value;
                    if (s3.owner == 0 & s3.value > highestValue) highestValue = 1.0*s3.value;
                    if (s4.owner == 0 & s4.value > highestValue) highestValue = 1.0*s4.value;

                    if (s1.owner == 0 & s1.strength != 0) {
                        if (s1.production > highest){
                            myHighest = s1.production;
                            highestcount = 1;
                        }
                        else if (s1.production == highest) highestcount++;
                    }
                    if (s2.owner == 0 & s2.strength != 0) {
                        if (s2.production > highest){
                            myHighest = s2.production;
                            highestcount = 1;
                        }
                        else if (s2.production == highest) highestcount++;
                    }
                    if (s3.owner == 0 & s3.strength != 0) {
                        if (s3.production > highest){
                            myHighest = s3.production;
                            highestcount = 1;
                        }
                        else if (s3.production == highest) highestcount++;
                    }
                    if (s4.owner == 0 & s4.strength != 0) {
                        if (s4.production > highest){
                            myHighest = s4.production;
                            highestcount = 1;
                        }
                        else if (s4.production == highest) highestcount++;
                    }
                    if (!enemyNear & ((s1.owner != myID & s1.owner != 0)|(s1.owner == 0 & s1.strength == 0))) enemyNear = true;
                    else if (!enemyNear & ((s2.owner != myID & s2.owner != 0)|(s2.owner == 0 & s2.strength == 0))) enemyNear = true;
                    else if (!enemyNear & ((s3.owner != myID & s3.owner != 0)|(s3.owner == 0 & s3.strength == 0))) enemyNear = true;
                    else if (!enemyNear & ((s4.owner != myID & s4.owner != 0)|(s4.owner == 0 & s4.strength == 0))) enemyNear = true;
                }
                else if (locations[x][y].site.owner!= myID & locations[x][y].site.owner != 0) {
                    upDown[x]++;
                    leftRight[y]++;
                }
                else decideBoundary(x, y);
            }
        }
        //if (highestcount < 10 & highestcount > 5) highest = highest - 1;
        //if (myTotal > 30 & highestcount < 5) highest = highest - 1; 
    }
    Direction checkInDanger(int x, int y) {//In Test Mode
        Location loc1 = getLocation(locations[x][y], Direction.WEST);
        Location loc2 = getLocation(locations[x][y], Direction.NORTH);
        Location loc3 = getLocation(locations[x][y], Direction.EAST);
        Location loc4 = getLocation(locations[x][y], Direction.SOUTH);
        Site s1 = loc1.getSite();
        Site s2 = loc2.getSite();
        Site s3 = loc3.getSite();
        Site s4 = loc4.getSite();
        int st1 = -10, st2 = -10, st3 = -10, st4 = -10;
        if (s1.owner == 0 & s1.strength == 0) st1 = 1 + enemyStrengthTotal(loc1.x, loc1.y);// + leftRight[y]/2;
        if (s2.owner == 0 & s2.strength == 0) st2 = 1 + enemyStrengthTotal(loc2.x, loc2.y);// + upDown[x]/2;
        if (s3.owner == 0 & s3.strength == 0) st3 = 1 + enemyStrengthTotal(loc3.x, loc3.y);// + leftRight[y]/2;
        if (s4.owner == 0 & s4.strength == 0) st4 = 1 + enemyStrengthTotal(loc4.x, loc4.y);// + upDown[x]/2;
        if (st1 >= st2 & st1 >= st3 & st1 >= st4) return Direction.WEST;
        else if (st2 >= st1 & st2 >= st3 & st2 >= st4) return Direction.NORTH;
        else if (st3 >= st1 & st3 >= st2 & st3 >= st4) return Direction.EAST;
        else return Direction.SOUTH;
    }
    void decideBoundary(int x, int y){
        final Site s1 = getSite(locations[x][y], Direction.WEST);
        final Site s2 = getSite(locations[x][y], Direction.NORTH);
        final Site s3 = getSite(locations[x][y], Direction.EAST);
        final Site s4 = getSite(locations[x][y], Direction.SOUTH);
        if ((s1.owner != 0 & s1.owner != myID) | (s2.owner != 0 & s2.owner != myID) | (s3.owner != 0 & s3.owner != myID) | (s4.owner != 0 & s4.owner != myID)) locations[x][y].site.dangerous = true;
    }
    int enemyStrengthTotal(int x, int y) {
        int sum = 0;
        final Site s1 = getSite(locations[x][y], Direction.WEST);
        final Site s2 = getSite(locations[x][y], Direction.NORTH);
        final Site s3 = getSite(locations[x][y], Direction.EAST);
        final Site s4 = getSite(locations[x][y], Direction.SOUTH);
        if (s1.owner != myID & s1.owner != 0) sum += 5;
        if (s2.owner != myID & s2.owner != 0) sum += 5;
        if (s3.owner != myID & s3.owner != 0) sum += 5;
        if (s4.owner != myID & s4.owner != 0) sum += 5;
        return sum;
    }
    void enemyDetection() {
        frame++;
        determineMyHighest();
        List<Pair> collection = new ArrayList<Pair>(30);
        List<Pair> collection_Unn = new ArrayList<Pair>(30);//Unnecessary Going - need only nearby 4 grids for help
        //List<Pair> collection2 = new ArrayList<Pair>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (locations[x][y].site.owner == myID)
                {
                    final Site s1 = getSite(locations[x][y], Direction.WEST);
                    final Site s2 = getSite(locations[x][y], Direction.NORTH);
                    final Site s3 = getSite(locations[x][y], Direction.EAST);
                    final Site s4 = getSite(locations[x][y], Direction.SOUTH);
                    /*if (s1.owner != myID & s1.owner != 0 & s1.strength > 0)//At least one is actual enemy
                    {
                        //locations[x][y].site.setDangerous();
                        Pair p = new Pair(x, y, Direction.WEST, 0);
                        collection.add(p);
                        //attackStarted = true;
                        //enemyNear = true;
                    }
                    else if (s2.owner != myID & s2.owner != 0 & s2.strength > 0)
                    {
                        //locations[x][y].site.setDangerous();
                        Pair p = new Pair(x, y, Direction.NORTH, 0);
                        collection.add(p);
                        //attackStarted = true;
                        //enemyNear = true;
                    }
                    else if (s3.owner != myID & s3.owner != 0 & s3.strength > 0)
                    {
                        //locations[x][y].site.setDangerous();
                        Pair p = new Pair(x, y, Direction.EAST, 0);
                        collection.add(p);
                        //attackStarted = true;
                        //enemyNear = true;
                    }
                    else if (s4.owner != myID & s4.owner != 0 & s4.strength > 0)
                    {
                        //locations[x][y].site.setDangerous();
                        Pair p = new Pair(x, y, Direction.SOUTH, 0);
                        collection.add(p);
                        //attackStarted = true;
                        //enemyNear = true;
                    }
                    else */if (s1.owner != myID & s1.strength == 0)//The Ones That Has been Taken: 0 strength, with at least one enemy nearby(Advanced: Chooose the position where there is the most enemy strength)
                    {
                        //locations[x][y].site.setDangerous();
                        Pair p = new Pair(x, y, checkInDanger(x, y), 0);
                        collection.add(p);
                        //enemyNear = true;
                    }
                    else if (s2.owner != myID & s2.strength == 0)
                    {
                        //locations[x][y].site.setDangerous();
                        Pair p = new Pair(x, y, checkInDanger(x, y), 0);
                        collection.add(p);
                        //enemyNear = true;
                    }
                    else if (s3.owner != myID & s3.strength == 0)
                    {
                        //locations[x][y].site.setDangerous();
                        Pair p = new Pair(x, y, checkInDanger(x, y), 0);
                        collection.add(p);
                        //enemyNear = true;
                    }
                    else if (s4.owner != myID & s4.strength == 0)
                    {
                        //locations[x][y].site.setDangerous();
                        Pair p = new Pair(x, y, checkInDanger(x, y), 0);
                        collection.add(p);
                        //enemyNear = true;
                    }
                    //The Above are the ones with enemy/enemies around. If not, check 
                    else if ((s1.owner == 0 | s2.owner == 0 | s3.owner == 0 | s4.owner == 0) & ((myTotal < total*2/3)))//& (!enemyNear)))
                    {
                        //locations[x][y].site.setBrim();
                        boolean exist = false;
                        Pair p = new Pair(x, y, Direction.STILL, 10);
                        double value = -1.0;
                        int modify = 0;
                        if (myTotal > firstTargetLength) modify = 1;//Choose the best first five of my target;
                        if (s1.owner == 0 & ((attackStarted | !s1.dangerous | myTotal > total/2) & (!(s1.production < 1 & frame < 150) | (locations[x][y].site.production > 3 & s1.strength <= 30))))// & s1.production >= myHighest)//No enemy around, but with a non-Agressive neighbor
                        {
                            if (myTotal > 5){
                                if (/*highestcount < 10 & */s1.production >= myHighest - modify) 
                                {
                                    p = new Pair(x, y, Direction.WEST, 8);
                                    exist = true;
                                    value = 1.0*s1.production / s1.strength;
                                }
                                else if (myTotal > 5)
                                {
                                    p = new Pair(x, y, Direction.WEST, 16);
                                    exist = true;
                                    value = 1.0*s1.production / s1.strength;
                                }
                            }
                            else if (1.0*s1.value == 1.0*highestValue) {
                                p = new Pair(x, y, Direction.WEST, 16);
                                exist = true;
                                value = 1.0*s1.value;
                            }
                        }    //locations[x][y].site.editDirection(Direction.WEST, highest-s1.production);
                        if (s2.owner == 0 & ((attackStarted | !s2.dangerous | myTotal > total/2) & (!(s2.production < 1 & frame < 150) | (locations[x][y].site.production > 3 & s2.strength <= 30))))// & s2.production >= myHighest)
                        {
                            if (myTotal > firstTargetLength){
                                if (/*highestcount < 10 & */s2.production >= myHighest - modify) 
                                {
                                    if (1.0*s2.production/s2.strength > value) {
                                        p = new Pair(x, y, Direction.NORTH, 8);
                                        value = 1.0*s2.production/s2.strength;
                                    }
                                    exist = true;
                                    
                                }
                                else {
                                    if (1.0*s2.production/s2.strength > value & myTotal > 5) {
                                        p = new Pair(x, y, Direction.NORTH, 16);
                                        value = 1.0*s2.production/s2.strength;
                                    }
                                    exist = true;
                                }
                            }
                            else if (1.0*s2.value >= 1.0*highestValue) {
                                p = new Pair(x, y, Direction.NORTH, 16);
                                exist = true;
                                value = 1.0*s2.value;
                            }
                        }
                            //locations[x][y].site.editDirection(Direction.NORTH, highest-s2.production);
                        if (s3.owner == 0 & ((attackStarted | !s3.dangerous | myTotal > total/2) & (!(s3.production < 1 & frame < 150) | (locations[x][y].site.production > 3 & s3.strength <= 30))))// & s3.production >= myHighest)
                        {
                            if (myTotal > firstTargetLength){
                                if (/*highestcount < 10 & */s3.production >= myHighest - modify) 
                                {
                                    if (1.0*s3.production/s3.strength > value) {
                                        p = new Pair(x, y, Direction.EAST, 8);
                                        value = 1.0*s3.production/s3.strength;
                                    }
                                    exist = true;
                                    
                                }
                                else {
                                    if (1.0*s3.production/s3.strength > value & myTotal > 5) {
                                        p = new Pair(x, y, Direction.EAST, 16);
                                        value = 1.0*s3.production/s3.strength;
                                    }
                                    exist = true;
                                }
                            }
                            else if (1.0*s3.value == 1.0*highestValue) {
                                p = new Pair(x, y, Direction.EAST, 16);
                                exist = true;
                                value = 1.0*s3.value;
                            }
                        }
                            //locations[x][y].site.editDirection(Direction.EAST, highest-s3.production);
                        if (s4.owner == 0 & ((attackStarted | !s4.dangerous | myTotal > total/2) & (!(s4.production < 1 & frame < 150) | (locations[x][y].site.production > 3 & s4.strength <= 30))))//& s4.production >= myHighest)
                        {
                            if (myTotal > firstTargetLength)
                            {
                                if (/*highestcount < 10 & */s4.production >= myHighest - modify) 
                                {
                                    if (1.0*s4.production/s4.strength > value) {
                                        p = new Pair(x, y, Direction.SOUTH, 8);
                                        value = 1.0*s4.production/s4.strength;
                                    }
                                    exist = true;
                                    
                                }
                                else {
                                    if (1.0*s4.production/s4.strength > value) {
                                        p = new Pair(x, y, Direction.SOUTH, 16);
                                        value = 1.0*s4.production/s4.strength;
                                    }
                                    exist = true;
                                }
                            }
                            else if (1.0*s4.value == 1.0*highestValue) {
                                p = new Pair(x, y, Direction.SOUTH, 16);
                                exist = true;
                                value = 1.0*s4.value;
                            }
                        }
                        if (exist) collection.add(p);
                            //locations[x][y].site.editDirection(Direction.SOUTH, highest-s4.production);
                    }
                    //else locations[x][y].site.setSafe();//If not the above, it must be safe(in the middle of all my territory)
                    //location[x][y].site.d1 = 0;
                    /*if(site.owner == myID) {
                        moves.add(new Move(location, Direction.randomDirection()));*/
                }
            }
        }
        //The Below are the statement that might prevent the bot from crashing near end of the game;
        if (myTotal > total *3 /4) length = 12;
        //if (myTotal > 800 & total > ) length > 
        //if (myTotal > 20 & collection.size() <= 4) length = 16;
        if (myTotal < 6){
            Iterator<Pair> iter = collection.iterator();
            while (collection.size() > 1) {
                iter.next();
                iter.remove();
            }
        }
        if (!collection.isEmpty()) {
            setDirection(collection, 0);
            //while (true) {myID=myID;}
        }
        length = 26;
        //if (!collection2.isEmpty())
            ///setDirection_NotOnTurn(collection2, 4);
    }
    void setDirection(List<Pair> collection, int turn) {
        List<Pair> secondCollection = new ArrayList<Pair>(30);
        if (turn <= length) {for (Pair temp : collection) locations[temp.x][temp.y].site.editDirection(temp.d, turn + temp.mod);
        for (Pair temp : collection){
            if (((temp.mod >= 4) & turn <= 12)|(temp.mod < 4 & turn <= length)) {//Bug Fixed!
            Location loc1 = getLocation(locations[temp.x][temp.y], Direction.WEST);
            Location loc2 = getLocation(locations[temp.x][temp.y], Direction.NORTH);
            Location loc3 = getLocation(locations[temp.x][temp.y], Direction.EAST);
            Location loc4 = getLocation(locations[temp.x][temp.y], Direction.SOUTH);
            Site s1 = loc1.getSite();
            Site s2 = loc2.getSite();
            Site s3 = loc3.getSite();
            Site s4 = loc4.getSite();
            int n = 0;//If the attacking one is at full strength,
            //if (turn < 2 & locations[temp.x][temp.y].site.strength > 180) n = -2;
            if (s1.owner == myID & s1.min > turn) 
            {
                int m = 1;//Temporary - could be 2
                if (temp.d == Direction.EAST) m = 0;
                Pair f = new Pair(loc1.x, loc1.y, Direction.EAST, temp.mod + m + n);
                secondCollection.add(f);
            }
            if (s2.owner == myID & s2.min > turn) 
            {
                int m = 1;
                if (temp.d == Direction.SOUTH) m = 0;
                Pair f = new Pair(loc2.x, loc2.y, Direction.SOUTH, temp.mod + m + n);
                secondCollection.add(f);
            }
            if (s3.owner == myID & s3.min > turn) 
            {
                int m = 1;
                if (temp.d == Direction.WEST) m = 0;
                Pair f = new Pair(loc3.x, loc3.y, Direction.WEST, temp.mod + m + n);
                secondCollection.add(f);
            }
            if (s4.owner == myID & s4.min > turn) 
            {
                int m = 1;
                if (temp.d == Direction.NORTH) m = 0;
                Pair f = new Pair(loc4.x, loc4.y, Direction.NORTH, temp.mod + m + n);
                secondCollection.add(f);
            }
        }}}
        if (!secondCollection.isEmpty()) /*& secondCollection.size()<20)*/ setDirection(secondCollection, turn + 2);
    }
    void finalCheck() {
        for (int y = 0; y < height; y++) 
            for (int x = 0; x < width; x++) 
                if (locations[x][y].site.owner == myID & !locations[x][y].site.decided) {
                    /*Location loc1 = getLocation(locations[x][y], Direction.WEST);
                    Location loc2 = getLocation(locations[x][y], Direction.NORTH);
                    Location loc3 = getLocation(locations[x][y], Direction.EAST);
                    Location loc4 = getLocation(locations[x][y], Direction.SOUTH);
                    Site s1 = loc1.getSite();
                    Site s2 = loc2.getSite();
                    Site s3 = loc3.getSite();
                    Site s4 = loc4.getSite();*/
                    Location locFinal = getLocation(locations[x][y], locations[x][y].site.finalMove);
                    Site siteFinal = locFinal.getSite();//Final Site of the final move
                    List<Direction> allyDirection;
                    
                    if (siteFinal.owner == 0 & siteFinal.strength == 0) {//Target is an enemy
                        allyDirection = anyOneIsMovingIn(x,y);
                        if (allyDirection.size() >= 1) {//if One Person is moving in
                            while (allyDirection.size() > 0)
                            {
                                Location locAlly = getLocation(locations[x][y], allyDirection.iterator().next());
                                Site ally = locAlly.getSite();
                                if (locations[x][y].site.strength + ally.strength < 255) {
                                    locations[x][y].site.decided = true;//Stay still to merge with ally
                                    locations[x][y].site.finalMove = Direction.STILL;
                                    locations[locAlly.x][locAlly.y].site.decided = true;//Let the ally merge, tell the friends behind to keep going
                                    passAction(locAlly.x, locAlly.y, ally.finalMove);
                                }
                                else {//Strength sum > 255 (CAP LOSS)
                                    if (ally.strength > locations[x][y].site.strength) {
                                        swap(x, y, locAlly.x, locAlly.y, allyDirection.iterator().next());//After Swapping, need to tell the friends behind to stop
                                        passAction(locAlly.x, locAlly.y, Direction.STILL);
                                    }
                                    else {
                                        locations[x][y].site.decided = true;//GOGOGO, need to tell the friends behind to keep going
                                        passAction(locAlly.x, locAlly.y, ally.finalMove);
                                    }
                                }
                                allyDirection.remove(allyDirection.iterator().next());
                                //allyDirection.remove(allyDirection.iterator().next());
                                while (allyDirection.size() > 0) {
                                    locAlly = getLocation(locations[x][y], allyDirection.iterator().next());
                                    ally = locAlly.getSite();
                                    locations[locAlly.x][locAlly.y].site.finalMove = Direction.STILL;
                                    locations[locAlly.x][locAlly.y].site.decided = true;
                                    passAction(locAlly.x, locAlly.y, Direction.STILL);
                                    allyDirection.remove(allyDirection.iterator().next());
                                }
                            }
                        }
                        else {//No one is backing up, == no ally
                            locations[locFinal.x][locFinal.y].site.decided = true;
                        }
                    }
                }
    }
    void passAction(int x, int y, Direction passDirection){
        List<Direction> allyDirection = anyOneIsMovingIn(x,y);
        if (allyDirection.size() >= 1) {//if someone is moving in
            while (allyDirection.size() > 0)
            { 
                Location locAlly = getLocation(locations[x][y], allyDirection.iterator().next());
                Site ally = locAlly.getSite();
                if (passDirection != Direction.STILL)//If the Action passed to me is to keep going --
                    if (locations[x][y].site.strength + ally.strength < 255) {
                        locations[x][y].site.decided = true;//Stay still to merge with ally
                        locations[x][y].site.finalMove = Direction.STILL;
                        locations[locAlly.x][locAlly.y].site.decided = true;//Let the ally merge
                        passAction(locAlly.x, locAlly.y, ally.finalMove);//Tell them to come
                    }
                    else {//Strength sum > 255 (CAP LOSS)
                        if (ally.strength > locations[x][y].site.strength) {
                            swap(x, y, locAlly.x, locAlly.y, allyDirection.iterator().next());
                            passAction(locAlly.x, locAlly.y, Direction.STILL);
                        }
                        else {
                            locations[x][y].site.decided = true;//GOGOGO
                            passAction(locAlly.x, locAlly.y, ally.finalMove);
                        }
                    }
                else if (passDirection == Direction.STILL) {//People in the front tell you to stop, tell everyone behind to stop()
                    locations[locAlly.x][locAlly.y].site.finalMove = Direction.STILL;
                    passAction(locAlly.x, locAlly.y, Direction.STILL);
                }
                allyDirection.remove(allyDirection.iterator().next());
                while (allyDirection.size() > 0) {
                     locAlly = getLocation(locations[x][y], allyDirection.iterator().next());
                     ally = locAlly.getSite();
                     locations[locAlly.x][locAlly.y].site.finalMove = Direction.STILL;
                     locations[locAlly.x][locAlly.y].site.decided = true;
                     passAction(locAlly.x, locAlly.y, Direction.STILL);
                     allyDirection.remove(allyDirection.iterator().next());
                 }
            }
        }
        //else {//No one is backing up, == no ally
        //    locations[locFinal.x][locFinal.y].site.decided = true;
        //}
    }
    List<Direction> anyOneIsMovingIn(int x, int y) {// In this place, I should detect if there are more than one allies coming in
        final Site s1 = getSite(locations[x][y], Direction.WEST);
        final Site s2 = getSite(locations[x][y], Direction.NORTH);
        final Site s3 = getSite(locations[x][y], Direction.EAST);
        final Site s4 = getSite(locations[x][y], Direction.SOUTH);
        List<Direction> allyDirection = new ArrayList<Direction>(4); //This part is not done yet(More Features to add)
        if (s1.owner == myID & s1.finalMove == Direction.EAST & !s1.decided)//If an Ally moving in
            allyDirection.add(Direction.WEST);
        if (s2.owner == myID & s2.finalMove == Direction.SOUTH & !s2.decided)
            allyDirection.add(Direction.NORTH);
        if (s3.owner == myID & s3.finalMove == Direction.WEST & !s3.decided)
            allyDirection.add(Direction.EAST);
        if (s4.owner == myID & s4.finalMove == Direction.NORTH & !s4.decided)
            allyDirection.add(Direction.SOUTH);
        return allyDirection;
    }
    /*void finalCheckOldVersion() {
        for (int y = 0; y < height; y++) 
            for (int x = 0; x < width; x++) 
                if (locations[x][y].site.owner == myID  & !locations[x][y].site.decided) {
                    /*Location loc1 = getLocation(locations[x][y], Direction.WEST);
                    Location loc2 = getLocation(locations[x][y], Direction.NORTH);
                    Location loc3 = getLocation(locations[x][y], Direction.EAST);
                    Location loc4 = getLocation(locations[x][y], Direction.SOUTH);
                    Site s1 = loc1.getSite();
                    Site s2 = loc2.getSite();
                    Site s3 = loc3.getSite();
                    Site s4 = loc4.getSite();
                    Location locFinal = getLocation(locations[x][y], locations[x][y].site.finalMove);
                    Site siteFinal = locFinal.getSite();//Final Site of the final move
                    Direction allyDirection;
                    
                    if (siteFinal.owner == 0 & siteFinal.strength == 0) {//Target is an enemy
                        allyDirection = anyOneIsMovingIn(x,y);
                        if (allyDirection != Direction.STILL) {//if someone is moving in
                            Location locAlly = getLocation(locations[x][y], allyDirection);
                            Site ally = locAlly.getSite();
                            if (locations[x][y].site.strength + ally.strength < 255) {
                                locations[x][y].site.decided = true;//Stay still to merge with ally
                                locations[x][y].site.finalMove = Direction.STILL;
                                locations[locAlly.x][locAlly.y].site.decided = true;//Let the ally merge
                            }
                            else {//Strength sum > 255 (CAP LOSS)
                                if (ally.strength > locations[x][y].site.strength) swap(x, y, locAlly.x, locAlly.y, allyDirection);
                                else locations[x][y].site.decided = true;//GOGOGO
                            }
                        }
                        else {//No one is backing up, == no ally
                            locations[locFinal.x][locFinal.y].site.decided = true;
                        }
                    }
                }
    }*/
    void swap(int x1, int y1, int x2, int y2, Direction allyDirection) {// x1, y1 is siteFinal, x2, y2 is ally
        locations[x1][y1].site.decided = true;//Swap to ally's spot
        locations[x1][y1].site.finalMove = allyDirection;
        locations[x2][y2].site.decided = true;//Let the ally swap into my spot
    }
    void preMoveCompile() {
        for (int y = 0; y < height; y++) 
            for (int x = 0; x < width; x++) 
                if (locations[x][y].site.owner == myID)
                    locations[x][y].site.finalMove = preMove(x, y);
    }
    Direction preMove(int x, int y) {
        final Site tempSite = getSite(locations[x][y], locations[x][y].site.preferedMove);   
        Direction dr; 
        if (tempSite.owner == myID) {
            if (locations[x][y].site.strength >= 6*locations[x][y].site.production) dr = locations[x][y].site.preferedMove;
            else dr = Direction.STILL;
        }
        else if (tempSite.owner != myID & tempSite.owner !=0){
            dr = locations[x][y].site.preferedMove;
        }
        else if (tempSite.strength == 0 & tempSite.owner == 0) {
            if (locations[x][y].site.strength > 2*locations[x][y].site.production) dr = locations[x][y].site.preferedMove;
            else dr = Direction.STILL;
        }
        else {
            if ((locations[x][y].site.strength >= tempSite.strength + 1)|locations[x][y].site.strength == 255) dr = locations[x][y].site.preferedMove;
            else dr = Direction.STILL;
        }//The Below are experimental, not sure if it works
        //if (dr != Direction.STILL) { //IF you are moving, but the target you are moving into is friendly
            //if ()
        if (locations[x][y].site.production == 0 & locations[x][y].site.strength == 0) dr = Direction.STILL;
        return dr;
    }
    void sendMoves() {
        List<Move> moves = new ArrayList<Move>(30);
        for (int y = 0; y < height; y++) 
            for (int x = 0; x < width; x++) 
                if (locations[x][y].site.owner == myID) moves.add(new Move(locations[x][y], locations[x][y].site.finalMove));
        Networking.sendFrame(moves);
    }

    void importID(int tempID) {
        myID = tempID;
    }
}