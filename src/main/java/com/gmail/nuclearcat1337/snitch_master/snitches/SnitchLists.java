package com.gmail.nuclearcat1337.snitch_master.snitches;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Created by Mr_Little_Kitty on 9/16/2016.
 */
public class SnitchLists implements Iterable<SnitchList>
{
    private SnitchMaster snitchMaster;
    private int nextPriority;
    private final ArrayList<SnitchList> snitchLists;

    private boolean universalRender = false;

    public SnitchLists(SnitchMaster snitchMaster)
    {
        this.snitchMaster = snitchMaster;
        snitchLists = new ArrayList<>();
        nextPriority = 1;
    }

    public void addSnitchList(SnitchList list)
    {
        if(list == null) //We dont let them add null lists
            return;

        if(doesListWithNameExist(list.getListName())) //We dont let them add 2 lists with the same name
            return;

        list.setRenderPriority(nextPriority++); //Make sure the render priorities are incremental

        //Go through all snitches currently loaded and see if they can be tagged in this list
        for(Snitch snitch : snitchMaster.getSnitches().getItems())
        {
            if(list.getQualifier().isQualified(snitch))
                snitch.attachSnitchList(list);
        }

        //Add this list to the list collection
        this.snitchLists.add(list);
    }

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

    public void toggleUniversalRender()
    {
        for(SnitchList list : snitchLists)
            list.setShouldRenderSnitches(universalRender);
        universalRender = !universalRender;
    }

    public void addDefaultSnitchLists()
    {
        for(SnitchList list : SnitchList.getDefaultSnitchLists())
            addSnitchList(list);
    }

    public boolean doesListWithNameExist(String name)
    {
        for (SnitchList list : snitchLists)
        {
            if(list.getListName().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    public void snitchListChanged()
    {
        if(snitchMaster.jmInterface != null)
            snitchMaster.jmInterface.refresh(snitchMaster.getSnitches().getItems());
    }

    public SnitchList get(int index)
    {
        return snitchLists.get(index);
    }

    public int size()
    {
        return snitchLists.size();
    }

    @Override
    public Iterator<SnitchList> iterator()
    {
        return snitchLists.iterator();
    }
}
