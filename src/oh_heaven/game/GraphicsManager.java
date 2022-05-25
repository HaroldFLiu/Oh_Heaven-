package oh_heaven.game;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import java.awt.Color;
import java.awt.Font;
import java.util.*;
import java.util.stream.Collectors;

public class GraphicsManager
{
    private final String trumpImage[] = {"bigspade.gif","bigheart.gif","bigdiamond.gif","bigclub.gif"};
    private final Location trumpLocation = new Location(50, 50);

    private final int handWidth = 400;
    private final Location[] handLocations = {
            new Location(350, 625),
            new Location(75, 350),
            new Location(350, 75),
            new Location(625, 350)
    };

    private final int trickWidth = 40;
    private final Location trickLocation = new Location(350, 350);

    private final Location[] scoreLocations = {
            new Location(575, 675),
            new Location(25, 575),
            new Location(575, 25),
            // new Location(650, 575)
            new Location(575, 575)
    };
    private Actor[] scoreActors = {null, null, null, null };
    private Font bigFont = new Font("Serif", Font.BOLD, 36);

    private static GraphicsManager instance = new GraphicsManager();

    private final Location textLocation = new Location(350, 450);

    private GraphicsManager() {}

    public static GraphicsManager getInstance()
    {
        return instance;
    }

    public Actor getTrumpActor(Suit trump)
    {
        return new Actor("sprites/" + trumpImage[trump.ordinal()]);
    }

    public Location getTrumpLocation()
    {
        return trumpLocation;
    }

    // todo CHANGE TO PLAYER INSTEAD OF INTEGER
    public RowLayout getLayout(CardGame game, Hand hand, int player)
    {
        RowLayout layout = new RowLayout(handLocations[player], this.handWidth);
        layout.setRotationAngle(90 * player);

        hand.setView(game, layout);
        hand.setTargetArea(new TargetArea(trickLocation));
        hand.draw();
        return layout;
    }

    public void setTrickView(CardGame game, Hand trick, Card selected)
    {
        trick.setView(game, new RowLayout(trickLocation, (trick.getNumberOfCards()+2)*trickWidth));
        trick.draw();
        selected.setVerso(false);
    }

    private void setScore(CardGame game, int player, int score, int trick, int bid)
    {
        String text = "[" + String.valueOf(score) + "]" + String.valueOf(trick) + "/" + String.valueOf(bid);
        scoreActors[player] = new TextActor(text, Color.WHITE, game.bgColor, bigFont);
        game.addActor(scoreActors[player], scoreLocations[player]);
    }

    //TODO MAKE THIS WORK WITH PLAYERS INSTEAD
    public void initScoreGraphics(CardGame game, int nbPlayers, int[] scores, int[] tricks, int[] bids)
    {
        for (int i = 0; i < nbPlayers; i++)
            setScore(game, i, scores[i], tricks[i], bids[i]);
    }

    public void updateScoreGraphics(CardGame game, int player, int score, int trick, int bid)
    {
        game.removeActor(scoreActors[player]);
        setScore(game, player, score, trick, bid);
    }

    public void addGameOverText(CardGame game)
    {
        game.addActor(new Actor("sprites/gameover.gif"), textLocation);
    }
}
