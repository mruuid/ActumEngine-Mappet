package unkn0wn.ae.api.scripts.mixins;

import mchorse.mappet.api.scripts.code.entities.ScriptEntity;
import mchorse.mappet.api.scripts.user.entities.IScriptEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import unkn0wn.ae.api.scripts.code.ScriptFancyWorld;
import unkn0wn.ae.api.scripts.user.IScriptFancyWorld;
import unkn0wn.ae.utils.mixins.utils.MixinTargetName;

@Mixin(ScriptEntity.class)
@MixinTargetName("mchorse.mappet.api.scripts.user.entity.IScriptEntity")
public abstract class MixinScriptEntity <T extends Entity> implements IScriptEntity {

    /**
     * Get entity's fancy world.
     *
     * <pre>{@code
     *    var s = c.getSubject();
     *    var fancyWorld = s.getFancyWorld();
     * }</pre>
     */
    @Shadow(remap = false) protected T entity;
    public IScriptFancyWorld getFancyWorld()
    {
        return new ScriptFancyWorld(this.entity.world);
    }
}
