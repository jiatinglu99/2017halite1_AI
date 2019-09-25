import java.util.ArrayList;
import java.util.List;

public class MyBot {
    public static void main(String[] args) throws java.io.IOException {

        final InitPackage iPackage = Networking.getInit();
        final int myID = iPackage.myID;
        final GameMap gameMap = iPackage.map;

        Networking.sendInit("MyJavaBot");

        while(true) {
            List<Move> moves = new ArrayList<Move>();

            Networking.updateFrame(gameMap);

            for (int y = 0; y < gameMap.height; y++) {
                for (int x = 0; x < gameMap.width; x++) {
int tempmove=0;
                    final Location location = gameMap.getLocation(x, y);
                    final Site site = location.getSite();
                    if(site.owner == myID) {
Location loc1 = gameMap.getLocation(location, Direction.WEST);
Location loc2 = gameMap.getLocation(location, Direction.NORTH);
Location loc3 = gameMap.getLocation(location, Direction.EAST);
Location loc4 = gameMap.getLocation(location, Direction.SOUTH);
Site s1 = loc1.getSite();
Site s2 = loc2.getSite();
Site s3 = loc3.getSite();
Site s4 = loc4.getSite();
site.safe = false;
while (true)
{
if (s1.owner != myID)
	{if (s1.strength < site.strength) {tempmove = 1; break;}
	else tempmove = Direction.STILL;}
if (s2.owner != myID)
	{if (s2.strength < site.strength) {tempmove = 2;break;}
	else tempmove = Direction.STILL;}
if (s3.owner != myID)
	{if (s3.strength < site.strength) {tempmove = 3;break;}
	else tempmove = Direction.STILL;}
if (s4.owner != myID)
	{if (s4.strength < site.strength) {tempmove = 4;break;}
	else tempmove = Direction.STILL;}
if (s1.owner != myID & s2.owner != myID & s3.owner != myID & s4.owner != myID) 
 tempmove = 0;
else site.safe = true;
break;
}

if (site.safe == true) 
{
if (site.production <= 3) {if (site.strength < 10) tempmove=0; else tempmove=5;}
else if (site.production > 3 & site.production < 5) {if (site.strength < 15) tempmove=0; else tempmove=5;}
else if (site.production  >= 5) {if (site.strength < 20) tempmove=0; else tempmove=5; }
}
if (tempmove==0) moves.add(new Move(location, Direction.STILL));
else if (tempmove==1) moves.add(new Move(location, Direction.WEST));
else if (tempmove==2) moves.add(new Move(location, Direction.NORTH));
else if (tempmove==3) moves.add(new Move(location, Direction.EAST));
else if (tempmove==4) moves.add(new Move(location, Direction.SOUTH));
else if (tempmove==5) moves.add(new Move(location, Direction.random()));
                }
            }
}
            Networking.sendFrame(moves);
        }
    }
}
