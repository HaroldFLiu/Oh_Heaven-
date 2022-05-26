package oh_heaven.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.Random;

public class SmartStrategy implements BotStrategy{
    private final Random random;
    public SmartStrategy(Random random) {
        this.random=random;
    }
    @Override
    public Card lead(Hand hand, Suit trumps, int current_bid, int target_bid) {
        if (current_bid==target_bid) {
            while (true){
                int x = random.nextInt(hand.getNumberOfCards());
                if (hand.getNumberOfCardsWithSuit(trumps) == hand.getNumberOfCards()){
                    return hand.get(x);
                }
                if (hand.get(x).getSuit() != trumps)
                    return hand.get(x);
            }
        }
        else {
            while (true){
                int x = random.nextInt(hand.getNumberOfCards());
                if (hand.getNumberOfCardsWithSuit(trumps) == 0){
                    return hand.get(x);
                }
                if (hand.get(x).getSuit() == trumps)
                    return hand.get(x);
            }
        }
    }

    @Override
    public Card follow(Hand hand, Suit trumps, Hand trick, int current_bid, int target_bid) {
        Suit leadSuit = (Suit) trick.getFirst().getSuit();

        // if no card of lead suit try playing best card
        if (hand.getNumberOfCardsWithSuit(leadSuit) == 0) {
            Card temp = null;
            for (Card card : hand.getCardList()) {
                if (temp == null || (card.getSuit() == temp.getSuit() && card.getRankId()<temp.getRankId()) ||
                        (card.getSuit() == trumps && temp.getSuit() != trumps)) {
                    temp = card;
                }
            }
            return temp;
        }
        // play best card with suit
        else {
            Card temp = null;
            for (Card card : hand.getCardsWithSuit(trumps)) {
                if (temp == null || card.getRankId()<temp.getRankId()) {
                    temp = card;
                }
            }
            return temp;
        }
    }

}