package oh_heaven.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public interface BotStrategy {
    Card lead(Hand hand, Suit trumps, int current_bid, int target_bid);
    Card follow(Hand hand, Suit trumps, Hand trick, int current_bid, int target_bid);
}
