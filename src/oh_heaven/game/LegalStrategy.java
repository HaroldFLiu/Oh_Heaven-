package oh_heaven.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.Random;

public class LegalStrategy implements BotStrategy{
    private final Random random;
    public LegalStrategy(Random random) {
        this.random=random;
    }
    @Override
    public Card lead(Hand hand, Suit trumps, int current_bid, int target_bid) {
        int x = random.nextInt(hand.getNumberOfCards());
        return hand.get(x);
    }

    @Override
    public Card follow(Hand hand, Suit trumps, Hand trick, int current_bid, int target_bid) {
        int x;
        Suit leadSuit = (Suit) trick.getFirst().getSuit();
        while (true) {
            x = random.nextInt(hand.getNumberOfCards());
            if (hand.getNumberOfCardsWithSuit(leadSuit) == 0)
                break;
            else if (hand.get(x).getSuit() == leadSuit)
                break;
        }
        return hand.get(x);
    }
}
