package com.gmail.nuclearcat1337.snitch_master.journeymap;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.gui.MainGUI;
import com.gmail.nuclearcat1337.snitch_master.handlers.KeyHandler;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.ILocation;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.IReadOnlyLocatableObjectList;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.Context;
import journeymap.client.api.display.DisplayType;
import journeymap.client.api.display.ImageOverlay;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.util.UIState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import javax.annotation.ParametersAreNonnullByDefault;

import java.util.ArrayList;
import java.util.EnumSet;

import static journeymap.client.api.event.ClientEvent.Type.*;

/**
 * Created by Mr_Little_Kitty on 9/20/2016.
 */
@ParametersAreNonnullByDefault
@journeymap.client.api.ClientPlugin
public class JourneyMapOverlayPlugin implements IClientPlugin, JourneyMapInterface
{
    private final KeyBinding renderJourneyMapOverlay = new KeyBinding("Journey Map Overlay", Keyboard.KEY_Y,"Snitch Master");
    private boolean renderOverlay;

    private IClientAPI api = null;

    public JourneyMapOverlayPlugin()
    {
        renderOverlay = false;
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event)
    {
        SnitchMaster.instance.jmInterface = this;

        if(renderJourneyMapOverlay.isPressed())
        {
            toggleRender();
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new TextComponentString("[SnitchMaster] JourneyMap Overlay: "+(renderOverlay ? "On" : "Off")));
        }
    }

    private void toggleRender()
    {
        renderOverlay = !renderOverlay;
        if(!renderOverlay)
            clearDisplayed();
        else
        {
            IReadOnlyLocatableObjectList<Snitch> snitches = SnitchMaster.instance.getSnitches();
            if(snitches != null)
            {
                String currentWorld = SnitchMaster.instance.getCurrentWorld();
                Iterable<Snitch> worldSnitches = snitches.getItemsForWorld(currentWorld);
                if(worldSnitches != null)
                {
                    for (Snitch snitch : worldSnitches)
                    {
                       renderedSnitches.add(snitch);
                        sendImage(snitch);
                    }
                }
            }
        }
    }

    @Override
    public void initialize(final IClientAPI jmAPI)
    {
        // Set ClientProxy.SampleModWaypointFactory with an implementation that uses the JourneyMap IClientAPI under the covers.
        this.api = jmAPI;
        SnitchMaster.instance.jmInterface = this;

        FMLCommonHandler.instance().bus().register(this);
        ClientRegistry.registerKeyBinding(renderJourneyMapOverlay);

        // Subscribe to desired ClientEvent types from JourneyMap
        this.api.subscribe(getModId(), EnumSet.of(MAPPING_STOPPED));

        SnitchMaster.logger.info("[SnitchMaster] JourneyMap overlay initialized");
    }

    /**
     * Used by JourneyMap to associate a modId with this plugin.
     */
    @Override
    public String getModId()
    {
        return SnitchMaster.MODID;
    }

    private ArrayList<Snitch> renderedSnitches = new ArrayList<>();

    private void sendImage(Snitch snitch)
    {
        if(api.playerAccepts(getModId(), DisplayType.Image))
        {
            //renderedSnitches.add(snitch.getLocation());
            ImageOverlay overlay = SnitchImageFactory.createSnitchOverlay(snitch);
            try
            {
                if(overlay != null)
                    api.show(overlay);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void displaySnitch(Snitch snitch)
    {
        if(renderOverlay)
        {
            if (!renderedSnitches.contains(snitch))
            {
                renderedSnitches.add(snitch);
                sendImage(snitch);
            }
        }
    }

    private void clearDisplayed()
    {
        api.removeAll(this.getModId());
        renderedSnitches.clear();
    }

    @Override
    public void onEvent(ClientEvent clientEvent)
    {
        switch (clientEvent.type)
        {
            case MAPPING_STOPPED:
                clearDisplayed();
                break;
        }
    }
}
