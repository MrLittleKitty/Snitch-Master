package com.gmail.nuclearcat1337.snitch_master.gui.snitchalerts;

import com.gmail.nuclearcat1337.snitch_master.util.Acceptor;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;

/**
 * Created by Mr_Little_Kitty on 6/11/2017.
 */
abstract class Subsection implements FormatBuilder
{
    private final FormatBuilder previousBuilder;
    private final Acceptor<Subsection> changeSubsection;

    Subsection(FormatBuilder previousBuilder, Acceptor<Subsection> changeSubsection)
    {
        this.previousBuilder = previousBuilder;
        this.changeSubsection = changeSubsection;
    }

    public abstract void instructionButtonPressed(byte instruction);

    //If "true" then this is called to enable
    //If "false" then this is called to disable
    public abstract void setButtonState(GuiButton button, boolean enabled);

    public abstract void drawScreen(int xLeft, int yUp, int width, int height, int mouseX, int mouseY, float partialTicks);

    public abstract void mouseClicked(int one, int two, int three) throws IOException;

    public abstract void keyTyped(char par1, int par2) throws IOException;

    public abstract void updateScreen();
}
