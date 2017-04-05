package com.gmail.nuclearcat1337.snitch_master.gui.tables;

import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr_Little_Kitty on 1/1/2017.
 */
public class TableColumnSelector<T> extends GuiListExtended
{
    //private static final int ARROW_BUTTON_WIDTH = 20;
    private static final String CONTROLS_HEADER = "Controls";
    private static final String NAME_HEADER = "Column Name";
    private static final String RENDER_HEADER = "Render Enabled";

    private List<ColumnEntry> entries;

    private final int renderLength;
    private final int entryWidth;

    private int maxNameLength = 0;

    public TableColumnSelector(GuiScreen parent, List<TableColumn<T>> allColumns, List<TableColumn<T>> renderColumns)
    {
        super(Minecraft.getMinecraft(), parent.width, parent.height, 32, parent.height - 32, 20);

        this.renderLength = mc.fontRendererObj.getStringWidth(RENDER_HEADER + "--");
        this.setHasListHeader(true, (int) ((float) mc.fontRendererObj.FONT_HEIGHT * 1.5));

        entries = new ArrayList<>(allColumns.size());

        maxNameLength = 0;

        for (TableColumn<T> col : allColumns)
        {
            entries.add(new ColumnEntry(col, renderColumns.contains(col)));
            int length = mc.fontRendererObj.getStringWidth(col.getColumnName() + "-");
            if (length > maxNameLength)
                maxNameLength = length;
        }

        int columnNameLength = mc.fontRendererObj.getStringWidth("-Column Name-");
        if (columnNameLength > maxNameLength)
            maxNameLength = columnNameLength;

        entryWidth = renderLength + GuiConstants.STANDARD_SEPARATION_DISTANCE + maxNameLength + GuiConstants.STANDARD_SEPARATION_DISTANCE + renderLength;

    }

    protected void drawListHeader(int xPosition, int yPosition, Tessellator tessalator)
    {
        String root = ChatFormatting.UNDERLINE + "" + ChatFormatting.BOLD;

        int controlsWidth = mc.fontRendererObj.getStringWidth(root + CONTROLS_HEADER);
        int nameWidth = mc.fontRendererObj.getStringWidth(root + NAME_HEADER);
        int renderWidth = mc.fontRendererObj.getStringWidth(root + RENDER_HEADER);

        int workingWidth = (this.width - xPosition);
        int startingXPos = xPosition + (workingWidth / 2) - (entryWidth / 2);

        int drawXPos = startingXPos + (renderLength / 2) - (controlsWidth / 2);

        this.mc.fontRendererObj.drawString(root + CONTROLS_HEADER, drawXPos, yPosition, 16777215);

        startingXPos += (renderWidth + GuiConstants.STANDARD_SEPARATION_DISTANCE);
        drawXPos = startingXPos + (maxNameLength / 2) - (nameWidth / 2);

        this.mc.fontRendererObj.drawString(root + NAME_HEADER, drawXPos, yPosition, 16777215);

        startingXPos += (maxNameLength + GuiConstants.STANDARD_SEPARATION_DISTANCE);
        drawXPos = startingXPos + (renderLength / 2) - (renderWidth / 2);

        this.mc.fontRendererObj.drawString(root + RENDER_HEADER, drawXPos, yPosition, 16777215);
    }

    private void swapItems(int index1, int index2)
    {
        if (index1 >= entries.size() || index2 >= entries.size() || index1 < 0 || index2 < 0)
            return;

        ColumnEntry one = entries.get(index1);
        ColumnEntry two = entries.get(index2);

        TableColumn<T> tempCol = one.column;
        boolean tempRender = one.render;

        one.column = two.column;
        one.render = two.render;

        two.column = tempCol;
        two.render = tempRender;

        one.setRenderText();
        two.setRenderText();
    }

    public ArrayList<TableColumn<T>> getAllColumns()
    {
        ArrayList<TableColumn<T>> allColumns = new ArrayList<>();
        for (ColumnEntry entry : entries)
            allColumns.add(entry.column);
        return allColumns;
    }

