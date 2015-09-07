package mods.eln.node.transparent;

import mods.eln.misc.INBTTReady;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

/**
 * Created by svein on 08/09/15.
 */
public class TransparentNodeElementFluidHandler implements IFluidHandler, INBTTReady {

    FluidTank tank;

    /**
     * Stores fluids.
     *
     * @param tankSize Tank size, in mB.
     */
    public TransparentNodeElementFluidHandler(int tankSize) {
        tank = new FluidTank(tankSize);
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
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
