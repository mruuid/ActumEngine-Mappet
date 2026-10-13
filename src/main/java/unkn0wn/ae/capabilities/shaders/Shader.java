package unkn0wn.ae.capabilities.shaders;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Shader implements IShader{
    private EntityPlayer player;
    public boolean enabled = false;
    public float red = 0.0F;
    public float green = 0.0F;
    public float blue = 0.0F;
    public float alpha = 1.0F;
    public float saturation = 1.0F;
    public float contrast = 1.0F;
    public float intensity = 1.0F;
    public float startRed = 0.0F;
    public float startGreen = 0.0F;
    public float startBlue = 0.0F;
    public float startAlpha = 0.0F;
    public float startSaturation = 1.0F;
    public float startContrast = 1.0F;
    public float startIntensity = 1.0F;
    public float currentRed = 0.0F;
    public float currentGreen = 0.0F;
    public float currentBlue = 0.0F;
    public float currentAlpha = 0.0F;
    public float currentSaturation = 1.0F;
    public float currentContrast = 1.0F;
    public float currentIntensity = 1.0F;
    public int fadeInTicks = 0;
    public int fadeOutTicks = 0;
    public long fadeInMs = 0L;
    public long fadeOutMs = 0L;
    public long fadeStartTime = -1L;
    public boolean fadingIn = false;
    public boolean fadingOut = false;
    public final List<ShaderLayer> layers = new ArrayList();
    public boolean vignetteEnabled = false;
    public float vignetteStrength = 0.0F;
    public float vignetteRadius = 0.5F;
    public float vignetteSoftness = 0.5F;
    public float vignetteRed = 0.0F;
    public float vignetteGreen = 0.0F;
    public float vignetteBlue = 0.0F;
    public float vignetteAlpha = 0.0F;
    public float vignetteSaturation = 1.0F;
    public float vignetteContrast = 1.0F;
    public float vignetteIntensity = 1.0F;
    public long vignetteFadeStartTime = -1L;
    public long vignetteFadeInMs = 0L;
    public long vignetteFadeOutMs = 0L;
    public boolean vignetteFadingIn = false;
    public boolean vignetteFadingOut = false;
    public float vignetteStartStrength;
    public float vignetteStartRadius;
    public float vignetteStartSoftness;
    public float vignetteStartRed;
    public float vignetteStartGreen;
    public float vignetteStartBlue;
    public float vignetteStartAlpha;
    public float vignetteStartSaturation;
    public float vignetteStartContrast;
    public float vignetteStartIntensity;
    public float flashIntensity = 0.0F;
    public int flashRemainingTicks = 0;
    public float flashRed = 1.0F;
    public float flashGreen = 1.0F;
    public float flashBlue = 1.0F;
    public final List<ShaderLightSource> lightSources = new ArrayList();

    public Shader() {
    }

    public static Shader get(EntityPlayer player) {
        IShader cap = player == null ? null : (IShader)player.getCapability(ShaderProvider.SHADER, (EnumFacing)null);
        if (cap instanceof Shader) {
            Shader s = (Shader)cap;
            s.player = player;
            return s;
        } else {
            return null;
        }
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public ShaderLayer getLayer(int priority) {
        for(ShaderLayer l : this.layers) {
            if (l.priority == priority) {
                return l;
            }
        }

        return null;
    }

    public ShaderLayer putLayer(int priority) {
        ShaderLayer layer = this.getLayer(priority);
        if (layer == null) {
            layer = new ShaderLayer(priority);
            this.layers.add(layer);
            this.layers.sort(Comparator.comparingInt((l) -> l.priority));
        }

        layer.enabled = true;
        layer.fadingOut = false;
        layer.fadeStartTime = -1L;
        return layer;
    }

    public void removeLayer(int priority, int fadeOutTicks) {
        ShaderLayer layer = this.getLayer(priority);
        if (layer != null) {
            if (fadeOutTicks <= 0) {
                this.layers.remove(layer);
            } else {
                layer.fadingOut = true;
                layer.fadingIn = false;
                layer.fadeOutTicks = fadeOutTicks;
                layer.fadeOutMs = (long)fadeOutTicks * 50L;
                layer.fadeStartTime = -1L;
                layer.startRed = layer.red;
                layer.startGreen = layer.green;
                layer.startBlue = layer.blue;
                layer.startAlpha = layer.alpha;
                layer.startSaturation = layer.saturation;
                layer.startContrast = layer.contrast;
                layer.startIntensity = layer.intensity;
            }

        }
    }

    public void clearLayers() {
        this.layers.clear();
    }

    public boolean hasAnythingToRender() {
        if (this.enabled) {
            return true;
        } else if (this.vignetteEnabled) {
            return true;
        } else if (this.vignetteFadingOut && this.vignetteStartStrength > 0.001F) {
            return true;
        } else if (this.flashRemainingTicks > 0 && this.flashIntensity > 0.01F) {
            return true;
        } else {
            for(ShaderLayer l : this.layers) {
                if (l.enabled) {
                    if (!l.fadingIn && !l.fadingOut) {
                        if (l.currentAlpha > 0.001F) {
                            return true;
                        }

                        if (!(l.currentSaturation < 0.999F) && !(l.currentContrast < 0.999F) && !(l.currentContrast > 1.001F)) {
                            if (l.currentIntensity < 0.999F) {
                                return true;
                            }
                            continue;
                        }

                        return true;
                    }

                    return true;
                }
            }

            return false;
        }
    }

    public float[] blendLayers(float baseR, float baseG, float baseB, float baseA, float baseSat, float baseCon, float baseIns) {
        float r = baseR;
        float g = baseG;
        float b = baseB;
        float a = baseA;
        float sat = baseSat;
        float con = baseCon;
        float ins = baseIns;
        List<ShaderLayer> toRemove = new ArrayList();

        for(ShaderLayer layer : this.layers) {
            boolean alive = layer.tick();
            if (!alive) {
                toRemove.add(layer);
            } else {
                float w = layer.currentAlpha;
                if (!(w <= 0.001F)) {
                    r = lerp(r, layer.currentRed, w);
                    g = lerp(g, layer.currentGreen, w);
                    b = lerp(b, layer.currentBlue, w);
                    a += w * (1.0F - a);
                    sat = lerp(sat, layer.currentSaturation, w);
                    con = lerp(con, layer.currentContrast, w);
                    ins = lerp(ins, layer.currentIntensity, w);
                }
            }
        }

        this.layers.removeAll(toRemove);
        return new float[]{Math.min(1.0F, r), Math.min(1.0F, g), Math.min(1.0F, b), Math.min(1.0F, a), sat, con, ins};
    }

    public NBTTagCompound serializeFlashNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("flashIsNew", true);
        tag.setFloat("flashIntensity", this.flashIntensity);
        tag.setInteger("flashRemainingTicks", this.flashRemainingTicks);
        tag.setFloat("flashRed", this.flashRed);
        tag.setFloat("flashGreen", this.flashGreen);
        tag.setFloat("flashBlue", this.flashBlue);
        return tag;
    }

    public void resetShaderParameters() {
        this.enabled = false;
        this.red = 0.0F;
        this.green = 0.0F;
        this.blue = 0.0F;
        this.alpha = 0.0F;
        this.saturation = 1.0F;
        this.contrast = 1.0F;
        this.intensity = 1.0F;
        this.startRed = 0.0F;
        this.startGreen = 0.0F;
        this.startBlue = 0.0F;
        this.startAlpha = 0.0F;
        this.startSaturation = 1.0F;
        this.startContrast = 1.0F;
        this.startIntensity = 1.0F;
        this.currentRed = 0.0F;
        this.currentGreen = 0.0F;
        this.currentBlue = 0.0F;
        this.currentAlpha = 0.0F;
        this.currentSaturation = 1.0F;
        this.currentContrast = 1.0F;
        this.currentIntensity = 1.0F;
        this.fadingIn = false;
        this.fadingOut = false;
        this.fadeStartTime = -1L;
    }

    public void resetVignetteParameters() {
        this.vignetteEnabled = false;
        this.vignetteStrength = 0.0F;
        this.vignetteRadius = 0.5F;
        this.vignetteSoftness = 0.5F;
        this.vignetteRed = 0.0F;
        this.vignetteGreen = 0.0F;
        this.vignetteBlue = 0.0F;
        this.vignetteAlpha = 0.0F;
        this.vignetteSaturation = 1.0F;
        this.vignetteContrast = 1.0F;
        this.vignetteIntensity = 1.0F;
        this.vignetteFadingIn = false;
        this.vignetteFadingOut = false;
        this.vignetteFadeStartTime = -1L;
        this.vignetteStartStrength = 0.0F;
        this.vignetteStartRadius = 0.5F;
        this.vignetteStartSoftness = 0.5F;
        this.vignetteStartRed = 0.0F;
        this.vignetteStartGreen = 0.0F;
        this.vignetteStartBlue = 0.0F;
        this.vignetteStartAlpha = 0.0F;
        this.vignetteStartSaturation = 1.0F;
        this.vignetteStartContrast = 1.0F;
        this.vignetteStartIntensity = 1.0F;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean e) {
        this.enabled = e;
    }

    public float getRed() {
        return this.red;
    }

    public float getGreen() {
        return this.green;
    }

    public float getBlue() {
        return this.blue;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public void setColor(float r, float g, float b, float alpha) {
        this.red = r;
        this.green = g;
        this.blue = b;
        this.alpha = alpha;
    }

    public float getContrast() {
        return this.contrast;
    }

    public void setContrast(float v) {
        this.contrast = v;
    }

    public float getIntensity() {
        return this.intensity;
    }

    public void setIntensity(float v) {
        this.intensity = Math.max(0.0F, Math.min(1.0F, v));
    }

    public long getFadeInDuration() {
        return this.fadeInMs;
    }

    public long getFadeOutDuration() {
        return this.fadeOutMs;
    }

    public void setFadeIn(long ms) {
        this.fadeInTicks = (int)(ms / 50L);
        this.fadeInMs = ms;
    }

    public void setFadeOut(long ms) {
        this.fadeOutTicks = (int)(ms / 50L);
        this.fadeOutMs = ms;
    }

    public long getFadeStartTime() {
        return 0L;
    }

    public boolean isFadingIn() {
        return this.fadingIn;
    }

    public boolean isFadingOut() {
        return this.fadingOut;
    }

    public void startFadeIn() {
    }

    public void startFadeOut() {
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("enabled", this.enabled);
        tag.setFloat("red", this.red);
        tag.setFloat("green", this.green);
        tag.setFloat("blue", this.blue);
        tag.setFloat("alpha", this.alpha);
        tag.setFloat("saturation", this.saturation);
        tag.setFloat("contrast", this.contrast);
        tag.setFloat("intensity", this.intensity);
        tag.setFloat("startRed", this.startRed);
        tag.setFloat("startGreen", this.startGreen);
        tag.setFloat("startBlue", this.startBlue);
        tag.setFloat("startAlpha", this.startAlpha);
        tag.setFloat("startSaturation", this.startSaturation);
        tag.setFloat("startContrast", this.startContrast);
        tag.setFloat("startIntensity", this.startIntensity);
        tag.setFloat("currentRed", this.currentRed);
        tag.setFloat("currentGreen", this.currentGreen);
        tag.setFloat("currentBlue", this.currentBlue);
        tag.setFloat("currentAlpha", this.currentAlpha);
        tag.setFloat("currentSaturation", this.currentSaturation);
        tag.setFloat("currentContrast", this.currentContrast);
        tag.setFloat("currentIntensity", this.currentIntensity);
        tag.setInteger("fadeInTicks", this.fadeInTicks);
        tag.setInteger("fadeOutTicks", this.fadeOutTicks);
        tag.setLong("fadeInMs", this.fadeInMs);
        tag.setLong("fadeOutMs", this.fadeOutMs);
        tag.setBoolean("fadingIn", this.fadingIn);
        tag.setBoolean("fadingOut", this.fadingOut);
        NBTTagList layerList = new NBTTagList();

        for(ShaderLayer l : this.layers) {
            NBTTagCompound lt = new NBTTagCompound();
            lt.setInteger("priority", l.priority);
            lt.setFloat("red", l.red);
            lt.setFloat("green", l.green);
            lt.setFloat("blue", l.blue);
            lt.setFloat("alpha", l.alpha);
            lt.setFloat("saturation", l.saturation);
            lt.setFloat("contrast", l.contrast);
            lt.setFloat("intensity", l.intensity);
            lt.setFloat("startRed", l.startRed);
            lt.setFloat("startGreen", l.startGreen);
            lt.setFloat("startBlue", l.startBlue);
            lt.setFloat("startAlpha", l.startAlpha);
            lt.setFloat("startSaturation", l.startSaturation);
            lt.setFloat("startContrast", l.startContrast);
            lt.setFloat("startIntensity", l.startIntensity);
            lt.setFloat("currentRed", l.currentRed);
            lt.setFloat("currentGreen", l.currentGreen);
            lt.setFloat("currentBlue", l.currentBlue);
            lt.setFloat("currentAlpha", l.currentAlpha);
            lt.setFloat("currentSaturation", l.currentSaturation);
            lt.setFloat("currentContrast", l.currentContrast);
            lt.setFloat("currentIntensity", l.currentIntensity);
            lt.setInteger("fadeInTicks", l.fadeInTicks);
            lt.setInteger("fadeOutTicks", l.fadeOutTicks);
            lt.setLong("fadeInMs", l.fadeInMs);
            lt.setLong("fadeOutMs", l.fadeOutMs);
            lt.setBoolean("fadingIn", l.fadingIn);
            lt.setBoolean("fadingOut", l.fadingOut);
            lt.setBoolean("enabled", l.enabled);
            layerList.appendTag(lt);
        }

        tag.setTag("layers", layerList);
        tag.setBoolean("vignetteEnabled", this.vignetteEnabled);
        tag.setFloat("vignetteStrength", this.vignetteStrength);
        tag.setFloat("vignetteRadius", this.vignetteRadius);
        tag.setFloat("vignetteSoftness", this.vignetteSoftness);
        tag.setFloat("vignetteRed", this.vignetteRed);
        tag.setFloat("vignetteGreen", this.vignetteGreen);
        tag.setFloat("vignetteBlue", this.vignetteBlue);
        tag.setFloat("vignetteAlpha", this.vignetteAlpha);
        tag.setFloat("vignetteSaturation", this.vignetteSaturation);
        tag.setFloat("vignetteContrast", this.vignetteContrast);
        tag.setFloat("vignetteIntensity", this.vignetteIntensity);
        tag.setLong("vignetteFadeInMs", this.vignetteFadeInMs);
        tag.setLong("vignetteFadeOutMs", this.vignetteFadeOutMs);
        tag.setBoolean("vignetteFadingIn", this.vignetteFadingIn);
        tag.setBoolean("vignetteFadingOut", this.vignetteFadingOut);
        tag.setFloat("vignetteStartStrength", this.vignetteStartStrength);
        tag.setFloat("vignetteStartRadius", this.vignetteStartRadius);
        tag.setFloat("vignetteStartSoftness", this.vignetteStartSoftness);
        tag.setFloat("vignetteStartRed", this.vignetteStartRed);
        tag.setFloat("vignetteStartGreen", this.vignetteStartGreen);
        tag.setFloat("vignetteStartBlue", this.vignetteStartBlue);
        tag.setFloat("vignetteStartAlpha", this.vignetteStartAlpha);
        tag.setFloat("vignetteStartSaturation", this.vignetteStartSaturation);
        tag.setFloat("vignetteStartContrast", this.vignetteStartContrast);
        tag.setFloat("vignetteStartIntensity", this.vignetteStartIntensity);
        return tag;
    }

    public void deserializeNBT(NBTTagCompound tag) {
        if (tag.hasKey("enabled")) {
            this.enabled = tag.getBoolean("enabled");
        }

        if (tag.hasKey("red")) {
            this.red = tag.getFloat("red");
        }

        if (tag.hasKey("green")) {
            this.green = tag.getFloat("green");
        }

        if (tag.hasKey("blue")) {
            this.blue = tag.getFloat("blue");
        }

        if (tag.hasKey("alpha")) {
            this.alpha = tag.getFloat("alpha");
        }

        if (tag.hasKey("saturation")) {
            this.saturation = tag.getFloat("saturation");
        }

        if (tag.hasKey("contrast")) {
            this.contrast = tag.getFloat("contrast");
        }

        if (tag.hasKey("intensity")) {
            this.intensity = tag.getFloat("intensity");
        }

        if (tag.hasKey("startRed")) {
            this.startRed = tag.getFloat("startRed");
        }

        if (tag.hasKey("startGreen")) {
            this.startGreen = tag.getFloat("startGreen");
        }

        if (tag.hasKey("startBlue")) {
            this.startBlue = tag.getFloat("startBlue");
        }

        if (tag.hasKey("startAlpha")) {
            this.startAlpha = tag.getFloat("startAlpha");
        }

        if (tag.hasKey("startSaturation")) {
            this.startSaturation = tag.getFloat("startSaturation");
        }

        if (tag.hasKey("startContrast")) {
            this.startContrast = tag.getFloat("startContrast");
        }

        if (tag.hasKey("startIntensity")) {
            this.startIntensity = tag.getFloat("startIntensity");
        }

        if (tag.hasKey("currentRed")) {
            this.currentRed = tag.getFloat("currentRed");
        }

        if (tag.hasKey("currentGreen")) {
            this.currentGreen = tag.getFloat("currentGreen");
        }

        if (tag.hasKey("currentBlue")) {
            this.currentBlue = tag.getFloat("currentBlue");
        }

        if (tag.hasKey("currentAlpha")) {
            this.currentAlpha = tag.getFloat("currentAlpha");
        }

        if (tag.hasKey("currentSaturation")) {
            this.currentSaturation = tag.getFloat("currentSaturation");
        }

        if (tag.hasKey("currentContrast")) {
            this.currentContrast = tag.getFloat("currentContrast");
        }

        if (tag.hasKey("currentIntensity")) {
            this.currentIntensity = tag.getFloat("currentIntensity");
        }

        if (tag.hasKey("fadeInTicks")) {
            this.fadeInTicks = tag.getInteger("fadeInTicks");
        }

        if (tag.hasKey("fadeOutTicks")) {
            this.fadeOutTicks = tag.getInteger("fadeOutTicks");
        }

        if (tag.hasKey("fadeInMs")) {
            this.fadeInMs = tag.getLong("fadeInMs");
        }

        if (tag.hasKey("fadeOutMs")) {
            this.fadeOutMs = tag.getLong("fadeOutMs");
        }

        if (tag.hasKey("fadingIn")) {
            this.fadingIn = tag.getBoolean("fadingIn");
        }

        if (tag.hasKey("fadingOut")) {
            this.fadingOut = tag.getBoolean("fadingOut");
        }

        this.fadeStartTime = -1L;
        this.layers.clear();
        if (tag.hasKey("layers")) {
            NBTTagList list = tag.getTagList("layers", 10);

            for(int i = 0; i < list.tagCount(); ++i) {
                NBTTagCompound lt = list.getCompoundTagAt(i);
                ShaderLayer l = new ShaderLayer(lt.getInteger("priority"));
                l.red = lt.getFloat("red");
                l.green = lt.getFloat("green");
                l.blue = lt.getFloat("blue");
                l.alpha = lt.getFloat("alpha");
                l.saturation = lt.getFloat("saturation");
                l.contrast = lt.getFloat("contrast");
                l.intensity = lt.getFloat("intensity");
                l.startRed = lt.getFloat("startRed");
                l.startGreen = lt.getFloat("startGreen");
                l.startBlue = lt.getFloat("startBlue");
                l.startAlpha = lt.getFloat("startAlpha");
                l.startSaturation = lt.getFloat("startSaturation");
                l.startContrast = lt.getFloat("startContrast");
                l.startIntensity = lt.getFloat("startIntensity");
                l.currentRed = lt.getFloat("currentRed");
                l.currentGreen = lt.getFloat("currentGreen");
                l.currentBlue = lt.getFloat("currentBlue");
                l.currentAlpha = lt.getFloat("currentAlpha");
                l.currentSaturation = lt.getFloat("currentSaturation");
                l.currentContrast = lt.getFloat("currentContrast");
                l.currentIntensity = lt.getFloat("currentIntensity");
                l.fadeInTicks = lt.getInteger("fadeInTicks");
                l.fadeOutTicks = lt.getInteger("fadeOutTicks");
                l.fadeInMs = lt.getLong("fadeInMs");
                l.fadeOutMs = lt.getLong("fadeOutMs");
                l.fadingIn = lt.getBoolean("fadingIn");
                l.fadingOut = lt.getBoolean("fadingOut");
                l.enabled = lt.getBoolean("enabled");
                l.fadeStartTime = -1L;
                this.layers.add(l);
            }

            this.layers.sort(Comparator.comparingInt((ll) -> ll.priority));
        }

        if (tag.hasKey("vignetteEnabled")) {
            this.vignetteEnabled = tag.getBoolean("vignetteEnabled");
        }

        if (tag.hasKey("vignetteStrength")) {
            this.vignetteStrength = tag.getFloat("vignetteStrength");
        }

        if (tag.hasKey("vignetteRadius")) {
            this.vignetteRadius = tag.getFloat("vignetteRadius");
        }

        if (tag.hasKey("vignetteSoftness")) {
            this.vignetteSoftness = tag.getFloat("vignetteSoftness");
        }

        if (tag.hasKey("vignetteRed")) {
            this.vignetteRed = tag.getFloat("vignetteRed");
        }

        if (tag.hasKey("vignetteGreen")) {
            this.vignetteGreen = tag.getFloat("vignetteGreen");
        }

        if (tag.hasKey("vignetteBlue")) {
            this.vignetteBlue = tag.getFloat("vignetteBlue");
        }

        if (tag.hasKey("vignetteAlpha")) {
            this.vignetteAlpha = tag.getFloat("vignetteAlpha");
        }

        if (tag.hasKey("vignetteSaturation")) {
            this.vignetteSaturation = tag.getFloat("vignetteSaturation");
        }

        if (tag.hasKey("vignetteContrast")) {
            this.vignetteContrast = tag.getFloat("vignetteContrast");
        }

        if (tag.hasKey("vignetteIntensity")) {
            this.vignetteIntensity = tag.getFloat("vignetteIntensity");
        }

        if (tag.hasKey("vignetteFadeInMs")) {
            this.vignetteFadeInMs = tag.getLong("vignetteFadeInMs");
        }

        if (tag.hasKey("vignetteFadeOutMs")) {
            this.vignetteFadeOutMs = tag.getLong("vignetteFadeOutMs");
        }

        if (tag.hasKey("vignetteFadingIn")) {
            this.vignetteFadingIn = tag.getBoolean("vignetteFadingIn");
        }

        if (tag.hasKey("vignetteFadingOut")) {
            this.vignetteFadingOut = tag.getBoolean("vignetteFadingOut");
        }

        if (tag.hasKey("vignetteStartStrength")) {
            this.vignetteStartStrength = tag.getFloat("vignetteStartStrength");
        }

        if (tag.hasKey("vignetteStartRadius")) {
            this.vignetteStartRadius = tag.getFloat("vignetteStartRadius");
        }

        if (tag.hasKey("vignetteStartSoftness")) {
            this.vignetteStartSoftness = tag.getFloat("vignetteStartSoftness");
        }

        if (tag.hasKey("vignetteStartRed")) {
            this.vignetteStartRed = tag.getFloat("vignetteStartRed");
        }

        if (tag.hasKey("vignetteStartGreen")) {
            this.vignetteStartGreen = tag.getFloat("vignetteStartGreen");
        }

        if (tag.hasKey("vignetteStartBlue")) {
            this.vignetteStartBlue = tag.getFloat("vignetteStartBlue");
        }

        if (tag.hasKey("vignetteStartAlpha")) {
            this.vignetteStartAlpha = tag.getFloat("vignetteStartAlpha");
        }

        if (tag.hasKey("vignetteStartSaturation")) {
            this.vignetteStartSaturation = tag.getFloat("vignetteStartSaturation");
        }

        if (tag.hasKey("vignetteStartContrast")) {
            this.vignetteStartContrast = tag.getFloat("vignetteStartContrast");
        }

        if (tag.hasKey("vignetteStartIntensity")) {
            this.vignetteStartIntensity = tag.getFloat("vignetteStartIntensity");
        }

        this.vignetteFadeStartTime = -1L;
        if (tag.hasKey("flashIsNew") && tag.getBoolean("flashIsNew")) {
            if (tag.hasKey("flashIntensity")) {
                this.flashIntensity = tag.getFloat("flashIntensity");
            }

            if (tag.hasKey("flashRemainingTicks")) {
                this.flashRemainingTicks = tag.getInteger("flashRemainingTicks");
            }

            if (tag.hasKey("flashRed")) {
                this.flashRed = tag.getFloat("flashRed");
            }

            if (tag.hasKey("flashGreen")) {
                this.flashGreen = tag.getFloat("flashGreen");
            }

            if (tag.hasKey("flashBlue")) {
                this.flashBlue = tag.getFloat("flashBlue");
            }
        }

    }
}
