package mods.eln.mechanical;

import mods.eln.misc.Coordonate;
import mods.eln.misc.Direction;
import mods.eln.misc.INBTTReady;
import mods.eln.node.NodeManager;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeElement;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;

/**
 * Created by svein on 12/09/15.
 */
public class Shaft implements INBTTReady {
    // See https://en.wikipedia.org/wiki/Flywheel_energy_storage#Energy_density
    static final float shapeFactor = 0.5f;
    /**
     * All elements that are part of this shaft.
     */
    ArrayList<ShaftElement> elements;
    /**
     * Radians/second rotation speed.
     */
    private float rads = 0;
    private float lastRadsPublished;


    public Shaft(ShaftElement element) {
        elements = new ArrayList<>();
        elements.add(element);
    }


    /**
     * Merge two shafts.
     *
     * @param other The shaft to merge into this one. Destroyed.
     */
    void mergeShafts(Shaft other) {
        // TODO: Some kind of explosion-effect for severely mismatched speeds?
        // For now, let's be nice.
        rads = Math.min(rads, other.rads);

        for (ShaftElement element : other.elements) {
            elements.add(element);
        }
        other.elements.clear();
    }

    /**
     * Convenience function that merges shafts for all adjacent blocks.
     *
     * Er, when they're pointed in the right direction.
     */
    public void onNeighborBlockChange() {
        Coordonate c = new Coordonate();
        for (ShaftElement e : new ArrayList<>(elements)) {
            Coordonate ec = e.coordonate();
            for (Direction dir : e.getShaftConnectivity()) {
                c.copyFrom(ec);
                c.move(dir);
                TransparentNodeElement n = NodeManager.instance.getTransparentNodeFromCoordinate(c);
                if (n instanceof ShaftElement) {
                    final ShaftElement shaftElement = (ShaftElement) n;
                    if (shaftElement.getShaft() != this) {
                        mergeShafts(shaftElement.getShaft());
                        shaftElement.setShaft(this);
                    }
                }
            }
        }
    }

    private float getEnergyFactor() {
        float mass = 0;
        for (ShaftElement e : elements) {
            mass += e.getMass();
        }
        assert mass != 0;
        return mass * mass * shapeFactor / 2;
    }

    public float getEnergy() {
        float factor = getEnergyFactor();
        return factor * rads;
    }

    public Shaft addEnergy(float energy) {
        float factor = getEnergyFactor();
        rads += energy / factor;
        if (rads < 0) rads = 0;
//        assert rads >= 0;

        if (lastRadsPublished > rads * 1.05 || lastRadsPublished < rads * 0.95) {
            for (ShaftElement element : elements) {
                element.needPublish();
            }
            lastRadsPublished = rads;
        }
        return this;
    }

    public float getRads() {
        return rads;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt, String str) {
        rads = nbt.getFloat(str + "rads");

    }

    @Override
    public void writeToNBT(NBTTagCompound nbt, String str) {
        nbt.setFloat(str + "rads", rads);
    }

}
