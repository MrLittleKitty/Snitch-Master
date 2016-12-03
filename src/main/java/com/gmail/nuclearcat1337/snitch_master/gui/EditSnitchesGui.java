package com.gmail.nuclearcat1337.snitch_master.gui;

import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Mr_Little_Kitty on 11/29/2016.
 */
public class EditSnitchesGui extends GuiScreen
{
    private GuiScreen parentScreen;
    private Collection<Snitch> snitches;

    private SnitchGui snitchesGui;

    private static final int DONE_BUTTON_WIDTH = GuiConstants.SMALL_BUTTON_WIDTH*3;
    //private static final int NEW_BUTTON_WIDTH = GuiConstants.SMALL_BUTTON_WIDTH;

    public EditSnitchesGui(GuiScreen guiscreen, Collection<Snitch> snitches)
    {
        this.parentScreen = guiscreen;
        this.snitches = snitches;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.snitchesGui.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);

        int index = snitchesGui.getSlotIndexFromScreenCoords(mouseX,mouseY);
        if(index > 0)
        {
            Snitch snitch = snitchesGui.getSnitchForIndex(index);
            if(mouseX >= snitchesGui.getCoordsLeftBound() && mouseX <= snitchesGui.getCoordsRightBound())
            {
                List<String> temp = new ArrayList<>(1);
                temp.add(snitch.getWorld());
                drawHoveringText(temp,mouseX,mouseY);
            }
            else if(mouseX >= snitchesGui.getNameLeftBound() && mouseX <= snitchesGui.getNameRightBound())
            {
                List<String> temp = new ArrayList<>(snitch.getAttachedSnitchLists().size()+1);
                temp.add("Snitch Lists:");
                for(SnitchList list : snitch.getAttachedSnitchLists())
                    temp.add(list.getListName());
                drawHoveringText(temp,mouseX,mouseY);
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void initGui()
    {
        snitchesGui = new SnitchGui(this,snitches);

        this.buttonList.clear();

        int xPos = (this.width/2)- DONE_BUTTON_WIDTH - (GuiConstants.STANDARD_SEPARATION_DISTANCE/2);
        int yPos = this.height - GuiConstants.STANDARD_BUTTON_HEIGHT - GuiConstants.STANDARD_SEPARATION_DISTANCE;

        this.buttonList.add(new GuiButton(1, xPos , yPos, DONE_BUTTON_WIDTH, 18, "Back"));

//        xPos = (this.width/2)+(GuiConstants.STANDARD_SEPARATION_DISTANCE/2);
//        this.buttonList.add(new GuiButton(5, xPos, yPos, NEW_BUTTON_WIDTH, 18, "New"));
//
//        xPos += GuiConstants.STANDARD_SEPARATION_DISTANCE+ NEW_BUTTON_WIDTH;
//        this.buttonList.add(new GuiButton(6, xPos, yPos, RENDER_ON_BUTTON_WIDTH, 18, "All On"));
//
//        xPos += GuiConstants.STANDARD_SEPARATION_DISTANCE+ RENDER_ON_BUTTON_WIDTH;
//        this.buttonList.add(new GuiButton(7, xPos, yPos, RENDER_OFF_BUTTON_WIDTH, 18, "All Off"));

        this.snitchesGui.registerScrollButtons(8, 9);
        super.initGui();
    }

    public void actionPerformed(GuiButton button)
    {
        if (!button.enabled) return;
        switch (button.id)
        {
            case 1: //Done
                this.mc.displayGuiScreen(parentScreen);
                break;
//            case 5: //New Snitch List
//                Minecraft.getMinecraft().displayGuiScreen(new NewSnitchListGui(this,snitchMaster));
//                break;
//            case 6: //Render all on
//                snitchListGUI.setAllRender(true);
//                break;
//            case 7: //Render all off
//                snitchListGUI.setAllRender(false);
//                break;
        }
    }

//    public void drawHoverText(List<String> text, int x, int y)
//    {
//        drawHoveringText(text,x,y);
//    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseEvent)
    {
        this.snitchesGui.mouseClicked(mouseX, mouseY, mouseEvent);
        try
        {
            super.mouseClicked(mouseX, mouseY, mouseEvent);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseReleased(int arg1, int arg2, int arg3)
    {
        //This method is ESSENTIAL to the functioning of the scroll bar
        this.snitchesGui.mouseReleased(arg1,arg2,arg3);
        super.mouseReleased(arg1,arg2,arg3);
    }

    @Override
    public void handleMouseInput()
    {
        //This method is ESSENTIAL to the functioning of the scroll bar
        this.snitchesGui.handleMouseInput();
        try
        {
            super.handleMouseInput();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
