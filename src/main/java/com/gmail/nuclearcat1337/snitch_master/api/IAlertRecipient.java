package com.gmail.nuclearcat1337.snitch_master.api;

/**
 * Created by Mr_Little_Kitty on 7/9/2016.
 * Represents an object that would like to be notified of all received Snitch alerts.
 */
public interface IAlertRecipient {
	/**
	 * Called when a Snitch alert is received by the player in chat.
	 */
	void receiveSnitchAlert(SnitchAlert alert);
}
