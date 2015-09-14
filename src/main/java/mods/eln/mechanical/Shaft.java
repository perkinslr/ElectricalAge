package mods.eln.mechanical;

import mods.eln.misc.Coordonate;
import mods.eln.misc.Direction;
import mods.eln.misc.INBTTReady;
import mods.eln.node.NodeManager;
import mods.eln.node.transparent.TransparentNodeElement;
import mods.eln.sim.process.destruct.ShaftSpeedWatchdog;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by svein on 12/09/15.
 */
public class Shaft implements INBTTReady {
    // See https://en.wikipedia.org/wiki/Flywheel_energy_storage#Energy_density
    static final float shapeFactor = 0.5f;
    /**
     * All elements that are part of this shaft.
     */
    HashSet<IShaftElement> elements = new HashSet<>();
    /**
     * Radians/second rotation speed.
     */
    private float rads = 0;
    private float lastRadsPublished;


    public Shaft(IShaftElement element) {
        elements.add(element);
    }

    /**
     * Creates a new, empty shaft. Only for rebuildNetwork.
     * @param shaft Shaft to copy stats from.
     */
    private Shaft(Shaft shaft) {
        rads = shaft.rads;
        lastRadsPublished = shaft.lastRadsPublished;
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

        assert other != this;
        for (IShaftElement element : other.elements) {
            elements.add(element);
            element.setShaft(this);
        }
        other.elements.clear();
    }

    /**
     * Connect a IShaftElement to a shaft network, merging any relevant adjacent networks.
     * @param from The IShaftElement that changed.
     */
    public void connectShaft(IShaftElement from) {
        final ArrayList<IShaftElement> neighbours = getNeighbours(from);
        for (IShaftElement neighbour : neighbours) {
            if (neighbour.getShaft() != this) {
                mergeShafts(neighbour.getShaft());
            }
        }
    }

    /**
     * Disconnect from a shaft network, because an element is dying.
     * @param from The IShaftElement that's going away.
     */
    public void disconnectShaft(IShaftElement from) {
        elements.remove(from);
        from.setShaft(null);
        // This may have split the network.
        // At the moment there's no better way to figure this out than by exhaustively walking it to check for partitions.
        rebuildNetwork();
    }

    /**
     * Walk the entire network, splitting as necessary.
     * Yes, this makes breaking a shaft block O(n). Not a problem right now.
     */
    private void rebuildNetwork() {
        HashSet<IShaftElement> unseen = new HashSet<>(elements);
        HashSet<IShaftElement> queue = new HashSet<>();
        Shaft shaft = this;
        while (unseen.size() > 0) {
            shaft.elements.clear();
            // Do a breadth-first search from an arbitrary element.
            final IShaftElement start = unseen.iterator().next();
            unseen.remove(start);
            queue.add(start);
            while (queue.size() > 0) {
                final IShaftElement next = queue.iterator().next();
                queue.remove(next);
                shaft.elements.add(next);
                next.setShaft(shaft);
                final ArrayList<IShaftElement> neighbours = getNeighbours(next);
                for (IShaftElement neighbour : neighbours) {
                    if (unseen.contains(neighbour)) {
                        unseen.remove(neighbour);
                        queue.add(neighbour);
                    }
                }
            }
            // We ran out of network. Any elements remaining in unseen should thus form a new network.
            shaft = new Shaft(this);
        }
    }

    private ArrayList<IShaftElement> getNeighbours(IShaftElement from) {
        Coordonate c = new Coordonate();
        ArrayList<IShaftElement> ret = new ArrayList<>(6);
        for (Direction dir : from.getShaftConnectivity()) {
            c.copyFrom(from.coordonate());
            c.move(dir);
            TransparentNodeElement n = NodeManager.instance.getTransparentNodeFromCoordinate(c);
            if (n instanceof IShaftElement) {
                final IShaftElement to = (IShaftElement) n;
                for (Direction dir2 : to.getShaftConnectivity()) {
                    if (dir2.getInverse() == dir) {
                        ret.add(to);
                        break;
                    }
                }
            }
        }
        return ret;
    }

    private float getEnergyFactor() {
        float mass = 0;
        assert elements.size() != 0;
        for (IShaftElement e : elements) {
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
            for (IShaftElement element : elements) {
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


    final static float absoluteMaximumShaftSpeed = 3200;

    public ShaftSpeedWatchdog createDefaultWatchdog(IShaftElement shaftElement) {
        ShaftSpeedWatchdog shaftSpeedWatchdog = new ShaftSpeedWatchdog(shaftElement, absoluteMaximumShaftSpeed);
        return shaftSpeedWatchdog;
    }

    final float defaultDragPerRad = 0.02f;

    public double getDefaultDrag() {
        return getRads() * defaultDragPerRad;
    }
}
