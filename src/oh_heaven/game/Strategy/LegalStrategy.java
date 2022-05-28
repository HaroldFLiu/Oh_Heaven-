package oh_heaven.game.Strategy;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import oh_heaven.game.RandomHandler;
import oh_heaven.game.Suit;

public class LegalStrategy implements BotStrategy
{
    @Override
    public Card playFirstCard(Hand hand, Suit trumps, int tricksWon, int tricksTarget)
    {
        return RandomHandler.getInstance().getRandomCard(hand);
    }

    @Override
    public Card playSubsequentCard(Hand hand, Suit trumps, Hand trick, int tricksWon, int tricksTarget)
    {
        Card cardToPlay;
        Suit leadSuit = (Suit) trick.getFirst().getSuit();

        while (true)
        {
            cardToPlay = RandomHandler.getInstance().getRandomCard(hand);
            if (hand.getNumberOfCardsWithSuit(leadSuit) == 0)
                break;
            else if (cardToPlay.getSuit() == leadSuit)
                break;
        }

        return cardToPlay;
    }
}
