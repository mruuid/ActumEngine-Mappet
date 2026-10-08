package unkn0wn.ae.api.scripts.code.renderer;

import net.minecraft.entity.player.EntityPlayerMP;
import unkn0wn.ae.AEMod;
import unkn0wn.ae.api.scripts.user.renderer.IScriptCameraOver;

public class ScriptCameraOver implements IScriptCameraOver {
    private final EntityPlayerMP player;
    private AEMod camera;
    public ScriptCameraOver(EntityPlayerMP player) {
        this.player = player;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.camera.hasEyesHeight = enabled;
    }

    @Override
    public boolean isEnabled() {
        return this.camera.hasEyesHeight;
    }

    @Override
    public void setSneakAnimationEnabled(boolean enabled) { this.camera.sneakAnimationEnabled = enabled; }

    @Override
    public boolean isSneakAnimationEnabled() {  return  this.camera.sneakAnimationEnabled; }

    @Override
    public void setViewBobbingEnabled(boolean enabled) { this.camera.viewBobbingEnabled = enabled; }

    @Override
    public boolean isViewBobbingEnabled() { return this.camera.viewBobbingEnabled; }

    @Override
    public void setTiltEnabled(boolean enabled) { this.camera.tiltEnabled = enabled; }

    @Override
    public boolean isTiltEnabled() { return this.camera.tiltEnabled; }

    @Override
    public void setViewBobbingAmplitude(float set) { this.camera.viewBobbingAmplitude = set; }

    @Override
    public void setVerticalTiltFactor(float set) { this.camera.verticalTiltFactor = set; }

    @Override
    public void setForwardTiltFactor(float set) { this.camera.forwardTiltFactor = set; }

    @Override
    public void setRotationTiltFactor(float set) { this.camera.rotationTiltFactor = set; }

    @Override
    public void setStrafingTiltFactor(float set) { this.camera.strafingTiltFactor = set; }
}
