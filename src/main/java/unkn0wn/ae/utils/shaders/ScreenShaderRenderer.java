package unkn0wn.ae.utils.shaders;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import unkn0wn.ae.capabilities.shaders.Shader;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL11.glBindTexture;

public class ScreenShaderRenderer {
    private static int programId = -1;
    private static int textureId = -1;
    private static int lastWidth = -1;
    private static int lastHeight = -1;

    public ScreenShaderRenderer() {
    }

    public static void init() {
        try {
            programId = createProgram();
            System.out.println("[ScreenShader] GLSL шейдер загружен.");
        } catch (Exception e) {
            System.err.println("[ScreenShader] Fallback: " + e.getMessage());
            programId = -1;
        }

    }

    public static void cleanup() {
        if (programId != -1) {
            GL20.glDeleteProgram(programId);
            programId = -1;
        }

        deleteTexture();
    }

    public static void renderShader(EntityPlayer player) {
        if (player != null) {
            Shader shader = Shader.get(player);
            if (shader != null && shader.hasAnythingToRender()) {
                float baseR;
                float baseG;
                float baseB;
                float baseA;
                float baseSat;
                float baseCon;
                float baseIns;
                if (shader.enabled) {
                    long now = System.currentTimeMillis();
                    if (shader.fadeStartTime < 0L) {
                        shader.fadeStartTime = now;
                    }

                    if (shader.fadingIn && shader.fadeInMs > 0L) {
                        float t = Math.min(1.0F, (float)(now - shader.fadeStartTime) / (float)shader.fadeInMs);
                        baseR = Shader.lerp(shader.startRed, shader.red, t);
                        baseG = Shader.lerp(shader.startGreen, shader.green, t);
                        baseB = Shader.lerp(shader.startBlue, shader.blue, t);
                        baseA = Shader.lerp(shader.startAlpha, shader.alpha, t);
                        baseSat = Shader.lerp(shader.startSaturation, shader.saturation, t);
                        baseCon = Shader.lerp(shader.startContrast, shader.contrast, t);
                        baseIns = Shader.lerp(shader.startIntensity, shader.intensity, t);
                        shader.currentRed = baseR;
                        shader.currentGreen = baseG;
                        shader.currentBlue = baseB;
                        shader.currentAlpha = baseA;
                        shader.currentSaturation = baseSat;
                        shader.currentContrast = baseCon;
                        shader.currentIntensity = baseIns;
                        if (t >= 1.0F) {
                            shader.fadingIn = false;
                            shader.fadeStartTime = -1L;
                        }
                    } else if (shader.fadingOut && shader.fadeOutMs > 0L) {
                        float t = Math.min(1.0F, (float)(now - shader.fadeStartTime) / (float)shader.fadeOutMs);
                        baseR = Shader.lerp(shader.startRed, shader.red, t);
                        baseG = Shader.lerp(shader.startGreen, shader.green, t);
                        baseB = Shader.lerp(shader.startBlue, shader.blue, t);
                        baseA = Shader.lerp(shader.startAlpha, 0.0F, t);
                        baseSat = Shader.lerp(shader.startSaturation, 1.0F, t);
                        baseCon = Shader.lerp(shader.startContrast, 1.0F, t);
                        baseIns = Shader.lerp(shader.startIntensity, 1.0F, t);
                        shader.currentRed = baseR;
                        shader.currentGreen = baseG;
                        shader.currentBlue = baseB;
                        shader.currentAlpha = baseA;
                        shader.currentSaturation = baseSat;
                        shader.currentContrast = baseCon;
                        shader.currentIntensity = baseIns;
                        if (t >= 1.0F) {
                            shader.fadingOut = false;
                            shader.enabled = false;
                            shader.fadeStartTime = -1L;
                            shader.resetShaderParameters();
                        }
                    } else {
                        baseR = shader.red;
                        baseG = shader.green;
                        baseB = shader.blue;
                        baseA = shader.alpha;
                        baseSat = shader.saturation;
                        baseCon = shader.contrast;
                        baseIns = shader.intensity;
                        shader.currentRed = baseR;
                        shader.currentGreen = baseG;
                        shader.currentBlue = baseB;
                        shader.currentAlpha = baseA;
                        shader.currentSaturation = baseSat;
                        shader.currentContrast = baseCon;
                        shader.currentIntensity = baseIns;
                    }
                } else {
                    baseR = 0.0F;
                    baseG = 0.0F;
                    baseB = 0.0F;
                    baseA = 0.0F;
                    baseSat = 1.0F;
                    baseCon = 1.0F;
                    baseIns = 1.0F;
                }

                float[] blended = shader.blendLayers(baseR, baseG, baseB, baseA, baseSat, baseCon, baseIns);
                float finalR = blended[0];
                float finalG = blended[1];
                float finalB = blended[2];
                float finalA = blended[3];
                float finalSat = blended[4];
                float finalCon = blended[5];
                float finalIns = blended[6];
                float vignetteStrength;
                float vignetteRadius;
                float vignetteSoftness;
                float vignetteRed;
                float vignetteGreen;
                float vignetteBlue;
                float vignetteAlpha;
                float vignetteSaturation;
                float vignetteContrast;
                float vignetteIntensity;
                if (!shader.vignetteEnabled && !shader.vignetteFadingOut) {
                    vignetteStrength = 0.0F;
                    vignetteRadius = 0.5F;
                    vignetteSoftness = 0.5F;
                    vignetteRed = 0.0F;
                    vignetteGreen = 0.0F;
                    vignetteBlue = 0.0F;
                    vignetteAlpha = 0.0F;
                    vignetteSaturation = 1.0F;
                    vignetteContrast = 1.0F;
                    vignetteIntensity = 1.0F;
                } else {
                    vignetteStrength = shader.vignetteStrength;
                    vignetteRadius = shader.vignetteRadius;
                    vignetteSoftness = shader.vignetteSoftness;
                    vignetteRed = shader.vignetteRed;
                    vignetteGreen = shader.vignetteGreen;
                    vignetteBlue = shader.vignetteBlue;
                    vignetteAlpha = shader.vignetteAlpha;
                    vignetteSaturation = shader.vignetteSaturation;
                    vignetteContrast = shader.vignetteContrast;
                    vignetteIntensity = shader.vignetteIntensity;
                }

                if (shader.vignetteEnabled || shader.vignetteFadingOut) {
                    long now = System.currentTimeMillis();
                    if (shader.vignetteFadeStartTime < 0L) {
                        shader.vignetteFadeStartTime = now;
                    }

                    if (shader.vignetteEnabled && shader.vignetteFadingIn && shader.vignetteFadeInMs > 0L) {
                        float t = Math.min(1.0F, (float)(now - shader.vignetteFadeStartTime) / (float)shader.vignetteFadeInMs);
                        vignetteStrength = Shader.lerp(shader.vignetteStartStrength, shader.vignetteStrength, t);
                        vignetteRadius = Shader.lerp(shader.vignetteStartRadius, shader.vignetteRadius, t);
                        vignetteSoftness = Shader.lerp(shader.vignetteStartSoftness, shader.vignetteSoftness, t);
                        vignetteRed = Shader.lerp(shader.vignetteStartRed, shader.vignetteRed, t);
                        vignetteGreen = Shader.lerp(shader.vignetteStartGreen, shader.vignetteGreen, t);
                        vignetteBlue = Shader.lerp(shader.vignetteStartBlue, shader.vignetteBlue, t);
                        vignetteAlpha = Shader.lerp(shader.vignetteStartAlpha, shader.vignetteAlpha, t);
                        vignetteSaturation = Shader.lerp(shader.vignetteStartSaturation, shader.vignetteSaturation, t);
                        vignetteContrast = Shader.lerp(shader.vignetteStartContrast, shader.vignetteContrast, t);
                        vignetteIntensity = Shader.lerp(shader.vignetteStartIntensity, shader.vignetteIntensity, t);
                        if (t >= 1.0F) {
                            shader.vignetteFadingIn = false;
                            shader.vignetteFadeStartTime = -1L;
                        }
                    } else if (shader.vignetteFadingOut && shader.vignetteFadeOutMs > 0L) {
                        float t = Math.min(1.0F, (float)(now - shader.vignetteFadeStartTime) / (float)shader.vignetteFadeOutMs);
                        vignetteStrength = Shader.lerp(shader.vignetteStartStrength, 0.0F, t);
                        vignetteRadius = Shader.lerp(shader.vignetteStartRadius, 0.5F, t);
                        vignetteSoftness = Shader.lerp(shader.vignetteStartSoftness, 0.5F, t);
                        vignetteRed = Shader.lerp(shader.vignetteStartRed, 0.0F, t);
                        vignetteGreen = Shader.lerp(shader.vignetteStartGreen, 0.0F, t);
                        vignetteBlue = Shader.lerp(shader.vignetteStartBlue, 0.0F, t);
                        vignetteAlpha = Shader.lerp(shader.vignetteStartAlpha, 0.0F, t);
                        vignetteSaturation = Shader.lerp(shader.vignetteStartSaturation, 1.0F, t);
                        vignetteContrast = Shader.lerp(shader.vignetteStartContrast, 1.0F, t);
                        vignetteIntensity = Shader.lerp(shader.vignetteStartIntensity, 1.0F, t);
                        if (t >= 1.0F) {
                            shader.vignetteEnabled = false;
                            shader.vignetteFadingOut = false;
                            shader.vignetteFadeStartTime = -1L;
                            shader.resetVignetteParameters();
                        }
                    }
                }

                boolean neutral = finalA <= 0.001F && vignetteAlpha <= 0.001F && Math.abs(finalSat - 1.0F) < 0.001F && Math.abs(finalCon - 1.0F) < 0.001F && Math.abs(finalIns - 1.0F) < 0.001F && Math.abs(vignetteStrength) < 0.001F && Math.abs(vignetteSaturation - 1.0F) < 0.001F && Math.abs(vignetteContrast - 1.0F) < 0.001F && Math.abs(vignetteIntensity - 1.0F) < 0.001F;
                if (!neutral) {
                    if (programId == -1) {
                        renderFallback(finalR, finalG, finalB, finalA);
                    } else {
                        Minecraft mc = Minecraft.getMinecraft();
                        int screenW = mc.displayWidth;
                        int screenH = mc.displayHeight;
                        if (screenW != lastWidth || screenH != lastHeight) {
                            createScreenTexture(screenW, screenH);
                            lastWidth = screenW;
                            lastHeight = screenH;
                        }

                        boolean blendWas = GL11.glIsEnabled(3042);
                        int src = GL11.glGetInteger(3041);
                        int dst = GL11.glGetInteger(3040);
                        glBindTexture(3553, textureId);
                        GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, screenW, screenH);
                        GL20.glUseProgram(programId);
                        GL20.glUniform1i(GL20.glGetUniformLocation(programId, "tex"), 0);
                        GL20.glUniform1f(GL20.glGetUniformLocation(programId, "red"), finalR);
                        GL20.glUniform1f(GL20.glGetUniformLocation(programId, "green"), finalG);
                        GL20.glUniform1f(GL20.glGetUniformLocation(programId, "blue"), finalB);
                        GL20.glUniform1f(GL20.glGetUniformLocation(programId, "alpha"), finalA);
                        GL20.glUniform1f(GL20.glGetUniformLocation(programId, "saturation"), finalSat);
                        GL20.glUniform1f(GL20.glGetUniformLocation(programId, "contrast"), finalCon);
                        GL20.glUniform1f(GL20.glGetUniformLocation(programId, "intensity"), finalIns);
                        GL20.glUniform1f(GL20.glGetUniformLocation(programId, "vignetteStrength"), vignetteStrength);
                        GL20.glUniform1f(GL20.glGetUniformLocation(programId, "vignetteRadius"), vignetteRadius);
                        GL20.glUniform1f(GL20.glGetUniformLocation(programId, "vignetteSoftness"), vignetteSoftness);
                        GL20.glUniform3f(GL20.glGetUniformLocation(programId, "vignetteColor"), vignetteRed, vignetteGreen, vignetteBlue);
                        GL20.glUniform1f(GL20.glGetUniformLocation(programId, "vignetteAlpha"), vignetteAlpha);
                        GL20.glUniform1f(GL20.glGetUniformLocation(programId, "vignetteSaturation"), vignetteSaturation);
                        GL20.glUniform1f(GL20.glGetUniformLocation(programId, "vignetteContrast"), vignetteContrast);
                        GL20.glUniform1f(GL20.glGetUniformLocation(programId, "vignetteIntensity"), vignetteIntensity);
                        ScaledResolution res = new ScaledResolution(mc);
                        int sw = res.getScaledWidth();
                        int sh = res.getScaledHeight();
                        GlStateManager.enableTexture2D();
                        GlStateManager.enableBlend();
                        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                        glBindTexture(3553, textureId);
                        Tessellator tess = Tessellator.getInstance();
                        BufferBuilder buf = tess.getBuffer();
                        buf.begin(7, DefaultVertexFormats.POSITION_TEX);
                        buf.pos((double)0.0F, (double)sh, (double)0.0F).tex((double)0.0F, (double)0.0F).endVertex();
                        buf.pos((double)sw, (double)sh, (double)0.0F).tex((double)1.0F, (double)0.0F).endVertex();
                        buf.pos((double)sw, (double)0.0F, (double)0.0F).tex((double)1.0F, (double)1.0F).endVertex();
                        buf.pos((double)0.0F, (double)0.0F, (double)0.0F).tex((double)0.0F, (double)1.0F).endVertex();
                        tess.draw();
                        GL20.glUseProgram(0);
                        glBindTexture(3553, 0);
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        if (!blendWas) {
                            GlStateManager.disableBlend();
                        } else {
                            GL11.glBlendFunc(src, dst);
                        }

                    }
                }
            }
        }
    }

    private static int createProgram() throws Exception {
        String vertSrc = "#version 120\nvoid main() {\n    gl_TexCoord[0] = gl_MultiTexCoord0;\n    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n}\n";
        String fragSrc = loadResource("/assets/ae/shaders/screen_overlay.frag");
        int vert = compileShader(35633, vertSrc);
        int frag = compileShader(35632, fragSrc);
        int prog = GL20.glCreateProgram();
        GL20.glAttachShader(prog, vert);
        GL20.glAttachShader(prog, frag);
        GL20.glLinkProgram(prog);
        if (GL20.glGetProgrami(prog, 35714) == 0) {
            throw new Exception("Link: " + GL20.glGetProgramInfoLog(prog, 512));
        } else {
            GL20.glDeleteShader(vert);
            GL20.glDeleteShader(frag);
            return prog;
        }
    }

    private static int compileShader(int type, String src) throws Exception {
        int id = GL20.glCreateShader(type);
        GL20.glShaderSource(id, src);
        GL20.glCompileShader(id);
        if (GL20.glGetShaderi(id, 35713) == 0) {
            throw new Exception((type == 35633 ? "Vert" : "Frag") + ": " + GL20.glGetShaderInfoLog(id, 512));
        } else {
            return id;
        }
    }

    private static void createScreenTexture(int w, int h) {
        deleteTexture();
        textureId = GL11.glGenTextures();
        glBindTexture(3553, textureId);
        GL11.glTexImage2D(3553, 0, 6408, w, h, 0, 6408, 5121, (ByteBuffer)null);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        glBindTexture(3553, 0);
    }

    private static void deleteTexture() {
        if (textureId != -1) {
            GL11.glDeleteTextures(textureId);
            textureId = -1;
        }

    }

    private static String loadResource(String path) throws Exception {
        InputStream is = ScreenShaderRenderer.class.getResourceAsStream(path);
        if (is == null) {
            throw new Exception("Not found: " + path);
        } else {
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        }
    }

    private static void renderFallback(float r, float g, float b, float a) {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        int w = res.getScaledWidth();
        int h = res.getScaledHeight();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(r, g, b, a);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(7, DefaultVertexFormats.POSITION);
        buf.pos((double)0.0F, (double)h, (double)0.0F).endVertex();
        buf.pos((double)w, (double)h, (double)0.0F).endVertex();
        buf.pos((double)w, (double)0.0F, (double)0.0F).endVertex();
        buf.pos((double)0.0F, (double)0.0F, (double)0.0F).endVertex();
        tess.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
