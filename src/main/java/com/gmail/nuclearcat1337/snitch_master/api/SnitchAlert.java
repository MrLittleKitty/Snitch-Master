package com.gmail.nuclearcat1337.snitch_master.api;

import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.ILocation;
import com.gmail.nuclearcat1337.snitch_master.util.Location;
import net.minecraft.util.text.ITextComponent;

/**
 * Created by Mr_Little_Kitty on 7/9/2016.
 * Represents a Snitch alert received in chat.
 */
public class SnitchAlert
{
    private final String playerName;
    private final ILocation point;
    private final String world;
    private ITextComponent rawMessage;

    public SnitchAlert(String player, int x, int y, int z, String world, ITextComponent rawMessage)
    {
        this.playerName = player;
        this.point = new Location(x,y,z,world);
        this.world = world;
        this.rawMessage = rawMessage;
    }

    /**
     * Returns the name of the player that triggered the Snitch alert.
     */
    public String getPlayerName()
    {
        return playerName;
    }

    /**
     * Returns the location of the Snitch that was triggered.
     */
    public ILocation getLocations()
    {
        return point;
    }

    /**
     * Returns the name of the world that the triggered Snitch is in.
     */
    public String getWorld()
    {
        return world;
    }

    /**
     * Returns the raw Snitch alert message.
     */
    public ITextComponent getRawMessage()
    {
        return rawMessage;
    }

    /**
     * Sets the raw message to be displayed to the player.
     */
    public void setRawMessage(ITextComponent rawMessage)
    {
        this.rawMessage = rawMessage;
    }
}
