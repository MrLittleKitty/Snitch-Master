package com.gmail.nuclearcat1337.snitch_master.api;

import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.Location;
import net.minecraft.util.text.ITextComponent;

/**
 * Created by Mr_Little_Kitty on 7/9/2016.
 * Represents a Snitch alert received in chat.
 */
public class SnitchAlert {
	private final String playerName;
	private final Location point;
	private final String snitchName;
	private final SnitchActivity activity;
	private final String world;
	private ITextComponent rawMessage;

	public SnitchAlert(String player, int x, int y, int z, String activityText, String snitchName, String world, ITextComponent rawMessage) {
		this.playerName = player;
		this.point = new Location(x, y, z, world);
		this.activity = SnitchActivity.FromText(activityText);
		this.snitchName = snitchName;
		this.world = world;
		this.rawMessage = rawMessage;
	}

	public String getPlayerName() {
		return playerName;
	}

	public Location getLocation() {
		return point;
	}

	public SnitchActivity getActivity() {
		return activity;
	}

	public String getSnitchName() {
		return snitchName;
	}

	public String getWorld() {
		return world;
	}

	public ITextComponent getRawMessage() {
		return rawMessage;
	}

	public void setRawMessage(ITextComponent rawMessage) {
		this.rawMessage = rawMessage;
	}
}
