package com.gmail.nuclearcat1337.snitch_master.gui;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.api.SnitchListQualifier;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchLists;
import com.gmail.nuclearcat1337.snitch_master.util.Acceptor;
import com.gmail.nuclearcat1337.snitch_master.util.Color;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Mr_Little_Kitty on 9/16/2016.
 */
public class SnitchListsGui extends GuiListExtended
{
    private final SnitchLists lists;
    private final EditSnitchListsGui guiSnitches;
    private final Minecraft mc;
    private final ListEntry[] iGuiList;

    private static final String CONTROLS_HEADER = "Controls";
    private static final String NAME_HEADER = "Name";
    private static final String COLOR_HEADER = "Color";
    private static final String QUALIFIER_HEADER = "Qualifier";
    private static final String VIEW_SNITCHES_HEADER = "Snitches";

    private static final int NAME_COLUMN_WIDTH = Minecraft.getMinecraft().fontRendererObj.getStringWidth(SnitchList.MAX_NAME_CHARACTERS);
    private int nameColumnLeftBound;

    private static final int ARROW_BUTTON_WIDTH = 20;
    private static final int ON_OFF_BUTTON_WIDTH = 30;
    private static final int EDIT_COLOR_BUTTON_WIDTH = 60;
    private static final int EDIT_QUALIFIER_BUTTON_WIDTH = 60;
    private static final int VIEW_SNITCHES_BUTTON_WIDTH = 60;

    private final int entryWidth;

    public SnitchListsGui(EditSnitchListsGui guiSnitches, SnitchLists lists)
    {
        super(Minecraft.getMinecraft(),
                guiSnitches.width,		// width
                guiSnitches.height, 		// height
                32, 						// top
                guiSnitches.height - 32, 	// bottom
                20);						// slot height

        this.lists = lists;
        this.guiSnitches = guiSnitches;
        this.mc = Minecraft.getMinecraft();

        int listSize = lists.size();
        this.iGuiList = new ListEntry[listSize];

        for (int k = 0; k < listSize; k++)
        {
            SnitchList snitchList = lists.get(k);

            this.iGuiList[k] = new ListEntry(guiSnitches,snitchList,k);
        }

        this.setHasListHeader(true, (int) ( (float) SnitchListsGui.this.mc.fontRendererObj.FONT_HEIGHT * 1.5));

        this.entryWidth =
                ARROW_BUTTON_WIDTH + GuiConstants.SMALL_SEPARATION_DISTANCE + ON_OFF_BUTTON_WIDTH + GuiConstants.SMALL_SEPARATION_DISTANCE + ARROW_BUTTON_WIDTH +
                        (GuiConstants.STANDARD_SEPARATION_DISTANCE*2) + NAME_COLUMN_WIDTH  + (GuiConstants.STANDARD_SEPARATION_DISTANCE*2) + EDIT_COLOR_BUTTON_WIDTH +
                        (GuiConstants.STANDARD_SEPARATION_DISTANCE*2) + EDIT_QUALIFIER_BUTTON_WIDTH + (GuiConstants.STANDARD_SEPARATION_DISTANCE*2) + VIEW_SNITCHES_BUTTON_WIDTH;

    }

    private void swapItems(int index, int nextIndex)
    {
        if(nextIndex >= iGuiList.length || nextIndex  < 0)
            return;

        ListEntry entry = iGuiList[nextIndex];
        iGuiList[nextIndex] = iGuiList[index];
        iGuiList[index] = entry;

        //        iGuiList[index].updateIndex(index);
        //        iGuiList[nextIndex].updateIndex(index);

        for(int i = 0; i < iGuiList.length; i++)
            iGuiList[i].snitchList.setRenderPriority(i+1);

        SnitchMaster.instance.refreshSnitchListPriorities();
        SnitchMaster.instance.getSnitchLists().sortSnitchLists();
    }

