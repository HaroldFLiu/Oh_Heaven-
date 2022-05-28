package oh_heaven.game;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import java.util.*;
import java.util.stream.Collectors;
import oh_heaven.game.Player.*;

@SuppressWarnings("serial")
public class GameManager extends CardGame
{
    public final int nbPlayers = 4;
    private int nbStartCards = 13;
    private int nbRounds = 3;
    private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");

    private Player[] players = new Player[nbPlayers];

    private final GraphicsManager graphics = new GraphicsManager();

    private Trick trick;

    public GameManager()
    {
        super(700, 700, 30);

        setProperties();
        startGame();
        runGame();
        endGame();
    }

    private void setProperties()
    {
        nbRounds = PropertiesLoader.getNbRounds();
        nbStartCards = PropertiesLoader.getNbStartCards();
        boolean enforceRules = PropertiesLoader.getEnforceRules();
        trick = new Trick(enforceRules);
        setSeed(PropertiesLoader.getSeed());
    }

    public void setSeed(int seed) {
        RandomHandler.getInstance().setRandomSeed(seed);
    }

    private void startGame()
    {
        graphics.setTitle(this);
        setStatusText("Initializing...");

        this.createPlayers();

        graphics.initPlayerScoreGraphics(this, players);
    }

    private void createPlayers()
    {
        for (int i = 0; i < nbPlayers; i++)
            players[i] = PlayerFactory.getPlayer(PropertiesLoader.getPlayer(i));
    }

    private void runGame()
    {
        for (int i=0; i <nbRounds; i++) {
            startRound();
            playRound();
            updateScores();
        }

        for (Player player : players)
            graphics.updatePlayerScore(this, player);
    }

    private void startRound() {
        for (Player player : players)
            player.startRound(deck);

        dealOutCards();

        for (Player player : players)
        {
            player.sortHand();

            if (player instanceof Human)
                ((Human) player).makeCardListener();
        }

        setPlayerHandLayouts();
    }

    private void dealOutCards() {
        Hand pack = deck.toHand(false);

        for (int i = 0; i < nbStartCards; i++)
        {
            for (Player player : players)
            {
                if (pack.isEmpty())
                    return;

                Card dealt = RandomHandler.getInstance().getRandomCard(pack);
                dealt.removeFromHand(false);
                player.getHand().insert(dealt, false);
            }
        }
    }

    private void setPlayerHandLayouts()
    {
        for (Player player : players)
            graphics.setHandLayout(this, player);
    }

    private void playRound()
    {
        // Select and display the trump suit
        Actor trumpsActor = graphics.displayTrumpSuit(this, trick.newGame());

        Player startingPlayer = RandomHandler.getInstance().getRandomPlayer(players);
        makeBids(startingPlayer);

        Player currentPlayer = startingPlayer;

        for (Player player : players)
            graphics.updatePlayerScore(this, player);

        for (int i = 0; i < nbStartCards; i++)
        {
            trick.startNewTrick(deck);

            for (int j = 0; j < nbPlayers; j++)
            {
                Card cardPlayed = trick.playCard(this, currentPlayer);
                graphics.updateTrickDisplay(this, trick.getTrickHand(), cardPlayed);

                // Winner gets updated if it isn't the first card played
                if (j != 0)
                    trick.updateWinner(currentPlayer);

                currentPlayer = nextPlayer(currentPlayer);
            }

            endTrick();
        }

        removeActor(trumpsActor);
    }

    private void makeBids(Player startingPlayer) {
        int totalTricksBid = 0;
        Player currentPlayer = startingPlayer;

        for (int i = 1; i <= nbPlayers; i++)
        {
            totalTricksBid += currentPlayer.makeBid(i == nbPlayers, totalTricksBid, nbStartCards);
            currentPlayer = nextPlayer(currentPlayer);
        }
    }

    private Player nextPlayer(Player player)
    {
        return players[(player.getPlayerNumber()+1)%nbPlayers];
    }

    private void endTrick()
    {
        delay(600);
        graphics.hideTrick(this, trick.getTrickHand());
        Player winner = trick.getWinner();
        setStatusText("Player " + winner.getPlayerNumber() + " wins trick.");
        winner.winTrick();
        graphics.updatePlayerScore(this, winner);
    }

    private void updateScores()
    {
        for (Player player : players)
            player.updateScore();
    }

    private void endGame()
    {
        HashSet<Integer> winners = findWinners();

        String winText;
        if (winners.size() == 1)
            winText = "Game over. Winner is player: " + winners.iterator().next();
        else
            winText = "Game Over. Drawn winners are players: "
                    + String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toSet()));

        graphics.addGameOverText(this);
        setStatusText(winText);
        refresh();
    }

    private HashSet<Integer> findWinners()
    {
        int winningScore = 0;
        for (Player player : players)
            winningScore = Math.max(winningScore, player.getScore());

        HashSet<Integer> winners = new HashSet<Integer>();
        for (Player player : players)
            if (player.getScore() == winningScore)
                winners.add(player.getPlayerNumber());

        return winners;
    }
}