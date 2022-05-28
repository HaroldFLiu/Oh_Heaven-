package oh_heaven.game;

import oh_heaven.game.Player.Bot;
import oh_heaven.game.Player.Human;
import oh_heaven.game.Player.Player;
import oh_heaven.game.Strategy.*;

public class PlayerFactory {
    public static Player getPlayer(String str) {
        if (str.equals("human"))
            return getHuman();
        else if (str.equals("smart")) {
            return getSmart();
        }
        else if (str.equals("legal")) {
            return getLegal();
        }
        else {
            return getRandom();
        }
    }
    private static Human getHuman() {
        return new Human();
    }
    private static Bot getLegal() {
        return new Bot(new LegalStrategy());
    }
    private static Bot getSmart() {
        return new Bot(new SmartStrategy());
    }
    private static Bot getRandom() {
        return new Bot(new RandomStrategy());
    }
}
