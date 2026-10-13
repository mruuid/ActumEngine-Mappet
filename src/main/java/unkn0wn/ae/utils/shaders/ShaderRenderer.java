package unkn0wn.ae.utils.shaders;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import unkn0wn.ae.api.shaders.ShaderData;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.LinkedHashMap;

public class ShaderRenderer {
    private static int program = -1;
    private static int compiledVertexSource = -1;
    private static int compiledFragmentSource = -1;
    private static boolean failed = false;
    private static String lastError = null;
    private static int uTex = -1;
    private static int uText = -1;
    private static int uTime = -1;
    private static int uITime = -1;
    private static int uScreenW = -1;
    private static int uScreenH = -1;
    private static int uResolution = -1;
    private static int uIResolution = -1;
    private static int uAspect = -1;
    private static int uWorldTime = -1;
    private static int uDayTime = -1;
    private static int uSunAngle = -1;
    private static int uIsDay = -1;
    private static int uSunDir = -1;
    private static int uSunBrightness = -1;
    private static int uSkyColor = -1;
    private static int uSunColor = -1;
    private static int uPlayerPos = -1;
    private static int uPlayerRot = -1;
    private static int uHealth = -1;
    private static int uMaxHealth = -1;
    private static int uHunger = -1;
    private static int uAir = -1;
    private static int uBrightness = -1;
    private static int uInWater = -1;
    private static int uOnFire = -1;
    private static int uIsSneaking = -1;
    private static int uIsSprinting = -1;
    private static int uFogStart = -1;
    private static int uFogEnd = -1;
    private static int uFogColor = -1;
    private static int uRed = -1;
    private static int uGreen = -1;
    private static int uBlue = -1;
    private static int uAlpha = -1;
    private static int uSaturation = -1;
    private static int uContrast = -1;
    private static int uIntensity = -1;
    private static int uVignetteStrength = -1;
    private static int uVignetteRadius = -1;
    private static int uVignetteSoftness = -1;
    private static int uVignetteColor = -1;
    private static int uVignetteAlpha = -1;
    private static int uVignetteSaturation = -1;
    private static int uVignetteContrast = -1;
    private static int uVignetteIntensity = -1;
    private static int uFlashIntensity = -1;
    private static int uFlashColor = -1;
    private static int uCustom0 = -1;
    private static int uCustom1 = -1;
    private static int uCustom2 = -1;
    private static int uCustom3 = -1;
    private static int uCustom4 = -1;
    private static int uCustom5 = -1;
    private static int uCustom6 = -1;
    private static int uCustom7 = -1;
    private static int uCustomColor0 = -1;
    private static int uCustomColor1 = -1;
    private static int uCustomColor2 = -1;
    private static int uCustomColor3 = -1;
    private static int uBlurRadius = -1;
    private static int uDistortStrength = -1;
    private static int uPixelSize = -1;
    private static int uChromAberration = -1;
    private static int uScanlineStrength = -1;
    private static int uScanlineCount = -1;
    private static int uNoiseStrength = -1;
    private static int uNoiseSpeed = -1;
    private static int uBloomThreshold = -1;
    private static int uBloomIntensity = -1;
    private static int uHue = -1;
    private static int uTemperature = -1;
    private static int uSharpness = -1;
    private static int uGamma = -1;
    private static int uWaveAmplitude = -1;
    private static int uWaveFrequency = -1;
    private static int uWaveSpeed = -1;
    private static int uDofFocus = -1;
    private static int uDofStrength = -1;
    private static long shaderStartTime = -1L;
    private static float customTime = -1.0F;
    private static final OverlayParams overlay = new OverlayParams();

    /* Ping-pong offscreen framebuffers used so the shader never samples the very
     * framebuffer it writes to (which is undefined behavior in OpenGL).
     * fboSrc holds an exact pixel snapshot of Minecraft's main framebuffer (made
     * with glBlitFramebuffer), fbo receives the shader output. */
    private static int fbo = -1;
    private static int fboTexture = -1;
    private static int fboSrc = -1;
    private static int fboSrcTexture = -1;
    private static int fboWidth = -1;
    private static int fboHeight = -1;

    /* Shaders the player has "opened" (script API + F7 toggle bookkeeping).
     * The renderer still drives a single GL program — this map only remembers
     * names so closeShaders()/getShaders() can address individual shaders. */
    private static final LinkedHashMap<String, ShaderData> openShaders = new LinkedHashMap<String, ShaderData>();
    private static String currentShaderName = null;
    private static ShaderData lastShaderData = null;

    public ShaderRenderer() {
    }

