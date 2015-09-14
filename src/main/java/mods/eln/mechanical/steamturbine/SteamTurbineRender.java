package mods.eln.mechanical.steamturbine;

import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElementRender;
import mods.eln.node.transparent.TransparentNodeEntity;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by svein on 06/09/15.
 */
public class SteamTurbineRender extends TransparentNodeElementRender {

    private final SteamTurbineDescriptor desc;

    float rads = 0;
    float angle = 0;

    public SteamTurbineRender(TransparentNodeEntity tileEntity, TransparentNodeDescriptor descriptor) {
        super(tileEntity, descriptor);
        this.desc = (SteamTurbineDescriptor)descriptor;
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

    // TODO: Factor out a common render class.
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
