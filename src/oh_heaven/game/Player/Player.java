package oh_heaven.game.Player;
import ch.aplu.jcardgame.*;
import java.util.*;

public abstract class Player
{
    private static final int madeBidBonus = 10;
    private static int playerCount = 0;

    protected Card selected;

    private Hand hand;
    private int playerScore;
    private int tricksWon;
    protected int bid;
    private int playerNumber;

    public Player()
    {
        playerNumber = playerCount++;
    }

    public void startRound(Deck deck)
    {
        tricksWon = 0;
        hand = new Hand(deck);
    }

    public void updateScore()
    {
        playerScore += tricksWon;
        if (tricksWon == bid)
            playerScore += madeBidBonus;
    }

    public abstract int makeBid(boolean lastBid, int previousBids, int nbStartCards, Random random);

    public abstract void selectCard(CardGame game);

    // TODO CHANGE TO TRICK CLASS
    public void playCard(Hand trick)
    {
        selected.transfer(trick, true);
    }

    public void winTrick()
    {
        tricksWon++;
    }

    public Hand getHand()
    {
        return hand;
    }

    public int getPlayerNumber()
    {
        return playerNumber;
    }
}