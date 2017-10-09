package com.gmail.nuclearcat1337.snitch_master.locatableobjectlist;

import java.util.Collection;

public abstract class IReadOnlyLocatableObjectList<T extends LocatableObject<T>> implements Iterable<T>, Collection<T> {
	public abstract int size();

	public abstract boolean isEmpty();

	public abstract boolean contains(ILocation location);

	public abstract boolean contains(T element);

	public abstract T get(ILocation location);

	public abstract T get(int index);

	public abstract int getMinIndexForWorld(String world);

	public abstract int getMaxIndexForWorld(String world);

	public abstract Iterable<T> getItemsForWorld(String world);
}
