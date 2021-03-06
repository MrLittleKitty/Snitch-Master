package com.gmail.nuclearcat1337.snitch_master.handlers;

import com.gmail.nuclearcat1337.snitch_master.Settings;
import com.gmail.nuclearcat1337.snitch_master.assistant.AssistantBlock;
import com.gmail.nuclearcat1337.snitch_master.assistant.AssistantManager;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.Location;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchManager;
import com.gmail.nuclearcat1337.snitch_master.util.GeneralUtils;
import com.gmail.nuclearcat1337.snitch_master.util.RenderUtils;
import com.gmail.nuclearcat1337.snitch_master.worldinfo.WorldProvider;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class AssistantRenderer {

    private static final int RENDER_DISTANCE = 36;
    private static final int RENDER_DISTANCE_SQUARED = RENDER_DISTANCE * RENDER_DISTANCE;

    private static final float MIN_TEXT_RENDER_SCALE = 0.0075f;
    private static final float MAX_TEXT_RENDER_SCALE = 0.07f;

    private static final float SCALE_STEP = (MAX_TEXT_RENDER_SCALE - MIN_TEXT_RENDER_SCALE) / RENDER_DISTANCE;

    private final AssistantManager manager;
    private final Settings settings; //TODO---Assistant render distance, global render control, etc.
    private final WorldProvider worldProvider;

    public AssistantRenderer(final AssistantManager manager, final Settings settings, final WorldProvider provider) {
        this.manager = manager;
        this.settings = settings;
        this.worldProvider = provider;

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void renderAssistant(RenderWorldLastEvent event) {
        if (!manager.shouldRender()) {
            return;
        }

        for (final AssistantBlock block : manager.getBlocksForWorld(worldProvider.getCurrentWorld())) {
            final Location location = block.getLocation();
            final double distanceXZ = GeneralUtils.distanceSquaredToPlayer(location.getX(), location.getZ());

            if (distanceXZ > RENDER_DISTANCE_SQUARED || !block.shouldRender(System.currentTimeMillis())) {
                continue;
            }

            final int boxRadius = block.getBoxRadius();
            if (boxRadius > 0) {
                RenderUtils.renderBox(location.getX(), location.getY(), location.getZ(),
                        Settings.SNITCH_RADIUS, block.getColor(), 0.29D, 0.25D, event.getPartialTicks());
            }

            RenderUtils.renderFloatingText(getFloatingText(block), (float) location.getX() + 0.5f,
                    location.getY() + 1.01f, location.getZ() + 0.5f, 0xFFFFFF,
                    MIN_TEXT_RENDER_SCALE, SCALE_STEP, true, event.getPartialTicks());

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            //Render the small box around the actual snitch block
            RenderUtils.renderBox(location.getX(), location.getY(), location.getZ(), 0, block.getColor(),
                    0.25D, 0.25D, event.getPartialTicks());
            GL11.glEnable(GL11.GL_DEPTH_TEST);

            block.wasRendered(System.currentTimeMillis());
        }
    }

    private String[] getFloatingText(final AssistantBlock block) {
        final Location location = block.getLocation();
        return new String[]{manager.getMode().getDisplayText() + " Mode",
                String.format("[%d %d %d]", location.getX(), location.getY(), location.getZ())};
    }
}
