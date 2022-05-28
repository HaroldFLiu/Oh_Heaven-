package oh_heaven.game;
import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import oh_heaven.game.Player.Player;

import java.util.ArrayList;
import java.util.Random;

public class RandomHandler
{
    private static final RandomHandler instance = new RandomHandler();

    private Random random;

    public static RandomHandler getInstance()
    {
        return instance;
    }

    public void setRandomSeed(int seed)
    {
        random = new Random(seed);
    }

    public <T extends Enum<?>> T randomEnum(Class<T> clazz)
    {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    // return random Card from Hand
    public Card getRandomCard(Hand hand)
    {
        int card = random.nextInt(hand.getNumberOfCards());
        return hand.get(card);
    }

    public Random getRandom()
    {
        return random;
    }

    public Player getRandomPlayer(Player[] players)
    {
        return players[random.nextInt(players.length)];
    }
}
