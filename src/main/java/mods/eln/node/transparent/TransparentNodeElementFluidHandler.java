package mods.eln.node.transparent;

import mods.eln.misc.INBTTReady;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

/**
 * Created by svein on 08/09/15.
 */
public class TransparentNodeElementFluidHandler implements IFluidHandler, INBTTReady {

    private Fluid filter;
    FluidTank tank;

    /**
     * Stores fluids.
     *
     * @param tankSize Tank size, in mB.
     */
    public TransparentNodeElementFluidHandler(int tankSize) {
        tank = new FluidTank(tankSize);
    }

    public void setFilter(Fluid filter) {
        assert filter != null;
        this.filter = filter;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (filter != null && resource.getFluid().getID() != filter.getID()) return 0;
        return tank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
       if (resource.isFluidEqual(tank.getFluid()))
           return tank.drain(resource.amount, doDrain);
       else
           return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return tank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        if (filter != null && fluid.getID() != filter.getID()) return false;
        return true;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[]{tank.getInfo()};
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt, String str) {
        tank.setFluid(new FluidStack(
                nbt.getInteger(str + "id"),
                nbt.getInteger(str + "mb")));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt, String str) {
        if (tank.getFluid() != null) {
            nbt.setInteger(str + "id", tank.getFluid().fluidID);
            nbt.setInteger(str + "mb", tank.getFluidAmount());
        }
    }
}
