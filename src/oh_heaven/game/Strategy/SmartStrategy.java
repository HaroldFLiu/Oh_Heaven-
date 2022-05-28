package oh_heaven.game.Strategy;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import oh_heaven.game.RandomHandler;
import oh_heaven.game.Suit;
import java.util.ArrayList;

public class SmartStrategy implements BotStrategy
{
    @Override
    public Card playFirstCard(Hand hand, Suit trumps, int tricksWon, int tricksTarget)
    {
        Card cardToPlay;
        boolean tryingToWinTrick = tricksWon != tricksTarget;

        while (true)
        {
            cardToPlay = RandomHandler.getInstance().getRandomCard(hand);

            boolean cardToPlaySuitIsTrumps = cardToPlay.getSuit() == trumps;
            int numberOfTrumpsInHand = hand.getNumberOfCardsWithSuit(trumps);

            if (tryingToWinTrick)
            {
                if (numberOfTrumpsInHand == 0 || cardToPlaySuitIsTrumps)
                    return cardToPlay;
            }
            else
                if (numberOfTrumpsInHand == hand.getNumberOfCards() || !cardToPlaySuitIsTrumps)
                    return cardToPlay;
        }
    }

    @Override
    public Card playSubsequentCard(Hand hand, Suit trumps, Hand trick, int tricksWon, int tricksTarget) {
        Suit leadSuit = (Suit) trick.getFirst().getSuit();
        boolean tryingToWinTrick = tricksWon != tricksTarget;
        boolean noLeadingSuit = hand.getNumberOfCardsWithSuit(leadSuit) == 0;
        Card cardToPlay = null;

        for (Card card : getValidCards(hand, leadSuit, noLeadingSuit))
            if (cardToPlay == null || isPreferredCard(card, cardToPlay, trumps, tryingToWinTrick, noLeadingSuit))
                cardToPlay = card;
        
        return cardToPlay;
    }

    private boolean isPreferredCard(Card card, Card cardToPlay, Suit trumps, boolean tryingToWinTrick, boolean noLeadingSuit)
    {
        if (cardToPlay == null)
            return true;

        if (!tryingToWinTrick)
        {
            // If no card of lead suit, play worst card
            if (noLeadingSuit)
                return (card.getSuit() == cardToPlay.getSuit() && card.getRankId() < cardToPlay.getRankId()) ||
                        (card.getSuit() == trumps && cardToPlay.getSuit() != trumps);

            // If a card of lead suit, play worst card with that suit
            return card.getRankId() < cardToPlay.getRankId();
        }

        // If no card of lead suit, play best card
        if (noLeadingSuit)
            return (card.getSuit() == cardToPlay.getSuit() && card.getRankId() < cardToPlay.getRankId()) ||
                    (card.getSuit() == trumps && cardToPlay.getSuit() != trumps);

        // If a card of lead suit, play best card with that suit
        return card.getRankId() > cardToPlay.getRankId();

    }

    ArrayList<Card> getValidCards(Hand hand, Suit leadSuit, boolean noLeadingSuit)
    {
        if (noLeadingSuit)
            return hand.getCardList();

        return hand.getCardsWithSuit(leadSuit);
    }
}