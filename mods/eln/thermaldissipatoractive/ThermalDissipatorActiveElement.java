package mods.eln.thermaldissipatoractive;

import java.io.DataOutputStream;
import java.io.IOException;

import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.misc.Utils;
import mods.eln.node.NodeElectricalLoad;
import mods.eln.node.NodePeriodicPublishProcess;
import mods.eln.node.NodeThermalLoad;
import mods.eln.node.TransparentNode;
import mods.eln.node.TransparentNodeDescriptor;
import mods.eln.node.TransparentNodeElement;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.ThermalLoad;
import mods.eln.sim.mna.component.Resistor;
import net.minecraft.entity.player.EntityPlayer;

public class ThermalDissipatorActiveElement extends TransparentNodeElement{
	ThermalDissipatorActiveDescriptor descriptor;
	NodeThermalLoad thermalLoad = new NodeThermalLoad("thermalLoad");
	NodeElectricalLoad positiveLoad = new NodeElectricalLoad("positiveLoad");
	ThermalDissipatorActiveSlowProcess slowProcess = new ThermalDissipatorActiveSlowProcess(this);
	Resistor powerResistor = new Resistor(positiveLoad,null);
	
	
	public ThermalDissipatorActiveElement(TransparentNode transparentNode,
			TransparentNodeDescriptor descriptor) {
		super(transparentNode, descriptor);
		thermalLoadList.add(thermalLoad);
		electricalLoadList.add(positiveLoad);
		electricalComponentList.add(powerResistor);
		
		slowProcessList.add(slowProcess);
		this.descriptor = (ThermalDissipatorActiveDescriptor) descriptor;
		slowProcessList.add(new NodePeriodicPublishProcess(node, 4f, 2f));
		

	}

	@Override
	public ElectricalLoad getElectricalLoad(Direction side, LRDU lrdu) {
		if(side == front || side == front.getInverse()) return positiveLoad;
		return null;
	}

	@Override
	public ThermalLoad getThermalLoad(Direction side, LRDU lrdu) {
		// TODO Auto-generated method stub
		if(side == Direction.YN || side == Direction.YP || lrdu != lrdu.Down) return null;
		if(side == front || side == front.getInverse()) return null;
		return thermalLoad;
	}

	@Override
	public int getConnectionMask(Direction side, LRDU lrdu) {
		// TODO Auto-generated method stub
		if(side == Direction.YN || side == Direction.YP  || lrdu != lrdu.Down) return 0;
		if(side == front || side == front.getInverse()) return node.maskElectricalPower;
		return node.maskThermal;
	}

	@Override
	public String multiMeterString(Direction side) {
		// TODO Auto-generated method stub
		return Utils.plotVolt("U : ", positiveLoad.getU()) + Utils.plotAmpere("I : ", positiveLoad.getCurrent());
	}

	@Override
	public String thermoMeterString(Direction side) {
		// TODO Auto-generated method stub
		return Utils.plotCelsius("T : ", thermalLoad.Tc) + Utils.plotPower("P : ",thermalLoad.getPower());
	}

	@Override
	public void initialize() {
		descriptor.applyTo(thermalLoad);
		descriptor.applyTo(positiveLoad,powerResistor);
		connect();
	}

	@Override
	public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side,
			float vx, float vy, float vz) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	@Override
	public void networkSerialize(DataOutputStream stream) {
		// TODO Auto-generated method stub
		super.networkSerialize(stream);
		try {
			stream.writeFloat(lastPowerFactor = (float) (powerResistor.getP()/descriptor.electricalNominalP));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Utils.println("DISIP");
	}
	public float lastPowerFactor;
	
	


}
