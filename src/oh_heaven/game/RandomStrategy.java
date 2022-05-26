package oh_heaven.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.CardGame;
import ch.aplu.jcardgame.Hand;

import java.util.Random;

public class RandomStrategy implements BotStrategy{
    @Override
    public Card lead(Hand hand, Suit trumps, int current_bid, int target_bid) {
        return RandomCard.getInstance().randomCard(hand);
    }

    @Override
    public Card follow(Hand hand, Suit trumps, Hand trick, int current_bid, int target_bid) {
        return RandomCard.getInstance().randomCard(hand);
    }
}
