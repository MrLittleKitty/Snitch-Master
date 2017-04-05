package com.gmail.nuclearcat1337.snitch_master.util;

/**
 * Created by Mr_Little_Kitty on 7/16/2016.
 */
public class GeneralUtils
{
    public static double DistanceSquared(int x1, int z1, int x2, int z2)
    {
        return Math.pow(x2 - x1, 2) + Math.pow(z2 - z1, 2);
    }

    public static double DistanceSquared(int x1, int z1, int y1, int x2, int z2, int y2)
    {
        return Math.pow(x2 - x1, 2) + Math.pow(z2 - z1, 2) + Math.pow(y2 - y1, 2);
    }

    //    public static int ToColor(float red, float green, float blue, float alpha)
    //    {
    //        int returnVal = (Normalize(alpha) << 24);
    //        returnVal = returnVal | (Normalize(blue) << 16);
    //        returnVal = returnVal | (Normalize(green) << 8);
    //        returnVal = returnVal | (Normalize(green));
    //        return  returnVal;
    //    }
    //
    //    private static final int RANGE = 127;
    //
    //    private static int Normalize(float value)
    //    {
    //        int b;
    //        if(Float.isNaN(value))
    //            b = 0;
    //        else if(value > 1)
    //            b = 1;
    //        else if (value < -1)
    //            b = -1;
    //        else
    //        {
    //            value = value * (RANGE);
    //            if(value >= 0)
    //                value += 0.5f;
    //            else
    //                value -= 0.5f;
    //            b = (int)value;
    //        }
    //        return b;
    //    }
}
