package oh_heaven.game.Player;
import ch.aplu.jcardgame.*;

public abstract class Player
{
    private static final int madeBidBonus = 10;
    private static int playerCount = 0;

    private Card selected;

    private Hand hand;
    private int playerScore;
    private int tricksWon;
    private int bid;
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

    public abstract int makeBid(boolean lastBid, int previousBids, int nbStartCards);

    public abstract void selectCard();

    // TODO CHANGE TO TRICK CLASS
    public abstract void playCard();

    public void winTrick()
    {
        tricksWon++;
    }

    public Hand getHand()
    {
        return hand;
    }
}