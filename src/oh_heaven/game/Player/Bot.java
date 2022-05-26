package oh_heaven.game.Player;
import oh_heaven.game.*;
import ch.aplu.jcardgame.*;
import java.util.*;

public class Bot extends Player
{
    private static final int thinkingTime = 2000;

    public Bot()
    {
        super();
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
    public void selectCard(CardGame game)
    {
        game.setStatusText("Player " + this.getPlayerNumber() + " thinking...");
        game.delay(thinkingTime);
        selected = GameManager.randomCard(this.getHand());
    }
}
