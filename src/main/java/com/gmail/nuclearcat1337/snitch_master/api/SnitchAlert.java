package com.gmail.nuclearcat1337.snitch_master.api;

import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.ILocation;
import com.gmail.nuclearcat1337.snitch_master.util.Location;
import net.minecraft.util.text.ITextComponent;

/**
 * Represents a Snitch alert received in chat.
 */
public class SnitchAlert {
	private final String playerName;
	private final ILocation point;
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

	public ILocation getLocation() {
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
