package mods.eln.mechanical;

import mods.eln.misc.BoundingBox;
import mods.eln.misc.Obj3D;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

/**
 * Created by svein on 15/09/15.
 */
public class ShaftDescriptor extends TransparentNodeDescriptor {
    protected Obj3D.Obj3DPart[] statics;
    protected Obj3D.Obj3DPart[] rotating;
    public final float shaftWeight = 5;

    public ShaftDescriptor(String name, Class ElementClass, Class RenderClass) {
        super(name, ElementClass, RenderClass);
    }

    public void draw(float angle) {
        drawShaft(statics, rotating, angle);
    }

    public void drawShaft(Obj3D.Obj3DPart statics[], Obj3D.Obj3DPart rotating[], float angle) {
        assert rotating.length > 0;
        for (Obj3D.Obj3DPart part : statics) {
            part.draw();
        }
        // TODO: Memoize this thing. Hopefully at the Obj3D level. Or something.
        final BoundingBox bb = rotating[0].boundingBox();
        final Vec3 centre = bb.centre();
        double ox = centre.xCoord,
                oy = centre.yCoord,
                oz = centre.zCoord;
        GL11.glTranslated(ox, oy, oz);
        GL11.glRotatef((float) (angle * 360 / 2 / Math.PI), 0, 0, 1);
        GL11.glTranslated(-ox, -oy, -oz);
        for (Obj3D.Obj3DPart part : rotating) {
            part.draw();
        }
    }
}
