package unkn0wn.ae.network.providers;

import net.minecraft.nbt.NBTTagCompound;

public interface IClientDataProvider{
    default void setData(NBTTagCompound value) {};
    default void setData() {};

    default NBTTagCompound getData() {
        return new NBTTagCompound();
    }

    default NBTTagCompound getData(NBTTagCompound nbtTagCompound) {
        return nbtTagCompound;
    }
}