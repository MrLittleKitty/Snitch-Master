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
 * Represents a Snitch block in Minecraft. (Jukebox or Noteblock as of Sept. 2016)
 */
public class Snitch extends LocatableObject<Snitch>
{
    /**
     * The maximum number of characters that are allowed to be in the name of a Snitch
     */
    public static final String MAX_NAME_CHARACTERS = "WWWWWWWWWWWWWWWWWWWW";

    /**
     * The maximum number of characters that are allowed to be in the name of a Citadel Group
     */
    public static final String MAX_CT_GROUP_NAME_CHARACTERS = "WWWWWWWWWWWWWWW";

    /**
     * The radius out from the Snitch block in all directions.
     */
    public static final int SNITCH_RADIUS = 11;

    /**
     * The default name used for populating Snitch names and Citadel group names when not specified.
     */
    public static final String DEFAULT_NAME = "Undefined";

    /**
     * The cull time amount that Snitches are reset to when a player walks through them.
     */
    public static final Double MAX_CULL_TIME = 672D;

    /**
     * The location of this Snitch block.
     */
    private final ILocation location;

    /**
     * The origin string from where this Snitch object was created.
     */
    private final String origin;

    /**
     * The cull time of this Snitch. This field can be NaN (Not a Number).
     */
    private double cullTime;

    /**
     * The Citadel group name of the group this Snitch is reinforced under.
     */
    private String ctGroup;

    /**
     * The name of this Snitch block.
     */
    private String name;

    /**
     * Array of SnitchLists that this Snitch is a part of.
     */
    private ArrayList<SnitchList> attachedSnitchLists;

    /**
     * Creates a new Snitch and populates "Name" and "Citadel Group Name" with the default name.
     * @param location The location of this Snitch block.
     * @param origin The origin string of where this Snitch object came from.
     */
    public Snitch(ILocation location, String origin)
    {
        this.location = location;
        this.origin = origin;
        this.cullTime = SnitchMaster.CULL_TIME_ENABLED ? MAX_CULL_TIME : Double.NaN;
        this.ctGroup = DEFAULT_NAME;
        this.name = DEFAULT_NAME;
        attachedSnitchLists = new ArrayList<>();
    }

    /**
     * Creates a new Snitch with all the values specified.
     * @param location The location of this Snitch block.
     * @param origin The origin string of where this Snitch object came from.
     * @param culltime The cull time remaining for this snitch. (Can be NaN)
     * @param ctGroup The Citadel group name of the group this Snitch is reinforced under.
     * @param snitchName The name of this Snitch.
     */
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

    /**
     * Returns true if the given point if within the area covered by this Snitch block.
     * Returns false otherwise.
     */
    public boolean isPointInThisSnitch(int x, int y, int z)
    {
        return x >= getFieldMinX() && x <= getFieldMaxX() && z >= getFieldMinZ() && z <= getFieldMaxZ() &&
                y >= getFieldMinY() && y <= getFieldMaxY();
    }

    @Override
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

    /**
     * Returns all the SnitchLists that are "attached" to this Snitch.
     * (The SnitchLists that this Snitch is in)
     */
    public ArrayList<SnitchList> getAttachedSnitchLists()
    {
        return attachedSnitchLists;
    }

    public void clearAttachedSnitchLists()
    {
        attachedSnitchLists.clear();
    }

    /**
     * Attaches the given SnitchList to this Snitch object.
     * (This Snitch is a part of that SnitchList)
     */
    public void attachSnitchList(SnitchList list)
    {
        this.attachedSnitchLists.add(list);
        Collections.sort(attachedSnitchLists,listComparator);
    }

    /**
     * Sorts the SnitchLists that this Snitch is in according to their render priorities.
     */
    public void sortSnitchLists()
    {
        Collections.sort(attachedSnitchLists,listComparator);
    }

    @Override
    public String getWorld()
    {
        return location.getWorld();
    }

    /**
     * Returns an arbitrary number meant to sort ILocation objects according to their location.
     */
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

    /**
     * Returns an arbitrary number which is the comparison of the two Snitch's locations.
     */
    @Override
    public int compareTo(Snitch other)
    {
        return compareTo(other.getLocation());
    }

    /**
     * A comparator that sorts SnitchLists according to their render priorities.
     */
    private static class SnitchListComparator implements Comparator<SnitchList>
    {
        @Override
        public int compare(SnitchList one, SnitchList two)
        {
            return Integer.compare(one.getRenderPriority(),two.getRenderPriority());
        }
    }

    /**
     * A static instance of the SnitchList comparator to use in all instances of the Snitch class.
     */
    private static final SnitchListComparator listComparator = new SnitchListComparator();

    /**
     * The number of parameters in a comma separated value string representing a Snitch object
     */
    private static final int NUMBER_OF_CSV_PARAMS = 8;

    /**
     * Returns a string that represents the given Snitch object.
     * The returned string is in comma separated value form.
     */
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

        return builder.toString();
    }

    /**
     * Returns a Snitch object created from the given comma separated value string.
     * The given Snitch has NO attached SnitchLists.
     * Throws a NumberFormatException if the given CSV string has the wrong number of parameters.
     */
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

        return snitch;
    }
}
