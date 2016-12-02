package com.gmail.nuclearcat1337.snitch_master.snitches;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.util.IOHandler;

import java.util.*;

/**
 * Created by Mr_Little_Kitty on 9/16/2016.
 * Handles the management of all SnitchLists for the SnitchMaster Mod.
 */
public class SnitchLists implements Iterable<SnitchList>
{
    private SnitchMaster snitchMaster;
    private int nextPriority;
    private final ArrayList<SnitchList> snitchLists;

    private boolean universalRender = false;

    /**
     * Creates a new SnitchLists object
     */
    public SnitchLists(SnitchMaster snitchMaster)
    {
        this.snitchMaster = snitchMaster;
        snitchLists = new ArrayList<>();
        nextPriority = 1;
    }

    /**
     * Adds a new SnitchList to the collection and assigns it the next render priority.
     * If the given SnitchList has the same name as an already existing SnitchList or it is null, it will NOT added to the collection.
     * @return True if the SnitchList was added, false otherwise.
     */
    public boolean addSnitchList(SnitchList list)
    {
        if(list == null) //We dont let them add null lists
            return false;

        if(doesListWithNameExist(list.getListName())) //We dont let them add 2 lists with the same name
            return false;

        list.setRenderPriority(nextPriority++); //Make sure the render priorities are incremental

        //Go through all snitches currently loaded and see if they can be tagged in this list
        for(Snitch snitch : snitchMaster.getSnitches())
        {
            if(list.getQualifier().isQualified(snitch))
                snitch.attachSnitchList(list);
        }

        //Add this list to the list collection
        this.snitchLists.add(list);

        return true;
    }

    public ArrayList<Snitch> getSnitchesInList(SnitchList list)
    {
        ArrayList<Snitch> snitches = new ArrayList<>();
        for(Snitch snitch : snitchMaster.getSnitches())
        {
            if(snitch.getAttachedSnitchLists().contains(list))
                snitches.add(snitch);
        }
        return snitches;
    }

    /**
     * Sorts the SnitchLists by their render priority.
     */
    public void sortSnitchLists()
    {
        Collections.sort(snitchLists, new Comparator<SnitchList>()
        {
            @Override
            public int compare(SnitchList o1, SnitchList o2)
            {
                return Integer.compare(o1.getRenderPriority(),o2.getRenderPriority());
            }
        });
    }

    /**
     * Toggles the universal render of all SnitchLists.
     * This sets the render of all SnitchLists according to a global variable, regardless of the current individual render status.
     */
    public void toggleUniversalRender()
    {
        for(SnitchList list : snitchLists)
            list.setShouldRenderSnitches(universalRender);
        universalRender = !universalRender;
    }

    /**
     * Adds the default SnitchLists to this collection.
     * If they are already in the collection, they will NOT be re-added.
     */
    public void addDefaultSnitchLists()
    {
        for(SnitchList list : SnitchList.getDefaultSnitchLists())
            addSnitchList(list);
    }

    /**
     * Returns true if a SnitchList with the given name exists in this collection.
     * Returns false otherwise.
     */
    public boolean doesListWithNameExist(String name)
    {
        for (SnitchList list : snitchLists)
        {
            if(list.getListName().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    /**
     * A method that updates all necessary values when a SnitchList is changed externally.
     * Example: If a SnitchList changes name or color, this method should be called to update values accordingly.
     */
    public void snitchListChanged()
    {
        if(snitchMaster.jmInterface != null)
            snitchMaster.jmInterface.refresh(snitchMaster.getSnitches());

        IOHandler.asyncSaveSnitchLists(this);
    }

    public void requalifySnitchList(SnitchList list)
    {
        for(Snitch snitch : snitchMaster.getSnitches())
        {
            snitch.getAttachedSnitchLists().remove(list);
            if(list.getQualifier().isQualified(snitch))
                snitch.attachSnitchList(list);
        }
    }

    public Collection<SnitchList> asCollection()
    {
        return snitchLists;
    }

    /**
     * Returns the SnitchList at the given index.
     * Index must be between 0 and (this.size()-1)
     */
    public SnitchList get(int index)
    {
        return snitchLists.get(index);
    }

    /**
     * Returns the number of SnitchLists in this collection.
     */
    public int size()
    {
        return snitchLists.size();
    }

    /**
     * Returns an Iterator for iterating over all SnitchLists in this collection.
     */
    @Override
    public Iterator<SnitchList> iterator()
    {
        return snitchLists.iterator();
    }
}
