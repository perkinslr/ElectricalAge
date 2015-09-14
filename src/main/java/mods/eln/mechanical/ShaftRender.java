package mods.eln.mechanical;

import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElementRender;
import mods.eln.node.transparent.TransparentNodeEntity;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by svein on 15/09/15.
 */
public class ShaftRender extends TransparentNodeElementRender {
    protected final ShaftDescriptor desc;
    float rads = 0;
    float angle = 0;

    public ShaftRender(TransparentNodeEntity tileEntity, TransparentNodeDescriptor descriptor) {
        super(tileEntity, descriptor);
        this.desc = (ShaftDescriptor)descriptor;
    }

    @Override
    public void draw() {
        front.glRotateXnRef();
        desc.draw(angle);
    }

    @Override
    public void refresh(float deltaT) {
        super.refresh(deltaT);
        angle += rads * deltaT;
    }

    @Override
    public void networkUnserialize(DataInputStream stream) {
        super.networkUnserialize(stream);
        try {
            rads = (float) (Math.log(stream.readFloat() + 1) / Math.log(1.2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
