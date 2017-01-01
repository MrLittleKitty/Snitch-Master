package com.gmail.nuclearcat1337.snitch_master.gui.tables;

import com.gmail.nuclearcat1337.snitch_master.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Mr_Little_Kitty on 12/31/2016.
 */
public abstract class TableTopGui<T> extends GuiScreen
{
    private final GuiScreen parentScreen;
    private Collection<T> items;
    private ArrayList<TableColumn<T>> columnsToBoundsCheck;

    private final String title;
    private final int titleWidth;

    private TableGui<T> tableGui;

    public TableTopGui(GuiScreen parentScreen, Collection<T> items, String title)
    {
        this.parentScreen = parentScreen;
        this.items = items;
        this.title = title;
        this.titleWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(title);
    }

    protected abstract void initializeButtons();

    protected abstract Collection<TableColumn<T>> initializeColumns();

    @Override
    public void initGui()
    {
        Collection<TableColumn<T>> columns = initializeColumns();
        tableGui = new TableGui<T>(this,items,columns);

        columnsToBoundsCheck = new ArrayList<>();
        for(TableColumn<T> col : columns)
            if(col.doBoundsCheck())
                columnsToBoundsCheck.add(col);

        assert columns != null && !columns.isEmpty();

        initializeButtons();

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        //Draw the background, the actual table, and anything from out parent
        this.drawDefaultBackground();
        this.tableGui.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);

        //Create positioning info for drawing the title
        int yPos = 16 - (mc.fontRendererObj.FONT_HEIGHT/2);
        int xPos = (this.width/2) - (titleWidth/2);

        //Draw the title
        mc.fontRendererObj.drawString(title, xPos ,yPos, 16777215);

        int index = tableGui.getSlotIndexFromScreenCoords(mouseX,mouseY);
        if(index >= 0)
        {
            for(TableColumn<T> col : columnsToBoundsCheck)
            {
                Pair<Integer,Integer> bounds = tableGui.getBoundsForColumn(col);
                if(mouseX >= bounds.getOne() && mouseX <= bounds.getTwo())
                {
                    List<String> text = col.hover(tableGui.getItemForSlotIndex(index),xPos,yPos);
                    if(text != null && !text.isEmpty())
                        drawHoveringText(text,mouseX,mouseY);
                    break;
                }
            }
        }
    }

//    @Override
//    public void drawHoveringText(List<String> text, int xPos, int yPos)
//    {
//        //We override this method with a public implementation so that it can be seen by the table GUI
//        super.drawHoveringText(text,xPos,yPos);
//    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseEvent)
    {
        tableGui.mouseClicked(mouseX, mouseY, mouseEvent);
        try
        {
            super.mouseClicked(mouseX, mouseY, mouseEvent);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseReleased(int arg1, int arg2, int arg3)
    {
        //This method is ESSENTIAL to the functioning of the scroll bar
        tableGui.mouseReleased(arg1,arg2,arg3);
        super.mouseReleased(arg1,arg2,arg3);
    }

    @Override
    public void handleMouseInput()
    {
        //This method is ESSENTIAL to the functioning of the scroll bar
        tableGui.handleMouseInput();
        try
        {
            super.handleMouseInput();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
