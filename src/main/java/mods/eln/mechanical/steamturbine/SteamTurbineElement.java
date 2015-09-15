package mods.eln.mechanical.steamturbine;

import mods.eln.mechanical.Shaft;
import mods.eln.mechanical.IShaftElement;
import mods.eln.mechanical.ShaftDescriptor;
import mods.eln.mechanical.SimpleShaftElement;
import mods.eln.misc.*;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElement;
import mods.eln.node.transparent.TransparentNodeElementFluidHandler;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.IProcess;
import mods.eln.sim.ThermalLoad;
import mods.eln.sim.process.destruct.WorldExplosion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * This is a steam turbine.
 * <p>
 * ...
 * <p>
 * Do you want me to say something else? It's a steam turbine. Oh, and I suppose also the very first MA block.
 */
public class SteamTurbineElement extends SimpleShaftElement {
    static Fluid steam = FluidRegistry.getFluid("steam");
    private final SteamTurbineDescriptor descriptor;

    TransparentNodeElementFluidHandler steamTank;
    SteamTurbineSlowProcess turbineSlowProcess;

    public SteamTurbineElement(TransparentNode transparentNode, TransparentNodeDescriptor descriptor) {
        super(transparentNode, (ShaftDescriptor) descriptor);
        this.descriptor = (SteamTurbineDescriptor) descriptor;

        steamTank = new TransparentNodeElementFluidHandler(1000);
        turbineSlowProcess = new SteamTurbineSlowProcess();
        slowProcessList.add(turbineSlowProcess);

        steamTank.setFilter(steam);
  }

    class SteamTurbineSlowProcess implements IProcess, INBTTReady {
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

        @Override
        public void readFromNBT(NBTTagCompound nbt, String str) {
            rc.readFromNBT(nbt, str);
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt, String str) {
            rc.writeToNBT(nbt, str);
        }
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
    public String thermoMeterString(Direction side) {
        return null;
    }

    @Override
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        steamTank.writeToNBT(nbt, "tank");
        turbineSlowProcess.writeToNBT(nbt, "proc");
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        steamTank.readFromNBT(nbt, "tank");
        turbineSlowProcess.readFromNBT(nbt, "proc");
    }

}
