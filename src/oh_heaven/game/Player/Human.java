package oh_heaven.game.Player;
import ch.aplu.jcardgame.*;
import oh_heaven.game.Suit;

import java.util.Random;

public class Human extends Player {
    public Human()
    {
        super();
    }

    public void makeCardListener()
    {
        CardListener cardListener = new CardAdapter()  // Human Player plays card
        {
            public void leftDoubleClicked(Card card) { selected = card; getHand().setTouchEnabled(false); }
        };
        this.getHand().addCardListener(cardListener);
    }

    @Override
    public int makeBid(boolean lastBid, int previousBids, int nbStartCards, Random random)
    {
        int bid = nbStartCards / 4 + random.nextInt(2);

        // If total bids is equal to number of cards
        if (lastBid && previousBids + bid == nbStartCards)
            bid++;

        this.bid = bid;
        return bid;
    }

    @Override
    public Card selectCard(CardGame game)
    {
        this.selected = null;

        getHand().setTouchEnabled(true);
        game.setStatusText("Player " + getPlayerNumber() + " double-click on card to lead.");
        while (null == this.selected) game.delay(100);
        return this.selected;
    }

    @Override
    public Card lead(CardGame game, Suit trumps) {
        return selectCard(game);
    }

    @Override
    public Card follow(CardGame game, Suit trumps, Hand trick) {
        return selectCard(game);
    }
}
