package unkn0wn.ae.api.shaders;

import mchorse.mappet.api.utils.AbstractData;
import net.minecraft.nbt.NBTTagCompound;

import java.nio.charset.StandardCharsets;

public class ShaderData extends AbstractData {
    public String vertexSource = "";
    public String fragmentSource = "";
    public boolean enableF1 = false;
    public float red = 0.0F;
    public float green = 0.0F;
    public float blue = 0.0F;
    public float alpha = 0.0F;
    public float saturation = 1.0F;
    public float contrast = 1.0F;
    public float intensity = 1.0F;
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
    public float flashIntensity = 0.0F;
    public float flashRed = 1.0F;
    public float flashGreen = 1.0F;
    public float flashBlue = 1.0F;
    public float custom0 = 0.0F;
    public float custom1 = 0.0F;
    public float custom2 = 0.0F;
    public float custom3 = 0.0F;
    public float custom4 = 0.0F;
    public float custom5 = 0.0F;
    public float custom6 = 0.0F;
    public float custom7 = 0.0F;
    public float customColor0R = 1.0F;
    public float customColor0G = 1.0F;
    public float customColor0B = 1.0F;
    public float customColor1R = 0.0F;
    public float customColor1G = 0.0F;
    public float customColor1B = 0.0F;
    public float customColor2R = 0.0F;
    public float customColor2G = 0.0F;
    public float customColor2B = 0.0F;
    public float customColor3R = 0.0F;
    public float customColor3G = 0.0F;
    public float customColor3B = 0.0F;
    public float blurRadius = 0.0F;
    public float distortStrength = 0.0F;
    public float pixelSize = 1.0F;
    public float chromAberration = 0.0F;
    public float scanlineStrength = 0.0F;
    public float scanlineCount = 100.0F;
    public float noiseStrength = 0.0F;
    public float noiseSpeed = 1.0F;
    public float bloomThreshold = 0.8F;
    public float bloomIntensity = 0.0F;
    public float hue = 0.0F;
    public float temperature = 0.0F;
    public float sharpness = 0.0F;
    public float gamma = 1.0F;
    public float waveAmplitude = 0.0F;
    public float waveFrequency = 10.0F;
    public float waveSpeed = 1.0F;
    public float dofFocus = 0.5F;
    public float dofStrength = 0.0F;

    public ShaderData() {
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByteArray("Vertex", this.vertexSource.getBytes(StandardCharsets.UTF_8));
        tag.setByteArray("Fragment", this.fragmentSource.getBytes(StandardCharsets.UTF_8));
        tag.setBoolean("EnableF1", this.enableF1);
        tag.setFloat("Red", this.red);
        tag.setFloat("Green", this.green);
        tag.setFloat("Blue", this.blue);
        tag.setFloat("Alpha", this.alpha);
        tag.setFloat("Saturation", this.saturation);
        tag.setFloat("Contrast", this.contrast);
        tag.setFloat("Intensity", this.intensity);
        tag.setFloat("VignetteStrength", this.vignetteStrength);
        tag.setFloat("VignetteRadius", this.vignetteRadius);
        tag.setFloat("VignetteSoftness", this.vignetteSoftness);
        tag.setFloat("VignetteRed", this.vignetteRed);
        tag.setFloat("VignetteGreen", this.vignetteGreen);
        tag.setFloat("VignetteBlue", this.vignetteBlue);
        tag.setFloat("VignetteAlpha", this.vignetteAlpha);
        tag.setFloat("VignetteSaturation", this.vignetteSaturation);
        tag.setFloat("VignetteContrast", this.vignetteContrast);
        tag.setFloat("VignetteIntensity", this.vignetteIntensity);
        tag.setFloat("FlashIntensity", this.flashIntensity);
        tag.setFloat("FlashRed", this.flashRed);
        tag.setFloat("FlashGreen", this.flashGreen);
        tag.setFloat("FlashBlue", this.flashBlue);
        tag.setFloat("Custom0", this.custom0);
        tag.setFloat("Custom1", this.custom1);
        tag.setFloat("Custom2", this.custom2);
        tag.setFloat("Custom3", this.custom3);
        tag.setFloat("Custom4", this.custom4);
        tag.setFloat("Custom5", this.custom5);
        tag.setFloat("Custom6", this.custom6);
        tag.setFloat("Custom7", this.custom7);
        tag.setFloat("CC0R", this.customColor0R);
        tag.setFloat("CC0G", this.customColor0G);
        tag.setFloat("CC0B", this.customColor0B);
        tag.setFloat("CC1R", this.customColor1R);
        tag.setFloat("CC1G", this.customColor1G);
        tag.setFloat("CC1B", this.customColor1B);
        tag.setFloat("CC2R", this.customColor2R);
        tag.setFloat("CC2G", this.customColor2G);
        tag.setFloat("CC2B", this.customColor2B);
        tag.setFloat("CC3R", this.customColor3R);
        tag.setFloat("CC3G", this.customColor3G);
        tag.setFloat("CC3B", this.customColor3B);
        tag.setFloat("BlurRadius", this.blurRadius);
        tag.setFloat("DistortStrength", this.distortStrength);
        tag.setFloat("PixelSize", this.pixelSize);
        tag.setFloat("ChromAberration", this.chromAberration);
        tag.setFloat("ScanlineStrength", this.scanlineStrength);
        tag.setFloat("ScanlineCount", this.scanlineCount);
        tag.setFloat("NoiseStrength", this.noiseStrength);
        tag.setFloat("NoiseSpeed", this.noiseSpeed);
        tag.setFloat("BloomThreshold", this.bloomThreshold);
        tag.setFloat("BloomIntensity", this.bloomIntensity);
        tag.setFloat("Hue", this.hue);
        tag.setFloat("Temperature", this.temperature);
        tag.setFloat("Sharpness", this.sharpness);
        tag.setFloat("Gamma", this.gamma);
        tag.setFloat("WaveAmplitude", this.waveAmplitude);
        tag.setFloat("WaveFrequency", this.waveFrequency);
        tag.setFloat("WaveSpeed", this.waveSpeed);
        tag.setFloat("DofFocus", this.dofFocus);
        tag.setFloat("DofStrength", this.dofStrength);
        return tag;
    }

