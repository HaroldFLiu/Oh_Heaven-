package oh_heaven.game.Strategy;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import oh_heaven.game.Suit;

public interface BotStrategy
{
    Card playFirstCard(Hand hand, Suit trumps, int tricksWon, int tricksTarget);
    Card playSubsequentCard(Hand hand, Suit trumps, Hand trick, int tricksWon, int tricksTarget);
}
