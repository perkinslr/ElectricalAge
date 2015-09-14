package mods.eln.mechanical.steamturbine;

import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElementRender;
import mods.eln.node.transparent.TransparentNodeEntity;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by svein on 06/09/15.
 */
public class SteamTurbineRender extends mods.eln.mechanical.ShaftRender {

    public SteamTurbineRender(TransparentNodeEntity tileEntity, TransparentNodeDescriptor descriptor) {
        super(tileEntity, descriptor);
    }

}
