package mods.eln.mechanical.generator;

import mods.eln.Eln;
import mods.eln.mechanical.ShaftDescriptor;
import mods.eln.mechanical.SimpleShaftElement;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.misc.Utils;
import mods.eln.node.NodeBase;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.IProcess;
import mods.eln.sim.ThermalLoad;
import mods.eln.sim.mna.SubSystem;
import mods.eln.sim.mna.component.Resistor;
import mods.eln.sim.mna.component.VoltageSource;
import mods.eln.sim.mna.misc.IRootSystemPreStepProcess;
import mods.eln.sim.nbt.NbtElectricalLoad;
import mods.eln.sim.nbt.NbtThermalLoad;
import mods.eln.sim.process.destruct.ThermalLoadWatchDog;
import mods.eln.sim.process.destruct.WorldExplosion;
import mods.eln.sim.process.heater.ElectricalLoadHeatThermalLoad;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by svein on 08/09/15.
 */
public class GeneratorElement extends SimpleShaftElement {
    GeneratorDescriptor desc;

    NbtElectricalLoad inputLoad = new NbtElectricalLoad("inputLoad");
    NbtElectricalLoad positiveLoad = new NbtElectricalLoad("positiveLoad");
    Resistor inputToPositiveResistor = new Resistor(inputLoad, positiveLoad);
    VoltageSource electricalPowerSource = new VoltageSource("PowerSource", positiveLoad, null);
    GeneratorElectricalProcess electricalProcess = new GeneratorElectricalProcess();
    GeneratorShaftProcess shaftProcess = new GeneratorShaftProcess();

    NbtThermalLoad thermal = new NbtThermalLoad("thermal");
    ElectricalLoadHeatThermalLoad heater = new ElectricalLoadHeatThermalLoad(inputLoad, thermal);

    ThermalLoadWatchDog thermalLoadWatchDog = new ThermalLoadWatchDog();

    public GeneratorElement(TransparentNode transparentNode, TransparentNodeDescriptor descriptor) {
        super(transparentNode, (ShaftDescriptor) descriptor);
        desc = (GeneratorDescriptor) descriptor;

        electricalLoadList.add(positiveLoad);
        electricalLoadList.add(inputLoad);
        electricalComponentList.add(electricalPowerSource);
        electricalComponentList.add(inputToPositiveResistor);

        slowProcessList.add(shaftProcess);

        desc.cable.applyTo(inputLoad);
        desc.cable.applyTo(inputToPositiveResistor);
        desc.cable.applyTo(positiveLoad);

        desc.thermalLoadInitializer.applyTo(thermal);
        desc.thermalLoadInitializer.applyTo(thermalLoadWatchDog);
        thermal.setAsSlow();
        thermalLoadList.add(thermal);
        slowProcessList.add(heater);
        thermalLoadWatchDog.set(thermal).set(new WorldExplosion(this).machineExplosion());
    }

    class GeneratorElectricalProcess implements IProcess, IRootSystemPreStepProcess {

        @Override
        public void process(double time) {
            double targetU = desc.RtoU.getValue(shaft.getRads());

            // Everything below is copied from TurbineElectricalProcess.
            // TODO: Factor this back in there. The only problem is that the generator isn't heat-based.
            // TODO: Or rewrite. The current behaviour isn't very realistic for a rotary generator.
            // Some comments on what math is going on would be great.
            SubSystem.Th th = positiveLoad.getSubSystem().getTh(positiveLoad, electricalPowerSource);
            double Ut;
            // TODO: Also, figure out how to make this thing act as an electric motor when underspeed.
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
            double E = electricalPowerSource.getP() * time;
            E += shaft.getDefaultDrag();
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
        if(side == front) return inputLoad;
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(Direction side, LRDU lrdu) {
        return thermal;
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
        return Utils.plotCelsius("T", thermal.getT());
    }

    @Override
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
        return false;
    }
}
