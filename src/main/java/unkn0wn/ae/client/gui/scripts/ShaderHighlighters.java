package unkn0wn.ae.client.gui.scripts;

import com.google.common.collect.ImmutableSet;
import mchorse.mappet.client.gui.scripts.highlights.Highlighters;
import mchorse.mappet.client.gui.scripts.utils.SyntaxHighlighter;

import java.io.File;
import java.util.regex.Pattern;

/**
 * GLSL syntax highlighter for the shader editors of ActumEngine.
 *
 * <p>The highlighter is stored exactly the same way as Mappet stores its own
 * JavaScript / Kotlin highlighters: {@code config/mappet/highlights/glsl.json}.
 * The file is generated on first use (just like Mappet generates
 * {@code js.json} and {@code kts.json}) and can be hand edited afterwards to
 * tweak the colors and keyword sets.</p>
 */
public class ShaderHighlighters
{
    /**
     * Name of the highlighter file (without extension) inside of
     * {@code config/mappet/highlights/}.
     */
    public static final String HIGHLIGHTER_NAME = "glsl";

    private static SyntaxHighlighter glsl;

    private ShaderHighlighters() {}

    /**
     * Get the GLSL highlighter, creating {@code glsl.json} on first use.
     */
    public static SyntaxHighlighter get()
    {
        if (glsl == null)
        {
            /* Make sure Mappet's highlighters folder and defaults are set up */
            Highlighters.initiate();

            File file = Highlighters.highlighterFile(HIGHLIGHTER_NAME);

            if (!file.isFile())
            {
                Highlighters.writeHighlighter(file, create());
            }

            glsl = Highlighters.readHighlighter(file);
        }

        return glsl;
    }

    /**
     * Create the default GLSL highlighter.
     *
     * <p>Keyword precedence inside of {@link SyntaxHighlighter} is:
     * primary (control flow) &gt; special (built-in variables) &gt; secondary
     * (types, qualifiers, preprocessor directives and function calls) &gt;
     * number literals.</p>
     */
    private static SyntaxHighlighter create()
    {
        SyntaxHighlighter highlighter = new SyntaxHighlighter();

        highlighter.operators = ImmutableSet.of("+", "-", "=", "/", "*", "<", ">", "~", "&", "|", "!", "%", "^", "?", ":");

        highlighter.primaryKeywords = ImmutableSet.of(
                "if", "else", "for", "while", "do", "switch", "case", "default",
                "break", "continue", "return", "discard"
        );

        highlighter.secondaryKeywords = ImmutableSet.of(
                /* Preprocessor directives (come after #) */
                "version", "extension", "pragma", "define", "undef", "include",
                "ifdef", "ifndef", "elif", "endif", "line", "error",
                /* Storage classes and qualifiers */
                "const", "uniform", "varying", "attribute", "in", "out", "inout",
                "precision", "highp", "mediump", "lowp", "flat", "smooth", "centroid",
                "layout", "struct", "coherent", "volatile", "restrict",
                /* Scalar types */
                "void", "bool", "int", "uint", "float", "double",
                /* Vector types */
                "vec2", "vec3", "vec4", "ivec2", "ivec3", "ivec4",
                "bvec2", "bvec3", "bvec4", "dvec2", "dvec3", "dvec4",
                /* Matrix types */
                "mat2", "mat3", "mat4",
                "mat2x2", "mat2x3", "mat2x4", "mat3x2", "mat3x3", "mat3x4",
                "mat4x2", "mat4x3", "mat4x4",
                /* Sampler types */
                "sampler2D", "samplerCube", "sampler3D", "sampler2DShadow",
                "samplerCubeShadow", "sampler2DArray", "isampler2D", "usampler2D"
        );

        highlighter.special = ImmutableSet.of(
                /* Built-in (predefined) output variables */
                "gl_Position", "gl_PointSize", "gl_PointCoord",
                "gl_FragColor", "gl_FragCoord", "gl_FragDepth", "gl_FragData",
                /* Built-in vertex attributes */
                "gl_Vertex", "gl_Normal", "gl_Color", "gl_SecondaryColor",
                "gl_MultiTexCoord0", "gl_MultiTexCoord1", "gl_MultiTexCoord2", "gl_MultiTexCoord3",
                "gl_TexCoord", "gl_VertexID", "gl_InstanceID",
                /* Built-in transformation matrices */
                "gl_ModelViewMatrix", "gl_ProjectionMatrix", "gl_ModelViewProjectionMatrix",
                "gl_NormalMatrix", "gl_ModelViewMatrixInverse", "gl_TextureMatrix",
                /* Other built-ins */
                "gl_DepthRange", "gl_ClipPlane", "gl_ClipDistance"
        );

        highlighter.typeKeywords = ImmutableSet.of("true", "false");

        highlighter.functionName = Pattern.compile("[\\w_][\\d\\w_]*", Pattern.CASE_INSENSITIVE);

        return highlighter;
    }
}
