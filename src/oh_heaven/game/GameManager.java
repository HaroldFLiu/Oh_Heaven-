package oh_heaven.game;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import java.awt.Color;
import java.awt.Font;
import java.util.*;
import java.util.stream.Collectors;

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

    private void dealingOut(Hand[] hands, int nbPlayers, int nbCardsPerPlayer) {
        Hand pack = deck.toHand(false);
        // pack.setView(Oh_Heaven.this, new RowLayout(hideLocation, 0));
        for (int i = 0; i < nbCardsPerPlayer; i++) {
            for (int j=0; j < nbPlayers; j++) {
                if (pack.isEmpty()) return;
                Card dealt = randomCard(pack);
                // System.out.println("Cards = " + dealt);
                dealt.removeFromHand(false);
                hands[j].insert(dealt, false);
                // dealt.transfer(hands[j], true);
            }
        }
    }

    public boolean rankGreater(Card card1, Card card2) {
        return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
    }

    private final String version = "1.0";
    public final int nbPlayers = 4;
    private int nbStartCards = 13;
    private int nbRounds = 3;
    public final int madeBidBonus = 10;
    private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    private final int thinkingTime = 2000;
    private Hand[] hands;
    private final Location hideLocation = new Location(-500, - 500);
    private boolean enforceRules=false;

    public void setStatus(String string) { setStatusText(string); }

    private int[] scores = new int[nbPlayers];
    private int[] tricks = new int[nbPlayers];
    private int[] bids = new int[nbPlayers];

    private GraphicsManager graphics = GraphicsManager.getInstance();

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

    private void initScores() {
        for (int i = 0; i < nbPlayers; i++) {
            scores[i] = 0;
        }
    }

    private void updateScores() {
        for (int i = 0; i < nbPlayers; i++) {
            scores[i] += tricks[i];
            if (tricks[i] == bids[i]) scores[i] += madeBidBonus;
        }
    }

    private void initTricks() {
        for (int i = 0; i < nbPlayers; i++) {
            tricks[i] = 0;
        }
    }

    private void initBids(Suit trumps, int nextPlayer) {
        int total = 0;
        for (int i = nextPlayer; i < nextPlayer + nbPlayers; i++) {
            int iP = i % nbPlayers;
            bids[iP] = nbStartCards / 4 + random.nextInt(2);
            total += bids[iP];
        }
        if (total == nbStartCards) {  // Force last bid so not every bid possible
            int iP = (nextPlayer + nbPlayers) % nbPlayers;
            if (bids[iP] == 0) {
                bids[iP] = 1;
            } else {
                bids[iP] += random.nextBoolean() ? -1 : 1;
            }
        }
        // for (int i = 0; i < nbPlayers; i++) {
        // 	 bids[i] = nbStartCards / 4 + 1;
        //  }
    }

    private Card selected;

    private void initRound() {
        hands = new Hand[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            hands[i] = new Hand(deck);
        }
        dealingOut(hands, nbPlayers, nbStartCards);
        for (int i = 0; i < nbPlayers; i++) {
            hands[i].sort(Hand.SortType.SUITPRIORITY, true);
        }
        // Set up human player for interaction
        CardListener cardListener = new CardAdapter()  // Human Player plays card
        {
            public void leftDoubleClicked(Card card) { selected = card; hands[0].setTouchEnabled(false); }
        };
        hands[0].addCardListener(cardListener);
        // graphics
        RowLayout[] layouts = new RowLayout[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            layouts[i] = graphics.getLayout(this, hands[i], i);
        }
//	    for (int i = 1; i < nbPlayers; i++) // This code can be used to visually hide the cards in a hand (make them face down)
//	      hands[i].setVerso(true);			// You do not need to use or change this code.
        // End graphics
    }

    private void playRound() {
        // Select and display trump suit
        final Suit trumps = randomEnum(Suit.class);
        final Actor trumpsActor = graphics.getTrumpActor(trumps);
        addActor(trumpsActor, graphics.getTrumpLocation());
        // End trump suit
        Hand trick;
        int winner;
        Card winningCard;
        Suit lead;
        int nextPlayer = random.nextInt(nbPlayers); // randomly select player to lead for this round
        initBids(trumps, nextPlayer);
        // initScore();
        for (int i = 0; i < nbPlayers; i++)
            graphics.updateScoreGraphics(this, i, scores[i], tricks[i], bids[i]);
        for (int i = 0; i < nbStartCards; i++) {
            trick = new Hand(deck);
            selected = null;
            // if (false) {
            if (0 == nextPlayer) {  // Select lead depending on player type
                hands[0].setTouchEnabled(true);
                setStatus("Player 0 double-click on card to lead.");
                while (null == selected) delay(100);
            } else {
                setStatusText("Player " + nextPlayer + " thinking...");
                delay(thinkingTime);
                selected = randomCard(hands[nextPlayer]);
            }
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
                selected = null;
                // if (false) {
                if (0 == nextPlayer) {
                    hands[0].setTouchEnabled(true);
                    setStatus("Player 0 double-click on card to follow.");
                    while (null == selected) delay(100);
                } else {
                    setStatusText("Player " + nextPlayer + " thinking...");
                    delay(thinkingTime);
                    selected = randomCard(hands[nextPlayer]);
                }
                // Follow with selected card
                graphics.setTrickView(this, trick, selected);
                // Check: Following card must follow suit if possible
                if (selected.getSuit() != lead && hands[nextPlayer].getNumberOfCardsWithSuit(lead) > 0) {
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
                // End Check
                selected.transfer(trick, true); // transfer to trick (includes graphic effect)
                System.out.println("winning: " + winningCard);
                System.out.println(" played: " + selected);
                // System.out.println("winning: suit = " + winningCard.getSuit() + ", rank = " + (13 - winningCard.getRankId()));
                // System.out.println(" played: suit = " +    selected.getSuit() + ", rank = " + (13 -    selected.getRankId()));
                if ( // beat current winner with higher card
                        (selected.getSuit() == winningCard.getSuit() && rankGreater(selected, winningCard)) ||
                                // trumped when non-trump was winning
                                (selected.getSuit() == trumps && winningCard.getSuit() != trumps)) {
                    System.out.println("NEW WINNER");
                    winner = nextPlayer;
                    winningCard = selected;
                }
                // End Follow
            }
            delay(600);
            trick.setView(this, new RowLayout(hideLocation, 0));
            trick.draw();
            nextPlayer = winner;
            setStatusText("Player " + nextPlayer + " wins trick.");
            tricks[nextPlayer]++;
            graphics.updateScoreGraphics(this, nextPlayer, scores[nextPlayer], tricks[nextPlayer], bids[nextPlayer]);
        }
        removeActor(trumpsActor);
    }

    public GameManager()
    {
        super(700, 700, 30);
        // property load
        this.setNbRounds(PropertiesLoader.getNbRounds());
        this.setNbStartCards(PropertiesLoader.getNbStartCards());
        this.setSeed(PropertiesLoader.getSeed());
        this.setEnforceRules(PropertiesLoader.getEnforceRules());

        setTitle("Oh_Heaven (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        initScores();
        graphics.initScoreGraphics(this, nbPlayers, scores, tricks, bids);
        for (int i=0; i <nbRounds; i++) {
            initTricks();
            initRound();
            playRound();
            updateScores();
        };
        for (int i=0; i <nbPlayers; i++)
            graphics.updateScoreGraphics(this, i, scores[i], tricks[i], bids[i]);
        int maxScore = 0;
        for (int i = 0; i <nbPlayers; i++) if (scores[i] > maxScore) maxScore = scores[i];
        Set <Integer> winners = new HashSet<Integer>();
        for (int i = 0; i <nbPlayers; i++) if (scores[i] == maxScore) winners.add(i);
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
}