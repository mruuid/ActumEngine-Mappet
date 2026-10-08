package unkn0wn.ae.capabilities.hud;

import mchorse.mappet.api.scripts.user.data.ScriptVector;
import net.minecraft.nbt.NBTTagCompound;
import unkn0wn.ae.api.scripts.code.ScriptVectorAngle;

public interface IHud {
    ScriptVector getScale();
    void setScale(double x, double y);

    ScriptVector getPosition();
    void setPosition(double x, double y);

    ScriptVectorAngle getRotate();
    void setRotate(double angle, double x, double y, double z);

    boolean isCanceled();
    void setCanceled(boolean canceled);

    void setName(String name);
    String getName();

    NBTTagCompound serializeNBT();
    void deserializeNBT(NBTTagCompound tag);
}
