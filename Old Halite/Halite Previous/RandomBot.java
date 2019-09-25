import java.util.ArrayList;
import java.util.List;

public class RandomBot {
    public static void main(String[] args) throws java.io.IOException {
        InitPackage iPackage = Networking.getInit();
        int myID = iPackage.myID;
        GameMap gameMap = iPackage.map;
        //int[][] ms = new int[60][60];


        Networking.sendInit("Terry'sBot");

        while(true) {
            List<Move> moves = new ArrayList<Move>();

            Networking.updateFrame(gameMap);

            for (int y = 0; y < gameMap.height; y++)
            {
                for (int x = 0; x < gameMap.width; x++)
                {
                    final Location location = gameMap.getLocation(x, y);
                    final Site site = location.getSite();
                        Location loc1 = gameMap.getLocation(location,Direction.WEST);
                        Location loc2 = gameMap.getLocation(location,Direction.NORTH);
                        Location loc3 = gameMap.getLocation(location,Direction.EAST);
                        Location loc4 = gameMap.getLocation(location,Direction.SOUTH);
                        Site s1 = loc1.getSite();
                        Site s2 = loc2.getSite();
                        Site s3 = loc3.getSite();
                        Site s4 = loc4.getSite();

                        if (s1.owner != myID | s2.owner != myID | s3.owner != myID | s4.owner != myID) site.wait = true;
                        else site.wait = false;
                }
            }

            //actual program
            for (int y = 0; y < gameMap.height; y++)
            {
                for (int x = 0; x < gameMap.width; x++)
                {
                    final Location location = gameMap.getLocation(x, y);
                    final Site site = location.getSite();
                    if(site.owner == myID) {
                        int m = 0;
                        //Neighbors
                        Location loc1 = gameMap.getLocation(location,Direction.WEST);
                        Location loc2 = gameMap.getLocation(location,Direction.NORTH);
                        Location loc3 = gameMap.getLocation(location,Direction.EAST);
                        Location loc4 = gameMap.getLocation(location,Direction.SOUTH);
                        Site s1 = loc1.getSite();
                        Site s2 = loc2.getSite();
                        Site s3 = loc3.getSite();
                        Site s4 = loc4.getSite();

                        if (site.wait == true)//(s1.owner != myID | s2.owner != myID | s3.owner != myID | s4.owner != myID)
                        {
                            if (s1.owner != myID & site.strength - s1.strength > 4) m = 1;
                            else if (s2.owner != myID & site.strength - s2.strength > 4) m = 2;
                            else if (s3.owner != myID & site.strength - s3.strength > 4) m = 3;
                            else if (s4.owner != myID & site.strength - s4.strength > 4) m = 4;
                            else if (site.strength > 80) 
                            {
                                if (s1.owner == myID & s1.strength == 255 & s1.wait != true) 
                                {
                                    m = 1; 
                                    s1.direction = 3;
                                }
                                else if (s2.owner == myID & s2.strength == 255 & s2.wait != true) 
                                {
                                    m = 2; 
                                    s2.direction = 4;
                                }
                                else if (s3.owner == myID & s3.strength == 255 & s3.wait != true) 
                                {
                                    m = 3; 
                                    s3.direction = 1;
                                }
                                else if (s4.owner == myID & s4.strength == 255 & s4.wait != true) 
                                {
                                    m = 4; 
                                    s1.direction = 2;
                                }
                            }
                            else 
                            {
                                if (s1.owner != myID) site.need = s1.strength - site.strength + 4;
                                if (s2.owner != myID & (s2.strength - site.strength + 4) < site.need) site.need = s2.strength - site.strength + 4; 
                                if (s3.owner != myID & (s3.strength - site.strength + 4) < site.need) site.need = s3.strength - site.strength + 4; 
                                if (s4.owner != myID & (s4.strength - site.strength + 4) < site.need) site.need = s4.strength - site.strength + 4; 
                                m = 0;
                                if (s1.owner == myID & s1.wait & s1.strength > site.strength & s1.need < site.strength) m = 1;
                                else if (s2.owner == myID & s2.wait & s2.strength > site.strength & s2.need < site.strength) m = 2;
                                else if (s3.owner == myID & s3.wait & s3.strength > site.strength & s3.need < site.strength) m = 3;
                                else if (s4.owner == myID & s4.wait & s4.strength > site.strength & s4.need < site.strength) m = 4;
                            }
                                
                        }
                        else 
                        {
                            site.need = 255;
                            if (site.strength >= 8*site.production & site.strength < 255)
                            {
                                //Not Complete
                                m = site.direction;
                                if (m == 5)
                                {
                                    if (s1.direction != 3) m = 1;
                                    else if (s2.direction != 4) m = 2;
                                    else if (s3.direction != 1) m = 3;
                                    else if (s4.direction != 2) m = 4;
                                }
                            }
                            else if (site.strength == 255) 
                            {
                                if (s1.direction != 3) m = 1; 
                                else if (s2.direction != 4) m = 2;
                                else if (s3.direction != 1) m = 3;
                                else if (s4.direction != 2) m = 4;
                                if (s1.owner == myID & s1.direction == 3 & s1.wait != true) m = site.direction;
                                else if (s2.owner == myID & s2.direction == 4 & s2.wait != true) m = site.direction;
                                else if (s3.owner == myID & s3.direction == 1 & s3.wait != true) m = site.direction;
                                else if (s4.owner == myID & s4.direction == 2 & s4.wait != true) m = site.direction;
                            }
                            else m = 0;
                        }
                        //Execute Move
                        if (site.strength == 255 & (m == 0 | m == 5)) 
                        {
                            if (s1.direction != 3) m = 1; 
                            else if (s2.direction != 4) m = 2;
                            else if (s3.direction != 1) m = 3;
                            else if (s4.direction != 2) m = 4;
                            else 
                            {
                                s2.direction = 2;
                                s3.direction = 3;
                                s4.direction = 4;
                                m = 3;
                            }
                        }
                        if (m == 1 & s1.owner != myID & s1.wait != true) m = 3;
                        else if (m == 2 & s2.owner != myID & s2.wait != true) m = 4;
                        else if (m == 3 & s3.owner != myID & s3.wait != true) m = 1;
                        else if (m == 4 & s4.owner != myID & s4.wait != true) m = 2;
                        if (m == 0) moves.add(new Move(location, Direction.STILL));
                        else if (m == 1) moves.add(new Move(location, Direction.WEST));
                        else if (m == 2) moves.add(new Move(location, Direction.NORTH));
                        else if (m == 3) moves.add(new Move(location, Direction.EAST));
                        else if (m == 4) moves.add(new Move(location, Direction.SOUTH));
                        site.wait = false;
                        if (m != 0) site.direction = m;
                    }
                }
            }
             /*for (int y = 0; y < gameMap.height; y++)
            {
                for (int x = 0; x < gameMap.width; x++)
                {
                    final Location location = gameMap.getLocation(x, y);
                    final Site site = location.getSite();

                    if(site.owner == myID) {
                        int m = 0;
                        //Neighbors
                        Location loc1 = gameMap.getLocation(location,Direction.WEST);
                        Location loc2 = gameMap.getLocation(location,Direction.NORTH);
                        Location loc3 = gameMap.getLocation(location,Direction.EAST);
                        Location loc4 = gameMap.getLocation(location,Direction.SOUTH);
                        Site s1 = loc1.getSite();
                        Site s2 = loc2.getSite();
                        Site s3 = loc3.getSite();
                        Site s4 = loc4.getSite();
                        
                        if (s1.owner == myID & s2.owner == myID & s3.owner == myID & s4.owner == myID)
                        {
                            if (site.strength >= 8*site.production)
                            {
                                //Not Complete
                                m = 4;
                            }
                            else m = 0;
                        }
                        //Execute Move
                        if (m == 0) moves.add(new Move(location, Direction.STILL));
                        else if (m == 1) moves.add(new Move(location, Direction.WEST));
                        else if (m == 2) moves.add(new Move(location, Direction.NORTH));
                        else if (m == 3) moves.add(new Move(location, Direction.EAST));
                        else if (m == 4) moves.add(new Move(location, Direction.SOUTH));
                    }
                }
            }*/
            Networking.sendFrame(moves);
        }
    }
    /*public static int directionEnemy(int x, int y)
    {
        final Location location = gM.getLocation(x, y);
        final Site site = location.getSite();
        int a = 0, b = 0, c = 0, d = 0;
        a = distanceEnemy(a, 1, location);
        b = distanceEnemy(b, 2, location);
        c = distanceEnemy(c, 3, location);
        d = distanceEnemy(d, 4, location);

        if (a <= b & a <= c & a <= d) return 1;
        else if (b <= a & b <= c & b <= d) return 2;
        else if (c <= a & c <= b & c <= d) return 3;
        else if (d <= a & d <= b & d <= c) return 4;
        else return 0;
    }

    public static int distanceEnemy(int count, int direction, Location location)
    {
        Location loc1 = location;
        if (direction == 1) loc1 = gM.getLocation(location,Direction.WEST);
        else if (direction == 2) loc1 = gM.getLocation(location,Direction.NORTH);
        else if (direction == 3) loc1 = gM.getLocation(location,Direction.EAST);
        else if (direction == 4) loc1 = gM.getLocation(location,Direction.SOUTH);
        Site s1 = loc1.getSite();
        if (direction == 2 | direction == 4)
        {
            if (s1.owner == ID & count < gM.height/3) count = distanceEnemy(count, direction, location);
            else count++;
        }
        else 
        {
            if (s1.owner == ID & count < gM.width/3) count = distanceEnemy(count, direction, location);
            else count++;
        }
        return count;
    }*/

}
