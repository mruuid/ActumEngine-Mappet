package unkn0wn.ae.capabilities.shaders;

public class ShaderLayer {
    public int priority;
    public float red = 0.0F;
    public float green = 0.0F;
    public float blue = 0.0F;
    public float alpha = 0.0F;
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
    public boolean enabled = true;

    public ShaderLayer(int priority) {
        this.priority = priority;
    }

    public boolean tick() {
        long now = System.currentTimeMillis();
        if (this.fadeStartTime < 0L) {
            this.fadeStartTime = now;
        }

        if (this.fadingIn && this.fadeInMs > 0L) {
            float t = Math.min(1.0F, (float)(now - this.fadeStartTime) / (float)this.fadeInMs);
            this.currentRed = lerp(this.startRed, this.red, t);
            this.currentGreen = lerp(this.startGreen, this.green, t);
            this.currentBlue = lerp(this.startBlue, this.blue, t);
            this.currentAlpha = lerp(this.startAlpha, this.alpha, t);
            this.currentSaturation = lerp(this.startSaturation, this.saturation, t);
            this.currentContrast = lerp(this.startContrast, this.contrast, t);
            this.currentIntensity = lerp(this.startIntensity, this.intensity, t);
            if (t >= 1.0F) {
                this.fadingIn = false;
                this.fadeStartTime = -1L;
            }
        } else if (this.fadingOut && this.fadeOutMs > 0L) {
            float t = Math.min(1.0F, (float)(now - this.fadeStartTime) / (float)this.fadeOutMs);
            this.currentAlpha = lerp(this.startAlpha, 0.0F, t);
            this.currentSaturation = lerp(this.startSaturation, 1.0F, t);
            this.currentContrast = lerp(this.startContrast, 1.0F, t);
            this.currentIntensity = lerp(this.startIntensity, 1.0F, t);
            this.currentRed = lerp(this.startRed, this.red, t);
            this.currentGreen = lerp(this.startGreen, this.green, t);
            this.currentBlue = lerp(this.startBlue, this.blue, t);
            if (t >= 1.0F) {
                this.fadingOut = false;
                this.enabled = false;
                this.currentAlpha = 0.0F;
                this.currentSaturation = 1.0F;
                this.currentContrast = 1.0F;
                this.currentIntensity = 1.0F;
                return false;
            }
        } else {
            this.currentRed = this.red;
            this.currentGreen = this.green;
            this.currentBlue = this.blue;
            this.currentAlpha = this.alpha;
            this.currentSaturation = this.saturation;
            this.currentContrast = this.contrast;
            this.currentIntensity = this.intensity;
        }

        return true;
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