    protected void drawListHeader(int xPosition, int yPosition, Tessellator tessalator)
    {
        String root = ChatFormatting.UNDERLINE + "" + ChatFormatting.BOLD;
        int controlsWidth = mc.fontRendererObj.getStringWidth(root+ CONTROLS_HEADER);
        int nameHeaderWidth = mc.fontRendererObj.getStringWidth(root+ NAME_HEADER);
        int colorHeaderWidth = mc.fontRendererObj.getStringWidth(root+ COLOR_HEADER);
        int qualifierHeaderWidth = mc.fontRendererObj.getStringWidth(root+ QUALIFIER_HEADER);
        int viewSnitchesHeaderWidth = mc.fontRendererObj.getStringWidth(root+ VIEW_SNITCHES_HEADER);

        //This stuff is supposed to center all of our columns on the screen
        int workingWidth = (this.width-xPosition);
        int startingXPos = xPosition + (workingWidth/2) - (entryWidth/2);

        int columnWidth = (ARROW_BUTTON_WIDTH *2) + ON_OFF_BUTTON_WIDTH + (GuiConstants.SMALL_SEPARATION_DISTANCE*2);

        int drawXPos = startingXPos + (columnWidth/2) - (controlsWidth/2);

        this.mc.fontRendererObj.drawString(root + CONTROLS_HEADER, drawXPos, yPosition, 16777215);

        startingXPos += columnWidth + (GuiConstants.STANDARD_SEPARATION_DISTANCE*2);

        nameColumnLeftBound = startingXPos;
        drawXPos = startingXPos + (NAME_COLUMN_WIDTH /2) - (nameHeaderWidth/2);

        this.mc.fontRendererObj.drawString(root + NAME_HEADER, drawXPos, yPosition, 16777215);

        startingXPos += NAME_COLUMN_WIDTH + (GuiConstants.STANDARD_SEPARATION_DISTANCE*2);

        drawXPos = startingXPos + (EDIT_COLOR_BUTTON_WIDTH /2) - (colorHeaderWidth/2);

        this.mc.fontRendererObj.drawString(root + COLOR_HEADER, drawXPos, yPosition, 16777215);

        startingXPos += EDIT_COLOR_BUTTON_WIDTH + (GuiConstants.STANDARD_SEPARATION_DISTANCE*2);

        drawXPos = startingXPos + (EDIT_QUALIFIER_BUTTON_WIDTH /2) - (qualifierHeaderWidth/2);

        this.mc.fontRendererObj.drawString(root + QUALIFIER_HEADER, drawXPos, yPosition, 16777215);

        startingXPos += EDIT_QUALIFIER_BUTTON_WIDTH + (GuiConstants.STANDARD_SEPARATION_DISTANCE*2);

        drawXPos = startingXPos + (VIEW_SNITCHES_BUTTON_WIDTH /2) - (viewSnitchesHeaderWidth/2);

        this.mc.fontRendererObj.drawString(root + VIEW_SNITCHES_HEADER, drawXPos, yPosition, 16777215);
    }

    protected int getSize() {
        return this.iGuiList.length;
    }

    public void setAllRender(boolean on)
    {
        for(int i = 0; i < iGuiList.length; i++)
            iGuiList[i].snitchList.setShouldRenderSnitches(on);
    }

    public GuiListExtended.IGuiListEntry getListEntry(int index) {
        return this.iGuiList[index];
    }

    protected int getScrollBarX() {
        return this.width - 16;
    }

    @Override
    protected void elementClicked(int slotIndex, boolean isRightClick, int mouseX, int mouseY)
    {
        if (slotIndex < 0 || slotIndex >= iGuiList.length) return;
        ((ListEntry) getListEntry(slotIndex)).mousePressed(slotIndex, mouseX, mouseY, isRightClick ? 1 : 0, 0, 0);
    }

    //    @Override
    //    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
    //        if (isDoubleClick || slotIndex < 0 || slotIndex >= iGuiList.length) return;
    //        ((ListEntry) getListEntry(slotIndex)).mousePressed(slotIndex, mouseX, mouseY, 0, 0, 0);
    //    }

    public int getListWidth() {
        return this.width;
    }

    @SideOnly(Side.CLIENT)
    public class ListEntry implements GuiListExtended.IGuiListEntry
    {
        private GuiScreen cancelToScreen;
        //private int index;
        private SnitchList snitchList;

