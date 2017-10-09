package com.gmail.nuclearcat1337.snitch_master.journeymap;

import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;

public interface JourneyMapInterface {
	void displaySnitch(Snitch snitch);

	void refresh(Iterable<Snitch> snitches);
}
