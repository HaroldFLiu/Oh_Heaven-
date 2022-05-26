package oh_heaven.game;
import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.Random;

public class RandomHandler
{
    private static final RandomHandler instance = new RandomHandler();

    private Random random;
    private int nbPlayers;

    private RandomHandler()
    {

    }

    public static RandomHandler getInstance()
    {
        return instance;
    }

    public void setRandom(int seed, int nbPlayers)
    {
        random = new Random(seed);
        this.nbPlayers = nbPlayers;
    }

    public <T extends Enum<?>> T randomEnum(Class<T> clazz)
    {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    // return random Card from Hand
    public Card randomCard(Hand hand)
    {
        int x = random.nextInt(hand.getNumberOfCards());
        return hand.get(x);
    }

    // return random Card from ArrayList
    public Card randomCard(ArrayList<Card> list)
    {
        int x = random.nextInt(list.size());
        return list.get(x);
    }

    public Random getRandom()
    {
        return random;
    }

    public int getRandomPlayerNumber()
    {
        return random.nextInt(nbPlayers);
    }
}
