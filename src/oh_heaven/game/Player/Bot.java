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
        game.setStatusText("Player " + this.getPlayerNumber() + " thinking...");
        game.delay(thinkingTime);
        return trick.isEmpty() ? strategy.playFirstCard(getHand(), trumps, getTricks(), getBid())
                : strategy.playSubsequentCard(getHand(),trumps,trick,getBid(),getBid());
    }
}
