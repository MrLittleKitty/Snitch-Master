package com.gmail.nuclearcat1337.snitch_master.snitches;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.ILocation;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.LocatableObject;
import com.gmail.nuclearcat1337.snitch_master.util.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Mr_Little_Kitty on 6/25/2016.
 */
public class Snitch extends LocatableObject<Snitch>
{
    public static final int SNITCH_RADIUS = 11;
    public static final String DEFAULT_NAME = "Undefined";
    public static final Double MAX_CULL_TIME = 672D;

    private final ILocation location;
    private final String origin;

    private double cullTime;

    private String ctGroup;
    private String name;

    private ArrayList<SnitchList> attachedSnitchLists;

    public Snitch(ILocation location, String origin)
    {
        this.location = location;
        this.origin = origin;
        this.cullTime = SnitchMaster.CULL_TIME_ENABLED ? MAX_CULL_TIME : Double.NaN;
        this.ctGroup = DEFAULT_NAME;
        this.name = DEFAULT_NAME;
        attachedSnitchLists = new ArrayList<>();
    }

    public Snitch(ILocation location, String origin,  double culltime, String ctGroup, String snitchName)
    {
        this(location,origin);
        this.cullTime = culltime;
        this.ctGroup = ctGroup == null ? DEFAULT_NAME : ctGroup;
        this.name = snitchName == null ? DEFAULT_NAME : snitchName;
        attachedSnitchLists = new ArrayList<>();
    }

    public void setCullTime(double cullTime)
    {
        this.cullTime = cullTime;
    }

    public double getCullTime()
    {
        return cullTime;
    }

    public boolean isPointInThisSnitch(int x, int y, int z)
    {
        return x >= getFieldMinX() && x <= getFieldMaxX() && z >= getFieldMinZ() && z <= getFieldMaxZ() &&
                y >= getFieldMinY() && y <= getFieldMaxY();
    }

    public ILocation getLocation()
    {
        return location;
    }

    public int getFieldMinX()
    {
        return location.getX() - SNITCH_RADIUS;
    }

    public int getFieldMinY()
    {
        return location.getY() - SNITCH_RADIUS;
    }

    public int getFieldMinZ()
    {
        return location.getZ() - SNITCH_RADIUS;
    }

    public int getFieldMaxX()
    {
        return location.getX() + SNITCH_RADIUS;
    }

    public int getFieldMaxY()
    {
        return location.getY() + SNITCH_RADIUS;
    }

    public int getFieldMaxZ()
    {
        return location.getZ() + SNITCH_RADIUS;
    }

    public String getSnitchName()
    {
        return name;
    }

    public String getGroupName()
    {
        return ctGroup;
    }

    public String getOrigin()
    {
        return origin;
    }

    public void setSnitchName(String name)
    {
        this.name = name;
    }

    public void setGroupName(String groupName)
    {
        this.ctGroup = groupName;
    }

    public ArrayList<SnitchList> getAttachedSnitchLists()
    {
        return attachedSnitchLists;
    }

    public void clearAttachedSnitchLists()
    {
        attachedSnitchLists.clear();
    }

    public void attachSnitchList(SnitchList list)
    {
        this.attachedSnitchLists.add(list);
        Collections.sort(attachedSnitchLists,listComparator);
    }

    public void sortSnitchLists()
    {
        Collections.sort(attachedSnitchLists,listComparator);
    }

    @Override
    public String getWorld()
    {
        return location.getWorld();
    }

    @Override
    public int compareTo(ILocation other)
    {
        if(location.getX() < other.getX())
            return -1;
        if(location.getX() > other.getX())
            return 1;

        if(location.getZ() < other.getZ())
            return -1;
        if(location.getZ() > other.getZ())
            return 1;

        if(location.getY() < other.getY())
            return -1;
        if(location.getY() > other.getY())
            return 1;

        return 0;
    }

    @Override
    public int compareTo(Snitch other)
    {
        return compareTo(other.getLocation());
    }

    private static class SnitchListComparator implements Comparator<SnitchList>
    {
        @Override
        public int compare(SnitchList one, SnitchList two)
        {
            return Integer.compare(one.getRenderPriority(),two.getRenderPriority());
        }
    }
    private static final SnitchListComparator listComparator = new SnitchListComparator();

    private static final int NUMBER_OF_CSV_PARAMS = 8;

    public static String ConvertSnitchToCSV(Snitch snitch)
    {
        //x, y, z, world, oring, groupName, snitchName, cullTime
        StringBuilder builder = new StringBuilder();
        builder.append(snitch.location.getX()).append(',');
        builder.append(snitch.location.getY()).append(',');
        builder.append(snitch.location.getZ()).append(',');
        builder.append(snitch.location.getWorld()).append(',');
        builder.append(snitch.getOrigin()).append(',');
        builder.append(snitch.getGroupName()).append(',');
        builder.append(snitch.getSnitchName()).append(',');
        builder.append(snitch.getCullTime()).append(',');

//        builder.append('{');
//        for(SnitchList list : snitch.getAttachedSnitchLists())
//            builder.append(list.getListName()).append(':');
//
//        if(builder.charAt(builder.length()-1) == ':')
//            builder.deleteCharAt(builder.length()-1); //remove the final '|'
//        builder.append('}');

        return builder.toString();
    }

    public static Snitch GetSnitchFromCSV(String csv, SnitchMaster snitchMaster)
    {
        String[] args = csv.split(",");
        if(args.length != NUMBER_OF_CSV_PARAMS)
            throw new NumberFormatException("The CSV string provided does not have the correct number of arguments for a Snitch.");

        int index = 0;

        int x = Integer.parseInt(args[index++]);
        int y = Integer.parseInt(args[index++]);
        int z = Integer.parseInt(args[index++]);
        String world = args[index++];
        String origin = args[index++];
        String groupName = args[index++];
        String snitchName = args[index++];
        double cullTime = Double.parseDouble(args[index++]);

        Snitch snitch = new Snitch(new Location(x,y,z,world),origin,cullTime,groupName,snitchName);

//        String[] snitchLists = args[7].replace("{","").replace("}","").split(":");
//        for(String snitchListName : snitchLists)
//        {
//            SnitchList list = snitchMaster.getSnitchListByName(snitchListName);
//
//            if(list == null)
//                continue; //TODO---Maybe post a log message notifing the user that we couldnt find the snitch list?
//
//            snitch.attachSnitchList(list);
//        }

        return snitch;
    }
}
