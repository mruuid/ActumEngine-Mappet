package unkn0wn.ae.utils.mixins.client;

import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mclib.client.gui.mclib.GuiAbstractDashboard;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unkn0wn.ae.client.gui.panels.ShadersPanel;
import unkn0wn.ae.utils.GuiMappetDashboardAccessor;

@Mixin(value = GuiMappetDashboard.class, remap = false)
public abstract class MixinGuiMappetDashboard extends GuiAbstractDashboard implements GuiMappetDashboardAccessor {
    public ShadersPanel shadersPanel;

    public MixinGuiMappetDashboard(Minecraft mc) { super(mc); }

    @Inject(method = "registerPanels", at = @At("TAIL"), remap = false)
    public void registerPanels(Minecraft mc, CallbackInfo ci) {
        this.shadersPanel = new ShadersPanel(mc, (GuiMappetDashboard) (Object) this);
        this.panels.registerPanel(this.shadersPanel, IKey.lang("ae.gui.panels.shaders"), Icons.BLOCK);
    }
    @Override
    public ShadersPanel getShadersPanel() { return this.shadersPanel; }
}
