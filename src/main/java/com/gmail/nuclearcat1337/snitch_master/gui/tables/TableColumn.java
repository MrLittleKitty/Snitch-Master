package com.gmail.nuclearcat1337.snitch_master.gui.tables;

import net.minecraft.client.gui.GuiButton;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Mr_Little_Kitty on 12/31/2016.
 */
public interface TableColumn<T> extends Comparator<T>
{
    GuiButton[] prepareEntry(T item);

    //Must be less than or equal to column width (this is also the header)
    String getColumnName();

    int getColumnWidth();

    int getRightSeparationDistance();

    boolean doBoundsCheck();

    void clicked(T item, boolean leftClick, int xPos, int yPos, GuiButton[] buttons);

    void released(T item, int xPos, int yPos, GuiButton[] buttons);

    void draw(T item, int xPos, int yPos, int slotHeight, GuiButton[] buttons);

    //Returns a list of strings to draw as hover text
    List<String> hover(T item, int xPos, int yPos);
}
