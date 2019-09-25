import java.util.ArrayList;
import java.util.List;

public class MyBot {

    public static void main(String[] args) throws java.io.IOException {

        final InitPackage iPackage = Networking.getInit();
        final int myID = iPackage.myID;
        final GameMap gameMap = iPackage.map;

        Networking.sendInit("TerriBot");
        gameMap.importID(myID);

        while(true) {
            //List<Move> moves = new ArrayList<Move>();

            Networking.updateFrame(gameMap);

            gameMap.enemyDetection();
            gameMap.tempMove();
            
            //Networking.sendFrame(moves);
        }
    }
}