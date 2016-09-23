package com.gmail.nuclearcat1337.snitch_master.locatableobjectlist;

/**
 * Created by Mr_Little_Kitty on 7/28/2016.
 */
public interface IReadOnlyLocatableObjectList<T extends LocatableObject<T>>
{
    int size();

    T get(ILocation location);

    boolean contains(ILocation location);

    T get(int index);

    int getMinIndexForWorld(String world);

    int getMaxIndexForWorld(String world);

    Iterable<T> getItemsForWorld(String world);

    Iterable<T> getItems();
}
