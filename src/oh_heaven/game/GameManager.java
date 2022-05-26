package oh_heaven.game;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import java.util.*;
import java.util.stream.Collectors;
import oh_heaven.game.Player.*;

@SuppressWarnings("serial")
public class GameManager extends CardGame
{
    private void dealingOut() {
        Hand pack = deck.toHand(false);
        // pack.setView(Oh_Heaven.this, new RowLayout(hideLocation, 0));
        for (int i = 0; i < nbStartCards; i++) {
            for (int j=0; j < nbPlayers; j++) {
                if (pack.isEmpty()) return;
                Card dealt = RandomHandler.getInstance().randomCard(pack);
                // System.out.println("Cards = " + dealt);
                dealt.removeFromHand(false);
                players[j].getHand().insert(dealt, false);
                // dealt.transfer(hands[j], true);
            }
        }
    }

    public boolean rankGreater(Card card1, Card card2) {
        return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
    }
    public final int nbPlayers = 4;
    private int nbStartCards = 13;
    private int nbRounds = 3;
    private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    private boolean enforceRules=false;

    private Player[] players = new Player[nbPlayers];

    private GraphicsManager graphics = new GraphicsManager();

    public static void setSeed(int seed) {
        RandomHandler.getInstance().setSeed(seed);
    }

    private void setProperties()
    {
        nbRounds = PropertiesLoader.getNbRounds();
        nbStartCards = PropertiesLoader.getNbStartCards();
        enforceRules = PropertiesLoader.getEnforceRules();
        this.setSeed(PropertiesLoader.getSeed());
    }

    private void createPlayers()
    {
        for (int i = 0; i < nbPlayers; i++)
            players[i] = PlayerFactory.getPlayer(PropertiesLoader.getPlayer(i));
    }
    private void startGame()
    {
        graphics.setTitle(this);
        setStatusText("Initializing...");

        this.createPlayers();

        graphics.initScoreGraphics(this, players);
    }

    private void runGame()
    {
        for (int i=0; i <nbRounds; i++) {
            initRound();
            playRound();
            updateScores();
        };

        for (int i=0; i <nbPlayers; i++)
            graphics.updateScoreGraphics(this, players[i]);
    }

    private void endGame()
    {
        int maxScore = 0;
        for (int i = 0; i <nbPlayers; i++)
            maxScore = Math.max(maxScore, players[i].getScore());

        HashSet<Integer> winners = new HashSet<Integer>();
        for (int i = 0; i <nbPlayers; i++)
            if (players[i].getScore() == maxScore)
                winners.add(i);

        String winText;
        if (winners.size() == 1) {
            winText = "Game over. Winner is player: " +
                    winners.iterator().next();
        }
        else {
            winText = "Game Over. Drawn winners are players: " +
                    String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toSet()));
        }

        graphics.addGameOverText(this);
        setStatusText(winText);
        refresh();
    }

    private void updateScores() {
        for (int i = 0; i < nbPlayers; i++) {
            players[i].updateScore();
        }
    }

    private void initBids(Suit trumps, int nextPlayer) {
        int total = 0;

        for (int i = nextPlayer; i < nextPlayer + nbPlayers; i++)
        {
            int iP = i % nbPlayers;
            total += players[iP].makeBid(i+1 == nextPlayer + nbPlayers, total, nbStartCards);
        }
    }

    private Card selected;

    private void initRound() {
        for (int i = 0; i < nbPlayers; i++)
            players[i].startRound(deck);

        dealingOut();

        for (int i = 0; i < nbPlayers; i++) {
            players[i].getHand().sort(Hand.SortType.SUITPRIORITY, true);
        }
        // Set up human player for interaction
        for (Player i:players) {
            if (i instanceof Human)
                ((Human) i).makeCardListener();
        }
        //human.makeCardListener();
        // graphics
        RowLayout[] layouts = new RowLayout[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            layouts[i] = graphics.getLayout(this, players[i]);
        }
//	    for (int i = 1; i < nbPlayers; i++) // This code can be used to visually hide the cards in a hand (make them face down)
//	      hands[i].setVerso(true);			// You do not need to use or change this code.
        // End graphics
    }

    private void ruleViolationCheck(Suit lead, int nextPlayer) {
        if (selected.getSuit() != lead && players[nextPlayer].getHand().getNumberOfCardsWithSuit(lead) > 0) {
            // Rule violation
            String violation = "Follow rule broken by player " + nextPlayer + " attempting to play " + selected;
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

    private void playRound() {
        // Select and display trump suit
        final Suit trumps = RandomHandler.getInstance().randomEnum(Suit.class);
        Actor trumpsActor = graphics.setTrumpGraphics(this, trumps);
        // End trump suit

        Hand trick;
        int winner;
        Card winningCard;
        Suit lead;
        int nextPlayer = RandomHandler.getInstance().getRandom().nextInt(nbPlayers); // randomly select player to lead for this round
        initBids(trumps, nextPlayer);

        for (int i = 0; i < nbPlayers; i++)
            graphics.updateScoreGraphics(this, players[i]);

        for (int i = 0; i < nbStartCards; i++) {
            trick = new Hand(deck);
            selected = players[nextPlayer].lead(this,trumps);
            // Lead with selected card
            graphics.setTrickView(this, trick, selected);
            // No restrictions on the card being lead
            lead = (Suit) selected.getSuit();
            players[nextPlayer].playCard(trick, selected);
            winner = nextPlayer;
            winningCard = selected;
            // End Lead

            for (int j = 1; j < nbPlayers; j++) {
                if (++nextPlayer >= nbPlayers) nextPlayer = 0;  // From last back to first
                selected = players[nextPlayer].follow(this,trumps,trick);

                // Follow with selected card
                graphics.setTrickView(this, trick, selected);
                // Check: Following card must follow suit if possible
                ruleViolationCheck(lead, nextPlayer);
                // End Check

                players[nextPlayer].playCard(trick, selected); // transfer to trick (includes graphic effect)
                System.out.println("winning: " + winningCard);
                System.out.println(" played: " + selected);
                // System.out.println("winning: suit = " + winningCard.getSuit() + ", rank = " + (13 - winningCard.getRankId()));
                // System.out.println(" played: suit = " +    selected.getSuit() + ", rank = " + (13 -    selected.getRankId()));
                if (newWinnerCheck(selected, winningCard, trumps)) {
                    System.out.println("NEW WINNER");
                    winner = nextPlayer;
                    winningCard = selected;
                }
                // End Follow
            }
            delay(600);
            graphics.hideTrick(this, trick);
            nextPlayer = winner;
            setStatusText("Player " + nextPlayer + " wins trick.");
            players[nextPlayer].winTrick();
            graphics.updateScoreGraphics(this, players[nextPlayer]);
        }
        removeActor(trumpsActor);
    }

    private boolean newWinnerCheck(Card selected, Card previous, Suit trumps) {
        return (selected.getSuit() == previous.getSuit() && rankGreater(selected, previous)) ||
                // trumped when non-trump was winning
                (selected.getSuit() == trumps && previous.getSuit() != trumps);
    }

    public GameManager()
    {
        super(700, 700, 30);

        setProperties();
        startGame();
        runGame();
        endGame();
    }
}