    public static void apply(ShaderData data) {
        cleanup();
        failed = false;
        lastError = null;
        shaderStartTime = System.currentTimeMillis();
        customTime = -1.0F;
        overlay.loadFromData(data);

        if (data != null) {
            lastShaderData = data;
            currentShaderName = data.getId();

            if (currentShaderName != null && !currentShaderName.isEmpty()) {
                openShaders.put(currentShaderName, data);
            }
        }

        try {
            int vert = compileShader(35633, data.vertexSource);
            int frag = compileShader(35632, data.fragmentSource);
            program = GL20.glCreateProgram();
            GL20.glAttachShader(program, vert);
            GL20.glAttachShader(program, frag);
            GL20.glLinkProgram(program);
            if (GL20.glGetProgrami(program, 35714) == 0) {
                String log = GL20.glGetProgramInfoLog(program, 4096);
                lastError = "Link error:\n" + log;
                logError(lastError);
                cleanup();
                failed = true;
                return;
            }

            compiledVertexSource = vert;
            compiledFragmentSource = frag;
            GL20.glUseProgram(program);
            uTex = loc("tex");
            uText = loc("text");
            uTime = loc("time");
            uITime = loc("iTime");
            uScreenW = loc("screenWidth");
            uScreenH = loc("screenHeight");
            uResolution = loc("resolution");
            uIResolution = loc("iResolution");
            uAspect = loc("iAspect");
            uWorldTime = loc("iWorldTime");
            uDayTime = loc("iDayTime");
            uSunAngle = loc("iSunAngle");
            uIsDay = loc("iIsDay");
            uSunDir = loc("iSunDir");
            uSunBrightness = loc("iSunBrightness");
            uSkyColor = loc("iSkyColor");
            uSunColor = loc("iSunColor");
            uPlayerPos = loc("iPlayerPos");
            uPlayerRot = loc("iPlayerRot");
            uHealth = loc("iHealth");
            uMaxHealth = loc("iMaxHealth");
            uHunger = loc("iHunger");
            uAir = loc("iAir");
            uBrightness = loc("iBrightness");
            uInWater = loc("iInWater");
            uOnFire = loc("iOnFire");
            uIsSneaking = loc("iIsSneaking");
            uIsSprinting = loc("iIsSprinting");
            uFogStart = loc("iFogStart");
            uFogEnd = loc("iFogEnd");
            uFogColor = loc("iFogColor");
            uRed = loc("red");
            uGreen = loc("green");
            uBlue = loc("blue");
            uAlpha = loc("alpha");
            uSaturation = loc("saturation");
            uContrast = loc("contrast");
            uIntensity = loc("intensity");
            uVignetteStrength = loc("vignetteStrength");
            uVignetteRadius = loc("vignetteRadius");
            uVignetteSoftness = loc("vignetteSoftness");
            uVignetteColor = loc("vignetteColor");
            uVignetteAlpha = loc("vignetteAlpha");
            uVignetteSaturation = loc("vignetteSaturation");
            uVignetteContrast = loc("vignetteContrast");
            uVignetteIntensity = loc("vignetteIntensity");
            uFlashIntensity = loc("flashIntensity");
            uFlashColor = loc("flashColor");
            uCustom0 = loc("custom0");
            uCustom1 = loc("custom1");
            uCustom2 = loc("custom2");
            uCustom3 = loc("custom3");
            uCustom4 = loc("custom4");
            uCustom5 = loc("custom5");
            uCustom6 = loc("custom6");
            uCustom7 = loc("custom7");
            uCustomColor0 = loc("customColor0");
            uCustomColor1 = loc("customColor1");
            uCustomColor2 = loc("customColor2");
            uCustomColor3 = loc("customColor3");
            uBlurRadius = loc("blurRadius");
            uDistortStrength = loc("distortStrength");
            uPixelSize = loc("pixelSize");
            uChromAberration = loc("chromAberration");
            uScanlineStrength = loc("scanlineStrength");
            uScanlineCount = loc("scanlineCount");
            uNoiseStrength = loc("noiseStrength");
            uNoiseSpeed = loc("noiseSpeed");
            uBloomThreshold = loc("bloomThreshold");
            uBloomIntensity = loc("bloomIntensity");
            uHue = loc("hue");
            uTemperature = loc("temperature");
            uSharpness = loc("sharpness");
            uGamma = loc("gamma");
            uWaveAmplitude = loc("waveAmplitude");
            uWaveFrequency = loc("waveFrequency");
            uWaveSpeed = loc("waveSpeed");
            uDofFocus = loc("dofFocus");
            uDofStrength = loc("dofStrength");
            GL20.glUseProgram(0);
            System.out.println("custom0=" + uCustom0 + " custom1=" + uCustom1 + " custom2=" + uCustom2 + " custom3=" + uCustom3 + " custom4=" + uCustom4 + " custom5=" + uCustom5 + " custom6=" + uCustom6 + " custom7=" + uCustom7);
        } catch (Exception e) {
            lastError = e.getMessage();
            logError(lastError);
            cleanup();
            failed = true;
        }

    }

