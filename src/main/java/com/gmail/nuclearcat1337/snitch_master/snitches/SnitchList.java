package com.gmail.nuclearcat1337.snitch_master.snitches;

import com.gmail.nuclearcat1337.snitch_master.api.SnitchListQualifier;
import com.gmail.nuclearcat1337.snitch_master.util.Color;

import java.util.*;

/**
 * Created by Mr_Little_Kitty on 6/25/2016.
 */
public class SnitchList
{
    public static final String MAX_NAME_CHARACTERS = "WWWWWWWWWWWWWWWWWWWW";

    private static Color defaultColor = new Color(240, 255, 240); //"HoneyDew"
    private static String defaultName = "Undefined";

    private int renderPriority;
    private String listName;
    private Color listColor;
    private SnitchListQualifier listQualifier;
    private boolean renderSnitches;

    //private final Point useablePt = new Point(0,0,0);

    public SnitchList(SnitchListQualifier listQualifier,boolean render)
    {
        listName = defaultName;
        listColor = defaultColor;
        this.listQualifier = listQualifier;
        this.renderSnitches = render;
    }

    public SnitchList(SnitchListQualifier listQualifier, boolean render, String name)
    {
        listName = name;
        listColor = defaultColor;
        this.listQualifier = listQualifier;
        this.renderSnitches = render;
    }

    public SnitchList(SnitchListQualifier listQualifier, boolean render, Color color)
    {
        listName = defaultName;
        listColor = color;
        this.listQualifier = listQualifier;
        this.renderSnitches = render;
    }

    public SnitchList(SnitchListQualifier listQualifier, boolean render, String name, Color color)
    {
        listName = name;
        listColor = color;
        this.listQualifier = listQualifier;
        this.renderSnitches = render;
    }

    public String getListName()
    {
        return listName;
    }

    public void setListName(String name)
    {
        this.listName = name;
    }

    public int getRenderPriority()
    {
        return renderPriority;
    }

    public void setRenderPriority(int newRenderPriority)
    {
        this.renderPriority = newRenderPriority;
    }

    public Color getListColor()
    {
        return listColor;
    }

    public void setListColor(Color newColor)
    {
        this.listColor = newColor;
    }

    public boolean shouldRenderSnitches()
    {
        return renderSnitches;
    }

    public void setShouldRenderSnitches(boolean render)
    {
        this.renderSnitches = render;
    }

    public SnitchListQualifier getQualifier()
    {
        return listQualifier;
    }

    private static final SnitchListQualifier friendly = new SnitchListQualifier("origin == 'jalist'");
    //private static final SnitchListQualifier hostile = new SnitchListQualifier("qualifier == 'hostile'");
    private static final SnitchListQualifier neutral = new SnitchListQualifier("origin == 'manual'");

    public static SnitchList[] getDefaultSnitchLists()
    {
        return new SnitchList[]{ new SnitchList(SnitchList.friendly, true, "Friendly",new Color(0, (int)(0.56D*255D), 255)),
                new SnitchList(SnitchList.neutral, true, "Neutral",new Color(238, 210, 2))}; //"Safety Yellow"
    }

    private static final int NUMBER_OF_CSV_PARAMS = 5;

    public static String ConvertSnitchListToCSV(SnitchList list)
    {
        StringBuilder builder = new StringBuilder();

        builder.append(list.getListName()).append(',');
        builder.append(list.getListColor().serialize()).append(',');
        builder.append(list.getRenderPriority()).append(',');
        builder.append(list.shouldRenderSnitches()).append(',');
        builder.append(list.getQualifier().toString());

        return builder.toString();
    }

    public static SnitchList GetSnitchListFromCSV(String csv)
    {
        String[] args = csv.split(",");
        if(args.length != NUMBER_OF_CSV_PARAMS)
            throw new NumberFormatException("The CSV string provided does not have the correct number of arguments for a Snitch List.");

        int index = 0;

        String name = args[index++];
        Color color = new Color(args[index++]);
        int priority = Integer.parseInt(args[index++]);
        boolean shouldRender = Boolean.parseBoolean(args[index++]);
        SnitchListQualifier qualifier = new SnitchListQualifier(args[index++]);

        SnitchList list = new SnitchList(qualifier,shouldRender,name,color); //TODO---You need to not have null as the first parameter
        list.setRenderPriority(priority);

        return list;
    }

//    private Long getHashForCoordinates(int x, int y, int z)
//    {
//        x = x >= 0 ? 2 * x : -2 * x - 1;
//        y = y >= 0 ? 2 * y : -2 * y - 1;
//        z = z >= 0 ? 2 * z : -2 * z - 1;
//
//        int max = max(x, y, z);
//        long hash = (max*max*max) + (2 * max * z) + z;
//        if (max == z)
//            hash += max(x, y)*max(x,y);
//        if (y >= x)
//            hash += x + y;
//        else
//            hash += y;
//        return hash;
//    }
//
//    private static int max(int x, int y)
//    {
//        if(x > y)
//            return x;
//        return y;
//    }
//
//    private static int max(int x, int y, int z)
//    {
//        int one = max(x,y);
//        return max(one,z);
//    }
}
