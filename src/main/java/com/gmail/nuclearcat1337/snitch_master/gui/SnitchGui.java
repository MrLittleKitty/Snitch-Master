package com.gmail.nuclearcat1337.snitch_master.gui;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchLists;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;

/**
 * Created by Mr_Little_Kitty on 11/28/2016.
 */
public class SnitchGui extends GuiListExtended
{
    private static final NumberFormat CULL_TIME_FORMAT = new DecimalFormat("#.000");

    private final Minecraft mc;
    private final SnitchEntry[] iGuiList;

    private static final String NAME_HEADER = "Snitch Name";
    private static final String GROUP_HEADER = "Citadel Group";
    private static final String X_HEADER = "X";
    private static final String Y_HEADER = "Y";
    private static final String Z_HEADER = "Z";
    private static final String CULL_TIME_HEADER = "Cull Time";
    private static final String SNITCH_LISTS_HEADER = "Snitch Lists";

    private static final int NAME_COLUMN_WIDTH = Minecraft.getMinecraft().fontRendererObj.getStringWidth(Snitch.MAX_NAME_CHARACTERS);
    private static final int GROUP_COLUMN_WIDTH = Minecraft.getMinecraft().fontRendererObj.getStringWidth(Snitch.MAX_CT_GROUP_NAME_CHARACTERS);

    private static final int CULLTIME_COLUMN_WIDTH = Minecraft.getMinecraft().fontRendererObj.getStringWidth("WWW.WWWW");

    private static final int X_COLUMN_WIDTH = Minecraft.getMinecraft().fontRendererObj.getStringWidth("WWWWW");
    private static final int Y_COLUMN_WIDTH = Minecraft.getMinecraft().fontRendererObj.getStringWidth("WWW");
    private static final int Z_COLUMN_WIDTH = Minecraft.getMinecraft().fontRendererObj.getStringWidth("WWWWW");

    private static final int SNITCH_LIST_COLUMN_WIDTH = Minecraft.getMinecraft().fontRendererObj.getStringWidth(SnitchList.MAX_NAME_CHARACTERS);

    private final int entryWidth;
    private final int snitchListNameColumnLeftBound;

    public SnitchGui(GuiScreen parentScreen, Collection<Snitch> snitchesToDisplay)
    {
        super(Minecraft.getMinecraft(),
                parentScreen.width,		// width
                parentScreen.height, 		// height
                32, 						// top
                parentScreen.height - 32, 	// bottom
                20);

        this.mc = Minecraft.getMinecraft();

        iGuiList = new SnitchEntry[snitchesToDisplay.size()];
        int i = 0;

        for(Snitch snitch : snitchesToDisplay)
        {
            iGuiList[i] = new SnitchEntry(parentScreen,snitch);
        }

        this.setHasListHeader(true, (int) ( (float) mc.fontRendererObj.FONT_HEIGHT * 1.5));

        this.entryWidth = NAME_COLUMN_WIDTH + GuiConstants.STANDARD_SEPARATION_DISTANCE
                        + GROUP_COLUMN_WIDTH + GuiConstants.STANDARD_SEPARATION_DISTANCE
                        + CULLTIME_COLUMN_WIDTH + GuiConstants.STANDARD_SEPARATION_DISTANCE
                        + X_COLUMN_WIDTH + GuiConstants.SMALL_SEPARATION_DISTANCE
                        + Y_COLUMN_WIDTH + GuiConstants.SMALL_SEPARATION_DISTANCE
                        + Z_COLUMN_WIDTH + GuiConstants.STANDARD_SEPARATION_DISTANCE
                        + SNITCH_LIST_COLUMN_WIDTH;

        this.snitchListNameColumnLeftBound = entryWidth - SNITCH_LIST_COLUMN_WIDTH;
    }

