package unkn0wn.ae.api.scripts.mixins;

import mchorse.mappet.api.scripts.code.entities.ScriptEntity;
import mchorse.mappet.api.scripts.user.entities.IScriptEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import unkn0wn.ae.api.scripts.code.ScriptFancyWorld;
import unkn0wn.ae.api.scripts.user.IScriptFancyWorld;
import unkn0wn.ae.utils.mixins.utils.MixinTargetName;

@Mixin(ScriptEntity.class)
@MixinTargetName("mchorse.mappet.api.scripts.user.entity.IScriptEntity")
public abstract class MixinScriptEntity <T extends Entity> implements IScriptEntity {
    @Shadow(remap = false) protected T entity;

    /**
     * Get entity's fancy world.
     *
     * <pre>{@code
     *    var s = c.getSubject();
     *    var fancyWorld = s.getFancyWorld();
     * }</pre>
     */
    public IScriptFancyWorld getFancyWorld()
    {
        return new ScriptFancyWorld(this.entity.world);
    }

    public boolean isObservable(IScriptEntity object) {
        World world = this.entity.world;
        Vec3d subjectPos = new Vec3d(this.entity.posX, this.entity.posY + (double)this.entity.getEyeHeight(), this.entity.posZ);
        Vec3d objectPos = new Vec3d(object.getPosition().x, object.getPosition().y + (double)object.getEyeHeight(), object.getPosition().z);
        RayTraceResult trace = world.rayTraceBlocks(subjectPos, objectPos, false, true, false);
        if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos hitPos = trace.getBlockPos();
            if (!world.isAirBlock(hitPos)) {
                return false;
            }
        }

        double deltaX = object.getPosition().x - this.entity.posX;
        double deltaZ = object.getPosition().z - this.entity.posZ;
        double yawToTarget = Math.atan2(deltaZ, deltaX) * (double)180.0F / Math.PI - (double)90.0F;
        double deltaY = this.entity.posY - object.getPosition().y;
        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        double pitchToTarget = Math.atan2(deltaY, distance) * (double)180.0F / Math.PI;
        float subjectYaw = this.entity.rotationYaw % 360.0F;
        float subjectPitch = this.entity.rotationPitch;
        float normalizedSubjectYaw = (subjectYaw + 360.0F) % 360.0F;
        float normalizedTargetYaw = ((float)yawToTarget + 360.0F) % 360.0F;
        float yawDiff = Math.abs(normalizedSubjectYaw - normalizedTargetYaw);
        if (yawDiff > 180.0F) {
            yawDiff = 360.0F - yawDiff;
        }

        float pitchDiff = Math.abs(subjectPitch - (float)pitchToTarget);
        return !(yawDiff > 65.0F) && !(pitchDiff > 55.0F);
    }
}
