package unkn0wn.ae.api.scripts.user.renderer;

public interface IScriptCameraOver {
    void setEnabled(boolean enabled);
    boolean isEnabled();

    void setSneakAnimationEnabled(boolean enabled);
    boolean isSneakAnimationEnabled();

    void setViewBobbingEnabled(boolean enabled);
    boolean isViewBobbingEnabled();

    void setTiltEnabled(boolean enabled);
    boolean isTiltEnabled();

    void setViewBobbingAmplitude(float set);

    void setVerticalTiltFactor(float set);

    void setForwardTiltFactor(float set);

    void setRotationTiltFactor(float set);

    void setStrafingTiltFactor(float set);
}
