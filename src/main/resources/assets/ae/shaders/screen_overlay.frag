#version 120

uniform sampler2D tex;
uniform float red;
uniform float green;
uniform float blue;
uniform float alpha;
uniform float saturation;
uniform float contrast; // Теперь это значение работает как коэффициент глубины тени
uniform float intensity;

uniform float vignetteStrength;
uniform float vignetteRadius;
uniform float vignetteSoftness;
uniform vec3  vignetteColor;
uniform float vignetteAlpha;
uniform float vignetteSaturation;
uniform float vignetteContrast;
uniform float vignetteIntensity;

void main() {
    vec2 uv = gl_TexCoord[0].xy;
    vec4 screen = texture2D(tex, uv);

    float gray = dot(screen.rgb, vec3(0.299, 0.587, 0.114));
    vec3 result = mix(vec3(gray), screen.rgb, saturation);

    result = pow(max(result, vec3(0.0)), vec3(contrast));

    result = clamp(result, 0.0, 1.0);
    result = result * clamp(intensity, 0.0, 1.0);

    vec3 tint = vec3(red, green, blue);
    result = mix(result, tint, alpha);

    if (vignetteStrength > 0.001 || vignetteAlpha > 0.001) {
        vec2 center = vec2(0.5, 0.5);
        float dist = distance(uv, center);
        float r = vignetteRadius * 0.8 + 0.2;
        float soft = max(vignetteSoftness * 0.5, 0.001);
        float vignetteFactor = smoothstep(r - soft, r + soft, dist);
        float vigEdge = vignetteFactor * vignetteStrength;

        vec3 edgeColor = mix(result, vignetteColor, vigEdge * vignetteAlpha);

        float edgeGray = dot(edgeColor, vec3(0.299, 0.587, 0.114));
        edgeColor = mix(vec3(edgeGray), edgeColor, vignetteSaturation);
    
        edgeColor = (edgeColor - 0.15) * vignetteContrast + 0.15;
        edgeColor = clamp(edgeColor, 0.0, 1.0);
        edgeColor = edgeColor * vignetteIntensity;

        result = mix(result, edgeColor, vigEdge);
    }

    gl_FragColor = vec4(result, screen.a);
}