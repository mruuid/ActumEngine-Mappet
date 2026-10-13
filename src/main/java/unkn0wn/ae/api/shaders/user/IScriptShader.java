package unkn0wn.ae.api.shaders.user;

/**
 * Scripting API for controlling a fullscreen GLSL shader at runtime.
 *
 * <p>These map directly onto {@code unkn0wn.ae.utils.shaders.ShaderRenderer}.
 * A uniform only takes effect while a shader is active and the shader actually
 * declares that uniform (unknown uniforms are silently ignored).</p>
 */
public interface IScriptShader {
    /**
     * Whether a shader is currently applied and running.
     */
    boolean isActive();

    /**
     * Set a float uniform by name. No-op if the shader isn't active or doesn't
     * declare the uniform.
     */
    void setUniform(String name, float value);

    /**
     * Set a vec2 uniform by name.
     */
    void setUniform2f(String name, float x, float y);

    /**
     * Set a vec3 uniform by name.
     */
    void setUniform3f(String name, float x, float y, float z);

    /**
     * Set a vec4 uniform by name.
     */
    void setUniform4f(String name, float x, float y, float z, float w);

    /**
     * Set an int/sampler uniform by name.
     */
    void setUniformi(String name, int value);

    /**
     * Override the shader's time (in seconds). Pass a negative value to restore
     * the automatic time based on when the shader was applied.
     */
    void setTime(float seconds);
}
