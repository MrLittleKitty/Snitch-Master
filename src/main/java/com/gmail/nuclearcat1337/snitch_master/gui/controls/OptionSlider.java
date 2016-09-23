package com.gmail.nuclearcat1337.snitch_master.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

/**
 * Created by Mr_Little_Kitty on 9/15/2016.
 */
public class OptionSlider extends GuiButton
{
    private boolean sliderClicked;

    private float currentValue;


    private final float lowerBound;
    private final float upperBound;

    public OptionSlider(int id, int xPos, int yPos, int width, int height, float lowerBound, float upperBound)
    {
        super(id, xPos, yPos, width,height, "");
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.currentValue = 1.0f;
    }

//    public boolean mousePressed(Minecraft p_146116_1_, int p_146116_2_, int p_146116_3_)
//    {
//        if (super.mousePressed(p_146116_1_, p_146116_2_, p_146116_3_))
//        {
//            this.currentValue = (float)(p_146116_2_ - (this.xPosition + 4)) / (float)(this.width - 8);
//
//            if (this.currentValue < 0.0F)
//            {
//                this.currentValue = 0.0F;
//            }
//
//            if (this.currentValue > 1.0F)
//            {
//                this.currentValue = 1.0F;
//            }
//
//            SV.settings.setOptionFloatValue(this.option, this.option.denormalizeValue(this.field_146134_p));
//            this.displayString = SV.settings.getKeyBinding(this.option);
//            this.field_146135_o = true;
//            return true;
//        }
//        else
//        {
//            return false;
//        }
//    }
//
//    /**
//     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
//     */
//    public void mouseReleased(int p_146118_1_, int p_146118_2_)
//    {
//        this.field_146135_o = false;
//    }
}
