package oh_heaven.game.Strategy;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import oh_heaven.game.RandomHandler;
import oh_heaven.game.Suit;

public class RandomStrategy implements BotStrategy
{
    @Override
    public Card playFirstCard(Hand hand, Suit trumps, int tricksWon, int tricksTarget)
    {
        return RandomHandler.getInstance().getRandomCard(hand);
    }

    @Override
    public Card playSubsequentCard(Hand hand, Suit trumps, Hand trick, int tricksWon, int tricksTarget)
    {
        return RandomHandler.getInstance().getRandomCard(hand);
    }
}
