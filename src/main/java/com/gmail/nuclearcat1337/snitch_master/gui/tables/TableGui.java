package com.gmail.nuclearcat1337.snitch_master.gui.tables;

import com.gmail.nuclearcat1337.snitch_master.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;

import java.util.*;

/**
 * Created by Mr_Little_Kitty on 12/31/2016.
 */
public class TableGui<T> extends GuiListExtended
{
    private final TableTopGui<T> tableTop;
    private final int entryWidth;

    private Collection<TableColumn<T>> columns;

    private List<TableEntry> entries;
    private HashMap<TableColumn<T>,Pair<Integer,Integer>> columnBounds;

    private boolean setOffset = false;

    public TableGui(TableTopGui<T> tableTop, Collection<T> items, Collection<TableColumn<T>> columns)
    {
        super(Minecraft.getMinecraft(),
                tableTop.width,
                tableTop.height,
                32,
                tableTop.height - 32,
                20);

        this.tableTop = tableTop;
        this.columns = columns;

        //Populate the entries list using the passed item
        entries = new ArrayList<>(items.size());
        for(T item : items)
            entries.add(new TableEntry(item));

        //Calculate all the column bounds
        columnBounds = new HashMap<>(columns.size());
        int totalWidth = 0;

        for(TableColumn<T> col : columns)
        {
            int leftBound = totalWidth;
            totalWidth += col.getColumnWidth();

            if(col.doBoundsCheck())
                columnBounds.put(col,new Pair<>(leftBound,totalWidth));

            totalWidth += col.getRightSeparationDistance();
        }

        entryWidth = totalWidth;
    }

    public Pair<Integer,Integer> getBoundsForColumn(TableColumn<T> column)
    {
        return columnBounds.get(column);
    }

    public T getItemForSlotIndex(int index)
    {
        return entries.get(index).item;
    }

    @Override
    public IGuiListEntry getListEntry(int i)
    {
        return entries.get(i);
    }

    @Override
    protected int getSize()
    {
        return entries.size();
    }

    @Override
    protected void elementClicked(int slotIndex, boolean isRightClick, int mouseX, int mouseY)
    {
        if (slotIndex < 0 || slotIndex >= entries.size())
            return;
        getListEntry(slotIndex).mousePressed(slotIndex, mouseX, mouseY, isRightClick ? 1 : 0, 0, 0);
    }

    @Override
    protected int getScrollBarX() {
        return this.width - 8;
    }

    @Override
    public int getListWidth() {
        return this.width;
    }


    private class TableEntry implements IGuiListEntry
    {
        private T item;
        private HashMap<String,GuiButton[]> buttons;

        public TableEntry(T item)
        {
            this.item = item;
            buttons = new HashMap<>();
            for(TableColumn<T> col : columns)
            {
                GuiButton[] buttons = col.prepareEntry(item);
                if(buttons != null)
                    this.buttons.put(col.getColumnName(),buttons);
            }
        }

        @Override
        public void drawEntry(int slotIndex, int xPosition, int yPosition, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
        {
            int workingWidth = (width-xPosition);
            int xPos = xPosition + (workingWidth/2) - (entryWidth/2);
            if(!setOffset)
            {
                setOffset = true;
                //TODO---Check if this stupid hack actually works.
                for(Map.Entry<TableColumn<T>,Pair<Integer,Integer>> entry : columnBounds.entrySet())
                {
                    Pair<Integer,Integer> pair = entry.getValue();
                    entry.getValue().setValues(pair.getOne()+xPos,pair.getTwo()+xPos);
                }
            }

            for(TableColumn<T> col : columns)
            {
                col.draw(item,xPos,yPosition,slotHeight,buttons.get(col.getColumnName()));
                xPos += (col.getColumnWidth() + col.getRightSeparationDistance());
            }
        }

        @Override
        public boolean mousePressed(int index, int xPos, int yPos, int mouseEvent, int relX, int relY)
        {
            for(Map.Entry<TableColumn<T>,Pair<Integer,Integer>> entry : columnBounds.entrySet())
            {
                if(xPos >= entry.getValue().getOne() && xPos <= entry.getValue().getTwo())
                {
                    entry.getKey().clicked(item,mouseEvent == 0,xPos,yPos,buttons.get(entry.getKey().getColumnName()));
                    return true;
                }
            }
            return false;
        }

        @Override
        public void mouseReleased(int index, int xPos, int yPos, int mouseEvent, int relX, int relY)
        {
            for(Map.Entry<TableColumn<T>,Pair<Integer,Integer>> entry : columnBounds.entrySet())
            {
                if(xPos >= entry.getValue().getOne() && xPos <= entry.getValue().getTwo())
                {
                    entry.getKey().released(item,xPos,yPos,buttons.get(entry.getKey().getColumnName()));
                    break;
                }
            }
        }

        @Override
        public void setSelected(int i, int i1, int i2)
        {

        }
    }
}
