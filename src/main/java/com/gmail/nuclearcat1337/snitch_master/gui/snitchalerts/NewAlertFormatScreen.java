package com.gmail.nuclearcat1337.snitch_master.gui.snitchalerts;

import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.gmail.nuclearcat1337.snitch_master.gui.controls.TextBox;
import com.gmail.nuclearcat1337.snitch_master.util.Acceptor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Mr_Little_Kitty on 6/5/2017.
 */
public class NewAlertFormatScreen extends GuiScreen implements FormatBuilder, Acceptor<Subsection>
{
    private final GuiScreen previousScreen;

    private AlertFormat format;
    private Acceptor<AlertFormat> acceptor;

    private ArrayList<Byte> instructionList;
    private ArrayList<String> literalList;

    private Subsection activeSubsection;

    private TextBox nameBox;

    NewAlertFormatScreen(GuiScreen previousScreen, AlertFormat format, Acceptor<AlertFormat> acceptor)
    {
        this.previousScreen = previousScreen;

        this.format = format;
        this.acceptor = acceptor;

        this.instructionList = new ArrayList<>();
        this.literalList = new ArrayList<>();

        this.activeSubsection = null;
    }

    int subsectionX,subsectionY,subsectionWidth,subsectionHeight;

    @Override
    public void initGui()
    {
        final int instructionButtonWidth = (GuiConstants.LONG_BUTTON_WIDTH/3)*2;
        int numberOfButtons = AlertLanguage.INSTRUCTIONS.length;

        int startXPos = (this.width/2) - GuiConstants.MEDIUM_BUTTON_WIDTH*2 - GuiConstants.STANDARD_SEPARATION_DISTANCE - (GuiConstants.STANDARD_SEPARATION_DISTANCE/2);
        int startYPos;

        if(numberOfButtons % 2 == 0)
            startYPos = (this.height/2) - (GuiConstants.SMALL_SEPARATION_DISTANCE/2) - (GuiConstants.SMALL_SEPARATION_DISTANCE*((numberOfButtons/2)-1)) - (GuiConstants.STANDARD_BUTTON_HEIGHT*(numberOfButtons/2));
        else
            startYPos = (this.height/2) - (GuiConstants.STANDARD_BUTTON_HEIGHT/2) - (GuiConstants.SMALL_SEPARATION_DISTANCE*((numberOfButtons-1)/2)) - (GuiConstants.STANDARD_BUTTON_HEIGHT *((numberOfButtons-1)/2));

        int xPos = (this.width/2) + GuiConstants.STANDARD_SEPARATION_DISTANCE;

        nameBox = new TextBox(format == null ? "" : format.getName(),mc.fontRendererObj,xPos,startYPos,GuiConstants.MEDIUM_BUTTON_WIDTH*2,GuiConstants.STANDARD_TEXTBOX_HEIGHT,false,false,100);
        nameBox.setFocused(true);

        for(int i = 0; i < numberOfButtons; i++)
        {
            byte instruction = AlertLanguage.INSTRUCTIONS[i];
            GuiButton button = new GuiButton((int)instruction,startXPos,startYPos,instructionButtonWidth,GuiConstants.STANDARD_BUTTON_HEIGHT,AlertLanguage.getInstructionName(instruction));
            buttonList.add(button);

            startYPos += GuiConstants.SMALL_SEPARATION_DISTANCE + GuiConstants.STANDARD_BUTTON_HEIGHT;
        }

        startYPos -= GuiConstants.SMALL_SEPARATION_DISTANCE + GuiConstants.STANDARD_BUTTON_HEIGHT;

        startXPos = (this.width/2) + (GuiConstants.STANDARD_SEPARATION_DISTANCE/2);
        buttonList.add(new GuiButton(-1,startXPos,startYPos,GuiConstants.MEDIUM_BUTTON_WIDTH,GuiConstants.STANDARD_BUTTON_HEIGHT,"Done"));

        startXPos += GuiConstants.MEDIUM_BUTTON_WIDTH + GuiConstants.STANDARD_SEPARATION_DISTANCE;

        buttonList.add(new GuiButton(0,startXPos,startYPos,GuiConstants.MEDIUM_BUTTON_WIDTH,GuiConstants.STANDARD_BUTTON_HEIGHT,"Save"));
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        if(button.id == -1) //Done button
        {
            if(activeSubsection != null)
            {
                mc.displayGuiScreen(previousScreen);
            }
        }
        else if(button.id == 0) //Save button
        {

            mc.displayGuiScreen(previousScreen);
        }
        else if(button.id > 0)
            addInstruction((byte)button.id);
    }

    @Override
    public void addInstructions(ArrayList<Byte> instructions, ArrayList<String> literals)
    {

    }

    private void addInstruction(byte instruction)
    {
        if(instruction == AlertLanguage.STRING_SUBSTITUTION)
        {
            for(GuiButton b : buttonList)
                if(b.id != 0 && b.id != -1)
                    b.enabled = false;

            activeSubsection = new LiteralSubsection(this,this);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.nameBox.drawTextBox();

        if(activeSubsection != null)
            activeSubsection.drawScreen(subsectionX,subsectionY,subsectionWidth,subsectionHeight,mouseX,mouseY,partialTicks);

        super.drawScreen(mouseX,mouseY,partialTicks);
    }

    @Override
    public void mouseClicked(int one, int two, int three) throws IOException
    {
        this.nameBox.mouseClicked(one,two,three);

        if(activeSubsection != null)
            activeSubsection.mouseClicked(one,two,three);

        super.mouseClicked(one,two,three);
    }

    @Override
    public void keyTyped(char par1, int par2) throws IOException
    {
        nameBox.textboxKeyTyped(par1,par2);

        if(activeSubsection != null)
            activeSubsection.keyTyped(par1,par2);

        super.keyTyped(par1,par2);
    }

    @Override
    public void updateScreen()
    {
        nameBox.updateCursorCounter();

        if(activeSubsection != null)
            activeSubsection.updateScreen();
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public boolean accept(Subsection item)
    {
        //This means that they want to change the subsection
        return false;
    }
}
