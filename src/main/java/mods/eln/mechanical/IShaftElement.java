package mods.eln.mechanical;

import mods.eln.misc.Coordonate;
import mods.eln.misc.Direction;

/**
 * Created by svein on 12/09/15.
 */
public interface IShaftElement {
    Shaft getShaft();

    void setShaft(Shaft shaft);

    // In kg. Only the actual rotating parts need apply.
    float getMass();

    /**
     * Shaft connectivity.
     * @return Directions in which this block should connect and merge shafts.
     */
    Direction[] getShaftConnectivity();

    Coordonate coordonate();

    void needPublish();
}
