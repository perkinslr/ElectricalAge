package mods.eln.mechanical.generator;

import mods.eln.mechanical.ShaftDescriptor;
import mods.eln.misc.BoundingBox;
import mods.eln.misc.IFunction;
import mods.eln.misc.LinearFunction;
import mods.eln.misc.Obj3D;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.sixnode.electricalcable.ElectricalCableDescriptor;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

/**
 * Created by svein on 08/09/15.
 */
public class GeneratorDescriptor extends ShaftDescriptor {
    final ElectricalCableDescriptor cable;
    public final float powerOutPerDeltaU;
    public final float nominalP;
    public IFunction RtoU;

    public GeneratorDescriptor(String name, Class ElementClass, Class RenderClass, ElectricalCableDescriptor cable, Obj3D obj, float nominalRads, float nominalU, float powerOutPerDeltaU, float nominalP) {
        super(name, ElementClass, RenderClass, obj);

        this.cable = cable;
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

}
