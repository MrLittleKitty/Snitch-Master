package com.gmail.nuclearcat1337.snitch_master.journeymap;

import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;

public class JourneyMapRelay implements JourneyMapInterface {

    private static JourneyMapRelay instance = null;

    public static JourneyMapRelay getInstance() {
        if (instance == null) {
            instance = new JourneyMapRelay();
        }
        return instance;
    }

    private JourneyMapInterface journeyMapInterface = null;

    private JourneyMapRelay() {

    }

    public void setJourneyMap(final JourneyMapInterface journeyMap) {
        this.journeyMapInterface = journeyMap;
    }

    @Override
    public void displaySnitch(final Snitch snitch) {
        if (journeyMapInterface != null) {
            journeyMapInterface.displaySnitch(snitch);
        }
    }

    @Override
    public void displaySnitches(Iterable<Snitch> snitches) {
        if(journeyMapInterface != null) {
            journeyMapInterface.displaySnitches(snitches);
        }
    }

    @Override
    public void refresh(final Iterable<Snitch> snitches) {
        if (journeyMapInterface != null) {
            journeyMapInterface.refresh(snitches);
        }
    }
}
