import java.util.ArrayList;
import java.util.List;

public class MyBot {

    public static void main(String[] args) throws java.io.IOException {

        final InitPackage iPackage = Networking.getInit();
        final int myID = iPackage.myID;
        final GameMap gameMap = iPackage.map;

        gameMap.mapAnalyze();
        Networking.sendInit("TerriBot");
        gameMap.importID(myID);
        int size = gameMap.width * gameMap.height;

        while(true) {
            //List<Move> moves = new ArrayList<Move>();

            Networking.updateFrame(gameMap);
            //if (size >= 600) gameMap.enemyDetectionBot();
            gameMap.enemyDetection();
            gameMap.preMoveCompile();
            gameMap.finalCheck();
            gameMap.sendMoves();
            
            //Networking.sendFrame(moves);
        }
    }
}
