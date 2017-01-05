package com.gmail.nuclearcat1337.snitch_master.handlers;

import com.gmail.nuclearcat1337.snitch_master.Settings;
import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.gui.SettingsGui;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.ILocation;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.IReadOnlyLocatableObjectList;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import com.gmail.nuclearcat1337.snitch_master.util.Color;
import com.gmail.nuclearcat1337.snitch_master.util.GeneralUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.SortedSet;

/**
 * Created by Mr_Little_Kitty on 6/26/2016.
 * Handles the rendering of Snitches in the Minecraft world that the player is currently in.
 */
public class SnitchRenderer
{
    private static final int BLOCK_RENDER_DISTANCE = 60;
    private static final int BOX_RENDER_DISTANCE = 36;
    private static final int TEXT_RENDER_DISTANCE = (int)((double)((Snitch.SNITCH_RADIUS)+1)*Math.sqrt(2))+2;

    private static final Minecraft mc = Minecraft.getMinecraft();
    private final SnitchMaster snitchMaster;

    public SnitchRenderer(SnitchMaster snitchMaster)
    {
        this.snitchMaster = snitchMaster;
    }

    /**
     * Renders all the Snitches in that current world that are in a certain distance of the player
     */
    @SubscribeEvent
    public void renderSnitches(RenderWorldLastEvent event)
    {
        boolean renderText = (Boolean)(snitchMaster.getSettings().getValue(Settings.RENDER_TEXT_KEY));
        for (Snitch snitch : snitchMaster.getSnitches().getItemsForWorld(snitchMaster.getCurrentWorld()))
        {
            SnitchList renderList = null;
            for (SnitchList list : snitch.getAttachedSnitchLists())
            {
                if (!list.shouldRenderSnitches())
                    continue;

                renderList= list;
                break;
            }

            if (renderList != null)
            {
                Color renderColor = renderList.getListColor();
                ILocation location = snitch.getLocation();
                double distanceSquared = GeneralUtils.DistanceSquared(location.getX(), location.getZ(), (int) mc.thePlayer.posX, (int) mc.thePlayer.posZ);

                if (distanceSquared <= BLOCK_RENDER_DISTANCE * BLOCK_RENDER_DISTANCE)
                {
                    if (distanceSquared <= BOX_RENDER_DISTANCE * BOX_RENDER_DISTANCE)
                    {
                        renderBox(location.getX(), location.getY(), location.getZ(), Snitch.SNITCH_RADIUS, renderColor, 0.1D, 0.25D, event.getPartialTicks());
                    }

                    if(renderText)
                    {
                        if (GeneralUtils.DistanceSquared(location.getX(), location.getZ(), location.getY(), (int) mc.thePlayer.posX, (int) mc.thePlayer.posZ, (int) mc.thePlayer.posY) <= TEXT_RENDER_DISTANCE * TEXT_RENDER_DISTANCE)
                        {
                            String[] text = new String[3];
                            text[0] = "Name: " + snitch.getSnitchName();
                            text[1] = "Group: " + snitch.getGroupName();
                            text[2] = "Location: " + location.toString();

                            RenderFloatingText(text, (float) location.getX() + 0.5f, location.getY() + 2f, location.getZ() + 0.5f, 0xFFFFFF, true, event.getPartialTicks());
                        }
                    }

                    GL11.glDisable(GL11.GL_DEPTH_TEST);

                    renderBox(location.getX(), location.getY(), location.getZ(), 0, renderColor, 0.25D, 0.25D, event.getPartialTicks());

                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                }
            }
        }
    }

    private static void RenderFloatingText(String[] text, float x, float y, float z, int color, boolean renderBlackBackground, float partialTickTime)
    {
        //Thanks to Electric-Expansion mod for the majority of this code
        //https://github.com/Alex-hawks/Electric-Expansion/blob/master/src/electricexpansion/client/render/RenderFloatingText.java

        RenderManager renderManager = mc.getRenderManager();

        float playerX = (float) (mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTickTime);
        float playerY = (float) (mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTickTime);
        float playerZ = (float) (mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTickTime);

        float dx = x-playerX;
        float dy = y-playerY;
        float dz = z-playerZ;
        //float distance = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
        float scale = 0.03f;

        GL11.glColor4f(1f, 1f, 1f, 0.5f);
        GL11.glPushMatrix();
        GL11.glTranslatef(dx, dy, dz);
        GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-scale, -scale, scale);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int textWidth = 0;
        for (String thisMessage : text)
        {
            int thisMessageWidth = mc.fontRendererObj.getStringWidth(thisMessage);

            if (thisMessageWidth > textWidth)
                textWidth = thisMessageWidth;
        }

        int lineHeight = 10;

