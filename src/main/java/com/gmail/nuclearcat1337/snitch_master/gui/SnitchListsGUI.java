package com.gmail.nuclearcat1337.snitch_master.gui;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchLists;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Mr_Little_Kitty on 9/16/2016.
 */
public class SnitchListsGUI extends GuiListExtended
{
    private final EditSnitchListsGUI guiSnitches;
    private final Minecraft mc;
    private final ListEntry[] iGuiList;

    private final String controlsHeader = "Controls";
    private final String nameHeader = "Name";
    private final String colorHeader = "Color";
    private final String qualifierHeader = "Qualifier";
    private final String viewSnitchesHeader = "Snitches";

    private final int nameWidth;
    private static final int leftBorderSeparation = 10;
    private static final int buttonHeight = 18;
    private static final int separationDistance = 2;
    private static final int arrowButtonWidth = 20;
    private static final int onOffButtonWidth = 30;
    private static final int editColorButtonWidth = 60;
    private static final int editQualifierButtonWidth = 60;
    private static final int viewSnitchesButtonWidth = 60;

    public SnitchListsGUI(EditSnitchListsGUI guiSnitches, SnitchLists lists)
    {
        super(Minecraft.getMinecraft(),
                guiSnitches.width,		// width
                guiSnitches.height, 		// height
                32, 						// top
                guiSnitches.height - 32, 	// bottom
                20);						// slot height

        this.guiSnitches = guiSnitches;
        this.mc = Minecraft.getMinecraft();

        int listSize = lists.size();
        this.iGuiList = new ListEntry[listSize];

        nameWidth = mc.fontRendererObj.getStringWidth(SnitchList.MAX_NAME_CHARACTERS); //20 characters

        for (int k = 0; k < listSize; k++)
        {
            SnitchList snitchList = lists.get(k);

            this.iGuiList[k] = new ListEntry(snitchList,k);
        }

        this.setHasListHeader(true, (int) ( (float) SnitchListsGUI.this.mc.fontRendererObj.FONT_HEIGHT * 1.5));
    }

    private void swapItems(int index, int nextIndex)
    {
        if(nextIndex >= iGuiList.length || nextIndex  < 0)
            return;

        ListEntry entry = iGuiList[nextIndex];
        iGuiList[nextIndex] = iGuiList[index];
        iGuiList[index] = entry;

        iGuiList[index].updateIndex(index);
        iGuiList[nextIndex].updateIndex(index);

        for(int i = 0; i < iGuiList.length; i++)
            iGuiList[i].snitchList.setRenderPriority(i+1);

        SnitchMaster.instance.refreshSnitchListPriorities();
        SnitchMaster.instance.getSnitchLists().sortSnitchLists();
    }

    protected void drawListHeader(int listHeader1, int listHeader2, Tessellator tessalator)
    {
        String root = ChatFormatting.UNDERLINE + "" + ChatFormatting.BOLD;

        int xPosition = listHeader1;
        int yFinal = listHeader2;

        int controlsWidth = mc.fontRendererObj.getStringWidth(root+controlsHeader);
        int nameHeaderWidth = mc.fontRendererObj.getStringWidth(root+nameHeader);
        int colorHeaderWidth = mc.fontRendererObj.getStringWidth(root+colorHeader);
        int qualifierHeaderWidth = mc.fontRendererObj.getStringWidth(root+qualifierHeader);
        int viewSnitchesHeaderWidth = mc.fontRendererObj.getStringWidth(root+viewSnitchesHeader);

        int columnWidth = (arrowButtonWidth*2) + onOffButtonWidth + (separationDistance*2);

        int xPos = xPosition + leftBorderSeparation + (columnWidth/2) - (controlsWidth/2);

        this.mc.fontRendererObj.drawString(root + controlsHeader, xPos, yFinal, 16777215);

        xPosition += leftBorderSeparation + columnWidth + (separationDistance*4);

        xPos = xPosition + (nameWidth/2) - (nameHeaderWidth/2);

        this.mc.fontRendererObj.drawString(root + nameHeader, xPos, yFinal, 16777215);

        xPosition += nameWidth + (separationDistance*4);

        xPos = xPosition + (editColorButtonWidth/2) - (colorHeaderWidth/2);

        this.mc.fontRendererObj.drawString(root + colorHeader, xPos, yFinal, 16777215);

        xPosition += editColorButtonWidth + (separationDistance*2);

        xPos = xPosition + (editQualifierButtonWidth/2) - (qualifierHeaderWidth/2);

        this.mc.fontRendererObj.drawString(root + qualifierHeader, xPos, yFinal, 16777215);

        xPosition += editQualifierButtonWidth + (separationDistance*2);

        xPos = xPosition + (viewSnitchesButtonWidth/2) - (viewSnitchesHeaderWidth/2);

        this.mc.fontRendererObj.drawString(root + viewSnitchesHeader, xPos, yFinal, 16777215);
    }

