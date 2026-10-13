package unkn0wn.ae.client.gui.scripts;

import unkn0wn.ae.client.renderer.Beautifier;

/**
 * Lazily-initialized singleton around {@link Beautifier}. Creating a
 * {@link Beautifier} spins up a fresh Nashorn engine and evaluates the whole
 * js-beautify bundle, so we keep exactly one instance around for the UI.
 */
public final class ScriptBeautifier {
    private ScriptBeautifier() {
    }

    private static Beautifier instance;

    /**
     * Beautify the given JavaScript code with the default options. If anything
     * goes wrong (engine failed to load, parse error, ...), the original code is
     * returned unchanged so the editor never ends up empty.
     */
    public static String beautify(String code) {
        try {
            if (instance == null) {
                instance = new Beautifier();
            }

            /* beautify.js failed to load (wrong resource path, etc.) */
            if (instance.engine == null) {
                return code;
            }

            return instance.beautify(code, Beautifier.getDefaultOptions());
        } catch (Exception e) {
            e.printStackTrace();

            return code;
        }
    }
}
