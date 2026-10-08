package unkn0wn.ae.api.scripts.mixins;

import mchorse.mappet.api.scripts.code.ScriptFactory;
import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixin;
import unkn0wn.ae.api.scripts.code.ScriptMatrix;
import unkn0wn.ae.api.scripts.code.ScriptVectorAngle;
import unkn0wn.ae.api.scripts.user.IScriptMatrix;
import unkn0wn.ae.api.scripts.user.IScriptVectorAngle;
import unkn0wn.ae.utils.mixins.utils.MixinTargetName;

@Mixin(value = ScriptFactory.class, remap = false)
@MixinTargetName("mchorse.mappet.api.scripts.user.IScriptFactory")
public abstract class MixinScriptFactory {
    /**
     * Creates and returns a new {@link IScriptMatrix} object with the specified number of rows and columns.
     *
     * @param rows the number of rows in the new matrix
     * @param cols the number of columns in the new matrix
     * @return a new {@link IScriptMatrix} object with the specified dimensions
     */
    public ScriptMatrix createMatrix(int rows, int cols) {
        return new ScriptMatrix(rows, cols);
    }

    /**
     * Checks if the mod is loaded with the specified ID.
     *
     * @param id
     */
    public boolean isModLoaded(String id) {
        return Loader.isModLoaded(id);
    }

    /**
     * create {@link IScriptVectorAngle}
     *
     * @param angle double
     * @param x double
     * @param y double
     * @param z double
     */
    public IScriptVectorAngle vectorAngle(double angle, double x, double y, double z) {
        return new ScriptVectorAngle(angle, x, y, z);
    }
}
