import java.util.ArrayList;
import java.util.List;
public class GameMap{

    private final Site[][] contents;
    private final Location[][] locations;
    public final int width, height;
    public int average, highest;
    public int myID;
    public int myHighest = 1;

    public GameMap(int width, int height, int[][] productions) {

        average = 0;
        highest = 0;
        this.width = width;
        this.height = height;
        this.contents = new Site[width][height];
        this.locations = new Location[width][height];

        for (int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                final Site site = new Site(productions[x][y]);
                contents[x][y] = site;
                locations[x][y] = new Location(x, y, site);
                if (productions[x][y] > highest) highest = productions[x][y];
                average += productions[x][y];
            }
        }
        average = average / width / height;

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
                return locations[location.getX()][(location.getY() == 0 ? height : location.getY()) -1];
            case EAST:
                return locations[location.getX() == width - 1 ? 0 : location.getX() + 1][location.getY()];
            case SOUTH:
                return locations[location.getX()][location.getY() == height - 1 ? 0 : location.getY() + 1];
            case WEST:
                return locations[(location.getX() == 0 ? width : location.getX()) - 1][location.getY()];
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
    void determineMyHighest() {
        myHighest = 1;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (locations[x][y].site.owner == myID)
                {
                    final Site s1 = getSite(locations[x][y], Direction.WEST);
                    final Site s2 = getSite(locations[x][y], Direction.NORTH);
                    final Site s3 = getSite(locations[x][y], Direction.EAST);
                    final Site s4 = getSite(locations[x][y], Direction.SOUTH);
                    if (s1.owner == 0 & s1.production > myHighest & s1.strength != 0) myHighest = s1.production; 
                    if (s2.owner == 0 & s2.production > myHighest & s2.strength != 0) myHighest = s2.production; 
                    if (s3.owner == 0 & s3.production > myHighest & s3.strength != 0) myHighest = s3.production; 
                    if (s4.owner == 0 & s4.production > myHighest & s4.strength != 0) myHighest = s4.production; 
                }
            }
        }
    }
    void enemyDetection() {
        determineMyHighest();
        List<Pair> collection = new ArrayList<Pair>();
        //List<Pair> collection2 = new ArrayList<Pair>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (locations[x][y].site.owner == myID)
                {
                    locations[x][y].site.reset();
                    final Site s1 = getSite(locations[x][y], Direction.WEST);
                    final Site s2 = getSite(locations[x][y], Direction.NORTH);
                    final Site s3 = getSite(locations[x][y], Direction.EAST);
                    final Site s4 = getSite(locations[x][y], Direction.SOUTH);
                    if (s1.owner != myID & s1.owner != 0)//At least one is actual enemy
                    {
                        //locations[x][y].site.setDangerous();
                        Pair p = new Pair(x, y, Direction.WEST);
                        collection.add(p);
                    }
                    else if (s2.owner != myID & s2.owner != 0)
                    {
                        //locations[x][y].site.setDangerous();
                        Pair p = new Pair(x, y, Direction.NORTH);
                        collection.add(p);
                    }
                    else if (s3.owner != myID & s3.owner != 0)
                    {
                        //locations[x][y].site.setDangerous();
                        Pair p = new Pair(x, y, Direction.EAST);
                        collection.add(p);
                    }
                    else if (s4.owner != myID & s4.owner != 0)
                    {
                        //locations[x][y].site.setDangerous();
                        Pair p = new Pair(x, y, Direction.SOUTH);
                        collection.add(p);
                    }//The Above are the ones with enemy/enemies around. If not, check 
                    else if (s1.owner == 0 | s2.owner == 0 | s3.owner == 0 | s4.owner == 0)
                    {
                        //locations[x][y].site.setBrim();
                        if (s1.owner == 0 & s1.production == myHighest)//No enemy around, but with a non-Agressive neighbor
                        {
                            Pair p = new Pair(x, y, Direction.WEST);
                            collection.add(p);
                        }    //locations[x][y].site.editDirection(Direction.WEST, highest-s1.production);
                        if (s2.owner == 0 & s2.production >= myHighest)
                        {
                            Pair p = new Pair(x, y, Direction.NORTH);
                            collection.add(p);
                        }
                            //locations[x][y].site.editDirection(Direction.NORTH, highest-s2.production);
                        if (s3.owner == 0 & s3.production >= myHighest)
                        {
                            Pair p = new Pair(x, y, Direction.EAST);
                            collection.add(p);
                        }
                            //locations[x][y].site.editDirection(Direction.EAST, highest-s3.production);
                        if (s4.owner == 0 & s4.production >= myHighest)
                        {
                            Pair p = new Pair(x, y, Direction.SOUTH);
                            collection.add(p);
                        }
                            //locations[x][y].site.editDirection(Direction.SOUTH, highest-s4.production);
                    }
                    else locations[x][y].site.setSafe();//If not the above, it must be safe(in the middle of all my territory)
                    //location[x][y].site.d1 = 0;
                    /*if(site.owner == myID) {
                        moves.add(new Move(location, Direction.randomDirection()));*/
                }
            }
        }
        if (!collection.isEmpty()) 
        {
            setDirection(collection, 1);
            //while (true) {myID=myID;}
        }
        //if (!collection2.isEmpty())
            ///setDirection_NotOnTurn(collection2, 4);
    }
    void setDirection(List<Pair> collection, int turn) {
        List<Pair> secondCollection = new ArrayList<Pair>();
        for (Pair temp : collection) locations[temp.x][temp.y].site.editDirection(temp.d, turn);
        for (Pair temp : collection){
            Location loc1 = getLocation(locations[temp.x][temp.y], Direction.WEST);
            Location loc2 = getLocation(locations[temp.x][temp.y], Direction.NORTH);
            Location loc3 = getLocation(locations[temp.x][temp.y], Direction.EAST);
            Location loc4 = getLocation(locations[temp.x][temp.y], Direction.SOUTH);
            Site s1 = loc1.getSite();
            Site s2 = loc2.getSite();
            Site s3 = loc3.getSite();
            Site s4 = loc4.getSite();
            if (s1.owner == myID & s1.min > turn + 1) 
            {
                Pair f = new Pair(loc1.x, loc1.y, Direction.EAST);
                secondCollection.add(f);
            }
            if (s2.owner == myID & s2.min > turn + 1) 
            {
                Pair f = new Pair(loc2.x, loc2.y, Direction.SOUTH);
                secondCollection.add(f);
            }
            if (s3.owner == myID & s3.min > turn + 1) 
            {
                Pair f = new Pair(loc3.x, loc3.y, Direction.WEST);
                secondCollection.add(f);
            }
            if (s4.owner == myID & s4.min > turn + 1) 
            {
                Pair f = new Pair(loc4.x, loc4.y, Direction.NORTH);
                secondCollection.add(f);
            }
        }
        if (!secondCollection.isEmpty()) setDirection(secondCollection, turn + 1);
    }
    /*void setDirection_NotOnTurn(List<Pair> collection, int turn) {
        List<Pair> secondCollection = new ArrayList<Pair>();
        for (Pair temp : collection) locations[temp.x][temp.y].site.editDirection(temp.d, turn);
        for (Pair temp : collection){
            Location loc1 = getLocation(locations[temp.x][temp.y], Direction.WEST);
            Location loc2 = getLocation(locations[temp.x][temp.y], Direction.NORTH);
            Location loc3 = getLocation(locations[temp.x][temp.y], Direction.EAST);
            Location loc4 = getLocation(locations[temp.x][temp.y], Direction.SOUTH);
            Site s1 = loc1.getSite();
            Site s2 = loc2.getSite();
            Site s3 = loc3.getSite();
            Site s4 = loc4.getSite();
            if (s1.min > turn + 1) 
            {
                Pair f = new Pair(loc1.x, loc1.y, Direction.WEST, 2);
                secondCollection.add(f);
            }
            if (s2.min > turn + 1) 
            {
                Pair f = new Pair(loc2.x, loc2.y, Direction.NORTH, 2);
                secondCollection.add(f);
            }
            if (s3.min > turn + 1) 
            {
                Pair f = new Pair(loc3.x, loc3.y, Direction.EAST, 2);
                secondCollection.add(f);
            }
            if (s4.min > turn + 1) 
            {
                Pair f = new Pair(loc4.x, loc4.y, Direction.SOUTH, 2);
                secondCollection.add(f);
            }
        }
        if (!secondCollection.isEmpty()) setDirection_NotOnTurn(secondCollection, turn + 2);
    }*/
    void tempMove() {
        List<Move> moves = new ArrayList<Move>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (locations[x][y].site.owner == myID)
                {
                    final Site tempsite = getSite(locations[x][y], locations[x][y].site.preferedMove);
                    moves.add(new Move(locations[x][y], locations[x][y].site.move(myID, tempsite)));
                }
            }
        }
        Networking.sendFrame(moves);
    }

    void importID(int tempID) {
        myID = tempID;
    }
}
