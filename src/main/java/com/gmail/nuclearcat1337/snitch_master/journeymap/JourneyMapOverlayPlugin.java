package com.gmail.nuclearcat1337.snitch_master.journeymap;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.IReadOnlyLocatableObjectList;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.DisplayType;
import journeymap.client.api.display.ImageOverlay;
import journeymap.client.api.event.ClientEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * The class that handles the displaying of Snitches on JourneyMap.
 */
@ParametersAreNonnullByDefault
@journeymap.client.api.ClientPlugin
public class JourneyMapOverlayPlugin implements IClientPlugin, JourneyMapInterface {
	private final KeyBinding renderJourneyMapOverlay = new KeyBinding("Journey Map Overlay", Keyboard.KEY_Y, "Snitch Master");
	private boolean renderOverlay;

	private IClientAPI api = null;

	public JourneyMapOverlayPlugin() {
		renderOverlay = false;
	}

	@SubscribeEvent
	public void onKeyPress(InputEvent.KeyInputEvent event) {
		if (renderJourneyMapOverlay.isPressed()) {
			toggleRender();
			SnitchMaster.SendMessageToPlayer("JourneyMap Overlay: " + (renderOverlay ? "On" : "Off"));
		}
	}

	private void toggleRender() {
		renderOverlay = !renderOverlay;
		if (!renderOverlay) {
			clearDisplayed();
		} else {
			IReadOnlyLocatableObjectList<Snitch> snitches = SnitchMaster.instance.getManager().getSnitches();
			if (snitches != null) {
				String currentWorld = SnitchMaster.instance.getCurrentWorld();
				Iterable<Snitch> worldSnitches = snitches.getItemsForWorld(currentWorld);
				if (worldSnitches != null) {
					refresh(worldSnitches);
				}
			}
		}
	}

	@Override
	public void initialize(final IClientAPI jmAPI) {
		this.api = jmAPI;
		SnitchMaster.jmInterface = this;

		FMLCommonHandler.instance().bus().register(this);
		ClientRegistry.registerKeyBinding(renderJourneyMapOverlay);

		SnitchMaster.logger.info("[SnitchMaster] JourneyMap overlay initialized");
	}

	/**
	 * Used by JourneyMap to associate a modId with this plugin.
	 */
	@Override
	public String getModId() {
		return SnitchMaster.MODID;
	}

	@Override
	public void onEvent(ClientEvent clientEvent) {

	}

	private void sendImage(Snitch snitch) {
		if (api.playerAccepts(getModId(), DisplayType.Image)) {
			ImageOverlay overlay = SnitchImageFactory.createSnitchOverlay(snitch);
			try {
				if (overlay != null) {
					api.show(overlay);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void displaySnitch(Snitch snitch) {
		if (renderOverlay) {
			sendImage(snitch);
		}
	}

	@Override
	public void refresh(Iterable<Snitch> snitches) {
		clearDisplayed();
		for (Snitch snitch : snitches) {
			displaySnitch(snitch);
		}
	}

	private void clearDisplayed() {
		api.removeAll(this.getModId());
	}
}
