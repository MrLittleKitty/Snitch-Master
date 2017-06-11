package com.gmail.nuclearcat1337.snitch_master.gui.snitchalerts;

import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.gmail.nuclearcat1337.snitch_master.gui.controls.TextBox;
import com.gmail.nuclearcat1337.snitch_master.util.Acceptor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

/**
 * Created by Mr_Little_Kitty on 6/5/2017.
 */
public class NewAlertFormatScreen extends GuiScreen
{
    private final GuiScreen previousScreen;

    private AlertFormat format;
    private Acceptor<AlertFormat> acceptor;

    private TextBox nameBox;

    public NewAlertFormatScreen(GuiScreen previousScreen, AlertFormat format, Acceptor<AlertFormat> acceptor)
    {
        this.previousScreen = previousScreen;

        this.format = format;
        this.acceptor = acceptor;
    }

    @Override
    public void initGui()
    {
        int numberOfButtons = AlertLanguage.getNumberOfInstructions();

        int startXPos = 0;
        int startYPos;

        if(numberOfButtons % 2 == 0)
            startYPos = (this.height/2) - (GuiConstants.SMALL_SEPARATION_DISTANCE/2) - (GuiConstants.SMALL_SEPARATION_DISTANCE*((numberOfButtons/2)-1)) - (GuiConstants.STANDARD_BUTTON_HEIGHT*(numberOfButtons/2));
        else
            startYPos = (this.height/2) - (GuiConstants.STANDARD_BUTTON_HEIGHT/2) - (GuiConstants.SMALL_SEPARATION_DISTANCE*((numberOfButtons-1)/2)) - (GuiConstants.STANDARD_BUTTON_HEIGHT *((numberOfButtons-1)/2));

        for(int i = 0; i < numberOfButtons; i++)
        {
            GuiButton button = new GuiButton(i+1,startXPos,startYPos,GuiConstants.MEDIUM_BUTTON_WIDTH,GuiConstants.STANDARD_BUTTON_HEIGHT,AlertLanguage.getInstructionName((byte)(i+1)));
            buttonList.add(button);

            startYPos += GuiConstants.SMALL_SEPARATION_DISTANCE + GuiConstants.STANDARD_BUTTON_HEIGHT;

        }
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        if(button.id == -1) //Done button
        {
            mc.displayGuiScreen(previousScreen);
        }
        else if(button.id == 0) //Save button
        {

            mc.displayGuiScreen(previousScreen);
        }
        else if(button.id > 0)
            addNewInstruction((byte)button.id);
    }

    private void addNewInstruction(byte instruction)
    {

    }

}
