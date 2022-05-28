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

    private Player[] players = new Player[nbPlayers];

    private final GraphicsManager graphics = new GraphicsManager();

    private Trick trick;

    public void setSeed(int seed) {
        RandomHandler.getInstance().setRandom(seed, nbPlayers);
    }

    private void setProperties()
    {
        nbRounds = PropertiesLoader.getNbRounds();
        nbStartCards = PropertiesLoader.getNbStartCards();
        boolean enforceRules = PropertiesLoader.getEnforceRules();
        trick = new Trick(enforceRules);
        setSeed(PropertiesLoader.getSeed());
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
        }

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

    private void initBids(int nextPlayer) {
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
        
        RowLayout[] layouts = new RowLayout[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            layouts[i] = graphics.getLayout(this, players[i]);
        }
    }

    private void playRound()
    {
        // Select and display the trump suit
        Actor trumpsActor = graphics.setTrumpGraphics(this, trick.newGame());

        int nextPlayer = RandomHandler.getInstance().getRandomPlayerNumber(); // randomly select player to lead for this round
        initBids(nextPlayer);

        for (int i = 0; i < nbPlayers; i++)
            graphics.updateScoreGraphics(this, players[i]);

        for (int i = 0; i < nbStartCards; i++)
        {
            trick.newTrick(deck);

            for (int j = 0; j < nbPlayers; j++)
            {
                Player currentPlayer = players[nextPlayer];
                Card cardPlayed = trick.playCard(this, currentPlayer);
                graphics.setTrickView(this, trick.getTrickHand(), cardPlayed);

                // Winner gets updated if it isn't the first card played
                if (j != 0)
                    trick.updateWinner(currentPlayer);

                if (++nextPlayer == nbPlayers)
                    nextPlayer = 0;
            }

            delay(600);
            graphics.hideTrick(this, trick.getTrickHand());
            Player winner = trick.getWinner();
            setStatusText("Player " + winner.getPlayerNumber() + " wins trick.");
            winner.winTrick();
            graphics.updateScoreGraphics(this, winner);
        }

        removeActor(trumpsActor);
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