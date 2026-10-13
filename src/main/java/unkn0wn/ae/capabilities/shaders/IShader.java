package unkn0wn.ae.capabilities.shaders;

import net.minecraft.nbt.NBTTagCompound;

public interface IShader {
    boolean isEnabled();

    void setEnabled(boolean var1);

    float getRed();

    float getGreen();

    float getBlue();

    float getAlpha();

    void setColor(float var1, float var2, float var3, float var4);

    float getContrast();

    void setContrast(float var1);

    float getIntensity();

    void setIntensity(float var1);

    long getFadeInDuration();

    long getFadeOutDuration();

    void setFadeIn(long var1);

    void setFadeOut(long var1);

    long getFadeStartTime();

    boolean isFadingIn();

    boolean isFadingOut();

    void startFadeIn();

    void startFadeOut();

    NBTTagCompound serializeNBT();

    void deserializeNBT(NBTTagCompound var1);
}
