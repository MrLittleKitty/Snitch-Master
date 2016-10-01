package com.gmail.nuclearcat1337.snitch_master.journeymap;

import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;

/**
 * Created by Mr_Little_Kitty on 9/21/2016.
 * Represents the basic interface for abstracting the JourneyMap plugin.
 */
public interface JourneyMapInterface
{
    /**
     * Displays the given Snitch on JourneyMap (both on the minimap and on the fullscreen map)
     */
    void displaySnitch(Snitch snitch);

    /**
     * Displays all the Snitches on JourneyMap (both on minimap and on fullscreen map)
     */
    void refresh(Iterable<Snitch> snitches);
}
