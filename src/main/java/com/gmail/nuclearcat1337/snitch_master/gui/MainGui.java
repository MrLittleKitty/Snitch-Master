package com.gmail.nuclearcat1337.snitch_master.gui;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

/**
 * Created by Mr_Little_Kitty on 9/11/2016.
 */
public class MainGui extends GuiScreen
{
    private SnitchMaster snitchMaster;

    public MainGui(SnitchMaster snitchMaster)
    {
        this.snitchMaster = snitchMaster;
    }

    @SuppressWarnings("unchecked")
    public void initGui()
    {
        //byte b0 = -16;
        this.buttonList.clear();

        String updateButtonMessage = snitchMaster.getChatSnitchParser().isUpdatingSnitchList() ? "Cancel Snitch Update" : "Full Snitch Update";

        int xPos = (this.width/2) - (GuiConstants.LONG_BUTTON_WIDTH/2);
        int yPos = (this.height/4) + 8 - (GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE);

        this.buttonList.add(new GuiButton(0, xPos, yPos, GuiConstants.LONG_BUTTON_WIDTH, GuiConstants.STANDARD_BUTTON_HEIGHT, updateButtonMessage));

        yPos = yPos + GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE;

        yPos = yPos + GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE;

        yPos = yPos + GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE;

        this.buttonList.add(new GuiButton(2, xPos, yPos, "View Snitch Lists"));

        yPos = yPos + GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE;

        this.buttonList.add(new GuiButton(3, xPos, yPos, "View Snitches"));

        yPos = yPos + GuiConstants.STANDARD_BUTTON_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE;

        this.buttonList.add(new GuiButton(4, xPos,yPos, "Done"));

//        if(SVChatHandler.snitchReport)
//        {
//            this.buttonList.add(new GuiButton(6, this.width / 2 - 100, this.height / 4 + 48 + b0, StatCollector
//                    .translateToLocal("svoptions.snitchReportCancel")));
//        }
//        else
//        {
//            this.buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 + b0, StatCollector
//                    .translateToLocal("svoptions.snitchReport")));
//        }
//        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 72 + b0, 98, 18, SV.settings
//                .getKeyBinding(SVSettings.Options.UPDATE_DETECTION)));
//
//        this.buttonList.add(new GuiButton(2, this.width / 2 + 2, this.height / 4 + 72 + b0, 98, 18, SV.settings
//                .getKeyBinding(SVSettings.Options.RENDER_ENABLED)));
//
//        this.buttonList.add(new SVGuiOptionSlider(100, this.width / 2 - 100, this.height / 4 + 96 + b0, renderDistance));
    }

    public void actionPerformed(GuiButton button)
    {
        switch (button.id)
        {
            case 0:
                if(snitchMaster.getChatSnitchParser().isUpdatingSnitchList())
                    snitchMaster.getChatSnitchParser().resetUpdatingSnitchList(true);
                else
                    snitchMaster.getChatSnitchParser().updateSnitchList();
                this.mc.displayGuiScreen((GuiScreen) null);
                this.mc.setIngameFocus();
                break;
//            case 1:
//                SV.settings.setOptionValue(SVSettings.Options.UPDATE_DETECTION);
//                button.displayString = SV.settings.getKeyBinding(SVSettings.Options.UPDATE_DETECTION);
//                SVFileIOHandler.saveSettings();
//                break;
//            case 2:
//                SV.settings.setOptionValue(SVSettings.Options.RENDER_ENABLED);
//                button.displayString = SV.settings.getKeyBinding(SVSettings.Options.RENDER_ENABLED);
//                SVFileIOHandler.saveSettings();
//                break;
            case 2: //"View Snitch Lists"
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new EditSnitchListsGui(this,snitchMaster,snitchMaster.getSnitchLists().asCollection(),true, "All Snitch Lists"));
                break;
            case 3: //"View Snitches"
                this.mc.displayGuiScreen(new EditSnitchesGui(this,snitchMaster.getSnitches(),"All Snitches"));
                break;
            case 4: //"Done"
                this.mc.displayGuiScreen((GuiScreen) null);
                this.mc.setIngameFocus();
                //SVFileIOHandler.saveSettings();
                break;
//            case 5:
//                SVChatHandler.tempList = new ArrayList<Block>();
//                SVChatHandler.snitchReport = true;
//                this.mc.displayGuiScreen((GuiScreen) null);
//                this.mc.setIngameFocus();
//                break;
//            case 6:
//                SVChatHandler.snitchReport = false;
//                SVChatHandler.jalistIndex = 1;
//                SVChatHandler.jainfoIndex = 1;
//                this.mc.displayGuiScreen((GuiScreen) null);
//                this.mc.setIngameFocus();
//                break;
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