    protected int getSize() {
        return this.iGuiList.length;
    }

    public void setAllRender(boolean on)
    {
        for(int i = 0; i < iGuiList.length; i++)
            iGuiList[i].snitchList.setShouldRenderSnitches(on);
    }
    /**
     * Gets the IGuiListEntry object for the given index
     */
    public GuiListExtended.IGuiListEntry getListEntry(int index) {
        return this.iGuiList[index];
    }

    protected int getScrollBarX() {
        return this.width - 16;
    }

    /**
     * Gets the width of the list
     */
    public int getListWidth() {
        return this.width;
    }

    @SideOnly(Side.CLIENT)
    public class ListEntry implements GuiListExtended.IGuiListEntry
    {
        private int index;
        private SnitchList snitchList;

        private GuiButton upButton;
        private GuiButton downButton;
        private GuiButton toggleRenderButton;
        private GuiButton editQualifierButton;
        private GuiButton editColorButton;
        private GuiButton viewSnitchesButton;

        private ListEntry(SnitchList snitchList, int index)
        {
            this.snitchList = snitchList;
            this.index = index;

            this.upButton = new GuiButton(10, SnitchListsGUI.this.width - 60, 0, arrowButtonWidth, buttonHeight, "↑");
            this.downButton = new GuiButton(11, SnitchListsGUI.this.width - 60, 0, arrowButtonWidth, buttonHeight, "↓");

            this.toggleRenderButton = new GuiButton(12, SnitchListsGUI.this.width - 60, 0, onOffButtonWidth, buttonHeight, snitchList.shouldRenderSnitches() ? "On" : "Off");

            this.editQualifierButton = new GuiButton(13, SnitchListsGUI.this.width - 60, 0, editQualifierButtonWidth, buttonHeight, "Edit");
            this.editColorButton = new GuiButton(14, SnitchListsGUI.this.width - 60, 0, editColorButtonWidth, buttonHeight, "Edit");

            this.viewSnitchesButton = new GuiButton(14, SnitchListsGUI.this.width - 60, 0, viewSnitchesButtonWidth, buttonHeight, "View");
        }

        public void updateIndex(int index)
        {
            this.index = index;
        }