    public static void clear() {
        cleanup();
        failed = false;
        lastError = null;
        shaderStartTime = -1L;
        customTime = -1.0F;
        overlay.reset();
        openShaders.clear();
        currentShaderName = null;
        /* lastShaderData is intentionally kept so F7 can re-enable the last
         * selected shader after everything was stopped. */
    }

    public static boolean isActive() {
        return program != -1 && !failed;
    }

    /* Named-shader bookkeeping used by the script API (ScriptShaders) and the
     * F7 toggle. */

    /**
     * Open (compile and start) a shader under the given id.
     *
     * @return false when the id/data is empty or the shader failed to compile
     */
    public static boolean openShader(String id, ShaderData data) {
        if (id == null || id.isEmpty() || data == null) {
            return false;
        }

        data.setId(id);
        apply(data);

        if (failed) {
            openShaders.remove(id);
            return false;
        }

        return true;
    }

    /**
     * Close a single opened shader. When the closed shader was the one
     * currently running, the most recently opened remaining shader (if any)
     * is applied instead.
     *
     * @return false when there is no shader with that id
     */
    public static boolean closeShader(String id) {
        if (id == null || !openShaders.containsKey(id)) {
            return false;
        }

        openShaders.remove(id);

        if (id.equals(currentShaderName)) {
            ShaderData next = null;

            for (ShaderData data : openShaders.values()) {
                next = data; /* insertion order — fall back to the latest one */
            }

            if (next != null) {
                apply(next);
            } else {
                clear();
            }
        }

        return true;
    }

    /**
     * Close every shader the player has opened.
     *
     * @return how many shaders were closed
     */
    public static int closeAllShaders() {
        int count = openShaders.size();

        clear();

        return count;
    }

    /** Whether the player has at least one opened (or running) shader. */
    public static boolean hasOpenShaders() {
        return !openShaders.isEmpty();
    }

    /** Get an opened shader's data by id, or null when there is none. */
    public static ShaderData getOpenShader(String id) {
        return id == null ? null : openShaders.get(id);
    }

    /** Id of the shader that is currently running, or null. */
    public static String getCurrentShaderName() {
        return currentShaderName;
    }

    /** Remember a shader as "selected" (used by the F7 toggle). */
    public static void rememberShader(ShaderData data) {
        if (data != null) {
            lastShaderData = data;
        }
    }

    /**
     * Toggle the last selected/ran shader (F7): stops everything that runs,
     * otherwise applies the remembered shader again.
     *
     * @return true when a shader is running after the call
     */
    public static boolean toggleLastShader() {
        if (isActive() || !openShaders.isEmpty()) {
            clear();
            return false;
        }

        if (lastShaderData == null) {
            return false;
        }

        apply(lastShaderData);
        return isActive();
    }

    /* Runtime uniform control (used by the scripting API). */

    public static boolean hasUniform(String name) {
        return isActive() && GL20.glGetUniformLocation(program, name) != -1;
    }

    public static void setUniform(String name, float value) {
        if (isActive()) {
            int loc = GL20.glGetUniformLocation(program, name);

            if (loc != -1) {
                GL20.glUniform1f(loc, value);
            }
        }
    }

    public static void setUniform2f(String name, float x, float y) {
        if (isActive()) {
            int loc = GL20.glGetUniformLocation(program, name);

            if (loc != -1) {
                GL20.glUniform2f(loc, x, y);
            }
        }
    }

    public static void setUniform3f(String name, float x, float y, float z) {
        if (isActive()) {
            int loc = GL20.glGetUniformLocation(program, name);

            if (loc != -1) {
                GL20.glUniform3f(loc, x, y, z);
            }
        }
    }

    public static void setUniform4f(String name, float x, float y, float z, float w) {
        if (isActive()) {
            int loc = GL20.glGetUniformLocation(program, name);

            if (loc != -1) {
                GL20.glUniform4f(loc, x, y, z, w);
            }
        }
    }

    public static void setUniformi(String name, int value) {
        if (isActive()) {
            int loc = GL20.glGetUniformLocation(program, name);

            if (loc != -1) {
                GL20.glUniform1i(loc, value);
            }
        }
    }

    public static String getLastError() {
        return lastError;
    }

    public static void setTime(float t) {
        customTime = t;
    }

    public static float getTime() {
        if (customTime >= 0.0F) {
            return customTime;
        } else {
            return shaderStartTime < 0L ? 0.0F : (float)(System.currentTimeMillis() - shaderStartTime) / 1000.0F;
        }
    }

    public static void setOverlayParams(OverlayParams params) {
        overlay.copyFrom(params);
    }

