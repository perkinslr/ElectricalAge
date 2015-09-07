package mods.eln.transparentnode.generator;

import mods.eln.mechanical.ShaftDescriptor;
import mods.eln.misc.IFunction;
import mods.eln.misc.LinearFunction;
import mods.eln.misc.Obj3D;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.sixnode.electricalcable.ElectricalCableDescriptor;

/**
 * Created by svein on 08/09/15.
 */
public class GeneratorDescriptor extends TransparentNodeDescriptor implements ShaftDescriptor {
    private final ElectricalCableDescriptor cable;
    private final Object obj;
    public final float powerOutPerDeltaU;
    public final float nominalP;
    private final Obj3D.Obj3DPart[] statics;
    private final Obj3D.Obj3DPart[] rotating;
    public final float shaftWeight = 20;
    public IFunction RtoU;

    public GeneratorDescriptor(String name, Class ElementClass, Class RenderClass, ElectricalCableDescriptor cable, Obj3D obj, float nominalRads, float nominalU, float powerOutPerDeltaU, float nominalP) {
        super(name, ElementClass, RenderClass);
        this.cable = cable;
        this.obj = obj;
        this.powerOutPerDeltaU = powerOutPerDeltaU;
        this.nominalP = nominalP;
        RtoU = new LinearFunction(0, 0, nominalRads, nominalU);

        this.statics = new Obj3D.Obj3DPart[]{
                obj.getPart("Cowl"),
                obj.getPart("Stand"),
        };
        this.rotating = new Obj3D.Obj3DPart[]{
                obj.getPart("Shaft"),
        };
    }

    public void draw(float angle) {
        drawShaft(statics, rotating, angle);
    }
}
