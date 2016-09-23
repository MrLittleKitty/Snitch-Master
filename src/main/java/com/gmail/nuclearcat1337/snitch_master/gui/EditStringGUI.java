package com.gmail.nuclearcat1337.snitch_master.gui;

import com.gmail.nuclearcat1337.snitch_master.api.SnitchListQualifier;
import com.gmail.nuclearcat1337.snitch_master.gui.controls.TextBox;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchLists;
import com.gmail.nuclearcat1337.snitch_master.util.Color;
import com.gmail.nuclearcat1337.snitch_master.util.IOHandler;
import com.gmail.nuclearcat1337.snitch_master.util.ReturningAcceptor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * Created by Mr_Little_Kitty on 9/23/2016.
 */
public class EditStringGui// extends GuiScreen
{
//    private GuiScreen cancelToScreen;
//
//    private ReturningAcceptor<Boolean,String> callback;
//    private String editString;
//    private TextBox stringBox;
//
//    private final int maxStringLength;
//    private int boxWidth = 100;
//    private static final int BOX_HEIGHT = 18;
//    private static final int BUTTON_HEIGHT = 18;
//
//
//
//    public EditStringGui(GuiScreen cancelToScreen, String editString, ReturningAcceptor<Boolean,String> callback, int maxStringLength)
//    {
//        this.cancelToScreen = cancelToScreen;
//        this.editString = editString;
//        this.callback = callback;
//        this.maxStringLength = maxStringLength;
//    }
//
//    public void initGui()
//    {
//        boxWidth = mc.fontRendererObj.getStringWidth(SnitchList.MAX_NAME_CHARACTERS+"WWW");
//
//        int xPos = (this.width/2) - boxWidth;
//
//        stringBox = new TextBox(editString,fontRendererObj,xPos,yPos,boxWidth,BOX_HEIGHT,false,false);
//        stringBox.setMaxStringLength(maxStringLength);
//        stringBox.setFocused(true);
//
//        this.buttonList.clear();
//
//        xPos += (blueBox.width + separationDistance);
//
//        this.buttonList.add(new GuiButton(1,xPos,yPos,buttonWidth,buttonHeight,"Cancel"));
//
//        xPos += (buttonWidth + separationDistance);
//
//        this.buttonList.add(new GuiButton(2,xPos,yPos,buttonWidth,buttonHeight,"Create"));
//
//        super.initGui();
//    }
//
//    public void updateScreen()
//    {
//        nameBox.updateCursorCounter();
//        qualifierBox.updateCursorCounter();
//        redBox.updateCursorCounter();
//        greenBox.updateCursorCounter();
//        blueBox.updateCursorCounter();
//    }
//
//    public void keyTyped(char par1, int par2) throws IOException
//    {
//        if(nameBox.isFocused())
//        {
//            if(par2 == Keyboard.KEY_TAB)
//            {
//                qualifierBox.setFocused(true);
//                nameBox.setFocused(false);
//            }
//            nameBox.textboxKeyTyped(par1, par2);
//        }
//        else if(qualifierBox.isFocused())
//        {
//            if(par2 == Keyboard.KEY_TAB)
//            {
//                redBox.setFocused(true);
//                qualifierBox.setFocused(false);
//            }
//            qualifierBox.textboxKeyTyped(par1, par2);
//        }
//        else if(redBox.isFocused())
//        {
//            if(par2 == Keyboard.KEY_TAB)
//            {
//                greenBox.setFocused(true);
//                redBox.setFocused(false);
//            }
//            redBox.textboxKeyTyped(par1, par2);
//        }
//        else if(greenBox.isFocused())
//        {
//            if(par2 == Keyboard.KEY_TAB)
//            {
//                blueBox.setFocused(true);
//                greenBox.setFocused(false);
//            }
//            greenBox.textboxKeyTyped(par1, par2);
//        }
//        else if(blueBox.isFocused())
//        {
//            if(par2 == Keyboard.KEY_TAB)
//            {
//                nameBox.setFocused(true);
//                blueBox.setFocused(false);
//            }
//            blueBox.textboxKeyTyped(par1, par2);
//        }
//        super.keyTyped(par1,par2);
//    }
//
//    @Override
//    public void drawScreen(int mouseX, int mouseY, float partialTicks)
//    {
//        this.drawDefaultBackground();
//
//        int constYValue = (textBoxHeight/2) - mc.fontRendererObj.FONT_HEIGHT/2;
//        int constXValue = separationDistance + qualifierStringWidth;
//
//        mc.fontRendererObj.drawString("Blue",blueBox.xPosition-constXValue,blueBox.yPosition + constYValue,16777215);
//        mc.fontRendererObj.drawString("Green",greenBox.xPosition-constXValue,greenBox.yPosition + constYValue,16777215);
//        mc.fontRendererObj.drawString("Red",redBox.xPosition-constXValue,redBox.yPosition + constYValue,16777215);
//        mc.fontRendererObj.drawString("Qualifier",qualifierBox.xPosition-constXValue,qualifierBox.yPosition + constYValue,16777215);
//        mc.fontRendererObj.drawString("Name",nameBox.xPosition-constXValue,nameBox.yPosition + constYValue,16777215);
//
//        int yPos = nameBox.yPosition - separationDistance - mc.fontRendererObj.FONT_HEIGHT;
//        int xPos = nameBox.xPosition + (nameBoxWidth/2) - (createNewListStringWidth/2);
//
//        mc.fontRendererObj.drawString(CREATE_NEW_LIST_STRING,xPos,yPos,16777215);
//
//        this.nameBox.drawTextBox();
//        this.qualifierBox.drawTextBox();
//        this.redBox.drawTextBox();
//        this.greenBox.drawTextBox();
//        this.blueBox.drawTextBox();
//        super.drawScreen(mouseX, mouseY, partialTicks);
//    }
//
//    public void mouseClicked(int one, int two, int three) throws IOException
//    {
//        this.nameBox.mouseClicked(one,two,three);
//        this.qualifierBox.mouseClicked(one,two,three);
//        this.redBox.mouseClicked(one,two,three);
//        this.blueBox.mouseClicked(one,two,three);
//        this.greenBox.mouseClicked(one,two,three);
//        super.mouseClicked(one,two,three);
//    }
//
//    public void actionPerformed(GuiButton button)
//    {
//        switch (button.id)
//        {
//            case 1:
//                Minecraft.getMinecraft().displayGuiScreen(cancelToScreen);
//                break;
//            case 2:
//                String name = nameBox.getText();
//                if(name == null)
//                    break;
//                Integer red = redBox.clamp();
//                if(red == null)
//                    break;
//                Integer green = greenBox.clamp();
//                if(green == null)
//                    break;
//                Integer blue = blueBox.clamp();
//                if(blue == null)
//                    break;
//                String qualifier = qualifierBox.getText();
//                if(qualifier == null || !SnitchListQualifier.isSyntaxValid(qualifier))
//                    break;
//
//                SnitchLists lists = snitchMaster.getSnitchLists();
//                if(lists.doesListWithNameExist(name))
//                    break;
//
//                SnitchList list = new SnitchList(new SnitchListQualifier(qualifier),true,name,new Color(red,green,blue));
//                lists.addSnitchList(list);
//
//                IOHandler.asyncSaveSnitchLists(lists);
//
//                Minecraft.getMinecraft().displayGuiScreen(new EditSnitchListsGui(null,snitchMaster));
//                break;
//        }
//    }
//
//    @Override
//    public boolean doesGuiPauseGame()
//    {
//        return false;
//    }
}
