package mods.eln.transparentnode.generator;

import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElementRender;
import mods.eln.node.transparent.TransparentNodeEntity;
import mods.eln.node.transparent.TransparentNodeRender;
import mods.eln.transparentnode.steamturbine.SteamTurbineDescriptor;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by svein on 08/09/15.
 */
public class GeneratorRender extends TransparentNodeElementRender {
    private final GeneratorDescriptor desc;

    float rads = 0;
    float angle = 1;

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

    public GeneratorRender(TransparentNodeEntity tileEntity, TransparentNodeDescriptor descriptor) {
        super(tileEntity, descriptor);
        this.desc = (GeneratorDescriptor) descriptor;
    }
}
