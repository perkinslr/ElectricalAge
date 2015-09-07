package mods.eln.transparentnode.generator;

import mods.eln.Eln;
import mods.eln.mechanical.Shaft;
import mods.eln.mechanical.ShaftElement;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.misc.Utils;
import mods.eln.node.NodeBase;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElement;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.IProcess;
import mods.eln.sim.PhysicalConstant;
import mods.eln.sim.ThermalLoad;
import mods.eln.sim.mna.SubSystem;
import mods.eln.sim.mna.component.VoltageSource;
import mods.eln.sim.mna.misc.IRootSystemPreStepProcess;
import mods.eln.sim.nbt.NbtElectricalLoad;
import mods.eln.transparentnode.turbine.TurbineDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by svein on 08/09/15.
 */
public class GeneratorElement extends TransparentNodeElement implements ShaftElement {
    GeneratorDescriptor desc;
    Shaft shaft;

    NbtElectricalLoad positiveLoad = new NbtElectricalLoad("positiveLoad");
    VoltageSource electricalPowerSource = new VoltageSource("PowerSource", positiveLoad, null);
    GeneratorElectricalProcess electricalProcess = new GeneratorElectricalProcess();
    GeneratorShaftProcess shaftProcess = new GeneratorShaftProcess();

    public GeneratorElement(TransparentNode transparentNode, TransparentNodeDescriptor descriptor) {
        super(transparentNode, descriptor);
        desc = (GeneratorDescriptor) descriptor;
        shaft = new Shaft(this);

        electricalLoadList.add(positiveLoad);
        electricalComponentList.add(electricalPowerSource);

        slowProcessList.add(shaftProcess);

        positiveLoad.setRs(0.1);
    }

    class GeneratorElectricalProcess implements IProcess, IRootSystemPreStepProcess {

        @Override
        public void process(double time) {
            double targetU = desc.RtoU.getValue(shaft.getRads());

            // Everything below is copied from TurbineElectricalProcess.
            // TODO: Factor this back in there. The only problem is that the generator isn't heat-based.
            // Some comments on what math is going on would be great.
            SubSystem.Th th = positiveLoad.getSubSystem().getTh(positiveLoad, electricalPowerSource);
            double Ut;
            if (targetU < th.U) {
                Ut = th.U;
            } else if (th.isHighImpedance()) {
                Ut = targetU;
            } else {
                double a = 1 / th.R;
                double b = desc.powerOutPerDeltaU - th.U / th.R;
                double c = -desc.powerOutPerDeltaU * targetU;
                Ut = (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
            }

            double i = (Ut - th.U) / th.R;
            double p = i * Ut;
            double pMax = desc.nominalP * 1.5;
            if (p > pMax) {
                Ut = (Math.sqrt(th.U * th.U + 4 * pMax * th.R) + th.U) / 2;
                Ut = Math.min(Ut, targetU);
                if (Double.isNaN(Ut)) Ut = 0;
                if (Ut < th.U) Ut = th.U;
            }

            electricalPowerSource.setU(Ut);
        }

        @Override
        public void rootSystemPreStepProcess() {
            process(0);
        }
    }

    class GeneratorShaftProcess implements IProcess {
        @Override
        public void process(double time) {
            // TODO: No.
            double eff = 1.0;

            double E = electricalPowerSource.getP() * time;
            double heat = E * (1.0 - eff);

            shaft.addEnergy((float) -E);
        }
    }


    @Override
    public void connectJob() {
        super.connectJob();
        Eln.simulator.mna.addProcess(electricalProcess);
    }

    @Override
    public void disconnectJob() {
        super.disconnectJob();
        Eln.simulator.mna.removeProcess(electricalProcess);
    }


    @Override
    public ElectricalLoad getElectricalLoad(Direction side, LRDU lrdu) {
        if(lrdu != LRDU.Down) return null;
        if(side == front) return positiveLoad;
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(Direction side, LRDU lrdu) {
        return null;
    }

    @Override
    public int getConnectionMask(Direction side, LRDU lrdu) {
        if (lrdu == LRDU.Down) {
            if (side == front) return NodeBase.maskElectricalPower;
        }
        return 0;
    }

    @Override
    public String multiMeterString(Direction side) {
        return Utils.plotER(shaft.getEnergy(), shaft.getRads()) +
                Utils.plotUIP(electricalPowerSource.getU(), electricalPowerSource.getI());
    }

    @Override
    public String thermoMeterString(Direction side) {
        return null;
    }

    @Override
    public void initialize() {
        connect();
    }

    @Override
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
        return false;
    }

    @Override
    public void onNeighborBlockChange() {
        super.onNeighborBlockChange();
        shaft.onNeighborBlockChange();
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
        return desc.shaftWeight;
    }

    @Override
    public Direction[] getShaftConnectivity() {
        return new Direction[] {
                front.left(), front.right()
        };
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
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        shaft.readFromNBT(nbt, "shaft");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        shaft.writeToNBT(nbt, "shaft");
    }
}
