package oh_heaven.game;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import java.util.*;
import java.util.stream.Collectors;
import oh_heaven.game.Player.*;

@SuppressWarnings("serial")
public class GameManager extends CardGame
{
    private static int seed = 30006;
    private static Random random = new Random(seed);

    // return random Enum value
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    // return random Card from Hand
    public static Card randomCard(Hand hand){
        int x = random.nextInt(hand.getNumberOfCards());
        return hand.get(x);
    }

    // return random Card from ArrayList
    public static Card randomCard(ArrayList<Card> list){
        int x = random.nextInt(list.size());
        return list.get(x);
    }

    private void dealingOut() {
        Hand pack = deck.toHand(false);
        // pack.setView(Oh_Heaven.this, new RowLayout(hideLocation, 0));
        for (int i = 0; i < nbStartCards; i++) {
            for (int j=0; j < nbPlayers; j++) {
                if (pack.isEmpty()) return;
                Card dealt = randomCard(pack);
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
    public final int madeBidBonus = 10;
    private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    private final int thinkingTime = 2000;
    private boolean enforceRules=false;

    private Player[] players = new Player[nbPlayers];
    private Human human;

    private GraphicsManager graphics = new GraphicsManager();

    public static void setSeed(int seed) {
        GameManager.seed = seed;
        GameManager.random = new Random(seed);
    }

    public void setEnforceRules(boolean enforceRules) {
        this.enforceRules = enforceRules;
    }

    public void setNbStartCards(int nbStartCards) {
        this.nbStartCards = nbStartCards;
    }

    public void setNbRounds(int nbRounds) {
        this.nbRounds = nbRounds;
    }

    private void setProperties()
    {
        this.setNbRounds(PropertiesLoader.getNbRounds());
        this.setNbStartCards(PropertiesLoader.getNbStartCards());
        this.setSeed(PropertiesLoader.getSeed());
        this.setEnforceRules(PropertiesLoader.getEnforceRules());
    }

    private void createPlayers()
    {
        human = new Human();
        players[0] = human;
        for (int i = 1; i < nbPlayers; i++)
            players[i] = new Bot();
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
            total += players[iP].makeBid(i+1 == nextPlayer + nbPlayers, total, nbStartCards, random);
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
        human.makeCardListener();
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
        final Suit trumps = randomEnum(Suit.class);
        Actor trumpsActor = graphics.setTrumpGraphics(this, trumps);
        // End trump suit

        Hand trick;
        int winner;
        Card winningCard;
        Suit lead;
        int nextPlayer = random.nextInt(nbPlayers); // randomly select player to lead for this round
        initBids(trumps, nextPlayer);

        for (int i = 0; i < nbPlayers; i++)
            graphics.updateScoreGraphics(this, players[i]);

        for (int i = 0; i < nbStartCards; i++) {
            trick = new Hand(deck);
            selected = players[nextPlayer].selectCard(this);
            // Lead with selected card
            graphics.setTrickView(this, trick, selected);
            // No restrictions on the card being lead
            lead = (Suit) selected.getSuit();
            selected.transfer(trick, true); // transfer to trick (includes graphic effect)
            winner = nextPlayer;
            winningCard = selected;
            // End Lead

            for (int j = 1; j < nbPlayers; j++) {
                if (++nextPlayer >= nbPlayers) nextPlayer = 0;  // From last back to first
                selected = players[nextPlayer].selectCard(this);

                // Follow with selected card
                graphics.setTrickView(this, trick, selected);
                // Check: Following card must follow suit if possible
                ruleViolationCheck(lead, nextPlayer);
                // End Check

                selected.transfer(trick, true); // transfer to trick (includes graphic effect)
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