package com.gmail.nuclearcat1337.snitch_master.handlers;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.nio.charset.StandardCharsets;

/**
 * Manages the current world the player is in. Handles servers with multiple worlds and single player.
 */
public class WorldInfoListener {
	private final Minecraft mc = Minecraft.getMinecraft();
	private final SnitchMaster snitchMaster;

	private static final int MIN_DELAY_MS = 1000;

	private static long lastRequest;
	private static long lastResponse;
	private static SimpleNetworkWrapper channel;
	private static String worldID = "single_player";

	public WorldInfoListener(SnitchMaster snitchMaster) {
		this.snitchMaster = snitchMaster;

		channel = NetworkRegistry.INSTANCE.newSimpleChannel("world_name");
		channel.registerMessage(WorldListener.class, WorldIDPacket.class, 0, Side.CLIENT);

		//IDK which one this classes uses and I cant be bothered to find out
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (!mc.isSingleplayer() && mc.player != null && !mc.player.isDead) {
			if (mc.player.getDisplayName().equals(event.getEntity().getDisplayName())) {
				worldID = null;
				if (this.channel != null) {
					requestWorldID();
				}

			}
		}
	}

	private void requestWorldID() {
		long now = System.currentTimeMillis();
		if (lastRequest + MIN_DELAY_MS < now) {
			channel.sendToServer(new WorldIDPacket());
			lastRequest = System.currentTimeMillis();
		}
	}

	/**
	 * Gets the name of the current world the player is.
	 * Returns "single player" is the player is playing single player.
	 */
	public String getWorldID() {
		if (lastResponse < lastRequest) {
			//No WorldInfo response so just use vanilla world names
			WorldProvider provider = Minecraft.getMinecraft().world.provider;
			if (provider instanceof WorldProviderEnd) {
				return "world_the_end";
			} else if (provider instanceof WorldProviderHell) {
				return "world_nether";
			} else {
				return "world";
			}
		} else {
			return worldID;
		}
	}

	/**
	 * The packet class to be sent to the server requesting the name of the world.
	 */
	public static class WorldIDPacket implements IMessage {
		private String worldID;

		public WorldIDPacket() {

		}

		public WorldIDPacket(String worldID) {
			this.worldID = worldID;
		}

		public String getWorldID() {
			return worldID;
		}

		public void fromBytes(ByteBuf buf) {
			worldID = ByteBufUtils.readUTF8String(buf);
		}

		public void toBytes(ByteBuf buf) {
			byte[] bytes = "NAME".getBytes(StandardCharsets.UTF_8);
			buf.clear();
			buf.writeBytes(bytes);
		}
	}

	/**
	 * Receives the response from the server with the name of the world the player is currently in.
	 */
	public static class WorldListener implements IMessageHandler<WorldIDPacket, IMessage> {
		@SideOnly(Side.CLIENT)
		@Override
		public IMessage onMessage(WorldIDPacket message, MessageContext ctx) {
			lastResponse = System.currentTimeMillis();
			worldID = message.getWorldID();
			return null;
		}
	}
}
