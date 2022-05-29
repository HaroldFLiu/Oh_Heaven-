package oh_heaven.game;

import ch.aplu.jcardgame.*;
import oh_heaven.game.Player.*;

public class Trick
{
    private Hand trickHand;
    private Suit currentSuit;

    private Suit trumpSuit;

    private Player winner;
    private Card winningCard;
    private Card cardPlayed;

    private boolean enforceRules;

    public Trick(boolean enforceRules)
    {
        this.enforceRules = enforceRules;
    }

    public Suit newGame()
    {
        trumpSuit = RandomHandler.getInstance().randomEnum(Suit.class);
        return trumpSuit;
    }

    public void startNewTrick(Deck deck)
    {
        trickHand = new Hand(deck);
        currentSuit = null;
    }

    public Card playCard(CardGame game, Player player)
    {
        boolean isFirstCard = currentSuit == null;

        cardPlayed = player.selectCard(game, trumpSuit, getTrickHand());

        if (isFirstCard)
        {
            currentSuit = (Suit) cardPlayed.getSuit();
            winningCard = cardPlayed;
            winner = player;
        }
        else
            ruleViolationCheck(player); // Check the player can play the card

        player.playCard(trickHand, cardPlayed);

        return cardPlayed;
    }

    private boolean newCardIsWinner() {
        return (cardPlayed.getSuit() == winningCard.getSuit() && cardPlayed.getRankId() < winningCard.getRankId()) ||
                // trumped when non-trump was winning
                (cardPlayed.getSuit() == trumpSuit && winningCard.getSuit() != trumpSuit);
    }

    public void updateWinner(Player player)
    {
        // Print out current status of cards played
        System.out.println("winning: " + winningCard);
        System.out.println(" played: " + cardPlayed);

        if (newCardIsWinner())
        {
            System.out.println("NEW WINNER");
            winningCard = cardPlayed;
            winner = player;
        }
    }

    private void ruleViolationCheck(Player player) {
        if (cardPlayed.getSuit() != currentSuit && player.getHand().getNumberOfCardsWithSuit(currentSuit) > 0) {
            // Rule violation
            String violation = "Follow rule broken by player " + player.getPlayerNumber() + " attempting to play " + cardPlayed;
            System.out.println(violation);
            if (enforceRules)
                try {
                    throw(new BrokeRuleException(violation));
                } catch (BrokeRuleException e) {
                    e.printStackTrace();
                    System.out.println("A cheating player spoiled the game!");
                    System.exit(0);
                }
        }

    }

    public Hand getTrickHand()
    {
        return trickHand;
    }

    public Player getWinner()
    {
        return winner;
    }
}
