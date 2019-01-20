package com.gmail.nuclearcat1337.snitch_master.locatableobjectlist;

/**
 * Created by Mr_Little_Kitty on 7/27/2016.
 */
public abstract class LocatableObject<T> implements Comparable<T> {
	public abstract String getWorld();

	public abstract int compareTo(ILocation location);

	public abstract ILocation getLocation();
}
