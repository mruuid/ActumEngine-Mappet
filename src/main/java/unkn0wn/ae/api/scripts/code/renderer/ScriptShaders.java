package unkn0wn.ae.api.scripts.code.renderer;

import mchorse.mappet.network.Dispatcher;
import net.minecraft.entity.player.EntityPlayerMP;
import unkn0wn.ae.AEMod;
import unkn0wn.ae.api.scripts.user.renderer.IScriptShaders;
import unkn0wn.ae.api.shaders.ShaderData;
import unkn0wn.ae.capabilities.CapabilitiesType;
import unkn0wn.ae.capabilities.shaders.Shader;
import unkn0wn.ae.network.packets.PacketCapability;
import unkn0wn.ae.network.packets.PacketScriptShader;

/**
 * Client-side implementation of {@link IScriptShaders} — a thin wrapper over
 * the named-shader bookkeeping in {@link ShaderRenderer}.
 */
public class ScriptShaders implements IScriptShaders {
    private final EntityPlayerMP player;

    public ScriptShaders(EntityPlayerMP player) {
        this.player = player;
    }

    private Shader getShader() {
        return Shader.get(this.player);
    }

    @Override
    public void setShaders(double r, double g, double b, double alpha, int fadeTicks, double saturation, double contrast, double intensity, boolean enabled) {
        Shader shader = this.getShader();
        if (shader != null) {
            shader.red = (float)Math.min((double)255.0F, Math.max((double)0.0F, r)) / 255.0F;
            shader.green = (float)Math.min((double)255.0F, Math.max((double)0.0F, g)) / 255.0F;
            shader.blue = (float)Math.min((double)255.0F, Math.max((double)0.0F, b)) / 255.0F;
            shader.alpha = (float)Math.min((double)1.0F, Math.max((double)0.0F, alpha));
            shader.saturation = (float)Math.min((double)2.0F, Math.max((double)0.0F, saturation));
            shader.contrast = (float)Math.min((double)2.0F, Math.max((double)0.0F, contrast));
            shader.intensity = (float)Math.min((double)1.0F, Math.max((double)0.0F, intensity));
            long fadeMs = (long)fadeTicks * 50L;
            if (enabled) {
                shader.enabled = true;
                if (fadeMs <= 0L) {
                    shader.fadingIn = false;
                    shader.fadingOut = false;
                    shader.currentRed = shader.red;
                    shader.currentGreen = shader.green;
                    shader.currentBlue = shader.blue;
                    shader.currentAlpha = shader.alpha;
                    shader.currentSaturation = shader.saturation;
                    shader.currentContrast = shader.contrast;
                    shader.currentIntensity = shader.intensity;
                } else {
                    shader.fadeInMs = fadeMs;
                    shader.fadeOutMs = fadeMs;
                    shader.fadingIn = true;
                    shader.fadingOut = false;
                    shader.fadeStartTime = System.currentTimeMillis();
                    shader.startRed = shader.red;
                    shader.startGreen = shader.green;
                    shader.startBlue = shader.blue;
                    shader.startAlpha = 0.0F;
                    shader.startSaturation = 1.0F;
                    shader.startContrast = 1.0F;
                    shader.startIntensity = 1.0F;
                    shader.currentAlpha = 0.0F;
                    shader.currentSaturation = 1.0F;
                    shader.currentContrast = 1.0F;
                    shader.currentIntensity = 1.0F;
                }
            } else if (fadeMs <= 0L) {
                shader.resetShaderParameters();
            } else {
                shader.fadeOutMs = fadeMs;
                shader.fadingOut = true;
                shader.fadingIn = false;
                shader.fadeStartTime = System.currentTimeMillis();
                shader.startRed = shader.red;
                shader.startGreen = shader.green;
                shader.startBlue = shader.blue;
                shader.startAlpha = shader.alpha;
                shader.startSaturation = shader.saturation;
                shader.startContrast = shader.contrast;
                shader.startIntensity = shader.intensity;
            }

            this.sendToCap(shader);
        }
    }

