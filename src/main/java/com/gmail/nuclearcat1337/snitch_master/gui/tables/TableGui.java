package com.gmail.nuclearcat1337.snitch_master.gui.tables;

import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.gmail.nuclearcat1337.snitch_master.util.Pair;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.Tessellator;

import java.util.*;

/**
 * Created by Mr_Little_Kitty on 12/31/2016.
 */
public class TableGui<T> extends GuiListExtended
{
    //private final TableTopGui<T> tableTop;
    private final int entryWidth;

    private Collection<TableColumn<T>> columns;

    private List<TableEntry> entries;
    private HashMap<TableColumn<T>,Pair<Integer,Integer>> columnBounds;
    private HashMap<TableColumn<T>,Integer> columnWidths;

    private boolean setOffset = false;

    public TableGui(TableTopGui<T> tableTop, Collection<T> items, Collection<TableColumn<T>> columns)
    {
        super(Minecraft.getMinecraft(),
                tableTop.width,
                tableTop.height,
                32,
                tableTop.height - 32,
                20);

        //this.tableTop = tableTop;
        this.columns = columns;

        this.setHasListHeader(true, (int) ( (float) mc.fontRendererObj.FONT_HEIGHT * 1.5));

        columnBounds = new HashMap<>(columns.size());
        columnWidths = new HashMap<>(columns.size());

        //Populate the entries list using the passed item
        entries = new ArrayList<>(items.size());
        for(T item : items)
        {
            entries.add(new TableEntry(item));
            for(TableColumn<T> col : columns)
            {
                int width = col.getDrawWidth(item);
                if(!columnWidths.containsKey(col))
                    columnWidths.put(col,width);
                else if(width > columnWidths.get(col))
                    columnWidths.put(col,width);
            }
        }

        String root = ChatFormatting.UNDERLINE + "" + ChatFormatting.BOLD;
        for(TableColumn<T> col : columns)
        {
            int headerWidth = mc.fontRendererObj.getStringWidth(root + col.getColumnName());
            if(!columnWidths.containsKey(col) || headerWidth > columnWidths.get(col))
                columnWidths.put(col,headerWidth);
        }

        //Calculate all the column bounds

        int totalWidth = 0;

        for(TableColumn<T> col : columns)
        {
            int leftBound = totalWidth;
            totalWidth += columnWidths.get(col);

            if(col.doBoundsCheck())
                columnBounds.put(col,new Pair<>(leftBound,totalWidth));

            totalWidth += GuiConstants.STANDARD_SEPARATION_DISTANCE;
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
    protected void drawListHeader(int xPosition, int yPosition, Tessellator tessalator)
    {
        String root = ChatFormatting.UNDERLINE + "" + ChatFormatting.BOLD;

        int workingWidth = (width-xPosition);
        int xPos = xPosition + (workingWidth/2) - (entryWidth/2);

        for(TableColumn<T> col : columns)
        {
            int columnWidth = columnWidths.get(col);
            String text = root + col.getColumnName();
            int textWidth = mc.fontRendererObj.getStringWidth(text);
            int drawXPos =  xPos + (columnWidth/2) - (textWidth/2);
            this.mc.fontRendererObj.drawString(text, drawXPos, yPosition, 16777215);
            xPos += (columnWidth + GuiConstants.STANDARD_SEPARATION_DISTANCE);
        }
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
                int columnWidth = columnWidths.get(col);
                col.draw(item,xPos,yPosition,columnWidth,slotHeight,buttons.get(col.getColumnName()));
                xPos += (columnWidth + GuiConstants.STANDARD_SEPARATION_DISTANCE);
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