        private GuiButton upButton;
        private GuiButton downButton;
        private GuiButton toggleRenderButton;
        private GuiButton editQualifierButton;
        private GuiButton editColorButton;
        private GuiButton viewSnitchesButton;

        private ListEntry(GuiScreen cancelToScreen, SnitchList snitchList, int index)
        {
            this.cancelToScreen = cancelToScreen;
            this.snitchList = snitchList;
            //this.index = index;

            this.upButton = new GuiButton(10, SnitchListsGui.this.width - 60, 0, ARROW_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, "^");
            this.downButton = new GuiButton(11, SnitchListsGui.this.width - 60, 0, ARROW_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, "v");

            this.toggleRenderButton = new GuiButton(12, SnitchListsGui.this.width - 60, 0, ON_OFF_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, snitchList.shouldRenderSnitches() ? "On" : "Off");

            this.editQualifierButton = new GuiButton(13, SnitchListsGui.this.width - 60, 0, EDIT_QUALIFIER_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, "Edit");
            this.editColorButton = new GuiButton(14, SnitchListsGui.this.width - 60, 0, EDIT_COLOR_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, "Edit");

            this.viewSnitchesButton = new GuiButton(14, SnitchListsGui.this.width - 60, 0, VIEW_SNITCHES_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, "View");
        }

        //public void updateIndex(int index)
        //        {
        //            this.index = index;
        //        }

