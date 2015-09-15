package mods.eln.mechanical;

import mods.eln.misc.BoundingBox;
import mods.eln.misc.Direction;
import mods.eln.misc.Obj3D;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

/**
 * Created by svein on 15/09/15.
 */
public class ShaftDescriptor extends TransparentNodeDescriptor {
    protected final Obj3D obj;
    protected Obj3D.Obj3DPart[] statics;
    protected Obj3D.Obj3DPart[] rotating;
    public final float shaftWeight = 5;

    public ShaftDescriptor(String name, Class ElementClass, Class RenderClass, Obj3D obj) {
        super(name, ElementClass, RenderClass);
        this.obj = obj;
    }

    public void draw(float angle) {
        for (Obj3D.Obj3DPart part : statics) {
            part.draw();
        }
        drawShaft(rotating, angle);
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        objItemScale(obj);
        Direction.ZN.glRotateXnRef();
        GL11.glPushMatrix();
        GL11.glTranslatef(0, -1, 0);
        GL11.glScalef(0.6f, 0.6f, 0.6f);
        draw(0);
        GL11.glPopMatrix();
    }

    public void drawShaft(Obj3D.Obj3DPart rotating[], float angle) {
        assert rotating.length > 0;
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

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
                                         ItemRendererHelper helper) {
        return true;
    }

    @Override
    public boolean use2DIcon() {
        return false;
    }
}
