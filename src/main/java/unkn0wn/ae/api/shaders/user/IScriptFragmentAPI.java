package unkn0wn.ae.api.shaders.user;

/**
 * Documents the GLSL built-ins that are available inside a user-authored
 * fragment (pixel) shader compiled by {@code ShaderRenderer}.
 *
 * <p>A fragment shader runs once per pixel and must write the final colour to
 * {@code gl_FragColor}. ActumEngine compiles fragment shaders with
 * {@code #version 120}, enabling {@code GL_OES_standard_derivatives} when
 * available, so the derivative functions ({@code dFdx}, {@code dFdy},
 * {@code fwidth}) are safe to use.</p>
 *
 * <p>Custom uniforms bound by the engine (see {@link IScriptShader}) are also
 * readable here, e.g. {@code tex}, {@code iTime}, {@code iResolution},
 * {@code iPlayerPos}, {@code iSunDir}, etc. A uniform only takes effect while
 * the shader is active and actually declares it.</p>
 */
public interface IScriptFragmentAPI {
    /**
     * The fragment shader entry point. Required. Runs once per fragment and
     * must assign a value to {@code gl_FragColor}.
     */
    void main();

    /**
     * {@code vec4} — the only mandatory output. Write the final RGBA colour of
     * the fragment here, in the {@code [0, 1]} range.
     */
    void gl_FragColor();

    /**
     * {@code vec4} — window-relative coordinate {@code (x, y, z, 1/w)} of the
     * current fragment. Useful for procedural effects that need pixel position.
     */
    void gl_FragCoord();

    /**
     * {@code vec2} floating-point vector type.
     */
    void vec2();

    /**
     * {@code vec3} floating-point vector type.
     */
    void vec3();

    /**
     * {@code vec4} floating-point vector type.
     */
    void vec4();

    /**
     * {@code float} scalar type.
     */
    void float_();

    /**
     * {@code int} scalar type.
     */
    void int_();

    /**
     * {@code bool} scalar type.
     */
    void bool();

    /**
     * {@code sampler2D} — handle to a 2D texture uniform (e.g. {@code tex}).
     */
    void sampler2D();

    /**
     * {@code vec4 texture2D(sampler2D sampler, vec2 coord)} — legacy GLSL 120
     * texture lookup. Samples {@code sampler} at {@code coord} (UV in the
     * {@code [0, 1]} range).
     */
    void texture2D();

    /**
     * {@code texture(sampler, coord)} — modern texture lookup (GLSL 130+).
     * Provided for forward-compatibility; prefer {@code texture2D} for
     * {@code #version 120}.
     */
    void texture();

    /**
     * {@code float sin(float)} / {@code genType sin(genType)} — sine,
     * component-wise on vectors.
     */
    void sin();

    /**
     * {@code float cos(float)} / {@code genType cos(genType)} — cosine,
     * component-wise on vectors.
     */
    void cos();

    /**
     * {@code float tan(float)} / {@code genType tan(genType)} — tangent.
     */
    void tan();

    /**
     * {@code float asin(float)} / {@code genType asin(genType)} — arc sine.
     */
    void asin();

    /**
     * {@code float acos(float)} / {@code genType acos(genType)} — arc cosine.
     */
    void acos();

    /**
     * {@code float atan(float)} / {@code genType atan(genType)} — arc tangent.
     */
    void atan();

    /**
     * {@code float pow(float x, float y)} / {@code genType pow(genType, genType)}
     * — {@code x} raised to the power {@code y}.
     */
    void pow();

    /**
     * {@code float exp(float)} / {@code genType exp(genType)} — natural
     * exponentiation ({@code e^x}).
     */
    void exp();

    /**
     * {@code float log(float)} / {@code genType log(genType)} — natural
     * logarithm.
     */
    void log();

    /**
     * {@code float sqrt(float)} / {@code genType sqrt(genType)} — square root.
     */
    void sqrt();

    /**
     * {@code float abs(float)} / {@code genType abs(genType)} — absolute
     * value.
     */
    void abs();

    /**
     * {@code float floor(float)} / {@code genType floor(genType)} — nearest
     * integer less than or equal to {@code x}.
     */
    void floor();

    /**
     * {@code float ceil(float)} / {@code genType ceil(genType)} — nearest
     * integer greater than or equal to {@code x}.
     */
    void ceil();

    /**
     * {@code float fract(float)} / {@code genType fract(genType)} — fractional
     * part, {@code x - floor(x)}.
     */
    void fract();

    /**
     * {@code float mod(float x, float y)} / {@code genType mod(genType, float)}
     * — modulus, {@code x - y * floor(x / y)}.
     */
    void mod();

    /**
     * {@code float min(float, float)} / {@code genType min(genType, genType)} —
     * component-wise minimum.
     */
    void min();

    /**
     * {@code float max(float, float)} / {@code genType max(genType, genType)} —
     * component-wise maximum.
     */
    void max();

    /**
     * {@code float clamp(float x, float minVal, float maxVal)} — clamps
     * {@code x} into the {@code [minVal, maxVal]} range.
     */
    void clamp();

    /**
     * {@code float mix(float x, float y, float a)} / {@code genType mix(...)} —
     * linear interpolation between {@code x} and {@code y} by {@code a}.
     */
    void mix();

    /**
     * {@code float step(float edge, float x)} — {@code 0.0} if {@code x < edge},
     * otherwise {@code 1.0}.
     */
    void step();

    /**
     * {@code float smoothstep(float edge0, float edge1, float x)} — Hermite
     * interpolation between {@code 0.0} and {@code 1.0}.
     */
    void smoothstep();

    /**
     * {@code float length(genType)} — Euclidean length (magnitude) of a vector.
     */
    void length();

    /**
     * {@code float distance(genType p0, genType p1)} — distance between two
     * points.
     */
    void distance();

    /**
     * {@code float dot(genType, genType)} — dot product of two vectors.
     */
    void dot();

    /**
     * {@code vec3 cross(vec3, vec3)} — cross product of two 3D vectors.
     */
    void cross();

    /**
     * {@code genType normalize(genType)} — returns a vector of unit length
     * pointing in the same direction.
     */
    void normalize();

    /**
     * {@code genType reflect(genType I, genType N)} — reflects incident vector
     * {@code I} about surface normal {@code N}.
     */
    void reflect();

    /**
     * {@code genType refract(genType I, genType N, float eta)} — refraction
     * vector for incident vector {@code I}, surface normal {@code N} and ratio
     * of indices of refraction {@code eta}.
     */
    void refract();

    /**
     * {@code float dFdx(float)} / {@code genType dFdx(genType)} — screen-space
     * derivative along {@code x} (requires {@code GL_OES_standard_derivatives}).
     */
    void dFdx();

    /**
     * {@code float dFdy(float)} / {@code genType dFdy(genType)} — screen-space
     * derivative along {@code y}.
     */
    void dFdy();

    /**
     * {@code float fwidth(float)} / {@code genType fwidth(genType)} —
     * {@code abs(dFdx(x)) + abs(dFdy(x))}.
     */
    void fwidth();
}