        if(renderBlackBackground)
        {
            int stringMiddle = textWidth / 2;

            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer worldrenderer = tessellator.getBuffer();

            //GL11.glDisable(GL11.GL_TEXTURE_2D);
            GlStateManager.disableTexture2D();

            /* OLD 1.8 rendering code
            //worldrenderer.startDrawingQuads();
            worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);	//field_181707_g maybe?

            GlStateManager.color(0.0F, 0.0F, 0.0F, 0.5F);
            worldrenderer.putPosition(-stringMiddle - 1, -1 + 0, 0.0D);
            worldrenderer.putPosition(-stringMiddle - 1, 8 + lineHeight*text.length-lineHeight, 0.0D);
            worldrenderer.putPosition(stringMiddle + 1, 8 + lineHeight*text.length-lineHeight, 0.0D);
            worldrenderer.putPosition(stringMiddle + 1, -1 + 0, 0.0D);
            */

            //This code taken from 1.8.8 net.minecraft.client.renderer.entity.Render.renderLivingLabel()
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos((double) (-stringMiddle - 1), (double) (-1), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos((double) (-stringMiddle - 1), (double) (8 + lineHeight*(text.length-1)), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos((double) (stringMiddle + 1), (double) (8 + lineHeight*(text.length-1)), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos((double) (stringMiddle + 1), (double) (-1), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();

            tessellator.draw();
            //GL11.glEnable(GL11.GL_TEXTURE_2D);
            GlStateManager.enableTexture2D();
        }

        int i = 0;
        for(String message : text)
        {
            int messageWidth = mc.fontRendererObj.getStringWidth(message);
            mc.fontRendererObj.drawString(message,  0-(messageWidth / 2), i*lineHeight, color);
            i++;
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    private void renderBox(int x, int y, int z, int radius, Color color, double alpha, double outlineAlpha, float partialTicks)
    {
        double renderPosX = (float) (mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX)
                * partialTicks);
        double renderPosY = (float) (mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY)
                * partialTicks);
        double renderPosZ = (float) (mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ)
                * partialTicks);

        GL11.glPushMatrix();

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(5.0F);
        //GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL13.GL_MULTISAMPLE);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.09F);
        GL11.glDepthMask(false);

        double px = -(renderPosX - x);
        double py = -(renderPosY - y);
        double pz = -(renderPosZ - z);

        double max = radius + 1.01D;   // +.99D; //.99
        double min = radius + .01D;    // - .01D;//-.01

        if(radius == 0)
        {
            min = 0.01D;
            max = 1.01;
        }

        AxisAlignedBB bb = new AxisAlignedBB(px - min, py - min, pz - min, px + max, py + max, pz + max);

        GL11.glColor4d(color.getRed(),color.getGreen(),color.getBlue(),outlineAlpha);

        drawCrossedOutlinedBoundingBox(bb);

        GL11.glColor4d(color.getRed(),color.getGreen(),color.getBlue(),alpha);

        drawBoundingBoxQuads(bb);

        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL13.GL_MULTISAMPLE);
        //GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glPopMatrix();

        GL11.glColor4d(1.0f,1.0f,1.0f,1.0f);
    }

    private static final VertexFormat format = DefaultVertexFormats.POSITION;

    private void drawBoundingBoxQuads(AxisAlignedBB bb)
    {
        Tessellator tess = Tessellator.getInstance();
        VertexBuffer buffer = tess.getBuffer();

        buffer.begin(GL11.GL_QUADS, format);
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        tess.draw();

        buffer.begin(GL11.GL_QUADS, format);
        buffer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        tess.draw();

        buffer.begin(GL11.GL_QUADS, format);
        buffer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        tess.draw();

        buffer.begin(GL11.GL_QUADS, format);
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        tess.draw();

        buffer.begin(GL11.GL_QUADS, format);
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        tess.draw();

        buffer.begin(GL11.GL_QUADS, format);
        buffer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        tess.draw();
    }

    private static void drawCrossedOutlinedBoundingBox(AxisAlignedBB bb)
    {
        Tessellator tess = Tessellator.getInstance();
        VertexBuffer buffer = tess.getBuffer();

        buffer.begin(GL11.GL_LINE_STRIP,format);
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        tess.draw();

        buffer.begin(GL11.GL_LINE_STRIP,format);
        buffer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        tess.draw();

        buffer.begin(GL11.GL_LINE_STRIP,format);
        buffer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        tess.draw();

        buffer.begin(GL11.GL_LINE_STRIP,format);
        buffer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        tess.draw();

        buffer.begin(GL11.GL_LINE_STRIP,format);
        buffer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        tess.draw();

        buffer.begin(GL11.GL_LINE_STRIP,format);
        buffer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        tess.draw();
    }
}
