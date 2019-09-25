import java.util.ArrayList;
import java.util.List;

public class RandomBot {

    public static void main(String[] args) throws java.io.IOException {

        final InitPackage iPackage = Networking.getInit();
        final int myID = iPackage.myID;
        final gameMap2 gameMap2 = iPackage.map;

        Networking.sendInit("RandomTerriBot");
        gameMap2.importID(myID);
        int size = gameMap2.width * gameMap2.height;

        while(true) {
            //List<Move> moves = new ArrayList<Move>();

            Networking.updateFrame(gameMap2);
            if (size >= 600) gameMap2.enemyDetection();
            else gameMap2.enemyDetectionBot();
            gameMap2.preMoveCompile();
            gameMap2.finalCheck();
            gameMap2.checkUnused();
            gameMap2.preMoveCompile();
            gameMap2.finalCheck();
            gameMap2.sendMoves();
            
            //Networking.sendFrame(moves);
        }
    }
}
