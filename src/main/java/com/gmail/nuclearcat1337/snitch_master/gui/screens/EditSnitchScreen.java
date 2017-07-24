package com.gmail.nuclearcat1337.snitch_master.gui.screens;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.gmail.nuclearcat1337.snitch_master.gui.controls.TextBox;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.ILocation;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchManager;
import net.minecraft.client.gui.*;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

import static com.gmail.nuclearcat1337.snitch_master.gui.snitchtable.SnitchRemoveColumn.removedSnitches;

/**
 * Created by Mr_Little_Kitty on 5/28/2017.
 */
public class EditSnitchScreen extends GuiScreen
{
    private SnitchManager snitchManager;
    private final GuiScreen previousScreen;
    private final Snitch snitch;

    private int yStartHeight;
    private int xTotalWidth;
    private int xLeft;

    //private GuiButton deleteButton;
    private boolean onConfirmationButton;

    private TextBox nameBox;
    private TextBox groupBox;

    private TextBox xBox;
    private TextBox yBox;
    private TextBox zBox;
    private TextBox worldBox;

    private TextBox cullTimeBox;
    private TextBox distanceBox;

    public EditSnitchScreen(Snitch snitch, SnitchManager snitchManager, GuiScreen previousScreen)
    {
        this.snitch = snitch;
        this.snitchManager = snitchManager;
        this.previousScreen = previousScreen;
        onConfirmationButton = false;
    }

    @Override
    public void initGui()
    {
        yStartHeight = this.height/2 - (GuiConstants.STANDARD_TEXTBOX_HEIGHT*3) - (fontRendererObj.FONT_HEIGHT*3) - (GuiConstants.STANDARD_SEPARATION_DISTANCE/2) - (GuiConstants.STANDARD_SEPARATION_DISTANCE*2);
        xTotalWidth = GuiConstants.MEDIUM_TEXBOX_LENGTH*2 + GuiConstants.STANDARD_SEPARATION_DISTANCE;
        xLeft = this.width/2 - xTotalWidth/2;

        int yHeight = yStartHeight;

        nameBox = new TextBox(snitch.getSnitchName(),fontRendererObj,xLeft,yHeight,xTotalWidth,GuiConstants.STANDARD_TEXTBOX_HEIGHT,false,false,100);

        yHeight += GuiConstants.STANDARD_TEXTBOX_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE + fontRendererObj.FONT_HEIGHT;

        groupBox = new TextBox(snitch.getGroupName(),fontRendererObj,xLeft,yHeight,xTotalWidth,GuiConstants.STANDARD_TEXTBOX_HEIGHT,false,false,100);

        yHeight += GuiConstants.STANDARD_TEXTBOX_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE + fontRendererObj.FONT_HEIGHT;

        int xPos = xLeft;

        xBox = new TextBox(String.valueOf(snitch.getLocation().getX()),fontRendererObj,xPos,yHeight,(xTotalWidth - (GuiConstants.SMALL_TEXBOX_LENGTH/3)*2 - (GuiConstants.STANDARD_SEPARATION_DISTANCE*2))/2,GuiConstants.STANDARD_TEXTBOX_HEIGHT,true,true,5);
        xBox.setEnabled(false);

        xPos += xBox.width + GuiConstants.STANDARD_SEPARATION_DISTANCE;

        yBox = new TextBox(String.valueOf(snitch.getLocation().getY()),fontRendererObj,xPos,yHeight,(GuiConstants.SMALL_TEXBOX_LENGTH/3)*2,GuiConstants.STANDARD_TEXTBOX_HEIGHT,true,true,5);
        yBox.setEnabled(false);

        xPos += yBox.width + GuiConstants.STANDARD_SEPARATION_DISTANCE;

        zBox = new TextBox(String.valueOf(snitch.getLocation().getZ()),fontRendererObj,xPos,yHeight,(xTotalWidth - (GuiConstants.SMALL_TEXBOX_LENGTH/3)*2 - (GuiConstants.STANDARD_SEPARATION_DISTANCE*2))/2,GuiConstants.STANDARD_TEXTBOX_HEIGHT,true,true,5);
        zBox.setEnabled(false);

        yHeight += GuiConstants.STANDARD_TEXTBOX_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE + fontRendererObj.FONT_HEIGHT;

        worldBox = new TextBox(snitch.getWorld(),fontRendererObj,xLeft,yHeight,xTotalWidth,GuiConstants.STANDARD_TEXTBOX_HEIGHT,false,false,100);
        worldBox.setEnabled(false);

        yHeight += GuiConstants.STANDARD_TEXTBOX_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE + fontRendererObj.FONT_HEIGHT;

        xPos = xLeft;

        String cullText = Double.isNaN(snitch.getCullTime()) ? "None" : (snitch.getCullTime()+" hrs");

        cullTimeBox = new TextBox(cullText,fontRendererObj,xPos,yHeight,(xTotalWidth/2) - (GuiConstants.STANDARD_SEPARATION_DISTANCE/2),GuiConstants.STANDARD_TEXTBOX_HEIGHT,false,false,100);
        cullTimeBox.setEnabled(false);

        xPos += cullTimeBox.width + GuiConstants.STANDARD_SEPARATION_DISTANCE;

        ILocation loc = snitch.getLocation();

        String text;
        if (!SnitchMaster.instance.getCurrentWorld().equalsIgnoreCase(loc.getWorld()))
            text = "N/A";
        else
        {
            int distance = getDistanceFromPlayer(loc.getX(), loc.getY(), loc.getZ());
            text = "" + distance+ " m";
        }

        distanceBox = new TextBox(text,fontRendererObj,xPos,yHeight,(xTotalWidth/2) - (GuiConstants.STANDARD_SEPARATION_DISTANCE/2),GuiConstants.STANDARD_TEXTBOX_HEIGHT,false,false,100);
        distanceBox.setEnabled(false);

        yHeight += GuiConstants.STANDARD_TEXTBOX_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE;

        this.buttonList.add(new GuiButton(1,(this.width/2)+GuiConstants.SMALL_SEPARATION_DISTANCE/2,yHeight,GuiConstants.SMALL_BUTTON_WIDTH,GuiConstants.STANDARD_BUTTON_HEIGHT,"Save"));

        this.buttonList.add(new GuiButton(2,(this.width/2)-GuiConstants.SMALL_SEPARATION_DISTANCE/2 - GuiConstants.SMALL_BUTTON_WIDTH,yHeight,GuiConstants.SMALL_BUTTON_WIDTH,GuiConstants.STANDARD_BUTTON_HEIGHT,"Cancel"));

        int delWidth = ((xTotalWidth - GuiConstants.SMALL_BUTTON_WIDTH*2 - GuiConstants.SMALL_SEPARATION_DISTANCE) - GuiConstants.SMALL_SEPARATION_DISTANCE*2)/2;

        this.buttonList.add(new GuiButton(3,xLeft,yHeight,delWidth,GuiConstants.STANDARD_BUTTON_HEIGHT,"X"));

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();

        this.nameBox.drawTextBox();
        this.groupBox.drawTextBox();
        this.xBox.drawTextBox();
        this.yBox.drawTextBox();
        this.zBox.drawTextBox();
        this.worldBox.drawTextBox();
        this.cullTimeBox.drawTextBox();
        this.distanceBox.drawTextBox();

        mc.fontRendererObj.drawString("Snitch Name", nameBox.xPosition, nameBox.yPosition - fontRendererObj.FONT_HEIGHT -1, 16777215);

        mc.fontRendererObj.drawString("Group Name", groupBox.xPosition, groupBox.yPosition - fontRendererObj.FONT_HEIGHT -1, 16777215);

        mc.fontRendererObj.drawString("X", xBox.xPosition, xBox.yPosition - fontRendererObj.FONT_HEIGHT -1, 16777215);

        mc.fontRendererObj.drawString("Y", yBox.xPosition, yBox.yPosition - fontRendererObj.FONT_HEIGHT -1, 16777215);

        mc.fontRendererObj.drawString("Z", zBox.xPosition, zBox.yPosition - fontRendererObj.FONT_HEIGHT -1, 16777215);

        mc.fontRendererObj.drawString("World", worldBox.xPosition, worldBox.yPosition - fontRendererObj.FONT_HEIGHT -1, 16777215);

        mc.fontRendererObj.drawString("Cull Time", cullTimeBox.xPosition, cullTimeBox.yPosition - fontRendererObj.FONT_HEIGHT -1, 16777215);

        mc.fontRendererObj.drawString("Distance", distanceBox.xPosition, distanceBox.yPosition - fontRendererObj.FONT_HEIGHT -1, 16777215);

        super.drawScreen(mouseX,mouseY,partialTicks);
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        //"Done" button
        if(button.id == 1)
            saveSnitch();
        else if(button.id == 2) //"Cancel" button
        {
            mc.displayGuiScreen(previousScreen);
        }
        else if(button.id == 3) //"Delete" button
        {
            if(onConfirmationButton)
            {
                snitchManager.getSnitches().remove(snitch);
                removedSnitches.add(snitch.getLocation());

                if (SnitchMaster.jmInterface != null)
                    SnitchMaster.jmInterface.refresh(snitchManager.getSnitches());

                mc.displayGuiScreen(previousScreen);
            }
            else
            {
                onConfirmationButton = true;
                button.xPosition += button.width + GuiConstants.SMALL_SEPARATION_DISTANCE*2 + GuiConstants.SMALL_BUTTON_WIDTH*2 + GuiConstants.SMALL_SEPARATION_DISTANCE;
            }
        }
    }

