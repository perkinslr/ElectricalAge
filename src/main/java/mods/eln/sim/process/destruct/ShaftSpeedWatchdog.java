package mods.eln.sim.process.destruct;

import mods.eln.mechanical.Shaft;
import mods.eln.mechanical.ShaftElement;

/**
 * Created by svein on 13/09/15.
 */
public class ShaftSpeedWatchdog extends ValueWatchdog {
    private final ShaftElement shaftElement;

    public ShaftSpeedWatchdog(ShaftElement shaftElement, double max) {
        this.shaftElement = shaftElement;
        this.max = max;
    }

    @Override
    double getValue() {
        return shaftElement.getShaft().getRads();
    }
}
