package com.gmail.nuclearcat1337.snitch_master.handlers;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.gui.screens.MainGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

/**
 * Created by Mr_Little_Kitty on 6/30/2016.
 * Handles the keys that are bound for SnitchMaster.
 * Currently this handles the settings key and the toggle render key.
 */
public class KeyHandler {
    public KeyBinding snitchMasterMainGUI = new KeyBinding("Snitch Master Settings", Keyboard.KEY_V, "Snitch Master");
    public KeyBinding toggleAllRender = new KeyBinding("Toggle Render Snitch Lists", Keyboard.KEY_N, "Snitch Master");

    private SnitchMaster snitchMaster;

    public KeyHandler(SnitchMaster snitchMaster) {
        this.snitchMaster = snitchMaster;
        ClientRegistry.registerKeyBinding(snitchMasterMainGUI);
        ClientRegistry.registerKeyBinding(toggleAllRender);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if (snitchMasterMainGUI.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new MainGui(snitchMaster.getManager(),
                    snitchMaster.getChatSnitchParser(), snitchMaster.getSettings()));
        }
        if (toggleAllRender.isPressed()) {
            snitchMaster.getManager().toggleGlobalRender();
            SnitchMaster.SendMessageToPlayer("Global render: " + (snitchMaster.getManager().getGlobalRender() ? "On" : "Off"));
        }
    }
}
