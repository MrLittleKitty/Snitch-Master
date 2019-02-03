package com.gmail.nuclearcat1337.snitch_master.handlers;

import com.gmail.nuclearcat1337.snitch_master.Settings;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.Location;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchManager;
import com.gmail.nuclearcat1337.snitch_master.util.Color;
import com.gmail.nuclearcat1337.snitch_master.util.GeneralUtils;
import com.gmail.nuclearcat1337.snitch_master.util.RenderUtils;
import com.gmail.nuclearcat1337.snitch_master.worldinfo.WorldProvider;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.apache.commons.lang3.text.WordUtils;

/**
 * Created by Mr_Little_Kitty on 6/26/2016.
 * Handles the rendering of Snitches in the Minecraft world that the player is currently in.
 */
public class SnitchRenderer {
    private static final int BLOCK_RENDER_DISTANCE = 60;
    private static final int BOX_RENDER_DISTANCE = 36;
    private static final int TEXT_RENDER_DISTANCE = (int) ((double) ((Snitch.SNITCH_RADIUS) + 1) * Math.sqrt(2)) + 2;

    private static final int BLOCK_RENDER_DISTANCE_SQUARED = BLOCK_RENDER_DISTANCE * BLOCK_RENDER_DISTANCE;
    private static final int BOX_RENDER_DISTANCE_SQUARED = BOX_RENDER_DISTANCE * BOX_RENDER_DISTANCE;
    private static final int TEXT_RENDER_DISTANCE_SQUARED = TEXT_RENDER_DISTANCE * TEXT_RENDER_DISTANCE;

    private static final float MIN_TEXT_RENDER_SCALE = 0.0075f;
    private static final float MAX_TEXT_RENDER_SCALE = 0.04f;

    private static final float SCALE_STEP = (MAX_TEXT_RENDER_SCALE - MIN_TEXT_RENDER_SCALE) / TEXT_RENDER_DISTANCE;

    private final SnitchManager manager;
    private final Settings settings;
    private final WorldProvider worldProvider;

    public SnitchRenderer(final SnitchManager manager, final Settings settings, final WorldProvider provider) {
        this.manager = manager;
        this.settings = settings;
        this.worldProvider = provider;

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void renderSnitches(RenderWorldLastEvent event) {
        if (!manager.getGlobalRender()) {
            return;
        }

        final boolean renderText = settings.getRenderText();
        for (final Snitch snitch : manager.getSnitches().getItemsForWorld(worldProvider.getCurrentWorld())) {
            final SnitchList renderList = manager.getRenderListForSnitch(snitch);
            if (renderList == null) {
                continue;
            }

            final Color renderColor = renderList.getColor();
            final Location location = snitch.getLocation();
            final double distanceToPlayerXZ = GeneralUtils.distanceSquaredToPlayer(location.getX(),
                    location.getZ());

            if (distanceToPlayerXZ > BLOCK_RENDER_DISTANCE_SQUARED) {
                continue;
            }

            //Render the outer box along the snitches capturing field
            if (distanceToPlayerXZ <= BOX_RENDER_DISTANCE_SQUARED) {
                RenderUtils.renderBox(location.getX(), location.getY(), location.getZ(),
                        Snitch.SNITCH_RADIUS, renderColor, 0.1D, 0.25D, event.getPartialTicks());
            }

            if (renderText && GeneralUtils.distanceSquaredToPlayer(location.getX(), location.getZ(), location.getY())
                    <= TEXT_RENDER_DISTANCE_SQUARED) {
                //Render the floating text over the snitch block that contains info about the snitch
                RenderUtils.renderFloatingText(getSnitchFloatingText(snitch), (float) location.getX() + 0.5f,
                        location.getY() + 1.01f, location.getZ() + 0.5f, 0xFFFFFF,
                        MIN_TEXT_RENDER_SCALE, SCALE_STEP, true, event.getPartialTicks());
            }


            GL11.glDisable(GL11.GL_DEPTH_TEST);

            //Render the small box around the actual snitch block
            RenderUtils.renderBox(location.getX(), location.getY(), location.getZ(), 0, renderColor,
                    0.25D, 0.25D, event.getPartialTicks());

            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }
    }

    private String[] getSnitchFloatingText(final Snitch snitch) {
        final Location location = snitch.getLocation();
        final String[] text = new String[3];
        text[0] = snitch.getName();
        text[1] = String.format("[%s] [%s]", snitch.getGroupName(), WordUtils.capitalize(snitch.getType()));
        text[2] = String.format("[%d %d %d]", location.getX(), location.getY(), location.getZ());
        return text;
    }
}