    public void deserializeNBT(NBTTagCompound tag) {
        this.vertexSource = new String(tag.getByteArray("Vertex"), StandardCharsets.UTF_8);
        this.fragmentSource = new String(tag.getByteArray("Fragment"), StandardCharsets.UTF_8);
        this.enableF1 = tag.getBoolean("EnableF1");
        if (tag.hasKey("Red", 5)) {
            this.red = tag.getFloat("Red");
        }

        if (tag.hasKey("Green", 5)) {
            this.green = tag.getFloat("Green");
        }

        if (tag.hasKey("Blue", 5)) {
            this.blue = tag.getFloat("Blue");
        }

        if (tag.hasKey("Alpha", 5)) {
            this.alpha = tag.getFloat("Alpha");
        }

        if (tag.hasKey("Saturation", 5)) {
            this.saturation = tag.getFloat("Saturation");
        }

        if (tag.hasKey("Contrast", 5)) {
            this.contrast = tag.getFloat("Contrast");
        }

        if (tag.hasKey("Intensity", 5)) {
            this.intensity = tag.getFloat("Intensity");
        }

        if (tag.hasKey("VignetteStrength", 5)) {
            this.vignetteStrength = tag.getFloat("VignetteStrength");
        }

        if (tag.hasKey("VignetteRadius", 5)) {
            this.vignetteRadius = tag.getFloat("VignetteRadius");
        }

        if (tag.hasKey("VignetteSoftness", 5)) {
            this.vignetteSoftness = tag.getFloat("VignetteSoftness");
        }

        if (tag.hasKey("VignetteRed", 5)) {
            this.vignetteRed = tag.getFloat("VignetteRed");
        }

        if (tag.hasKey("VignetteGreen", 5)) {
            this.vignetteGreen = tag.getFloat("VignetteGreen");
        }

        if (tag.hasKey("VignetteBlue", 5)) {
            this.vignetteBlue = tag.getFloat("VignetteBlue");
        }

        if (tag.hasKey("VignetteAlpha", 5)) {
            this.vignetteAlpha = tag.getFloat("VignetteAlpha");
        }

        if (tag.hasKey("VignetteSaturation", 5)) {
            this.vignetteSaturation = tag.getFloat("VignetteSaturation");
        }

        if (tag.hasKey("VignetteContrast", 5)) {
            this.vignetteContrast = tag.getFloat("VignetteContrast");
        }

        if (tag.hasKey("VignetteIntensity", 5)) {
            this.vignetteIntensity = tag.getFloat("VignetteIntensity");
        }

        if (tag.hasKey("FlashIntensity", 5)) {
            this.flashIntensity = tag.getFloat("FlashIntensity");
        }

        if (tag.hasKey("FlashRed", 5)) {
            this.flashRed = tag.getFloat("FlashRed");
        }

        if (tag.hasKey("FlashGreen", 5)) {
            this.flashGreen = tag.getFloat("FlashGreen");
        }

        if (tag.hasKey("FlashBlue", 5)) {
            this.flashBlue = tag.getFloat("FlashBlue");
        }

        if (tag.hasKey("Custom0", 5)) {
            this.custom0 = tag.getFloat("Custom0");
        }

        if (tag.hasKey("Custom1", 5)) {
            this.custom1 = tag.getFloat("Custom1");
        }

        if (tag.hasKey("Custom2", 5)) {
            this.custom2 = tag.getFloat("Custom2");
        }

        if (tag.hasKey("Custom3", 5)) {
            this.custom3 = tag.getFloat("Custom3");
        }

        if (tag.hasKey("Custom4", 5)) {
            this.custom4 = tag.getFloat("Custom4");
        }

        if (tag.hasKey("Custom5", 5)) {
            this.custom5 = tag.getFloat("Custom5");
        }

        if (tag.hasKey("Custom6", 5)) {
            this.custom6 = tag.getFloat("Custom6");
        }

        if (tag.hasKey("Custom7", 5)) {
            this.custom7 = tag.getFloat("Custom7");
        }

        if (tag.hasKey("CC0R", 5)) {
            this.customColor0R = tag.getFloat("CC0R");
        }

        if (tag.hasKey("CC0G", 5)) {
            this.customColor0G = tag.getFloat("CC0G");
        }

        if (tag.hasKey("CC0B", 5)) {
            this.customColor0B = tag.getFloat("CC0B");
        }

        if (tag.hasKey("CC1R", 5)) {
            this.customColor1R = tag.getFloat("CC1R");
        }

        if (tag.hasKey("CC1G", 5)) {
            this.customColor1G = tag.getFloat("CC1G");
        }

        if (tag.hasKey("CC1B", 5)) {
            this.customColor1B = tag.getFloat("CC1B");
        }

        if (tag.hasKey("CC2R", 5)) {
            this.customColor2R = tag.getFloat("CC2R");
        }

        if (tag.hasKey("CC2G", 5)) {
            this.customColor2G = tag.getFloat("CC2G");
        }

        if (tag.hasKey("CC2B", 5)) {
            this.customColor2B = tag.getFloat("CC2B");
        }

        if (tag.hasKey("CC3R", 5)) {
            this.customColor3R = tag.getFloat("CC3R");
        }

        if (tag.hasKey("CC3G", 5)) {
            this.customColor3G = tag.getFloat("CC3G");
        }

        if (tag.hasKey("CC3B", 5)) {
            this.customColor3B = tag.getFloat("CC3B");
        }

        if (tag.hasKey("BlurRadius", 5)) {
            this.blurRadius = tag.getFloat("BlurRadius");
        }

        if (tag.hasKey("DistortStrength", 5)) {
            this.distortStrength = tag.getFloat("DistortStrength");
        }

        if (tag.hasKey("PixelSize", 5)) {
            this.pixelSize = tag.getFloat("PixelSize");
        }

        if (tag.hasKey("ChromAberration", 5)) {
            this.chromAberration = tag.getFloat("ChromAberration");
        }

        if (tag.hasKey("ScanlineStrength", 5)) {
            this.scanlineStrength = tag.getFloat("ScanlineStrength");
        }

        if (tag.hasKey("ScanlineCount", 5)) {
            this.scanlineCount = tag.getFloat("ScanlineCount");
        }

        if (tag.hasKey("NoiseStrength", 5)) {
            this.noiseStrength = tag.getFloat("NoiseStrength");
        }

        if (tag.hasKey("NoiseSpeed", 5)) {
            this.noiseSpeed = tag.getFloat("NoiseSpeed");
        }

        if (tag.hasKey("BloomThreshold", 5)) {
            this.bloomThreshold = tag.getFloat("BloomThreshold");
        }

        if (tag.hasKey("BloomIntensity", 5)) {
            this.bloomIntensity = tag.getFloat("BloomIntensity");
        }

        if (tag.hasKey("Hue", 5)) {
            this.hue = tag.getFloat("Hue");
        }

        if (tag.hasKey("Temperature", 5)) {
            this.temperature = tag.getFloat("Temperature");
        }

        if (tag.hasKey("Sharpness", 5)) {
            this.sharpness = tag.getFloat("Sharpness");
        }

        if (tag.hasKey("Gamma", 5)) {
            this.gamma = tag.getFloat("Gamma");
        }

        if (tag.hasKey("WaveAmplitude", 5)) {
            this.waveAmplitude = tag.getFloat("WaveAmplitude");
        }

        if (tag.hasKey("WaveFrequency", 5)) {
            this.waveFrequency = tag.getFloat("WaveFrequency");
        }

        if (tag.hasKey("WaveSpeed", 5)) {
            this.waveSpeed = tag.getFloat("WaveSpeed");
        }

        if (tag.hasKey("DofFocus", 5)) {
            this.dofFocus = tag.getFloat("DofFocus");
        }

        if (tag.hasKey("DofStrength", 5)) {
            this.dofStrength = tag.getFloat("DofStrength");
        }

    }
}
