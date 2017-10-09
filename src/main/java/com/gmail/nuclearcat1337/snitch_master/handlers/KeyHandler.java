package com.gmail.nuclearcat1337.snitch_master.handlers;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.gui.screens.MainGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class KeyHandler {
	public KeyBinding snitchMasterMainGUI = new KeyBinding("Snitch Master Settings", Keyboard.KEY_V, "Snitch Master");
	public KeyBinding toggleAllRender = new KeyBinding("Toggle Render Snitch Lists", Keyboard.KEY_N, "Snitch Master");

	private SnitchMaster snitchMaster;

	public KeyHandler(SnitchMaster snitchMaster) {
		this.snitchMaster = snitchMaster;
		ClientRegistry.registerKeyBinding(snitchMasterMainGUI);
		ClientRegistry.registerKeyBinding(toggleAllRender);

		FMLCommonHandler.instance().bus().register(this);
	}

	@SubscribeEvent
	public void onKeyPress(InputEvent.KeyInputEvent event) {
		if (snitchMasterMainGUI.isPressed()) {
			Minecraft.getMinecraft().displayGuiScreen(new MainGui(snitchMaster));
		}
		if (toggleAllRender.isPressed()) {
			snitchMaster.getManager().toggleGlobalRender();
			SnitchMaster.SendMessageToPlayer("Global render: " + (snitchMaster.getManager().getGlobalRender() ? "On" : "Off"));
		}
	}
}
