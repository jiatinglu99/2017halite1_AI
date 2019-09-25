import java.util.ArrayList;
import java.util.List;

public class RandomBot {

    public static void main(String[] args) throws java.io.IOException {

        final InitPackage iPackage = Networking.getInit();
        final int myID = iPackage.myID;
        final GameMap gameMap = iPackage.map;

        Networking.sendInit("RandomTerriBot");
        gameMap.importID(myID);

        while(true) {
            //List<Move> moves = new ArrayList<Move>();

            Networking.updateFrame(gameMap);

            gameMap.enemyDetection();
            gameMap.preMoveCompile();
            gameMap.finalCheck();
            gameMap.sendMoves();
            
            //Networking.sendFrame(moves);
        }
    }
}