    public static void setOverlay(float r, float g, float b, float a, float sat, float con, float ins, float vigStr, float vigRad, float vigSoft, float vigR, float vigG, float vigB, float vigA, float vigSat, float vigCon, float vigInt, float flashInt, float flashR, float flashG, float flashB) {
        overlay.red = r;
        overlay.green = g;
        overlay.blue = b;
        overlay.alpha = a;
        overlay.saturation = sat;
        overlay.contrast = con;
        overlay.intensity = ins;
        overlay.vignetteStrength = vigStr;
        overlay.vignetteRadius = vigRad;
        overlay.vignetteSoftness = vigSoft;
        overlay.vignetteRed = vigR;
        overlay.vignetteGreen = vigG;
        overlay.vignetteBlue = vigB;
        overlay.vignetteAlpha = vigA;
        overlay.vignetteSaturation = vigSat;
        overlay.vignetteContrast = vigCon;
        overlay.vignetteIntensity = vigInt;
        overlay.flashIntensity = flashInt;
        overlay.flashRed = flashR;
        overlay.flashGreen = flashG;
        overlay.flashBlue = flashB;
    }

    public static void render() {
        if (isActive()) {
            Minecraft mc = Minecraft.getMinecraft();
            Framebuffer fb = mc.getFramebuffer();
            WorldClient world = mc.world;
            EntityPlayerSP player = mc.player;
            float t = getTime();
            float fw = (float)fb.framebufferWidth;
            float fh = (float)fb.framebufferHeight;

            try {
                GL13.glActiveTexture(33984);
                GL11.glEnable(3553);
                fb.bindFramebufferTexture();
                GL20.glUseProgram(program);
                set1i(uTex, 0);
                set1i(uText, 0);
                set1f(uTime, t);
                set1f(uITime, t);
                set1f(uScreenW, fw);
                set1f(uScreenH, fh);
                set2f(uResolution, fw, fh);
                set2f(uIResolution, fw, fh);
                set1f(uAspect, fh > 0.0F ? fw / fh : 1.0F);
                if (world != null) {
                    float worldTicks = (float)(world.getWorldTime() % 24000L);
                    float dayNorm = worldTicks / 24000.0F;
                    float sunAngleRad = (float)((double)world.getCelestialAngle(1.0F) * Math.PI * (double)2.0F);
                    set1f(uWorldTime, worldTicks);
                    set1f(uDayTime, dayNorm);
                    set1f(uSunAngle, sunAngleRad);
                    set1f(uIsDay, worldTicks < 12000.0F ? 1.0F : 0.0F);
                    float sinA = (float)Math.sin((double)sunAngleRad);
                    float cosA = (float)Math.cos((double)sunAngleRad);
                    set3f(uSunDir, sinA, cosA, 0.0F);
                    set1f(uSunBrightness, world.getSunBrightness(1.0F));
                    Vec3d skyVec = world.getSkyColor(mc.getRenderViewEntity(), 1.0F);
                    if (skyVec != null) {
                        set3f(uSkyColor, (float)skyVec.x, (float)skyVec.y, (float)skyVec.z);
                    }

                    Vec3d fogVec = world.getFogColor(1.0F);
                    if (fogVec != null) {
                        set3f(uSunColor, (float)fogVec.x, (float)fogVec.y, (float)fogVec.z);
                    }

                    try {
                        FloatBuffer fogRgba = BufferUtils.createFloatBuffer(16);
                        GL11.glGetFloat(2918, fogRgba);
                        set1f(uFogStart, GL11.glGetFloat(2915));
                        set1f(uFogEnd, GL11.glGetFloat(2916));
                        set3f(uFogColor, fogRgba.get(0), fogRgba.get(1), fogRgba.get(2));
                    } catch (Exception var15) {
                    }
                }

                if (player != null) {
                    set3f(uPlayerPos, (float)player.posX, (float)player.posY, (float)player.posZ);
                    set2f(uPlayerRot, player.rotationYaw, player.rotationPitch);
                    set1f(uHealth, player.getHealth());
                    set1f(uMaxHealth, player.getMaxHealth());
                    set1f(uHunger, (float)player.getFoodStats().getFoodLevel());
                    set1f(uAir, (float)player.getAir());
                    set1f(uBrightness, player.getBrightness());
                    set1f(uInWater, player.isInWater() ? 1.0F : 0.0F);
                    set1f(uOnFire, player.isBurning() ? 1.0F : 0.0F);
                    set1f(uIsSneaking, player.isSneaking() ? 1.0F : 0.0F);
                    set1f(uIsSprinting, player.isSprinting() ? 1.0F : 0.0F);
                }

                set1f(uRed, overlay.red);
                set1f(uGreen, overlay.green);
                set1f(uBlue, overlay.blue);
                set1f(uAlpha, overlay.alpha);
                set1f(uSaturation, overlay.saturation);
                set1f(uContrast, overlay.contrast);
                set1f(uIntensity, overlay.intensity);
                set1f(uVignetteStrength, overlay.vignetteStrength);
                set1f(uVignetteRadius, overlay.vignetteRadius);
                set1f(uVignetteSoftness, overlay.vignetteSoftness);
                set3f(uVignetteColor, overlay.vignetteRed, overlay.vignetteGreen, overlay.vignetteBlue);
                set1f(uVignetteAlpha, overlay.vignetteAlpha);
                set1f(uVignetteSaturation, overlay.vignetteSaturation);
                set1f(uVignetteContrast, overlay.vignetteContrast);
                set1f(uVignetteIntensity, overlay.vignetteIntensity);
                set1f(uFlashIntensity, overlay.flashIntensity);
                set3f(uFlashColor, overlay.flashRed, overlay.flashGreen, overlay.flashBlue);
                set1f(uCustom0, overlay.custom0);
                set1f(uCustom1, overlay.custom1);
                set1f(uCustom2, overlay.custom2);
                set1f(uCustom3, overlay.custom3);
                set1f(uCustom4, overlay.custom4);
                set1f(uCustom5, overlay.custom5);
                set1f(uCustom6, overlay.custom6);
                set1f(uCustom7, overlay.custom7);
                set3f(uCustomColor0, overlay.customColor0R, overlay.customColor0G, overlay.customColor0B);
                set3f(uCustomColor1, overlay.customColor1R, overlay.customColor1G, overlay.customColor1B);
                set3f(uCustomColor2, overlay.customColor2R, overlay.customColor2G, overlay.customColor2B);
                set3f(uCustomColor3, overlay.customColor3R, overlay.customColor3G, overlay.customColor3B);
                set1f(uBlurRadius, overlay.blurRadius);
                set1f(uDistortStrength, overlay.distortStrength);
                set1f(uPixelSize, overlay.pixelSize);
                set1f(uChromAberration, overlay.chromAberration);
                set1f(uScanlineStrength, overlay.scanlineStrength);
                set1f(uScanlineCount, overlay.scanlineCount);
                set1f(uNoiseStrength, overlay.noiseStrength);
                set1f(uNoiseSpeed, overlay.noiseSpeed);
                set1f(uBloomThreshold, overlay.bloomThreshold);
                set1f(uBloomIntensity, overlay.bloomIntensity);
                set1f(uHue, overlay.hue);
                set1f(uTemperature, overlay.temperature);
                set1f(uSharpness, overlay.sharpness);
                set1f(uGamma, overlay.gamma);
                set1f(uWaveAmplitude, overlay.waveAmplitude);
                set1f(uWaveFrequency, overlay.waveFrequency);
                set1f(uWaveSpeed, overlay.waveSpeed);
                set1f(uDofFocus, overlay.dofFocus);
                set1f(uDofStrength, overlay.dofStrength);
                GL11.glMatrixMode(5889);
                GL11.glPushMatrix();
                GL11.glLoadIdentity();
                GL11.glOrtho((double)-1.0F, (double)1.0F, (double)-1.0F, (double)1.0F, (double)-1.0F, (double)1.0F);
                GL11.glMatrixMode(5888);
                GL11.glPushMatrix();
                GL11.glLoadIdentity();
                GL11.glBegin(7);
                GL11.glTexCoord2f(0.0F, 0.0F);
                GL11.glVertex2f(-1.0F, -1.0F);
                GL11.glTexCoord2f(1.0F, 0.0F);
                GL11.glVertex2f(1.0F, -1.0F);
                GL11.glTexCoord2f(1.0F, 1.0F);
                GL11.glVertex2f(1.0F, 1.0F);
                GL11.glTexCoord2f(0.0F, 1.0F);
                GL11.glVertex2f(-1.0F, 1.0F);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glMatrixMode(5889);
                GL11.glPopMatrix();
                GL11.glMatrixMode(5888);
                GL20.glUseProgram(0);
                fb.unbindFramebufferTexture();
            } catch (Exception e) {
                logError("Render error: " + e.getMessage());
                clear();
            }

        }
    }

