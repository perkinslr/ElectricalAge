package mods.eln.mechanical.steamturbine;

import mods.eln.mechanical.Shaft;
import mods.eln.mechanical.ShaftElement;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.misc.RcInterpolator;
import mods.eln.misc.Utils;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElement;
import mods.eln.node.transparent.TransparentNodeElementFluidHandler;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.IProcess;
import mods.eln.sim.ThermalLoad;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This is a steam turbine.
 * <p>
 * ...
 * <p>
 * Do you want me to say something else? It's a steam turbine. Oh, and I suppose also the very first MA block.
 */
public class SteamTurbineElement extends TransparentNodeElement implements ShaftElement {

    static Fluid steam = FluidRegistry.getFluid("steam");

    private final SteamTurbineDescriptor descriptor;
    TransparentNodeElementFluidHandler steamTank;
    SteamTurbineSlowProcess turbineSlowProcess;
    private Shaft shaft;

    public SteamTurbineElement(TransparentNode transparentNode, TransparentNodeDescriptor descriptor) {
        super(transparentNode, descriptor);
        this.descriptor = (SteamTurbineDescriptor) descriptor;

        steamTank = new TransparentNodeElementFluidHandler(1000);
        turbineSlowProcess = new SteamTurbineSlowProcess();
        slowProcessList.add(turbineSlowProcess);
        shaft = new Shaft(this);

        steamTank.setFilter(steam);

        // TODO: Overspeed watchdog.
    }

    @Override
    public Shaft getShaft() {
        return shaft;
    }

    @Override
    public void setShaft(Shaft shaft) {
        this.shaft = shaft;
    }

    @Override
    public float getMass() {
        return descriptor.shaftWeight;
    }

    @Override
    public Direction[] getShaftConnectivity() {
        return new Direction[]{
                front.left(), front.right()
        };
    }

    @Override
    public IFluidHandler getFluidHandler() {
        return steamTank;
    }

    @Override
    public ElectricalLoad getElectricalLoad(Direction side, LRDU lrdu) {
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(Direction side, LRDU lrdu) {
        return null;
    }

    @Override
    public int getConnectionMask(Direction side, LRDU lrdu) {
        return 0;
    }

    @Override
    public String multiMeterString(Direction side) {
        return Utils.plotER(shaft.getEnergy(), shaft.getRads());
    }

    @Override
    public String thermoMeterString(Direction side) {
        return null;
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
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        steamTank.writeToNBT(nbt, "tank");
        shaft.writeToNBT(nbt, "shaft");
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        steamTank.readFromNBT(nbt, "tank");
        shaft.readFromNBT(nbt, "shaft");
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

    class SteamTurbineSlowProcess implements IProcess {
        RcInterpolator rc = new RcInterpolator(descriptor.steamInertia);

        @Override
        public void process(double time) {
            final FluidStack ss = steamTank.drain(ForgeDirection.DOWN, (int) (descriptor.steamConsumption * time), true);
            float steam = ss != null ? ss.amount : 0;
            rc.setTarget((float) (steam / time));
            rc.step((float) time);
            steam = rc.get();
            // TODO: Make it inefficient at speeds other than optimal.
            // (Which of course correspond to the 800V output.)
            float power = steam * descriptor.steamPower;
            float energy = (float) (power * time);
            shaft.addEnergy(energy);
       }
    }
}
