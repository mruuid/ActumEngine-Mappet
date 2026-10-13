package unkn0wn.ae.api.shaders.user;

/**
 * Documents the GLSL built-ins that are available inside a user-authored
 * vertex shader compiled by {@code ShaderRenderer}.
 *
 * <p>A vertex shader runs once per vertex and must transform the incoming
 * position into clip space by writing {@code gl_Position}. ActumEngine compiles
 * vertex shaders with {@code #version 120} and feeds them a single fullscreen
 * quad, so the standard fixed-function attributes and matrices below are the
 * ones you will actually use.</p>
 */
public interface IScriptVertexAPI {
    /**
     * The vertex shader entry point. Required. Runs once per vertex and must
     * assign {@code gl_Position}.
     */
    void main();

    /**
     * {@code vec4} — output. The transformed vertex position in clip space.
     * Typically {@code gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;}.
     */
    void gl_Position();

    /**
     * {@code vec4} — input attribute. The object-space position of the current
     * vertex.
     */
    void gl_Vertex();

    /**
     * {@code vec4} — input attribute. Texture coordinate set 0 of the current
     * vertex (the only attribute the engine's fullscreen quad populates).
     */
    void gl_MultiTexCoord0();

    /**
     * {@code vec4 gl_TexCoord[]} — varying array forwarded to the fragment
     * shader. Assign {@code gl_TexCoord[0]} to pass UVs through, e.g.
     * {@code gl_TexCoord[0] = gl_MultiTexCoord0;}.
     */
    void gl_TexCoord();

    /**
     * {@code mat4} — combined model-view-projection matrix uniform.
     */
    void gl_ModelViewProjectionMatrix();

    /**
     * {@code mat4} — model-view matrix uniform.
     */
    void gl_ModelViewMatrix();

    /**
     * {@code mat4} — projection matrix uniform.
     */
    void gl_ProjectionMatrix();

    /**
     * {@code mat3} — normal matrix uniform, used to transform normals into eye
     * space.
     */
    void gl_NormalMatrix();

    /**
     * {@code vec3} — input attribute. The object-space normal of the current
     * vertex.
     */
    void gl_Normal();

    /**
     * {@code float} — output. The rasterized point size in pixels.
     */
    void gl_PointSize();
}