    private static void drawFullscreenQuad() {
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho((double)-1.0F, (double)1.0F, (double)-1.0F, (double)1.0F, (double)-1.0F, (double)1.0F);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex2f(-1.0F, -1.0F);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex2f(1.0F, -1.0F);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex2f(1.0F, 1.0F);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex2f(-1.0F, 1.0F);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
    }

    /**
     * Toggle the fixed-function states we touch while drawing the shader.
     * Every change goes through GlStateManager (so its cache matches) AND a
     * raw GL call (so the real state matches even if the cache was stale).
     */
    private static void setRenderStates(boolean blend, boolean depth, boolean alpha, boolean cull, boolean lighting) {
        if (blend) {
            GlStateManager.enableBlend();
            GL11.glEnable(GL11.GL_BLEND);
        } else {
            GlStateManager.disableBlend();
            GL11.glDisable(GL11.GL_BLEND);
        }

        if (depth) {
            GlStateManager.enableDepth();
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        } else {
            GlStateManager.disableDepth();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }

        if (alpha) {
            GlStateManager.enableAlpha();
            GL11.glEnable(GL11.GL_ALPHA_TEST);
        } else {
            GlStateManager.disableAlpha();
            GL11.glDisable(GL11.GL_ALPHA_TEST);
        }

        if (cull) {
            GlStateManager.enableCull();
            GL11.glEnable(GL11.GL_CULL_FACE);
        } else {
            GlStateManager.disableCull();
            GL11.glDisable(GL11.GL_CULL_FACE);
        }

        if (lighting) {
            GlStateManager.enableLighting();
            GL11.glEnable(GL11.GL_LIGHTING);
        } else {
            GlStateManager.disableLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
        }
    }