    public ArrayList<TableColumn<T>> getRenderColumns()
    {
        ArrayList<TableColumn<T>> renderColumns = new ArrayList<>();
        for (ColumnEntry entry : entries)
            if (entry.render)
                renderColumns.add(entry.column);
        return renderColumns;
    }

    @Override
    protected int getScrollBarX()
    {
        return this.width - 8;
    }

    @Override
    public int getListWidth()
    {
        return this.width;
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

    private class ColumnEntry implements IGuiListEntry
    {
        private TableColumn<T> column;
        private boolean render;

        private GuiButton renderButton;
        private GuiButton downButton;
        private GuiButton upButton;

        public ColumnEntry(TableColumn<T> column, boolean render)
        {
            this.column = column;
            this.render = render;

            this.upButton = new GuiButton(0, width - 60, 0, renderLength / 2 - GuiConstants.SMALL_SEPARATION_DISTANCE / 2, GuiConstants.STANDARD_BUTTON_HEIGHT, "/\\");
            this.downButton = new GuiButton(1, width - 60, 0, renderLength / 2 - GuiConstants.SMALL_SEPARATION_DISTANCE / 2, GuiConstants.STANDARD_BUTTON_HEIGHT, "\\/");
            this.renderButton = new GuiButton(2, width - 60, 0, renderLength - 2, GuiConstants.STANDARD_BUTTON_HEIGHT, "");
            setRenderText();
        }

        @Override
        public void drawEntry(int slotIndex, int xPosition, int yPosition, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
        {
            int stringYPosition = yPosition + ((slotHeight - mc.fontRendererObj.FONT_HEIGHT) / 2);
            yPosition = yPosition + ((slotHeight - GuiConstants.STANDARD_BUTTON_HEIGHT) / 2);

            int workingWidth = (width - xPosition);
            int xPos = xPosition + (workingWidth / 2) - (entryWidth / 2);

            upButton.yPosition = yPosition;// + (upButton.height/3);
            upButton.xPosition = xPos;

            xPos += upButton.width + GuiConstants.SMALL_SEPARATION_DISTANCE;

            downButton.yPosition = yPosition;// + (downButton.height/3);
            downButton.xPosition = xPos;

            xPos += downButton.width + (GuiConstants.STANDARD_SEPARATION_DISTANCE);

            int stringWidth = mc.fontRendererObj.getStringWidth(column.getColumnName());

            int namePos = xPos + (maxNameLength / 2) - (stringWidth / 2);

            mc.fontRendererObj.drawString(column.getColumnName(), namePos, stringYPosition, 16777215);

            xPos += maxNameLength + (GuiConstants.STANDARD_SEPARATION_DISTANCE);

            renderButton.yPosition = yPosition;
            renderButton.xPosition = xPos + (renderLength / 2) - (renderButton.width / 2);

            this.upButton.drawButton(mc, mouseX, mouseY);
            this.downButton.drawButton(mc, mouseX, mouseY);
            this.renderButton.drawButton(mc, mouseX, mouseY);
        }

        @Override
        public boolean mousePressed(int index, int xPos, int yPos, int mouseEvent, int relX, int relY)
        {
            //Dont let them right click the buttons
            if (mouseEvent == 1)
                return false;

            if (this.upButton.mousePressed(mc, xPos, yPos))
            {
                swapItems(index, index - 1); //The array is goes from bottom to top. so index 0 is at top of screen
                return true;
            }
            if (this.downButton.mousePressed(mc, xPos, yPos))
            {
                swapItems(index, index + 1); //The array is goes from bottom to top. so index 0 is at top of screen
                return true;
            }
            if (renderButton.mousePressed(mc, xPos, yPos))
            {
                render = !render;
                setRenderText();
                return true;
            }
            return false;
        }

        private void setRenderText()
        {
            renderButton.displayString = "Render: " + (render ? "On" : "Off");
        }

        @Override
        public void mouseReleased(int index, int xPos, int yPos, int mouseEvent, int relX, int relY)
        {
            this.upButton.mouseReleased(xPos, yPos);
            this.downButton.mouseReleased(xPos, yPos);
            this.renderButton.mouseReleased(xPos, yPos);
        }

        @Override
        public void setSelected(int i, int i1, int i2)
        {

        }
    }
}
