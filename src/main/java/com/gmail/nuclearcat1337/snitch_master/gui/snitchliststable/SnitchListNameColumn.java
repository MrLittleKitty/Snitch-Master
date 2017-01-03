package com.gmail.nuclearcat1337.snitch_master.gui.snitchliststable;

import com.gmail.nuclearcat1337.snitch_master.gui.EditStringGui;
import com.gmail.nuclearcat1337.snitch_master.gui.tables.TableColumn;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchLists;
import com.gmail.nuclearcat1337.snitch_master.util.Acceptor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

/**
 * Created by Mr_Little_Kitty on 1/2/2017.
 */
public class SnitchListNameColumn implements TableColumn<SnitchList>
{
    private static Minecraft mc;

    private final SnitchLists lists;

    public SnitchListNameColumn(SnitchLists lists)
    {
        mc = Minecraft.getMinecraft();
        this.lists = lists;
    }

    @Override
    public GuiButton[] prepareEntry(SnitchList item)
    {
        return null;
    }

    @Override
    public String getColumnName()
    {
        return "Name";
    }

    @Override
    public boolean doBoundsCheck()
    {
        return true;
    }

    @Override
    public void clicked(SnitchList list, boolean leftClick, int xPos, int yPos, GuiButton[] buttons, GuiScreen parentScreen,int slotIndex)
    {
        //Only if its a right click
        if(!leftClick)
            mc.displayGuiScreen(new EditStringGui(parentScreen,list.getListName(),"Edit List Name",new EditNameAcceptor(list),20));
    }

    @Override
    public void released(SnitchList list, int xPos, int yPos, GuiButton[] buttons, GuiScreen parentScreen,int slotIndex)
    {

    }

    @Override
    public void draw(SnitchList list, int xPos, int yPos, int columnWidth, int slotHeight, GuiButton[] buttons,int slotIndex, int mouseX, int mouseY)
    {
        int stringYPosition = yPos + ((slotHeight - mc.fontRendererObj.FONT_HEIGHT) / 2);
        String text = list.getListName();
        int stringWidth = mc.fontRendererObj.getStringWidth(text);
        int namePos = xPos + (columnWidth /2) - (stringWidth/2);
        mc.fontRendererObj.drawString(text, namePos , stringYPosition,16777215);
    }

    @Override
    public int getDrawWidth(SnitchList list)
    {
        return mc.fontRendererObj.getStringWidth(list.getListName());
    }

    @Override
    public List<String> hover(SnitchList item, int xPos, int yPos)
    {
        return null;
    }

    @Override
    public int compare(SnitchList o1, SnitchList o2)
    {
        return 0;
    }

    private class EditNameAcceptor implements Acceptor<String>
    {
        private final SnitchList list;

        private EditNameAcceptor(SnitchList list)
        {
            this.list = list;
        }

        @Override
        public boolean accept(String item)
        {
            boolean valid = !lists.doesListWithNameExist(item);
            if(valid)
            {
                list.setListName(item);
                lists.snitchListChanged();
                return true;
            }
            return false;
        }
    }
}
