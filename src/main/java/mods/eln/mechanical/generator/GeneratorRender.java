package mods.eln.mechanical.generator;

import mods.eln.mechanical.ShaftRender;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElementRender;
import mods.eln.node.transparent.TransparentNodeEntity;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by svein on 08/09/15.
 */
public class GeneratorRender extends ShaftRender {
    public GeneratorRender(TransparentNodeEntity tileEntity, TransparentNodeDescriptor descriptor) {
        super(tileEntity, descriptor);
    }
}
