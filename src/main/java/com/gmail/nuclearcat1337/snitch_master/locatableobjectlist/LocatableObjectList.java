package com.gmail.nuclearcat1337.snitch_master.locatableobjectlist;

import java.util.*;

public class LocatableObjectList<T extends LocatableObject<T>> extends IReadOnlyLocatableObjectList<T> implements Collection<T>, RandomAccess {
	private int modCount = 0;
	private final ArrayList<T> wrappedList;
	private final HashMap<String, IntPair> worldIndices;

	public LocatableObjectList() {
		wrappedList = new ArrayList<>();
		worldIndices = new HashMap<>();
	}

	@Override
	public boolean add(T item) {
		int index = Collections.binarySearch(wrappedList, item);
		//This means the item isnt in the list but the returned vault is -(insertion point) - 1 = x
		if (index < 0) {
			//According to the documentation of the binarySearch method this solves for the insertion point of the item
			index = (index + 1) * -1;
		} else {
			//If the item is already contained in the list then we do nothing
			return false;
		}

		//Insert the item into the list at the insertion point. Doing this for all adds guarantees the list is sorted.
		wrappedList.add(index, item);

		//cocurrency mod count needs to be incremented
		modCount++;

		//Get the min and max index for the world of this item were adding
		IntPair minMax = worldIndices.get(item.getWorld());

		//This means that there were not any items with this world yet
		if (minMax == null) {
			//Create a new int pair with the min and max being the same index of this item
			minMax = new IntPair(index, index);
			//Add the pair into the hashmap
			worldIndices.put(item.getWorld(), minMax);
			//Return because we don't need to do anything else
			return true;
		}

		//Increase the max index for this world.
		//No matter where in the array we added the new item we know the max index for this world increases
		minMax.two++;

		//Go through all the world indices
		for (Map.Entry<String, IntPair> entry : worldIndices.entrySet()) {
			if (entry == null) {
				continue;
			}

			//Dont do anything to the world were adding
			if (!entry.getKey().equals(item.getWorld())) {
				//For any worlds that are stored after this one, increase their indices because we inserted an item
				if (entry.getValue().one >= index) {
					entry.getValue().one++;
					entry.getValue().two++;
				}
			}
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean changed = false;
		for (T value : c) {
			if (add(value)) {
				changed = true;
			}
		}
		return changed;
	}

	public boolean remove(T element) {
		return remove(element.getLocation()) != null;
	}

	public T remove(ILocation location) {
		//Were only going to search the area in the arraylist that corresponds to the right world
		IntPair pair = worldIndices.get(location.getWorld());
		if (pair == null) {
			return null;
		}
		int index = LocatableObjectList.binarySearch(wrappedList, pair.one, pair.two, location);
		//If the item wasn't found then we return false
		if (index < 0) {
			return null;
		}
		T removedValue = remove(index);
		//Return true because we removed the item
		return removedValue;
	}

	public T remove(int index) {
		if (index >= 0 && index < size()) {
			//Remove the item from the list at the index it was found
			T item = wrappedList.remove(index);

			//Increase cocurrency mod count
			modCount++;

			IntPair minMax = worldIndices.get(item.getWorld());

			//The min max int pair should NEVER be null when we are removing an item we know is in the array
			assert minMax != null;

			//This means there is only one value and we just removed it so we remove the world
			if (minMax.one == minMax.two) {
				worldIndices.remove(item.getWorld());
			} else {
				minMax.two--; //Decrease the max because we are shifting back one value
			}

			//Go through all the world indices
			for (Map.Entry<String, IntPair> entry : worldIndices.entrySet()) {
				//Dont do anything to the world were adding
				if (!entry.getKey().equals(item.getWorld())) {
					//For any worlds that are stored after this one, decrease their indices because we removed an item
					if (entry.getValue().one >= index) {
						entry.getValue().one--;
						entry.getValue().two--;
					}
				}
			}

			return item;
		}
		throw new IndexOutOfBoundsException();
	}

	@Override
	public boolean remove(Object o) {
		if (o instanceof ILocation) {
			T removedValue = remove((ILocation) o);
			return removedValue != null;
		}
		else if (o instanceof LocatableObject) {
			LocatableObject obj = (LocatableObject) o;

			return remove(obj.getLocation()) != null;
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object obj : c) {
			if (remove(obj)) {
				changed = true;
			}
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean changed = false;
		for (int i = wrappedList.size() - 1; i >= 0; i--) {
			if (!c.contains(get(i)) && remove(i) != null) {
				changed = true;
			}
		}
		return changed;
	}

	@Override
	public void clear() {
		wrappedList.clear();
		worldIndices.clear();
		modCount++;
	}

	@Override
	public int size() {
		return wrappedList.size();
	}

	@Override
	public boolean isEmpty() {
		return wrappedList.isEmpty();
	}

	@Override
	public boolean contains(ILocation location) {
		IntPair pair = worldIndices.get(location.getWorld());
		if (pair == null) {
			return false;
		}
		return LocatableObjectList.binarySearch(wrappedList, pair.one, pair.two, location) >= 0;
	}

	public boolean contains(T element) {
		return contains(element.getLocation());
	}

	@Override
	public boolean contains(Object o) {
		if (o instanceof ILocation) {
			return contains((ILocation) o);
		} else if (o instanceof LocatableObject) {
			return contains(((LocatableObject) o).getLocation());
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object val : c) {
			if (!contains(val)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Iterator<T> iterator() {
		return new HiddenIterator(0, size() - 1, modCount);
	}

	@Override
	public Object[] toArray() {
		return wrappedList.toArray();
	}

	@Override
	public <T1> T1[] toArray(T1[] a) {
		return wrappedList.toArray(a);
	}

	@Override
	public T get(ILocation location) {
		//Were only going to search the area in the arraylist that corresponds to the right world
		IntPair pair = worldIndices.get(location.getWorld());
		if (pair == null) {
			return null;
		}

		int index = LocatableObjectList.binarySearch(wrappedList, pair.one, pair.two, location);

		//The index will be less than zero if the search did not find a match
		if (index < 0) {
			return null;
		}
		return get(index);
	}

	@Override
	public T get(int index) {
		return wrappedList.get(index);
	}

	@Override
	public int getMinIndexForWorld(String world) {
		IntPair pair = worldIndices.get(world);
		if (pair == null) {
			return -1;
		}
		return pair.one;
	}

	@Override
	public int getMaxIndexForWorld(String world) {
		IntPair pair = worldIndices.get(world);
		if (pair == null) {
			return -1;
		}
		return pair.two;
	}

	@Override
	public Iterable<T> getItemsForWorld(String world) {
		final IntPair pair = worldIndices.get(world);
		if (pair == null) {
			return new Iterable<T>() {
				@Override
				public Iterator<T> iterator() {
					return new EmptyIterator();
				}
			};
		}
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new HiddenIterator(pair.one, pair.two, modCount);
			}
		};
	}

	public LocatableObjectList<T> deepCopy() {
		LocatableObjectList<T> copy = new LocatableObjectList<T>();
		copy.wrappedList.addAll(this.wrappedList);
		for (Map.Entry<String, IntPair> pair:  this.worldIndices.entrySet()) {
			copy.worldIndices.put(pair.getKey(), pair.getValue().deepCopy());
		}
		copy.modCount = this.modCount;
		return copy;
	}

	private static int binarySearch(ArrayList<? extends LocatableObject> list, int low, int high, ILocation location) {
		while (low <= high) {
			int mid = (low + high) >>> 1;
			LocatableObject midVal = list.get(mid);
			int cmp = midVal.compareTo(location);

			if (cmp < 0) {
				low = mid + 1;
			} else if (cmp > 0) {
				high = mid - 1;
			} else {
				return mid; // key found
			}
		}
		return -(low + 1);  // key not found
	}

	private class EmptyIterator implements Iterator<T> {
		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public T next() {
			return null;
		}

		@Override
		public void remove() {

		}
	}

	private class HiddenIterator implements Iterator<T> {
		private final int max, expectedModCount;
		private int index;

		public HiddenIterator(int min, int max, int expectedModCount) {
			this.max = max;
			this.index = min;
			this.expectedModCount = expectedModCount;
		}

		@Override
		public boolean hasNext() {
			return index <= max;
		}

		@Override
		public T next() {
			checkForConcurrentModification();
			if (hasNext()) {
				return get(index++);
			}
			throw new IndexOutOfBoundsException("Exceeded the maximum index for this iterator");
		}

		@Override
		public void remove() {
			checkForConcurrentModification();
			//TODO---Decide if we should support removing elements with the iterator
			throw new UnsupportedOperationException("You can not remove items using this iterator.");
		}

		private void checkForConcurrentModification() {
			if (expectedModCount != modCount) {
				throw new ConcurrentModificationException("The Locatable Object List was modified while using an iterator.");
			}
		}
	}

	private static class IntPair {
		public int one, two;

		public IntPair() {

		}

		public IntPair(int one, int two) {
			this.one = one;
			this.two = two;
		}

		public IntPair deepCopy() {
			return new IntPair(one, two);
		}
	}
}
