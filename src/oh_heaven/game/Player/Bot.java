package oh_heaven.game.Player;
import oh_heaven.game.*;
import ch.aplu.jcardgame.*;
import java.util.*;

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
    public int makeBid(boolean lastBid, int previousBids, int nbStartCards, Random random)
    {
        int bid = nbStartCards / 4 + random.nextInt(2);

        // If total bids is equal to number of cards
        if (lastBid && previousBids + bid == nbStartCards)
        {
            if (bid == 0)
                bid = 1;
            else
                bid += random.nextBoolean() ? -1 : 1;
        }

        this.bid = bid;
        return bid;
    }

    @Override
    public Card selectCard(CardGame game)
    {
        game.setStatusText("Player " + this.getPlayerNumber() + " thinking...");
        game.delay(thinkingTime);
        this.selected = GameManager.randomCard(this.getHand());

        return null;
    }

    @Override
    public Card lead(CardGame game, Suit trumps) {
        game.setStatusText("Player " + this.getPlayerNumber() + " thinking...");
        game.delay(thinkingTime);
        return strategy.lead(this.getHand(),trumps,this.getTricks(), this.getBid());
    }

    @Override
    public Card follow(CardGame game, Suit trumps, Hand trick) {
        game.setStatusText("Player " + this.getPlayerNumber() + " thinking...");
        game.delay(thinkingTime);
        return strategy.follow(this.getHand(),trumps,trick,this.getBid(),this.getBid());
    }

}
