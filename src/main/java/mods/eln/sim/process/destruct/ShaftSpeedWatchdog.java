package mods.eln.sim.process.destruct;

import mods.eln.mechanical.IShaftElement;

/**
 * Created by svein on 13/09/15.
 */
public class ShaftSpeedWatchdog extends ValueWatchdog {
    private final IShaftElement shaftElement;

    public ShaftSpeedWatchdog(IShaftElement shaftElement, double max) {
        this.shaftElement = shaftElement;
        this.max = max;
    }

    @Override
    double getValue() {
        return shaftElement.getShaft().getRads();
    }
}