    @Override
    public void setVignette(boolean enabled, double strength, double radius, double softness, double r, double g, double b, double alpha, int fadeTicks, double saturation, double contrast, double intensity) {
        Shader sh = this.getShader();
        if (sh != null) {
            long fadeMs = (long)fadeTicks * 50L;
            if (enabled) {
                sh.vignetteEnabled = true;
                sh.vignetteStrength = (float)Math.min((double)1.0F, Math.max((double)0.0F, strength));
                sh.vignetteRadius = (float)Math.min((double)1.0F, Math.max((double)0.0F, radius));
                sh.vignetteSoftness = (float)Math.min((double)1.0F, Math.max((double)0.0F, softness));
                sh.vignetteRed = (float)Math.min((double)1.0F, Math.max((double)0.0F, r / (double)255.0F));
                sh.vignetteGreen = (float)Math.min((double)1.0F, Math.max((double)0.0F, g / (double)255.0F));
                sh.vignetteBlue = (float)Math.min((double)1.0F, Math.max((double)0.0F, b / (double)255.0F));
                sh.vignetteAlpha = (float)Math.min((double)1.0F, Math.max((double)0.0F, alpha));
                sh.vignetteSaturation = (float)Math.min((double)2.0F, Math.max((double)0.0F, saturation));
                sh.vignetteContrast = (float)Math.min((double)2.0F, Math.max((double)0.0F, contrast));
                sh.vignetteIntensity = (float)Math.min((double)1.0F, Math.max((double)0.0F, intensity));
                if (fadeMs <= 0L) {
                    sh.vignetteFadingIn = false;
                    sh.vignetteFadingOut = false;
                    sh.vignetteStartStrength = sh.vignetteStrength;
                    sh.vignetteStartRadius = sh.vignetteRadius;
                    sh.vignetteStartSoftness = sh.vignetteSoftness;
                    sh.vignetteStartRed = sh.vignetteRed;
                    sh.vignetteStartGreen = sh.vignetteGreen;
                    sh.vignetteStartBlue = sh.vignetteBlue;
                    sh.vignetteStartAlpha = sh.vignetteAlpha;
                    sh.vignetteStartSaturation = sh.vignetteSaturation;
                    sh.vignetteStartContrast = sh.vignetteContrast;
                    sh.vignetteStartIntensity = sh.vignetteIntensity;
                } else {
                    sh.vignetteFadingIn = true;
                    sh.vignetteFadingOut = false;
                    sh.vignetteFadeInMs = fadeMs;
                    sh.vignetteFadeOutMs = fadeMs;
                    sh.vignetteFadeStartTime = -1L;
                    sh.vignetteStartStrength = 0.0F;
                    sh.vignetteStartRadius = 0.5F;
                    sh.vignetteStartSoftness = 0.5F;
                    sh.vignetteStartRed = 0.0F;
                    sh.vignetteStartGreen = 0.0F;
                    sh.vignetteStartBlue = 0.0F;
                    sh.vignetteStartAlpha = 0.0F;
                    sh.vignetteStartSaturation = 1.0F;
                    sh.vignetteStartContrast = 1.0F;
                    sh.vignetteStartIntensity = 1.0F;
                }
            } else if (fadeMs <= 0L) {
                sh.resetVignetteParameters();
            } else {
                sh.vignetteFadingOut = true;
                sh.vignetteFadingIn = false;
                sh.vignetteFadeOutMs = fadeMs;
                sh.vignetteFadeStartTime = -1L;
                sh.vignetteStartStrength = sh.vignetteStrength;
                sh.vignetteStartRadius = sh.vignetteRadius;
                sh.vignetteStartSoftness = sh.vignetteSoftness;
                sh.vignetteStartRed = sh.vignetteRed;
                sh.vignetteStartGreen = sh.vignetteGreen;
                sh.vignetteStartBlue = sh.vignetteBlue;
                sh.vignetteStartAlpha = sh.vignetteAlpha;
                sh.vignetteStartSaturation = sh.vignetteSaturation;
                sh.vignetteStartContrast = sh.vignetteContrast;
                sh.vignetteStartIntensity = sh.vignetteIntensity;
            }

            this.sendToCap(sh);
        }
    }

    @Override
    public boolean isEnabled() {
        Shader shader = this.getShader();
        return shader != null && shader.enabled;
    }

    @Override
    public ShaderData openShaders(String data) {
        /* ShaderRenderer is client-only, and scripts run on the server, so the
         * actual open happens on the client. Load the data on the server too so
         * a valid ShaderData can be returned to the script. */
        ShaderData shaderData = AEMod.getShadersManager().load(data);

        if (shaderData != null) {
            unkn0wn.ae.network.Dispatcher.sendTo(
                    new PacketScriptShader(PacketScriptShader.Operation.OPEN, data), this.player);
        }

        return shaderData;
    }

    @Override
    public boolean closeShaders(String data) {
        unkn0wn.ae.network.Dispatcher.sendTo(
                new PacketScriptShader(PacketScriptShader.Operation.CLOSE, data), this.player);

        return true;
    }

    @Override
    public int closeAllShaders() {
        unkn0wn.ae.network.Dispatcher.sendTo(
                new PacketScriptShader(PacketScriptShader.Operation.CLOSE_ALL, ""), this.player);

        return 0;
    }

    private void sendToCap(Shader shader) {
        Dispatcher.sendTo(new PacketCapability(shader.serializeNBT(), CapabilitiesType.SHADERS), this.player);
    }
}