        public void drawEntry(int p_148279_1_, int xPosition, int yPosition, int p_148279_4_, int p_148279_5_,
                              int p_148279_7_, int p_148279_8_, boolean p_148279_9_)
        {
            //xPosition = xPosition - 1;
            int yFinal = yPosition + (p_148279_5_ + SnitchListsGUI.this.mc.fontRendererObj.FONT_HEIGHT) / 2;

            int xPos = xPosition + leftBorderSeparation;

            upButton.yPosition = yPosition + (buttonHeight/3);
            upButton.xPosition = xPos;

            xPos += upButton.width + separationDistance;

            toggleRenderButton.displayString = snitchList.shouldRenderSnitches() ? "On" : "Off";
            toggleRenderButton.yPosition = yPosition + (buttonHeight/3);
            toggleRenderButton.xPosition = xPos;

            xPos += toggleRenderButton.width + separationDistance;

            downButton.yPosition = yPosition + (buttonHeight/3);
            downButton.xPosition = xPos;

            xPos += downButton.width + separationDistance*4;

            this.upButton.drawButton(SnitchListsGUI.this.mc, p_148279_7_, p_148279_8_);
            this.downButton.drawButton(SnitchListsGUI.this.mc, p_148279_7_, p_148279_8_);
            this.toggleRenderButton.drawButton(SnitchListsGUI.this.mc, p_148279_7_, p_148279_8_);

            int stringWidth = mc.fontRendererObj.getStringWidth(snitchList.getListName());

            int namePos = xPos + (nameWidth/2) - (stringWidth/2);

            mc.fontRendererObj.drawString(snitchList.getListName(), namePos ,yFinal,16777215);

            xPos += nameWidth + (separationDistance*4);

            editColorButton.yPosition = yPosition + (buttonHeight/3);
            editColorButton.xPosition = xPos;

            xPos += editColorButtonWidth + (separationDistance*2);

            editQualifierButton.yPosition = yPosition + (buttonHeight/3);
            editQualifierButton.xPosition = xPos;

            xPos += editQualifierButtonWidth + (separationDistance*2);

            viewSnitchesButton.yPosition = yPosition +(buttonHeight/3);
            viewSnitchesButton.xPosition = xPos;

            this.editColorButton.drawButton(SnitchListsGUI.this.mc, p_148279_7_, p_148279_8_);
            this.editQualifierButton.drawButton(SnitchListsGUI.this.mc, p_148279_7_, p_148279_8_);
            this.viewSnitchesButton.drawButton(SnitchListsGUI.this.mc, p_148279_7_, p_148279_8_);
        }

        /**
         * Returns true if the mouse has been pressed on this control.
         */
        public boolean mousePressed(int index, int xPos, int yPos, int mouseEvent, int relX, int relY) {

            //LogManager.getLogger("SnitchVisualizer").info("MousePress on SnitchListItem Detected!");

            if (this.upButton.mousePressed(SnitchListsGUI.this.mc, xPos, yPos))
            {
                swapItems(index,index-1); //The array is goes from bottom to top. so index 0 is at top of screen
                return true;
            }
            if (this.downButton.mousePressed(SnitchListsGUI.this.mc, xPos, yPos))
            {
                swapItems(index,index+1); //The array is goes from bottom to top. so index 0 is at top of screen
                return true;
            }
            if (this.toggleRenderButton.mousePressed(SnitchListsGUI.this.mc, xPos, yPos))
            {
                snitchList.setShouldRenderSnitches(!snitchList.shouldRenderSnitches());
                return true;
            }
            if (this.editColorButton.mousePressed(SnitchListsGUI.this.mc, xPos, yPos))
            {
                return true;
            }
            if (this.editQualifierButton.mousePressed(SnitchListsGUI.this.mc, xPos, yPos))
            {
                return true;
            }
            if (this.viewSnitchesButton.mousePressed(SnitchListsGUI.this.mc, xPos, yPos))
            {
                return true;
            }

            return false;
        }

        /**
         * Fired when the mouse button is released. Arguments: index, x, y,
         * mouseEvent, relativeX, relativeY
         */

        public void mouseReleased(int index, int xPos, int yPos, int mouseEvent, int relX, int relY)
        {
            this.upButton.mouseReleased(xPos, yPos);
            this.downButton.mouseReleased(xPos, yPos);
            this.toggleRenderButton.mouseReleased(xPos, yPos);
            this.editColorButton.mouseReleased(xPos, yPos);
            this.editQualifierButton.mouseReleased(xPos, yPos);
            this.viewSnitchesButton.mouseReleased(xPos,yPos);
        }

//        ListEntry(SnitchList p_i45030_2_, Object notUsed) {
//            this(p_i45030_2_);
//        }

        @Override
        public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
            // TODO Auto-generated method stub

        }
    }

    @Override
    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
        if (isDoubleClick || slotIndex < 0 || slotIndex >= iGuiList.length) return;
        ((ListEntry) getListEntry(slotIndex)).mousePressed(slotIndex, mouseX, mouseY, 0, 0, 0);
    }
}
