package unkn0wn.ae.api.scripts.mixins;

import mchorse.mappet.api.scripts.code.ScriptEvent;
import mchorse.mappet.api.scripts.user.IScriptEvent;
import mchorse.mappet.api.utils.DataContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import unkn0wn.ae.api.scripts.code.ScriptFancyWorld;
import unkn0wn.ae.api.scripts.user.IScriptFancyWorld;
import unkn0wn.ae.utils.mixins.utils.MixinTargetName;

@Mixin(ScriptEvent.class)
@MixinTargetName("mchorse.mappet.api.scripts.user.IScriptEvent")
public abstract class MixinScriptEvent implements IScriptEvent {
    @Shadow(remap = false) private DataContext context;
    public IScriptFancyWorld fancyWorld;

    /**
     * Get the fancy world in which this event happened.
     */
    public IScriptFancyWorld getFancyWorld() {
        if (this.fancyWorld == null && this.context.world != null) {
            this.fancyWorld = new ScriptFancyWorld(this.context.world);
        }

        return this.fancyWorld;
    }
}
