package oh_heaven.game;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import java.awt.Color;
import java.awt.Font;
import java.util.*;
import java.util.stream.Collectors;

public class GraphicsManager
{
    final String trumpImage[] = {"bigspade.gif","bigheart.gif","bigdiamond.gif","bigclub.gif"};


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
}