    protected void drawListHeader(int xPosition, int yPosition, Tessellator tessalator)
    {
        String root = ChatFormatting.UNDERLINE + "" + ChatFormatting.BOLD;
        int nameWidth = mc.fontRendererObj.getStringWidth(root+ NAME_HEADER);
        int groupWidth = mc.fontRendererObj.getStringWidth(root+ GROUP_HEADER);

        int xWidth = mc.fontRendererObj.getStringWidth(root+ X_HEADER);
        int yWidth = mc.fontRendererObj.getStringWidth(root+ Y_HEADER);
        int zWidth = mc.fontRendererObj.getStringWidth(root+ Z_HEADER);

        int cullTimeWidth = mc.fontRendererObj.getStringWidth(root+ CULL_TIME_HEADER);
        int snitchListsWidth = mc.fontRendererObj.getStringWidth(root+ SNITCH_LISTS_HEADER);

        //This stuff is supposed to center all of our columns on the screen
        int workingWidth = (this.width-xPosition);
        int startingXPos = xPosition + (workingWidth/2) - (entryWidth/2);

        int drawXPos = startingXPos + (NAME_COLUMN_WIDTH/2) - (nameWidth/2);
        this.mc.fontRendererObj.drawString(root + NAME_HEADER, drawXPos, yPosition, 16777215);
        startingXPos += NAME_COLUMN_WIDTH + (GuiConstants.STANDARD_SEPARATION_DISTANCE);

        drawXPos = startingXPos + (GROUP_COLUMN_WIDTH /2) - (groupWidth/2);
        this.mc.fontRendererObj.drawString(root + GROUP_HEADER, drawXPos, yPosition, 16777215);
        startingXPos += GROUP_COLUMN_WIDTH + (GuiConstants.STANDARD_SEPARATION_DISTANCE);

        drawXPos = startingXPos + (CULLTIME_COLUMN_WIDTH /2) - (cullTimeWidth/2);
        this.mc.fontRendererObj.drawString(root + CULL_TIME_HEADER, drawXPos, yPosition, 16777215);
        startingXPos += CULLTIME_COLUMN_WIDTH + (GuiConstants.STANDARD_SEPARATION_DISTANCE);

        drawXPos = startingXPos + (X_COLUMN_WIDTH /2) - (xWidth/2);
        this.mc.fontRendererObj.drawString(root + X_HEADER, drawXPos, yPosition, 16777215);
        startingXPos += X_COLUMN_WIDTH + (GuiConstants.SMALL_SEPARATION_DISTANCE);

        drawXPos = startingXPos + (Y_COLUMN_WIDTH /2) - (yWidth/2);
        this.mc.fontRendererObj.drawString(root + Y_HEADER, drawXPos, yPosition, 16777215);
        startingXPos += Y_COLUMN_WIDTH + (GuiConstants.SMALL_SEPARATION_DISTANCE);

        drawXPos = startingXPos + (Z_COLUMN_WIDTH /2) - (zWidth/2);
        this.mc.fontRendererObj.drawString(root + Z_HEADER, drawXPos, yPosition, 16777215);
        startingXPos += Z_COLUMN_WIDTH + (GuiConstants.STANDARD_SEPARATION_DISTANCE);

        drawXPos = startingXPos + (SNITCH_LIST_COLUMN_WIDTH /2) - (snitchListsWidth/2);
        this.mc.fontRendererObj.drawString(root + SNITCH_LISTS_HEADER, drawXPos, yPosition, 16777215);
        //startingXPos += SNITCH_LIST_COLUMN_WIDTH + (GuiConstants.STANDARD_SEPARATION_DISTANCE*2);
    }

    protected int getSize() {
        return this.iGuiList.length;
    }

    public GuiListExtended.IGuiListEntry getListEntry(int index) {
        return this.iGuiList[index];
    }

    protected int getScrollBarX() {
        return this.width - 8;
    }

    public int getListWidth() {
        return this.width;
    }

    @Override
    protected void elementClicked(int slotIndex, boolean isRightClick, int mouseX, int mouseY)
    {
        if (slotIndex < 0 || slotIndex >= iGuiList.length) return;
        ((SnitchEntry) getListEntry(slotIndex)).mousePressed(slotIndex, mouseX, mouseY, isRightClick ? 1 : 0, 0, 0);
    }

    @SideOnly(Side.CLIENT)
    private class SnitchEntry implements GuiListExtended.IGuiListEntry
    {
        private GuiScreen cancelToScreen;

        private Snitch snitch;

        private SnitchEntry(GuiScreen cancelToScreen, Snitch snitch)
        {
            this.cancelToScreen = cancelToScreen;

            this.snitch = snitch;
        }

