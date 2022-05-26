package oh_heaven.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class LegalStrategy implements BotStrategy{
    @Override
    public Card lead(Hand hand, Suit trumps, int current_bid, int target_bid) {
        int x = RandomHandler.getInstance().getRandom().nextInt(hand.getNumberOfCards());
        return hand.get(x);
    }

    @Override
    public Card follow(Hand hand, Suit trumps, Hand trick, int current_bid, int target_bid) {
        int x;
        Suit leadSuit = (Suit) trick.getFirst().getSuit();
        while (true) {
            x = RandomHandler.getInstance().getRandom().nextInt(hand.getNumberOfCards());
            if (hand.getNumberOfCardsWithSuit(leadSuit) == 0)
                break;
            else if (hand.get(x).getSuit() == leadSuit)
                break;
        }
        return hand.get(x);
    }
}
