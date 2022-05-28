package oh_heaven.game.Player;
import oh_heaven.game.*;
import ch.aplu.jcardgame.*;
import oh_heaven.game.Strategy.*;

public class Bot extends Player
{
    private static final int thinkingTime = 2000;
    private final BotStrategy strategy;

    public Bot(BotStrategy strategy)
    {
        super();
        this.strategy = strategy;
    }

    @Override
    public int makeBid(boolean lastBid, int previousBids, int nbStartCards)
    {
        int bid = nbStartCards / 4 + RandomHandler.getInstance().getRandom().nextInt(2);

        // If total bids is equal to number of cards
        if (lastBid && previousBids + bid == nbStartCards)
        {
            if (bid == 0)
                bid = 1;
            else
                bid += RandomHandler.getInstance().getRandom().nextBoolean() ? -1 : 1;
        }

        this.bid = bid;
        return bid;
    }

    @Override
    public Card selectCard(CardGame game, Suit trumps, Hand trick)
    {
        game.setStatusText("Player " + this.getPlayerNumber() + " thinking...");
        game.delay(thinkingTime);
        return trick.isEmpty() ? strategy.playFirstCard(getHand(), trumps, getTricks(), getBid())
                : strategy.playSubsequentCard(getHand(),trumps,trick,getBid(),getBid());
    }
}
