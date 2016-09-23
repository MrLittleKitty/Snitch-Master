package com.gmail.nuclearcat1337.snitch_master.gui;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

/**
 * Created by Mr_Little_Kitty on 9/11/2016.
 */
public class MainGUI extends GuiScreen
{
    private SnitchMaster snitchMaster;

    public MainGUI(SnitchMaster snitchMaster)
    {
        this.snitchMaster = snitchMaster;
    }

    @SuppressWarnings("unchecked")
    public void initGui()
    {
        byte b0 = -16;
        this.buttonList.clear();

        String updateButtonMessage = snitchMaster.getChatSnitchParser().isUpdatingSnitchList() ? "Cancel Snitch Update" : "Full Snitch Update";
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 24 + b0, updateButtonMessage));
//
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

        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 4 + 96 + b0, "View Snitch Lists"));

        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 4 + 120 + b0, "View Snitches"));

        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 120 + 24 + b0, "Done"));
    }

    public void actionPerformed(GuiButton button)
    {
        switch (button.id)
        {
            case 0:
                if(snitchMaster.getChatSnitchParser().isUpdatingSnitchList())
                    snitchMaster.getChatSnitchParser().resetUpdatingSnitchList();
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
                this.mc.displayGuiScreen(new EditSnitchListsGUI(this,snitchMaster));
                break;
            case 3: //"View Snitches"

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