    /* Create or resize the offscreen ping-pong framebuffer. */
    private static void ensureFbo(int width, int height) {
        if (fbo != -1 && fboWidth == width && fboHeight == height) {
            return;
        }

        deleteFbo();

        fbo = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(36160, fbo);

        fboTexture = GL11.glGenTextures();
        GL11.glBindTexture(3553, fboTexture);
        GL11.glTexImage2D(3553, 0, 32856, width, height, 0, 6408, 5121, (ByteBuffer)null);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10242, 33071);
        GL11.glTexParameteri(3553, 10243, 33071);
        GL11.glBindTexture(3553, 0);

        GL30.glFramebufferTexture2D(36160, 36064, 3553, fboTexture, 0);

        int status = GL30.glCheckFramebufferStatus(36160);
        GL30.glBindFramebuffer(36160, 0);

        if (status != 36053) {
            deleteFbo();
            throw new RuntimeException("Failed to create offscreen framebuffer (status 0x" + Integer.toHexString(status) + ")");
        }

        fboWidth = width;
        fboHeight = height;
    }

    private static void deleteFbo() {
        if (fboTexture != -1) {
            GL11.glDeleteTextures(fboTexture);
            fboTexture = -1;
        }

        if (fbo != -1) {
            GL30.glDeleteFramebuffers(fbo);
            fbo = -1;
        }

        fboWidth = -1;
        fboHeight = -1;
    }

    private static int loc(String n) {
        return GL20.glGetUniformLocation(program, n);
    }

    private static void set1f(int loc, float v) {
        if (loc != -1) {
            GL20.glUniform1f(loc, v);
        }

    }

    private static void set1i(int loc, int v) {
        if (loc != -1) {
            GL20.glUniform1i(loc, v);
        }

    }

    private static void set2f(int loc, float x, float y) {
        if (loc != -1) {
            GL20.glUniform2f(loc, x, y);
        }

    }

    private static void set3f(int loc, float x, float y, float z) {
        if (loc != -1) {
            GL20.glUniform3f(loc, x, y, z);
        }

    }

    /**
     * Validate that the given vertex/fragment sources compile and link without
     * touching the currently applied shader. Returns null on success, or an
     * error message describing what went wrong.
     */
    public static String validate(String vertexSource, String fragmentSource) {
        int vert = -1;
        int frag = -1;
        int prog = -1;

        try {
            vert = compileShader(35633, vertexSource);
            frag = compileShader(35632, fragmentSource);
            prog = GL20.glCreateProgram();
            GL20.glAttachShader(prog, vert);
            GL20.glAttachShader(prog, frag);
            GL20.glLinkProgram(prog);

            if (GL20.glGetProgrami(prog, 35714) == 0) {
                return "Link error:\n" + GL20.glGetProgramInfoLog(prog, 4096);
            }

            return null;
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            if (vert != -1) {
                GL20.glDeleteShader(vert);
            }

            if (frag != -1) {
                GL20.glDeleteShader(frag);
            }

            if (prog != -1) {
                GL20.glDeleteProgram(prog);
            }
        }
    }

    private static int compileShader(int type, String source) throws Exception {
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);
        if (GL20.glGetShaderi(shader, 35713) == 0) {
            String log = GL20.glGetShaderInfoLog(shader, 4096);
            GL20.glDeleteShader(shader);
            String typeName = type == 35633 ? "Vertex" : "Fragment";
            throw new Exception(typeName + " compile error:\n" + log);
        } else {
            return shader;
        }
    }

    private static void cleanup() {
        deleteFbo();

        if (compiledVertexSource != -1) {
            GL20.glDeleteShader(compiledVertexSource);
            compiledVertexSource = -1;
        }

        if (compiledFragmentSource != -1) {
            GL20.glDeleteShader(compiledFragmentSource);
            compiledFragmentSource = -1;
        }

        if (program != -1) {
            GL20.glDeleteProgram(program);
            program = -1;
        }

        uAspect = -1;
        uIResolution = -1;
        uResolution = -1;
        uScreenH = -1;
        uScreenW = -1;
        uITime = -1;
        uTime = -1;
        uText = -1;
        uTex = -1;
        uIsDay = -1;
        uSunAngle = -1;
        uDayTime = -1;
        uWorldTime = -1;
        uSunColor = -1;
        uSkyColor = -1;
        uSunBrightness = -1;
        uSunDir = -1;
        uAir = -1;
        uHunger = -1;
        uMaxHealth = -1;
        uHealth = -1;
        uPlayerRot = -1;
        uPlayerPos = -1;
        uIsSprinting = -1;
        uIsSneaking = -1;
        uOnFire = -1;
        uInWater = -1;
        uBrightness = -1;
        uFogColor = -1;
        uFogEnd = -1;
        uFogStart = -1;
        uIntensity = -1;
        uContrast = -1;
        uSaturation = -1;
        uAlpha = -1;
        uBlue = -1;
        uGreen = -1;
        uRed = -1;
        uVignetteSoftness = -1;
        uVignetteRadius = -1;
        uVignetteStrength = -1;
        uVignetteIntensity = -1;
        uVignetteContrast = -1;
        uVignetteSaturation = -1;
        uVignetteAlpha = -1;
        uVignetteColor = -1;
        uFlashColor = -1;
        uFlashIntensity = -1;
        uCustom7 = -1;
        uCustom6 = -1;
        uCustom5 = -1;
        uCustom4 = -1;
        uCustom3 = -1;
        uCustom2 = -1;
        uCustom1 = -1;
        uCustom0 = -1;
        uCustomColor3 = -1;
        uCustomColor2 = -1;
        uCustomColor1 = -1;
        uCustomColor0 = -1;
        uPixelSize = -1;
        uDistortStrength = -1;
        uBlurRadius = -1;
        uScanlineCount = -1;
        uScanlineStrength = -1;
        uChromAberration = -1;
        uNoiseSpeed = -1;
        uNoiseStrength = -1;
        uBloomIntensity = -1;
        uBloomThreshold = -1;
        uGamma = -1;
        uSharpness = -1;
        uTemperature = -1;
        uHue = -1;
        uWaveSpeed = -1;
        uWaveFrequency = -1;
        uWaveAmplitude = -1;
        uDofStrength = -1;
        uDofFocus = -1;
    }

    private static void logError(String message) {
        System.err.println("[ShaderRenderer] " + message);
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null) {
            mc.player.sendMessage(new TextComponentString(TextFormatting.RED + "[Shader] " + TextFormatting.WHITE + message));
        }

        try {
            Class<?> logsClass = Class.forName("mchorse.mappet.utils.Logs");
            Method logMethod = logsClass.getMethod("error", Object.class);
            logMethod.invoke((Object)null, "[Shader] " + message);
        } catch (Exception var4) {
        }

    }

    public static class OverlayParams {
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

        public OverlayParams() {
        }

        public void reset() {
            this.red = this.green = this.blue = this.alpha = 0.0F;
            this.saturation = this.contrast = this.intensity = 1.0F;
            this.vignetteStrength = this.vignetteAlpha = 0.0F;
            this.vignetteRadius = this.vignetteSoftness = 0.5F;
            this.vignetteRed = this.vignetteGreen = this.vignetteBlue = 0.0F;
            this.vignetteSaturation = this.vignetteContrast = this.vignetteIntensity = 1.0F;
            this.flashIntensity = 0.0F;
            this.flashRed = this.flashGreen = this.flashBlue = 1.0F;
            this.custom0 = this.custom1 = this.custom2 = this.custom3 = this.custom4 = this.custom5 = this.custom6 = this.custom7 = 0.0F;
            this.customColor0R = this.customColor0G = this.customColor0B = 1.0F;
            this.customColor1R = this.customColor1G = this.customColor1B = 0.0F;
            this.customColor2R = this.customColor2G = this.customColor2B = 0.0F;
            this.customColor3R = this.customColor3G = this.customColor3B = 0.0F;
            this.blurRadius = this.distortStrength = 0.0F;
            this.pixelSize = 1.0F;
            this.chromAberration = this.scanlineStrength = 0.0F;
            this.scanlineCount = 100.0F;
            this.noiseStrength = 0.0F;
            this.noiseSpeed = 1.0F;
            this.bloomThreshold = 0.8F;
            this.bloomIntensity = 0.0F;
            this.hue = this.temperature = this.sharpness = 0.0F;
            this.gamma = 1.0F;
            this.waveAmplitude = 0.0F;
            this.waveFrequency = 10.0F;
            this.waveSpeed = 1.0F;
            this.dofFocus = 0.5F;
            this.dofStrength = 0.0F;
        }

        public void loadFromData(ShaderData d) {
            this.red = d.red;
            this.green = d.green;
            this.blue = d.blue;
            this.alpha = d.alpha;
            this.saturation = d.saturation;
            this.contrast = d.contrast;
            this.intensity = d.intensity;
            this.vignetteStrength = d.vignetteStrength;
            this.vignetteRadius = d.vignetteRadius;
            this.vignetteSoftness = d.vignetteSoftness;
            this.vignetteRed = d.vignetteRed;
            this.vignetteGreen = d.vignetteGreen;
            this.vignetteBlue = d.vignetteBlue;
            this.vignetteAlpha = d.vignetteAlpha;
            this.vignetteSaturation = d.vignetteSaturation;
            this.vignetteContrast = d.vignetteContrast;
            this.vignetteIntensity = d.vignetteIntensity;
            this.flashIntensity = d.flashIntensity;
            this.flashRed = d.flashRed;
            this.flashGreen = d.flashGreen;
            this.flashBlue = d.flashBlue;
            this.custom0 = d.custom0;
            this.custom1 = d.custom1;
            this.custom2 = d.custom2;
            this.custom3 = d.custom3;
            this.custom4 = d.custom4;
            this.custom5 = d.custom5;
            this.custom6 = d.custom6;
            this.custom7 = d.custom7;
            this.customColor0R = d.customColor0R;
            this.customColor0G = d.customColor0G;
            this.customColor0B = d.customColor0B;
            this.customColor1R = d.customColor1R;
            this.customColor1G = d.customColor1G;
            this.customColor1B = d.customColor1B;
            this.customColor2R = d.customColor2R;
            this.customColor2G = d.customColor2G;
            this.customColor2B = d.customColor2B;
            this.customColor3R = d.customColor3R;
            this.customColor3G = d.customColor3G;
            this.customColor3B = d.customColor3B;
            this.blurRadius = d.blurRadius;
            this.distortStrength = d.distortStrength;
            this.pixelSize = d.pixelSize;
            this.chromAberration = d.chromAberration;
            this.scanlineStrength = d.scanlineStrength;
            this.scanlineCount = d.scanlineCount;
            this.noiseStrength = d.noiseStrength;
            this.noiseSpeed = d.noiseSpeed;
            this.bloomThreshold = d.bloomThreshold;
            this.bloomIntensity = d.bloomIntensity;
            this.hue = d.hue;
            this.temperature = d.temperature;
            this.sharpness = d.sharpness;
            this.gamma = d.gamma;
            this.waveAmplitude = d.waveAmplitude;
            this.waveFrequency = d.waveFrequency;
            this.waveSpeed = d.waveSpeed;
            this.dofFocus = d.dofFocus;
            this.dofStrength = d.dofStrength;
        }

        public void copyFrom(OverlayParams o) {
            this.red = o.red;
            this.green = o.green;
            this.blue = o.blue;
            this.alpha = o.alpha;
            this.saturation = o.saturation;
            this.contrast = o.contrast;
            this.intensity = o.intensity;
            this.vignetteStrength = o.vignetteStrength;
            this.vignetteRadius = o.vignetteRadius;
            this.vignetteSoftness = o.vignetteSoftness;
            this.vignetteRed = o.vignetteRed;
            this.vignetteGreen = o.vignetteGreen;
            this.vignetteBlue = o.vignetteBlue;
            this.vignetteAlpha = o.vignetteAlpha;
            this.vignetteSaturation = o.vignetteSaturation;
            this.vignetteContrast = o.vignetteContrast;
            this.vignetteIntensity = o.vignetteIntensity;
            this.flashIntensity = o.flashIntensity;
            this.flashRed = o.flashRed;
            this.flashGreen = o.flashGreen;
            this.flashBlue = o.flashBlue;
            this.custom0 = o.custom0;
            this.custom1 = o.custom1;
            this.custom2 = o.custom2;
            this.custom3 = o.custom3;
            this.custom4 = o.custom4;
            this.custom5 = o.custom5;
            this.custom6 = o.custom6;
            this.custom7 = o.custom7;
            this.customColor0R = o.customColor0R;
            this.customColor0G = o.customColor0G;
            this.customColor0B = o.customColor0B;
            this.customColor1R = o.customColor1R;
            this.customColor1G = o.customColor1G;
            this.customColor1B = o.customColor1B;
            this.customColor2R = o.customColor2R;
            this.customColor2G = o.customColor2G;
            this.customColor2B = o.customColor2B;
            this.customColor3R = o.customColor3R;
            this.customColor3G = o.customColor3G;
            this.customColor3B = o.customColor3B;
            this.blurRadius = o.blurRadius;
            this.distortStrength = o.distortStrength;
            this.pixelSize = o.pixelSize;
            this.chromAberration = o.chromAberration;
            this.scanlineStrength = o.scanlineStrength;
            this.scanlineCount = o.scanlineCount;
            this.noiseStrength = o.noiseStrength;
            this.noiseSpeed = o.noiseSpeed;
            this.bloomThreshold = o.bloomThreshold;
            this.bloomIntensity = o.bloomIntensity;
            this.hue = o.hue;
            this.temperature = o.temperature;
            this.sharpness = o.sharpness;
            this.gamma = o.gamma;
            this.waveAmplitude = o.waveAmplitude;
            this.waveFrequency = o.waveFrequency;
            this.waveSpeed = o.waveSpeed;
            this.dofFocus = o.dofFocus;
            this.dofStrength = o.dofStrength;
        }
    }
}
