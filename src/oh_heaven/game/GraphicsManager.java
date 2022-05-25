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

    private static GraphicsManager instance = new GraphicsManager();

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
}
