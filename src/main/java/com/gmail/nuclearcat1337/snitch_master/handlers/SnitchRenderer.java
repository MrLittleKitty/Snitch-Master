package com.gmail.nuclearcat1337.snitch_master.handlers;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.ILocation;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.IReadOnlyLocatableObjectList;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import com.gmail.nuclearcat1337.snitch_master.util.Color;
import com.gmail.nuclearcat1337.snitch_master.util.GeneralUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
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
        for(Snitch snitch : snitchMaster.getSnitches().getItemsForWorld(snitchMaster.getCurrentWorld()))
        {
            Color renderColor = null;
            for(SnitchList list : snitch.getAttachedSnitchLists())
            {
                if(!list.shouldRenderSnitches())
                    continue;

                renderColor = list.getListColor();
                break;
            }

            if(renderColor != null)
            {
                ILocation location = snitch.getLocation();
                double distanceSquared = GeneralUtils.DistanceSquared(location.getX(), location.getZ(), (int)mc.thePlayer.posX, (int)mc.thePlayer.posZ);

                if (distanceSquared < BLOCK_RENDER_DISTANCE * BLOCK_RENDER_DISTANCE)
                {
                    if (distanceSquared < BOX_RENDER_DISTANCE * BOX_RENDER_DISTANCE)
                    {
                        renderBox(location.getX(), location.getY(), location.getZ(), Snitch.SNITCH_RADIUS, renderColor, 0.1D, 0.25D, event.getPartialTicks());
                    }

                    GL11.glDisable(GL11.GL_DEPTH_TEST);

                    renderBox(location.getX(), location.getY(), location.getZ(), 0, renderColor, 0.25D, 0.25D, event.getPartialTicks());

                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                }
            }
        }
    }

    private void renderBox(int x, int y, int z, int radius, Color color, double alpha, double outlineAlpha, float partialTicks)
    {
        double renderPosX = (float) (mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX)
                * partialTicks);
        double renderPosY = (float) (mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY)
                * partialTicks);
        double renderPosZ = (float) (mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ)
                * partialTicks);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(5.0F);
        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL13.GL_MULTISAMPLE);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.09F);
        GL11.glDepthMask(false);

        GL11.glPushMatrix();
        double px = -(renderPosX - x);
        double py = -(renderPosY - y);
        double pz = -(renderPosZ - z);

        double max = radius +.99D; //.99
        double min = radius - .01D;//-.01

        if(radius == 0)
        {
            min = 0.01D;
            max = 1.01;
        }

        AxisAlignedBB bb = new AxisAlignedBB(px - min, py - min, pz - min, px + max, py + max, pz + max);
        //AxisAlignedBB bb = new AxisAlignedBB(px - min, py - max, pz - min, px + max, py + min, pz + max);

        GL11.glColor4d(color.getRed(),color.getGreen(),color.getBlue(),outlineAlpha);

        drawCrossedOutlinedBoundingBox(bb);

        GL11.glColor4d(color.getRed(),color.getGreen(),color.getBlue(),alpha);

        drawBoundingBoxQuads(bb);
        GL11.glPopMatrix();

        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL13.GL_MULTISAMPLE);
        GL11.glEnable(GL11.GL_LIGHTING);
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
