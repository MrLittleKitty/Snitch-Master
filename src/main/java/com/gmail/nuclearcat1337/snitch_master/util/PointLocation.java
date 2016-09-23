package com.gmail.nuclearcat1337.snitch_master.util;

import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.ILocation;

/**
 * Created by Mr_Little_Kitty on 8/22/2016.
 */
public class PointLocation implements ILocation
{
    public int x,y,z;
    public String world;

    public PointLocation(int x, int y, int z, String world)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    @Override
    public String getWorld()
    {
        return world;
    }

    @Override
    public int getX()
    {
        return x;
    }

    @Override
    public int getY()
    {
        return y;
    }

    @Override
    public int getZ()
    {
        return z;
    }
}
