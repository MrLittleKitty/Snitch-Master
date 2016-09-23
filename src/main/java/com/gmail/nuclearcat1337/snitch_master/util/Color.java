package com.gmail.nuclearcat1337.snitch_master.util;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import org.apache.commons.lang3.SerializationException;

/**
 * Created by Mr_Little_Kitty on 7/3/2016.
 */
public class Color
{
    private int red,green,blue;

    public Color(int red, int green, int blue)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Color(String serializedString)
    {
        String[] args = serializedString.replace("{","").replace("}","").split(":");
        if(args.length != 3)
            throw new SerializationException("Invalid color string: "+serializedString);

        red = Integer.parseInt(args[0]);
        green = Integer.parseInt(args[1]);
        blue = Integer.parseInt(args[2]);
    }

    public double getRed()
    {
        return red/255D;
    }

    public double getGreen()
    {
        return green/255D;
    }

    public double getBlue()
    {
        return blue/255D;
    }

    public String serialize()
    {
        return "{"+red+":"+green+":"+blue+"}";
    }
}
