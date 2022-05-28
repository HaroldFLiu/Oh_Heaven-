package oh_heaven.game;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import java.awt.Color;
import java.awt.Font;
import oh_heaven.game.Player.*;

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

    private final Location textLocation = new Location(350, 450);

    private final Location hideLocation = new Location(-500, - 500);

    private final String version = "1.0";

    public Actor displayTrumpSuit(CardGame game, Suit trump)
    {
        Actor trumpActor = new Actor("sprites/" + trumpImage[trump.ordinal()]);
        game.addActor(trumpActor, trumpLocation);
        return trumpActor;
    }

    public void setHandLayout(CardGame game, Player player)
    {
        RowLayout handLayout = new RowLayout(handLocations[player.getPlayerNumber()], this.handWidth);
        handLayout.setRotationAngle(90 * player.getPlayerNumber());

        Hand playerHand = player.getHand();
        playerHand.setView(game, handLayout);
        playerHand.setTargetArea(new TargetArea(trickLocation));
        playerHand.draw();
    }

    public void updateTrickDisplay(CardGame game, Hand trick, Card selected)
    {
        trick.setView(game, new RowLayout(trickLocation, (trick.getNumberOfCards()+2)*trickWidth));
        trick.draw();
        selected.setVerso(false);
    }

    private void setPlayerScore(CardGame game, Player player)
    {
        String text = "[" + String.valueOf(player.getScore()) + "]" + String.valueOf(player.getTricks()) + "/" + String.valueOf(player.getBid());
        scoreActors[player.getPlayerNumber()] = new TextActor(text, Color.WHITE, game.bgColor, bigFont);
        game.addActor(scoreActors[player.getPlayerNumber()], scoreLocations[player.getPlayerNumber()]);
    }

    public void initPlayerScoreGraphics(CardGame game, Player[] players)
    {
        for (Player p : players)
            setPlayerScore(game, p);
    }

    public void updatePlayerScore(CardGame game, Player player)
    {
        game.removeActor(scoreActors[player.getPlayerNumber()]);
        setPlayerScore(game, player);
    }

    public void addGameOverText(CardGame game)
    {
        game.addActor(new Actor("sprites/gameover.gif"), textLocation);
    }

    public void hideTrick(CardGame game, Hand trick)
    {
        trick.setView(game, new RowLayout(hideLocation, 0));
        trick.draw();
    }

    public void setTitle(CardGame game)
    {
        game.setTitle("Oh_Heaven (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
    }
}