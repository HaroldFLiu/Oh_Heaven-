package oh_heaven.game.Player;
import ch.aplu.jcardgame.*;
import oh_heaven.game.Suit;

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

        playerScore = 0;
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

    public abstract Card selectCard(CardGame game);

    // TODO CHANGE TO TRICK CLASS
    public void playCard(Hand trick, Card selected)
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

    public int getScore()
    {
        return playerScore;
    }

    public int getTricks()
    {
        return tricksWon;
    }

    public int getBid()
    {
        return bid;
    }

    public abstract Card lead(CardGame game, Suit trumps);
    public abstract Card follow(CardGame game, Suit trumps, Hand trick);
}