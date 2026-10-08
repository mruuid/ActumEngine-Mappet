package unkn0wn.ae.network.providers;

import net.minecraft.nbt.NBTTagCompound;
import net.optifine.shaders.Shaders;
import unkn0wn.ae.utils.ClientData;

public class ShaderProvider implements IClientDataProvider {
    public NBTTagCompound getData() {

        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.setString(ClientData.SHADER.toString(), Shaders.getShaderPack().getName());

        return nbtTagCompound;
    }

    @Override
    public void setData(NBTTagCompound data) {}
}