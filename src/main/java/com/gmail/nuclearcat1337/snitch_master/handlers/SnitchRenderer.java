package com.gmail.nuclearcat1337.snitch_master.handlers;

import com.gmail.nuclearcat1337.snitch_master.Settings;
import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.ILocation;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchManager;
import com.gmail.nuclearcat1337.snitch_master.util.Color;
import com.gmail.nuclearcat1337.snitch_master.util.GeneralUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.apache.commons.lang3.text.WordUtils;

public class SnitchRenderer {
	private static final int BLOCK_RENDER_DISTANCE = 60;
	private static final int BOX_RENDER_DISTANCE = 36;
	private static final int TEXT_RENDER_DISTANCE = (int) ((double) ((Snitch.SNITCH_RADIUS) + 1) * Math.sqrt(2)) + 2;

	private static final Minecraft mc = Minecraft.getMinecraft();
	private final SnitchMaster snitchMaster;
	private final SnitchManager manager;

	public SnitchRenderer(SnitchMaster snitchMaster) {
		this.snitchMaster = snitchMaster;
		manager = snitchMaster.getManager();

		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void renderSnitches(RenderWorldLastEvent event) {
		if (manager.getGlobalRender()) {
			boolean renderText = (Boolean) (snitchMaster.getSettings().getValue(Settings.RENDER_TEXT_KEY));
			for (Snitch snitch : manager.getSnitches().getItemsForWorld(snitchMaster.getCurrentWorld())) {
				SnitchList renderList = manager.getRenderListForSnitch(snitch);
				if (renderList != null) {
					Color renderColor = renderList.getListColor();
					ILocation location = snitch.getLocation();
					double distanceSquared = GeneralUtils.DistanceSquared(location.getX(), location.getZ(), (int) mc.player.posX, (int) mc.player.posZ);

					if (distanceSquared <= BLOCK_RENDER_DISTANCE * BLOCK_RENDER_DISTANCE) {
						if (distanceSquared <= BOX_RENDER_DISTANCE * BOX_RENDER_DISTANCE) {
							renderBox(location.getX(), location.getY(), location.getZ(), Snitch.SNITCH_RADIUS, renderColor, 0.1D, 0.25D, event.getPartialTicks());
						}

						if (renderText) {
							if (GeneralUtils.DistanceSquared(location.getX(), location.getZ(), location.getY(), (int) mc.player.posX, (int) mc.player.posZ, (int) mc.player.posY) <= TEXT_RENDER_DISTANCE * TEXT_RENDER_DISTANCE) {
								String[] text = new String[3];
								text[0] = snitch.getName();
								text[1] = String.format("[%s] %s", snitch.getGroupName(), WordUtils.capitalize(snitch.getType()));
								text[2] = String.format("[%d %d %d]", location.getX(), location.getY(), location.getZ());

								RenderFloatingText(text, (float) location.getX() + 0.5f, location.getY() + 1.01f, location.getZ() + 0.5f, 0xFFFFFF, true, event.getPartialTicks());
							}
						}

						GL11.glDisable(GL11.GL_DEPTH_TEST);

						renderBox(location.getX(), location.getY(), location.getZ(), 0, renderColor, 0.25D, 0.25D, event.getPartialTicks());

						GL11.glEnable(GL11.GL_DEPTH_TEST);
					}
				}
			}
		}
	}

	private static final float MIN_TEXT_RENDER_SCALE = 0.0075f;
	private static final float MAX_TEXT_RENDER_SCALE = 0.04f;

	private static final float SCALE_STEP = (MAX_TEXT_RENDER_SCALE - MIN_TEXT_RENDER_SCALE) / TEXT_RENDER_DISTANCE;

	private static void RenderFloatingText(String[] text, float x, float y, float z, int color, boolean renderBlackBackground, float partialTickTime) {
		//Thanks to Electric-Expansion mod for the majority of this code
		//https://github.com/Alex-hawks/Electric-Expansion/blob/master/src/electricexpansion/client/render/RenderFloatingText.java

		RenderManager renderManager = mc.getRenderManager();

		float playerX = (float) (mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * partialTickTime);
		float playerY = (float) (mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * partialTickTime);
		float playerZ = (float) (mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * partialTickTime);

		float dx = x - playerX;
		float dy = y - playerY;
		float dz = z - playerZ;
		float distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
		float scale = MIN_TEXT_RENDER_SCALE + (distance * SCALE_STEP);//.01f; //Min font scale for max text render distance

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
		for (String thisMessage : text) {
			int thisMessageWidth = mc.fontRenderer.getStringWidth(thisMessage);

			if (thisMessageWidth > textWidth)
				textWidth = thisMessageWidth;
		}

		int lineHeight = 10;
		int initialValue = lineHeight * text.length;

		if (renderBlackBackground) {
			int stringMiddle = textWidth / 2;

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder vertexBuffer = tessellator.getBuffer();

			GlStateManager.disableTexture2D();

			//This code taken from 1.8.8 net.minecraft.client.renderer.entity.Render.renderLivingLabel()
			vertexBuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
			vertexBuffer.pos((double) (-stringMiddle - 1), (double) (-1) - initialValue, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
			vertexBuffer.pos((double) (-stringMiddle - 1), (double) (8 + lineHeight * (text.length - 1)) - initialValue, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
			vertexBuffer.pos((double) (stringMiddle + 1), (double) (8 + lineHeight * (text.length - 1)) - initialValue, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
			vertexBuffer.pos((double) (stringMiddle + 1), (double) (-1) - initialValue, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();

			tessellator.draw();

			GlStateManager.enableTexture2D();
		}

		int i = 0;
		for (String message : text) {
			int messageWidth = mc.fontRenderer.getStringWidth(message);
			mc.fontRenderer.drawString(message, 0 - (messageWidth / 2), (i * lineHeight) - initialValue, color);
			i++;
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPopMatrix();
	}

	private void renderBox(int x, int y, int z, int radius, Color color, double alpha, double outlineAlpha, float partialTicks) {
		double renderPosX = (float) (mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * partialTicks);
		double renderPosY = (float) (mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * partialTicks);
		double renderPosZ = (float) (mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * partialTicks);

		GL11.glPushMatrix();

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glLineWidth(5.0F);

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

		if (radius == 0) {
			min = 0.01D;
			max = 1.01;
		}

		AxisAlignedBB bb = new AxisAlignedBB(px - min, py - min, pz - min, px + max, py + max, pz + max);

		GL11.glColor4d(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha);

		drawCrossedOutlinedBoundingBox(bb);

		GL11.glColor4d(color.getRed(), color.getGreen(), color.getBlue(), alpha);

		drawBoundingBoxQuads(bb);

		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL13.GL_MULTISAMPLE);

		GL11.glPopMatrix();

		GL11.glColor4d(1.0f, 1.0f, 1.0f, 1.0f);
	}

	private static final VertexFormat format = DefaultVertexFormats.POSITION;

	private void drawBoundingBoxQuads(AxisAlignedBB bb) {
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buffer = tess.getBuffer();

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

	private static void drawCrossedOutlinedBoundingBox(AxisAlignedBB bb) {
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buffer = tess.getBuffer();

		buffer.begin(GL11.GL_LINE_STRIP, format);
		buffer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
		buffer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
		buffer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
		buffer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
		buffer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
		tess.draw();

		buffer.begin(GL11.GL_LINE_STRIP, format);
		buffer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
		buffer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
		buffer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
		buffer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
		buffer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
		tess.draw();

		buffer.begin(GL11.GL_LINE_STRIP, format);
		buffer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
		buffer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
		tess.draw();

		buffer.begin(GL11.GL_LINE_STRIP, format);
		buffer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
		buffer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
		tess.draw();

		buffer.begin(GL11.GL_LINE_STRIP, format);
		buffer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
		buffer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
		tess.draw();

		buffer.begin(GL11.GL_LINE_STRIP, format);
		buffer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
		buffer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
		tess.draw();
	}
}
