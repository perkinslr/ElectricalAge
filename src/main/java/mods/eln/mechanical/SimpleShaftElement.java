package mods.eln.mechanical;

import mods.eln.mechanical.steamturbine.SteamTurbineDescriptor;
import mods.eln.misc.Direction;
import mods.eln.misc.Utils;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElement;
import mods.eln.sim.process.destruct.WorldExplosion;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by svein on 15/09/15.
 */
public abstract class SimpleShaftElement extends TransparentNodeElement implements IShaftElement {
    private final float shaftWeight;
    protected Shaft shaft;

    public SimpleShaftElement(TransparentNode transparentNode, ShaftDescriptor descriptor) {
        super(transparentNode, descriptor);
        this.shaftWeight = descriptor.shaftWeight;
        shaft = new Shaft(this);

        final WorldExplosion exp = new WorldExplosion(this).machineExplosion();
        slowProcessList.add(shaft.createDefaultWatchdog(this).set(exp));
    }

    public Shaft getShaft() {
        return shaft;
    }

    public void setShaft(Shaft shaft) {
        this.shaft = shaft;
    }

    public float getMass() {
        return shaftWeight;
    }

    public Direction[] getShaftConnectivity() {
        return new Direction[]{
                front.left(), front.right()
        };
    }

    @Override
    public String multiMeterString(Direction side) {
        return Utils.plotER(shaft.getEnergy(), shaft.getRads());
    }

    @Override
    public void initialize() {
        reconnect();
        shaft.connectShaft(this);
    }

    @Override
    public void onBreakElement() {
        super.onBreakElement();
        shaft.disconnectShaft(this);
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            stream.writeFloat(shaft.getRads());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        shaft.writeToNBT(nbt, "shaft");
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        shaft.readFromNBT(nbt, "shaft");
    }

}
