package unkn0wn.ae.api.scripts.user.renderer;

import unkn0wn.ae.api.shaders.ShaderData;

/**
 * Script API for controlling screen shaders.
 *
 * <p>Shaders are addressed by their id (the name under which they are stored
 * in the world's {@code mappet/shaders} folder). Several shaders can be
 * "opened" at once — the renderer draws the one that was opened last, and
 * closing it falls back to the previously opened one.</p>
 *
 * <p>Client-side: like the dashboard panel, these calls affect the local
 * player's renderer (use them from client-side script contexts).</p>
 */
public interface IScriptShaders {
    void setShaders(double var1, double var3, double var5, double var7, int var9, double var10, double var12, double var14, boolean var16);

    void setVignette(boolean var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, int var16, double var17, double var19, double var21);

    boolean isEnabled();

    ShaderData openShaders(String data);

    boolean closeShaders(String data);

    int closeAllShaders();
}