        public void drawEntry(int slotIndex, int xPosition, int yPosition, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
        {
            int yFinal = yPosition + (slotHeight + SnitchListsGui.this.mc.fontRendererObj.FONT_HEIGHT) / 2;

            int workingWidth = (width-xPosition);
            int xPos = xPosition + (workingWidth/2) - (entryWidth/2);

            upButton.yPosition = yPosition + (upButton.height/3);
            upButton.xPosition = xPos;

            xPos += upButton.width + GuiConstants.SMALL_SEPARATION_DISTANCE;

            toggleRenderButton.displayString = snitchList.shouldRenderSnitches() ? "On" : "Off";
            toggleRenderButton.yPosition = yPosition + (toggleRenderButton.height/3);
            toggleRenderButton.xPosition = xPos;

            xPos += toggleRenderButton.width + GuiConstants.SMALL_SEPARATION_DISTANCE;

            downButton.yPosition = yPosition + (downButton.height/3);
            downButton.xPosition = xPos;

            xPos += downButton.width + (GuiConstants.STANDARD_SEPARATION_DISTANCE*2);

            this.upButton.drawButton(SnitchListsGui.this.mc, mouseX, mouseY);
            this.downButton.drawButton(SnitchListsGui.this.mc, mouseX, mouseY);
            this.toggleRenderButton.drawButton(SnitchListsGui.this.mc, mouseX, mouseY);

            int stringWidth = mc.fontRendererObj.getStringWidth(snitchList.getListName());

            int namePos = xPos + (NAME_COLUMN_WIDTH /2) - (stringWidth/2);

            mc.fontRendererObj.drawString(snitchList.getListName(), namePos ,yFinal,16777215);

            xPos += NAME_COLUMN_WIDTH + (GuiConstants.STANDARD_SEPARATION_DISTANCE*2);

            editColorButton.yPosition = yPosition + (editColorButton.height/3);
            editColorButton.xPosition = xPos;

            xPos += EDIT_COLOR_BUTTON_WIDTH + (GuiConstants.STANDARD_SEPARATION_DISTANCE*2);

            editQualifierButton.yPosition = yPosition + (editQualifierButton.height/3);
            editQualifierButton.xPosition = xPos;

            xPos += EDIT_QUALIFIER_BUTTON_WIDTH + (GuiConstants.STANDARD_SEPARATION_DISTANCE*2);

            viewSnitchesButton.yPosition = yPosition +(viewSnitchesButton.height/3);
            viewSnitchesButton.xPosition = xPos;

            this.editColorButton.drawButton(SnitchListsGui.this.mc, mouseX, mouseY);
            this.editQualifierButton.drawButton(SnitchListsGui.this.mc, mouseX, mouseY);
            this.viewSnitchesButton.drawButton(SnitchListsGui.this.mc, mouseX, mouseY);
        }

        public boolean mousePressed(int index, int xPos, int yPos, int mouseEvent, int relX, int relY)
        {
            if(mouseEvent == 1) //If its a right click
            {
                int rightBound = nameColumnLeftBound + NAME_COLUMN_WIDTH;
                if(xPos >= nameColumnLeftBound && xPos <= rightBound) //Check if they right clicked in the name column
                {
                    mc.displayGuiScreen(new EditStringGui(cancelToScreen,snitchList.getListName(),"Edit List Name",new EditNameAcceptor(snitchList),20));
                    return true;
                }

                //Only allow right clicks on certain controls. So not all things should be triggered by right clicks
                return false;
            }
            if (this.upButton.mousePressed(SnitchListsGui.this.mc, xPos, yPos))
            {
                swapItems(index,index-1); //The array is goes from bottom to top. so index 0 is at top of screen
                return true;
            }
            if (this.downButton.mousePressed(SnitchListsGui.this.mc, xPos, yPos))
            {
                swapItems(index,index+1); //The array is goes from bottom to top. so index 0 is at top of screen
                return true;
            }
            if (this.toggleRenderButton.mousePressed(SnitchListsGui.this.mc, xPos, yPos))
            {
                snitchList.setShouldRenderSnitches(!snitchList.shouldRenderSnitches());
                return true;
            }
            if (this.editColorButton.mousePressed(SnitchListsGui.this.mc, xPos, yPos))
            {
                mc.displayGuiScreen(new EditColorGui(cancelToScreen,snitchList.getListColor(),"Edit Color",new EditColorAcceptor(snitchList)));
                return true;
            }
            if (this.editQualifierButton.mousePressed(SnitchListsGui.this.mc, xPos, yPos))
            {
                mc.displayGuiScreen(new EditStringGui(cancelToScreen,snitchList.getQualifier().toString(),"Edit Qualifier",new EditQualifierAcceptor(snitchList),100));
                return true;
            }
            if (this.viewSnitchesButton.mousePressed(SnitchListsGui.this.mc, xPos, yPos))
            {
                viewSnitchesButton.displayString = "not implemented";
                return true;
            }

            return false;
        }

        public void mouseReleased(int index, int xPos, int yPos, int mouseEvent, int relX, int relY)
        {
            this.upButton.mouseReleased(xPos, yPos);
            this.downButton.mouseReleased(xPos, yPos);
            this.toggleRenderButton.mouseReleased(xPos, yPos);
            this.editColorButton.mouseReleased(xPos, yPos);
            this.editQualifierButton.mouseReleased(xPos, yPos);
            this.viewSnitchesButton.mouseReleased(xPos,yPos);
        }

        @Override
        public void setSelected(int p_178011_1, int p_178011_2_, int p_178011_3_)
        {}
    }

    private class EditColorAcceptor implements Acceptor<Color>
    {
        private final SnitchList list;

        private EditColorAcceptor(SnitchList list)
        {
            this.list = list;
        }

        @Override
        public boolean accept(Color item)
        {
            list.setListColor(item);
            lists.snitchListChanged();
            return true;
        }
    }

    private class EditNameAcceptor implements Acceptor<String>
    {
        private final SnitchList list;

        private EditNameAcceptor(SnitchList list)
        {
            this.list = list;
        }

        @Override
        public boolean accept(String item)
        {
            boolean valid = !lists.doesListWithNameExist(item);
            if(valid)
            {
                list.setListName(item);
                lists.snitchListChanged();
                return true;
            }
            return false;
        }
    }

    private class EditQualifierAcceptor implements Acceptor<String>
    {
        private final SnitchList list;

        private EditQualifierAcceptor(SnitchList list)
        {
            this.list = list;
        }

        @Override
        public boolean accept(String item)
        {
            boolean valid = SnitchListQualifier.isSyntaxValid(item);
            if(valid)
            {
                list.getQualifier().updateQualifier(item);
                lists.snitchListChanged();
                return true;
            }
            return false;
        }
    }
}
