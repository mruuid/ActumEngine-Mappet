package unkn0wn.ae.capabilities.shaders;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class ShaderProvider implements ICapabilitySerializable<NBTBase> {
    @CapabilityInject(IShader.class)
    public static final Capability<IShader> SHADER = null;
    private IShader instance;

    public ShaderProvider() {
        this.instance = (IShader)SHADER.getDefaultInstance();
    }

    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == SHADER;
    }

    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return (T)(this.hasCapability(capability, facing) ? SHADER.cast(this.instance) : null);
    }

    public NBTBase serializeNBT() {
        return SHADER.getStorage().writeNBT(SHADER, this.instance, (EnumFacing)null);
    }

    public void deserializeNBT(NBTBase nbt) {
        SHADER.getStorage().readNBT(SHADER, this.instance, (EnumFacing)null, nbt);
    }
}
