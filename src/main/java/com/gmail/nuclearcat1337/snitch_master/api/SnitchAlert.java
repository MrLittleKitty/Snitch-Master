package com.gmail.nuclearcat1337.snitch_master.api;

import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.ILocation;
import com.gmail.nuclearcat1337.snitch_master.util.Location;
import net.minecraft.util.text.ITextComponent;

/**
 * Created by Mr_Little_Kitty on 7/9/2016.
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

    public String getPlayerName()
    {
        return playerName;
    }

    public ILocation getLocations()
    {
        return point;
    }

    public String getWorld()
    {
        return world;
    }

    public ITextComponent getRawMessage()
    {
        return rawMessage;
    }

    public void setRawMessage(ITextComponent rawMessage)
    {
        this.rawMessage = rawMessage;
    }
}
