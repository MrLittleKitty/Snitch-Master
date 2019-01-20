package com.gmail.nuclearcat1337.snitch_master.journeymap;

import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;

/**
 * Created by Mr_Little_Kitty on 9/21/2016.
 * Represents the basic interface for abstracting the JourneyMap plugin.
 */
public interface JourneyMapInterface {
	void displaySnitch(Snitch snitch);

	void refresh(Iterable<Snitch> snitches);
}