        public void drawEntry(int slotIndex, int xPosition, int yPosition, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
        {
            int yFinal = yPosition + (slotHeight + mc.fontRendererObj.FONT_HEIGHT) / 2;

            int workingWidth = (width-xPosition);
            int xPos = xPosition + (workingWidth/2) - (entryWidth/2);

            //Draw the Snitches name in the center of the name column
            int nameWidth = mc.fontRendererObj.getStringWidth(snitch.getSnitchName());
            int namePos = xPos + (NAME_COLUMN_WIDTH /2) - (nameWidth/2);
            mc.fontRendererObj.drawString(snitch.getSnitchName(), namePos ,yFinal,16777215);
            xPos += NAME_COLUMN_WIDTH + (GuiConstants.STANDARD_SEPARATION_DISTANCE);

            //Draw the Citadel group name in the center of the citadel group name column
            int groupWidth = mc.fontRendererObj.getStringWidth(snitch.getGroupName());
            int groupPos = xPos + (GROUP_COLUMN_WIDTH /2) - (groupWidth/2);
            mc.fontRendererObj.drawString(snitch.getGroupName(), groupPos ,yFinal,16777215);
            xPos += GROUP_COLUMN_WIDTH + (GuiConstants.STANDARD_SEPARATION_DISTANCE);

            // Draw the cull time in the middle of the cull time column
            String cullString = SnitchMaster.CULL_TIME_ENABLED ? (Double.isNaN(snitch.getCullTime()) ? "Off" : CULL_TIME_FORMAT.format(snitch.getCullTime())) : "Off";
            int cullWidth = mc.fontRendererObj.getStringWidth(cullString);
            int cullPos = xPos + (CULLTIME_COLUMN_WIDTH /2) - (cullWidth/2);
            mc.fontRendererObj.drawString(cullString, cullPos ,yFinal,16777215);
            xPos += CULLTIME_COLUMN_WIDTH + (GuiConstants.STANDARD_SEPARATION_DISTANCE);

            //Draw the x coordinate in the middle of the X column
            String xString = ""+snitch.getLocation().getX();
            int xWidth = mc.fontRendererObj.getStringWidth(xString);
            int xStringPos = xPos + (X_COLUMN_WIDTH /2) - (xWidth/2);
            mc.fontRendererObj.drawString(xString, xStringPos ,yFinal,16777215);
            xPos += X_COLUMN_WIDTH + (GuiConstants.SMALL_SEPARATION_DISTANCE);

            //Draw the y coordinate in the middle of the Y column
            String yString = ""+snitch.getLocation().getY();
            int yWidth = mc.fontRendererObj.getStringWidth(yString);
            int yStringPos = xPos + (Y_COLUMN_WIDTH /2) - (yWidth/2);
            mc.fontRendererObj.drawString(yString, yStringPos ,yFinal,16777215);
            xPos += Y_COLUMN_WIDTH + (GuiConstants.SMALL_SEPARATION_DISTANCE);

            //Draw the z coordinate in the middle of the Z column
            String zString = ""+snitch.getLocation().getZ();
            int zWidth = mc.fontRendererObj.getStringWidth(zString);
            int zStringPos = xPos + (Z_COLUMN_WIDTH /2) - (zWidth/2);
            mc.fontRendererObj.drawString(zString, zStringPos ,yFinal,16777215);
            xPos += Z_COLUMN_WIDTH + (GuiConstants.STANDARD_SEPARATION_DISTANCE);

            //Draw the snitch list name (or arbitrary string_ in the middle of the snitch lists column
            String snitchListString = snitch.getAttachedSnitchLists().size() > 1 ? "View Snitch Lists" : snitch.getAttachedSnitchLists().get(0).getListName();
            int snitchListWidth = mc.fontRendererObj.getStringWidth(snitchListString);
            int snitchListStringPos = xPos + (SNITCH_LIST_COLUMN_WIDTH /2) - (snitchListWidth/2);
            mc.fontRendererObj.drawString(snitchListString, snitchListStringPos ,yFinal,16777215);
            //xPos += SNITCH_LIST_COLUMN_WIDTH + (GuiConstants.STANDARD_SEPARATION_DISTANCE);
        }

        public boolean mousePressed(int index, int xPos, int yPos, int mouseEvent, int relX, int relY)
        {
            if(mouseEvent == 1) //If its a right click
            {
                int rightBound = snitchListNameColumnLeftBound + SNITCH_LIST_COLUMN_WIDTH;
                if(xPos >= snitchListNameColumnLeftBound && xPos <= rightBound) //Check if they right clicked in the name column
                {
                    //TODO---This is where we will open a new gui displaying all the snitch lists connected to this snitch
                    //mc.displayGuiScreen(new EditStringGui(cancelToScreen,snitchList.getListName(),"Edit List Name",new EditNameAcceptor(snitchList),20));
                    return true;
                }
                return false;
            }
            return false;
        }

        public void mouseReleased(int index, int xPos, int yPos, int mouseEvent, int relX, int relY)
        {}

        @Override
        public void setSelected(int p_178011_1, int p_178011_2_, int p_178011_3_)
        {}
    }
}
