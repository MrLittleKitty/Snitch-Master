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

    private static final int separationDistance = 2;
    private static final int doneButtonWidth = 147;
    private static final int newButtonWidth = 147/3;
    private static final int renderOnWidth = 147/3;
    private static final int renderOffWidth = 147/3;

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

        int xPos = (this.width/2)-doneButtonWidth-(separationDistance/2);
        this.buttonList.add(new GuiButton(4, xPos , this.height - 4 + b0, doneButtonWidth, 18, "Back to Settings"));

        xPos = (this.width/2)+(separationDistance/2);
        this.buttonList.add(new GuiButton(5, xPos, this.height - 4 + b0, newButtonWidth, 18, "New"));

        xPos += separationDistance+newButtonWidth;
        this.buttonList.add(new GuiButton(6, xPos, this.height - 4 + b0, renderOnWidth, 18, "All On"));

        xPos += separationDistance+renderOnWidth;
        this.buttonList.add(new GuiButton(7, xPos, this.height - 4 + b0, renderOffWidth, 18, "All Off"));

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
