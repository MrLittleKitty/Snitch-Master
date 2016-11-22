package com.gmail.nuclearcat1337.snitch_master.gui;

import com.gmail.nuclearcat1337.snitch_master.gui.controls.TextBox;
import com.gmail.nuclearcat1337.snitch_master.util.Acceptor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

/**
 * Created by Mr_Little_Kitty on 9/23/2016.
 */
public class EditStringGui extends GuiScreen
{
    private GuiScreen cancelToScreen;

    private final String titleText;
    private final int titleWidth;

    private Acceptor<String> callback;
    private String editString;
    private TextBox stringBox;

    private final int maxStringLength;

    public EditStringGui(GuiScreen cancelToScreen, String editString, String titleText, Acceptor<String> callback, int maxStringLength)
    {
        this.cancelToScreen = cancelToScreen;
        this.editString = editString;
        this.callback = callback;
        this.maxStringLength = maxStringLength;

        this.titleText = titleText;
        this.titleWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(titleText);
    }

    public void initGui()
    {
        int xPos = (this.width/2) - (GuiConstants.LARGE_TEXBOX_LENGTH/2);
        int yPos = (this.height/2) - (GuiConstants.STANDARD_TEXTBOX_HEIGHT) - (GuiConstants.STANDARD_SEPARATION_DISTANCE/2);

        stringBox = new TextBox(editString,fontRendererObj,xPos,yPos,GuiConstants.LARGE_TEXBOX_LENGTH,GuiConstants.STANDARD_TEXTBOX_HEIGHT,false,false);
        stringBox.setMaxStringLength(maxStringLength);
        stringBox.setFocused(true);

        this.buttonList.clear();

        yPos = (this.height/2) + (GuiConstants.STANDARD_SEPARATION_DISTANCE/2);
        int buttonWidth = (stringBox.width/2) - GuiConstants.STANDARD_SEPARATION_DISTANCE;

        this.buttonList.add(new GuiButton(1,xPos,yPos,buttonWidth,GuiConstants.STANDARD_BUTTON_HEIGHT,"Cancel"));

        xPos += (buttonWidth + GuiConstants.STANDARD_SEPARATION_DISTANCE);

        this.buttonList.add(new GuiButton(2,xPos,yPos,buttonWidth,GuiConstants.STANDARD_BUTTON_HEIGHT,"Save"));

        super.initGui();
    }

    public void updateScreen()
    {
        stringBox.updateCursorCounter();
        super.updateScreen();
    }

    public void keyTyped(char par1, int par2) throws IOException
    {
        if(stringBox.isFocused())
            stringBox.textboxKeyTyped(par1, par2);
        super.keyTyped(par1,par2);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.stringBox.drawTextBox();

        int xPos = stringBox.xPosition + (stringBox.width/2) - (titleWidth/2);
        int yPos = stringBox.yPosition - mc.fontRendererObj.FONT_HEIGHT - GuiConstants.STANDARD_SEPARATION_DISTANCE;

        mc.fontRendererObj.drawString(titleText,xPos,yPos,16777215);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void mouseClicked(int one, int two, int three) throws IOException
    {
        this.stringBox.mouseClicked(one,two,three);
        super.mouseClicked(one,two,three);
    }

    public void actionPerformed(GuiButton button)
    {
        switch (button.id)
        {
            case 1:
                mc.displayGuiScreen(cancelToScreen);
                break;
            case 2:
                String newText = stringBox.getText();
                if (!newText.isEmpty() && callback.accept(newText))
                    mc.displayGuiScreen(cancelToScreen);
                break;
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