    private void saveSnitch()
    {
        snitchManager.setSnitchName(snitch,nameBox.getText());
        snitchManager.setSnitchGroup(snitch,groupBox.getText());

        snitchManager.saveSnitches();

        mc.displayGuiScreen(previousScreen);
    }

    @Override
    public void keyTyped(char par1, int par2) throws IOException
    {
        if(par2 == Keyboard.KEY_RETURN)
        {
            saveSnitch();
            return;
        }

        if(nameBox.isFocused())
        {
            if (par2 == Keyboard.KEY_TAB)
            {
                groupBox.setFocused(true);
                nameBox.setFocused(false);
            }
            nameBox.textboxKeyTyped(par1, par2);
        }
        else if(groupBox.isFocused())
        {
            if (par2 == Keyboard.KEY_TAB)
            {
                nameBox.setFocused(true);
                groupBox.setFocused(false);
            }
            groupBox.textboxKeyTyped(par1, par2);
        }
        else
        {
            if (par2 == Keyboard.KEY_TAB)
                nameBox.setFocused(true);
        }
        super.keyTyped(par1, par2);
    }

    @Override
    public void mouseClicked(int one, int two, int three) throws IOException
    {
        nameBox.mouseClicked(one,two,three);
        groupBox.mouseClicked(one,two,three);
        super.mouseClicked(one, two, three);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    private int getDistanceFromPlayer(int x, int y, int z)
    {
        int x1 = x - (int) mc.thePlayer.posX;
        int y1 = y - (int) mc.thePlayer.posY;
        int z1 = z - (int) mc.thePlayer.posZ;

        return (int) Math.sqrt((x1 * x1) + (y1 * y1) + (z1 * z1));
    }
}
