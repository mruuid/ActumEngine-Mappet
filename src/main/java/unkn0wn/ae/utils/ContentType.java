package unkn0wn.ae.utils;

import mchorse.mappet.api.utils.AbstractData;
import mchorse.mappet.api.utils.IContentType;
import mchorse.mappet.api.utils.manager.IManager;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.panels.GuiMappetDashboardPanel;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import unkn0wn.ae.AEMod;

public enum ContentType implements IContentType {
    SHADERS {
        @Override
        public IManager<? extends AbstractData> getManager() { return AEMod.getShadersManager(); }

        @Override
        @SideOnly(Side.CLIENT)
        public GuiMappetDashboardPanel get(GuiMappetDashboard dashboard) {
            return ((GuiMappetDashboardAccessor) dashboard).getShadersPanel();
        }

        @Override
        @SideOnly(Side.CLIENT)
        public IKey getPickLabel() { return IKey.lang("ae.gui.overlays.shaders"); }

        @Override
        public String getName() { return "SHADERS"; }
    }
}
