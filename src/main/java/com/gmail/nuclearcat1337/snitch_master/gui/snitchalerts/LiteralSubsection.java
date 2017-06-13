package com.gmail.nuclearcat1337.snitch_master.gui.snitchalerts;

import com.gmail.nuclearcat1337.snitch_master.gui.GuiConstants;
import com.gmail.nuclearcat1337.snitch_master.gui.controls.TextBox;
import com.gmail.nuclearcat1337.snitch_master.util.Acceptor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Mr_Little_Kitty on 6/11/2017.
 */
public class LiteralSubsection extends Subsection
{
    private static Minecraft mc;

    private TextBox literalTextbox;
    private GuiButton saveButton;

    LiteralSubsection(FormatBuilder previousBuilder, Acceptor<Subsection> changeSubsection)
    {
        super(previousBuilder, changeSubsection);
        mc = Minecraft.getMinecraft();
    }

    @Override
    public void drawScreen(int xLeft, int yUp, int width, int height, int mouseX, int mouseY, float partialTicks)
    {
        if(saveButton == null)
        {
            int x = xLeft + (width/2) - GuiConstants.MEDIUM_BUTTON_WIDTH/2;
            int y = yUp + (height/2) + GuiConstants.STANDARD_SEPARATION_DISTANCE/2;
            saveButton = new GuiButton(1, x,y,GuiConstants.MEDIUM_BUTTON_WIDTH,GuiConstants.STANDARD_BUTTON_HEIGHT,"Save");

            x = xLeft + (width/2) - GuiConstants.LARGE_TEXBOX_LENGTH;
            y = yUp + (height/2) - GuiConstants.STANDARD_TEXTBOX_HEIGHT - GuiConstants.STANDARD_SEPARATION_DISTANCE/2;

            literalTextbox = new TextBox("",mc.fontRendererObj,x,y,GuiConstants.LARGE_TEXBOX_LENGTH, GuiConstants.STANDARD_TEXTBOX_HEIGHT,false,false,30);
            literalTextbox.setFocused(true);
        }

        saveButton.drawButton(mc,mouseX,mouseY);
        literalTextbox.drawTextBox();
    }

    @Override
    public void addInstructions(ArrayList<Byte> instructions, ArrayList<String> literals)
    {

    }

    @Override
    public void mouseClicked(int one, int two, int three) throws IOException
    {
        literalTextbox.mouseClicked(one,two,three);

        if(saveButton.mousePressed(mc,one,two))
        {

        }
    }

    @Override
    public void keyTyped(char par1, int par2) throws IOException
    {
        literalTextbox.textboxKeyTyped(par1,par2);
    }

    @Override
    public void updateScreen()
    {
        literalTextbox.updateCursorCounter();
    }
}
