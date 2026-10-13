package unkn0wn.ae.capabilities.shaders;

import net.minecraft.util.math.BlockPos;

public class ShaderLightSource {
    public final BlockPos pos;
    public int ticksLeft;
    public final int totalTicks;
    public final int lightLevel;
    public boolean blockRemoved = false;

    public ShaderLightSource(BlockPos pos, int ticksLeft, int lightLevel) {
        this.pos = pos;
        this.ticksLeft = ticksLeft;
        this.totalTicks = ticksLeft;
        this.lightLevel = lightLevel;
    }
}
