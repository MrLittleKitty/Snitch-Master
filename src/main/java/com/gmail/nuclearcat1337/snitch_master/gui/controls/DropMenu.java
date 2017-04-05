package com.gmail.nuclearcat1337.snitch_master.gui.controls;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Mr_Little_Kitty on 1/20/2017.
 */
public class DropMenu extends GuiButton
{
    public static final ResourceLocation MENU_BUTTON = new ResourceLocation(SnitchMaster.MODID, "snitchMasterMain.png");

    public DropMenu(int buttonId, int x, int y)
    {
        super(buttonId, x, y, 16, 16, "");
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        if (super.mousePressed(mc, mouseX, mouseY))
        {

            return true;
        }
        return false;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            //GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int i = this.getHoverState(this.hovered);

            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);

            //            if(i == 2) //They are hovering over the button
            //            {
            //                float f3 = (float)(16777120 >> 24 & 255) / 255.0F;
            //                float f = (float)(16777120 >> 16 & 255) / 255.0F;
            //                float f1 = (float)(16777120 >> 8 & 255) / 255.0F;
            //                float f2 = (float)(16777120 & 255) / 255.0F;
            //                GlStateManager.color(f, f1, f2, f3);
            //            }

            mc.getTextureManager().bindTexture(MENU_BUTTON);
            this.drawTexturedModalRect(this.xPosition + 4, this.yPosition + 4, 0, 0, 8, 8);

            this.mouseDragged(mc, mouseX, mouseY);
        }
    }
}
