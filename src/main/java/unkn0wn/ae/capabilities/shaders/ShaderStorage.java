package unkn0wn.ae.capabilities.shaders;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class ShaderStorage implements Capability.IStorage<IShader> {
    public ShaderStorage() {
    }

    public NBTBase writeNBT(Capability<IShader> capability, IShader instance, EnumFacing side) {
        return instance.serializeNBT();
    }

    public void readNBT(Capability<IShader> capability, IShader instance, EnumFacing side, NBTBase nbt) {
        if (nbt instanceof NBTTagCompound) {
            instance.deserializeNBT((NBTTagCompound)nbt);
        }

    }
}
