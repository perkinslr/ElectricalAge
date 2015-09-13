package mods.eln.transparentnode.steamturbine;

import mods.eln.mechanical.Shaft;
import mods.eln.mechanical.ShaftDescriptor;
import mods.eln.misc.Obj3D;
import mods.eln.node.transparent.TransparentNodeDescriptor;

/**
 * Created by svein on 06/09/15.
 */
public class SteamTurbineDescriptor extends TransparentNodeDescriptor implements ShaftDescriptor {
    private final Obj3D obj;
    private final Obj3D.Obj3DPart[] statics;
    private final Obj3D.Obj3DPart[] rotating;
    /**
     * Overall time for steam input changes to take effect, in seconds.
     */
    public final float steamInertia = 20;
    // Optimal steam consumed per second, mB.
    // Computed to equal a single 36LP Railcraft boiler, or half of a 36HP.
    public final float steamConsumption = 7200;
    // Joules per mB, at optimal turbine speed.
    // Computed to equal what you'd get from Railcraft steam engines, plus a small
    // bonus because you're using Electrical Age you crazy person you.
    // This pretty much fills up a VHV line.
    // TODO: This should be tied into the config options.
    public final float steamPower = 2;

    public final float shaftWeight = 5;


    public SteamTurbineDescriptor(String name, Class ElementClass, Class RenderClass, Obj3D obj) {
        super(name, ElementClass, RenderClass);
        this.obj = obj;

        this.statics = new Obj3D.Obj3DPart[]{
                obj.getPart("Cowl"),
                obj.getPart("Stand"),
        };
        this.rotating = new Obj3D.Obj3DPart[]{
                obj.getPart("Shaft"),
                obj.getPart("Fan"),
        };
    }

    public void draw(float angle) {
        drawShaft(statics, rotating, angle);
    }
}
