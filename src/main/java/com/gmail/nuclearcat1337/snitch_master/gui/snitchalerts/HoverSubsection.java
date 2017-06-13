package com.gmail.nuclearcat1337.snitch_master.gui.snitchalerts;

import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.gmail.nuclearcat1337.snitch_master.gui.controls.TextBox;
import com.gmail.nuclearcat1337.snitch_master.util.Acceptor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Mr_Little_Kitty on 6/12/2017.
 */
public class HoverSubsection extends Subsection
{
    private static final HashSet<Byte> DISABLED_BUTTONS = new HashSet<>();
    {
        DISABLED_BUTTONS.add(AlertLanguage.HOVER_EVENT);
    }

    private static Minecraft mc;

    private TextBox visibleTextBox;
    private TextBox hoveringTextBox;

    private GuiButton addVisibleLiteral;
    private GuiButton addHoveringLiteral;

    private GuiButton saveButton;
    private GuiButton doneButton;

    private ArrayList<Byte> instructions;
    private ArrayList<String> literals;

    HoverSubsection(FormatBuilder previousBuilder, Acceptor<Subsection> changeSubsection)
    {
        super(previousBuilder, changeSubsection);
        mc = Minecraft.getMinecraft();

        instructions = new ArrayList<>();
        literals = new ArrayList<>();
    }

    @Override
    public void addInstructions(ArrayList<Byte> instructions, ArrayList<String> literals)
    {
        //The only instructions that the hover section can receive is a string literal
        //The only part we care about is that the literals list should have 1 literal

        if(literals.size() == 1)
        {

        }
    }

    @Override
    public void instructionButtonPressed(byte instruction)
    {
        if(instruction == AlertLanguage.STRING_SUBSTITUTION)
        {

        }
    }

    @Override
    public void setButtonState(GuiButton button, boolean enabled)
    {
        if(DISABLED_BUTTONS.contains((byte)button.id))
            button.enabled = enabled;
        else
            button.enabled = !enabled;
    }


    @Override
    public void drawScreen(int xLeft, int yUp, int width, int height, int mouseX, int mouseY, float partialTicks)
    {
        if(visibleTextBox == null)
        {
            int buttonWidth = (width - GuiConstants.STANDARD_SEPARATION_DISTANCE) / 2;

            int yStart = yUp + height/2 - GuiConstants.STANDARD_BUTTON_HEIGHT/2 - GuiConstants.STANDARD_SEPARATION_DISTANCE - GuiConstants.STANDARD_BUTTON_HEIGHT;
            int xStart = xLeft + width/2 - GuiConstants.STANDARD_SEPARATION_DISTANCE/2 - buttonWidth;

            visibleTextBox = new TextBox("",mc.fontRendererObj,xStart,yStart,buttonWidth,GuiConstants.STANDARD_TEXTBOX_HEIGHT,false,false,40);
            hoveringTextBox = new TextBox("",mc.fontRendererObj,xStart + buttonWidth + GuiConstants.STANDARD_SEPARATION_DISTANCE,yStart,buttonWidth,GuiConstants.STANDARD_TEXTBOX_HEIGHT,false,false,40);

            yStart += GuiConstants.STANDARD_TEXTBOX_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE;

            addVisibleLiteral = new GuiButton(1,xStart,yStart,buttonWidth,GuiConstants.STANDARD_BUTTON_HEIGHT,"Add Literal");
            addHoveringLiteral = new GuiButton(2,xStart + buttonWidth + GuiConstants.STANDARD_SEPARATION_DISTANCE,yStart,buttonWidth,GuiConstants.STANDARD_BUTTON_HEIGHT,"Add Literal");

            yStart += GuiConstants.STANDARD_TEXTBOX_HEIGHT + GuiConstants.STANDARD_SEPARATION_DISTANCE;

            doneButton = new GuiButton(3,xStart,yStart,buttonWidth,GuiConstants.STANDARD_BUTTON_HEIGHT,"Back");
            saveButton = new GuiButton(4,xStart + buttonWidth + GuiConstants.STANDARD_SEPARATION_DISTANCE,yStart,buttonWidth,GuiConstants.STANDARD_BUTTON_HEIGHT,"Save");
        }

        visibleTextBox.drawTextBox();
        hoveringTextBox.drawTextBox();

        addVisibleLiteral.drawButton(mc,mouseX,mouseY);
        addHoveringLiteral.drawButton(mc,mouseX,mouseY);
        doneButton.drawButton(mc,mouseX,mouseY);
        saveButton.drawButton(mc,mouseX,mouseY);
    }

    @Override
    public void mouseClicked(int one, int two, int three) throws IOException
    {
        visibleTextBox.mouseClicked(one,two,three);
        hoveringTextBox.mouseClicked(one,two,three);

        if(addVisibleLiteral.mousePressed(mc,one,two))
        {

        }
        else if(addHoveringLiteral.mousePressed(mc,one,two))
        {

        }
        else if(saveButton.mousePressed(mc,one,two))
        {

        }
        else if(doneButton.mousePressed(mc,one,two))
        {

        }
    }

    @Override
    public void keyTyped(char par1, int par2) throws IOException
    {
        if (visibleTextBox.isFocused())
        {
            if (par2 == Keyboard.KEY_TAB)
            {
                visibleTextBox.setEnabled(false);
                hoveringTextBox.setEnabled(true);
            }
        }
        else if(hoveringTextBox.isFocused())
        {
            if(par2 == Keyboard.KEY_TAB)
            {
                hoveringTextBox.setFocused(false);
                visibleTextBox.setEnabled(true);
            }
        }

        visibleTextBox.textboxKeyTyped(par1,par2);
        hoveringTextBox.textboxKeyTyped(par1,par2);
    }

    @Override
    public void updateScreen()
    {
        visibleTextBox.updateCursorCounter();
        hoveringTextBox.updateCursorCounter();
    }
}
