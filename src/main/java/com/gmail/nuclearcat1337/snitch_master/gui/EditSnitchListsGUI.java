package com.gmail.nuclearcat1337.snitch_master.gui;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

/**
 * Created by Mr_Little_Kitty on 9/16/2016.
 */
public class EditSnitchListsGui extends GuiScreen
{
    public GuiScreen parentScreen;

    private SnitchListsGui snitchListGUI;
    private SnitchMaster snitchMaster;

    private static final int DONE_BUTTON_WIDTH = GuiConstants.MEDIUM_BUTTON_WIDTH*3;
    private static final int NEW_BUTTON_WIDTH = GuiConstants.MEDIUM_BUTTON_WIDTH;
    private static final int RENDER_ON_BUTTON_WIDTH = GuiConstants.MEDIUM_BUTTON_WIDTH;
    private static final int RENDER_OFF_BUTTON_WIDTH = GuiConstants.MEDIUM_BUTTON_WIDTH;

    public EditSnitchListsGui(GuiScreen guiscreen, SnitchMaster snitchMaster)
    {
        this.parentScreen = guiscreen;
        this.snitchMaster = snitchMaster;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.snitchListGUI.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void initGui()
    {
        byte b0 = -20;

        this.snitchListGUI = new SnitchListsGui(this, snitchMaster.getSnitchLists());

        this.buttonList.clear();

        int xPos = (this.width/2)- DONE_BUTTON_WIDTH -(GuiConstants.STANDARD_SEPARATION_DISTANCE/2);
        int yPos = this.height - GuiConstants.STANDARD_BUTTON_HEIGHT - GuiConstants.STANDARD_SEPARATION_DISTANCE;

        this.buttonList.add(new GuiButton(4, xPos , yPos, DONE_BUTTON_WIDTH, 18, "Back to Settings"));

        xPos = (this.width/2)+(GuiConstants.STANDARD_SEPARATION_DISTANCE/2);
        this.buttonList.add(new GuiButton(5, xPos, yPos, NEW_BUTTON_WIDTH, 18, "New"));

        xPos += GuiConstants.STANDARD_SEPARATION_DISTANCE+ NEW_BUTTON_WIDTH;
        this.buttonList.add(new GuiButton(6, xPos, yPos, RENDER_ON_BUTTON_WIDTH, 18, "All On"));

        xPos += GuiConstants.STANDARD_SEPARATION_DISTANCE+ RENDER_ON_BUTTON_WIDTH;
        this.buttonList.add(new GuiButton(7, xPos, yPos, RENDER_OFF_BUTTON_WIDTH, 18, "All Off"));

        this.snitchListGUI.registerScrollButtons(4, 5);
        super.initGui();
    }

    public void actionPerformed(GuiButton button)
    {
        if (!button.enabled) return;
        switch (button.id)
        {
            case 4: //Done
                this.mc.displayGuiScreen(parentScreen);
                break;
            case 5: //New Snitch List
                Minecraft.getMinecraft().displayGuiScreen(new NewSnitchListGui(this,snitchMaster));
                break;
            case 6: //Render all on
                snitchListGUI.setAllRender(true);
                break;
            case 7: //Render all off
                snitchListGUI.setAllRender(false);
                break;
        }
    }


    protected void mouseClicked(int mouseX, int mouseY, int mouseEvent)
    {
        this.snitchListGUI.mouseClicked(mouseX, mouseY, mouseEvent);

        try
        {
            super.mouseClicked(mouseX, mouseY, mouseEvent);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
