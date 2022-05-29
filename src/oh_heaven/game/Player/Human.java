package oh_heaven.game.Player;
import ch.aplu.jcardgame.*;
import oh_heaven.game.RandomHandler;
import oh_heaven.game.Rank;
import oh_heaven.game.Suit;

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
    public int makeBid(boolean lastBid, int previousBids, int nbStartCards)
    {
        int bid = 0;
        if (!lastBid)
            bid = nbStartCards / 4 + RandomHandler.getInstance().getRandom().nextInt(2);
        else if (previousBids == nbStartCards)
        {
            if (this.bid == 0)
                this.bid = 1;
            else
                this.bid += RandomHandler.getInstance().getRandom().nextBoolean() ? -1 : 1;

            return this.bid;
        }
        else
        {
            return 0;
        }

        this.bid = bid;
        return bid;
    }

    @Override
    public Card selectCard(CardGame game, Suit trumps, Hand trick)
    {
        this.selected = null;

        getHand().setTouchEnabled(true);
        game.setStatusText("Player " + getPlayerNumber() + " double-click on card to lead.");
        while (null == this.selected) game.delay(100);
        return this.selected;
    }
}
