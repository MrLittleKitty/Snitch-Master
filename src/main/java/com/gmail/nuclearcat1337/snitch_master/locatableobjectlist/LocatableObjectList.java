package com.gmail.nuclearcat1337.snitch_master.locatableobjectlist;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;

import java.util.*;

/**
 * Created by Mr_Little_Kitty on 7/26/2016.
 */
public class LocatableObjectList<T extends LocatableObject<T>> implements IReadOnlyLocatableObjectList<T>
{
    private final ArrayList<T> wrappedList;
    private final HashMap<String,IntPair> worldIndices;

    public LocatableObjectList()
    {
        wrappedList = new ArrayList<>();
        worldIndices = new HashMap<>();
    }

    public void add(T item)
    {
        int index = Collections.binarySearch(wrappedList,item);
        //This means the item isnt in the list but the returned vault is -(insertion point) - 1 = x
        if(index < 0)
        {
            //According to the documentation of the binarySearch method this solves for the insertion point of the item
            index = (index + 1)*-1;
        }
        else
        {
            //If the item is already contained in the list then we do nothing
            return;
        }

        //Insert the into the list at the insertion point. Doing this for all adds guarantees the list is sorted.
        wrappedList.add(index,item);

        //Get the min and max index for the world of this item were adding
        IntPair minMax = worldIndices.get(item.getWorld());

        //This means that there were not any items with this world yet
        if(minMax == null)
        {
            //Create a new int pair with the min and max being the same index of this item
            minMax = new IntPair(index,index);
            //Add the pair into the hashmap
            worldIndices.put(item.getWorld(),minMax);
            //Return because we don't need to do anything else
            return;
        }

        //Increase the max index for this world.
        //No matter where in the array we added the new item we know the max index for this world increases
        minMax.two++;

        //Go through all the world indices
        for(Map.Entry<String,IntPair> entry : worldIndices.entrySet())
        {
            if(entry == null)
                continue;

//            SnitchMaster.logger.info("key="+entry.getKey()+",value="+entry.getValue());
            //Dont do anything to the world were adding
            if(!entry.getKey().equals(item.getWorld()))
            {
                //For any worlds that are stored after this one, increase their indices because we inserted an item
                if(entry.getValue().one >= index)
                {
                    entry.getValue().one++;
                    entry.getValue().two++;
                }
            }
        }
    }

    public T remove(ILocation location)
    {
        int index = LocatableObjectList.binarySearch(wrappedList,location);
        //If the item wasn't found then we return false
        if(index < 0)
            return null;

        T removedValue = remove(index);

        //Return true because we removed the item
        return removedValue;
    }

    public T remove(int index)
    {
        if(index >= 0 && index < size())
        {
            //Remove the item from the list at the index it was found
            T item = wrappedList.remove(index);

            IntPair minMax = worldIndices.get(item.getWorld());

            //The min max int pair should NEVER be null when we are removing an item we know is in the array
            assert minMax != null;

            //This means there is only one value and we just removed it so we remove the world
            if (minMax.one == minMax.two)
                worldIndices.remove(item.getWorld());
            else
                minMax.two--; //Decrease the max because we are shifting back one value

            //Go through all the world indices
            for (Map.Entry<String, IntPair> entry : worldIndices.entrySet())
            {
                //Dont do anything to the world were adding
                if (!entry.getKey().equals(item.getWorld()))
                {
                    //For any worlds that are stored after this one, decrease their indices because we removed an item
                    if (entry.getValue().one >= index)
                    {
                        entry.getValue().one--;
                        entry.getValue().two--;
                    }
                }
            }

            return item;
        }
        throw new IndexOutOfBoundsException();
    }

    public int size()
    {
        return wrappedList.size();
    }

    public T get(ILocation location)
    {
        int index = LocatableObjectList.binarySearch(wrappedList,location);

        //The index will be less than zero if the search did not find a match
        if(index < 0)
            return null;

        return get(index);
    }

    public boolean contains(ILocation location)
    {
        return LocatableObjectList.binarySearch(wrappedList,location) >= 0;
    }

    public T get(int index)
    {
        return wrappedList.get(index);
    }

    public int getMinIndexForWorld(String world)
    {
        IntPair pair = worldIndices.get(world);
        if(pair == null)
            return -1;
        return pair.one;
    }

    public int getMaxIndexForWorld(String world)
    {
        IntPair pair = worldIndices.get(world);
        if(pair == null)
            return -1;
        return pair.two;
    }

    public Iterable<T> getItems()
    {
        return wrappedList;
    }

    public Iterable<T> getItemsForWorld(String world)
    {
        final IntPair pair = worldIndices.get(world);
        if(pair == null)
        {
            return new Iterable<T>()
            {
                @Override
                public Iterator<T> iterator()
                {
                    return new EmptyIterator();
                }
            };
        }
        return new Iterable<T>()
        {
            @Override
            public Iterator<T> iterator()
            {
                return new HiddenIterator(pair.one,pair.two);
            }
        };
    }

    private static int binarySearch(ArrayList<? extends LocatableObject> list, ILocation location)
    {
        int low = 0;
        int high = list.size()-1;

        while (low <= high)
        {
            int mid = (low + high) >>> 1;
            LocatableObject  midVal = list.get(mid);
            int cmp = midVal.compareTo(location);

            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found
    }

    private class EmptyIterator implements Iterator<T>
    {
        @Override
        public boolean hasNext()
        {
            return false;
        }

        @Override
        public T next()
        {
            return null;
        }

        @Override
        public void remove()
        {

        }
    }


    private class HiddenIterator implements Iterator<T>
    {
        private final int max;
        private int index;

        public HiddenIterator(int min, int max)
        {
            this.max = max;
            this.index = min;
        }

        @Override
        public boolean hasNext()
        {
            return index <= max;
        }

        @Override
        public T next()
        {
            //Increment the index after getting the value at the current index
            if(hasNext())
                return get(index++);
            throw new IndexOutOfBoundsException("Exceeded the maximum index for this iterator");
        }

        @Override
        public void remove()
        {
            //TODO---Decide if we should support removing elements with the iterator
            throw new UnsupportedOperationException("You can not remove items using this iterator.");
        }
    }

    private static class IntPair
    {
        public int one,two;

        public IntPair()
        {

        }

        public IntPair(int one, int two)
        {
            this.one = one;
            this.two = two;
        }
    }
}
