package unkn0wn.ae.api.scripts.mixins;

import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.common.tileentity.TileEntityModelSettings;
import mchorse.blockbuster.network.common.PacketModifyModelBlock;
import mchorse.mappet.api.scripts.code.ScriptWorld;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import unkn0wn.ae.utils.mixins.utils.MixinTargetName;

import java.util.Objects;

@Mixin(value = ScriptWorld.class, remap = false)
@MixinTargetName("mchorse.mappet.api.scripts.user.IScriptWorld")
public abstract class MixinScriptWorld {
    @Shadow private World world;

    @Shadow private BlockPos.MutableBlockPos pos;

    public void chunkLoad(int x, int z) {
        this.world.getChunkFromBlockCoords(this.pos.setPos(x, 0, z)).onLoad();
    }

    @Deprecated //see `getBBModelBlock(x, y, z)`
    @net.minecraftforge.fml.common.Optional.Method(modid = "blockbuster")
    private void setModelBlockMorphBlockbuster(String nbt, int x, int y, int z, boolean force) {
        try {
            AbstractMorph morph = MorphManager.INSTANCE.morphFromNBT(JsonToNBT.getTagFromJson(nbt));
            TileEntity tile = this.world.getTileEntity(new BlockPos(x, y, z));

            if (tile instanceof TileEntityModel) {
                TileEntityModel oldModel = (TileEntityModel) this.world.getTileEntity(new BlockPos(x, y, z));
                TileEntityModelSettings oldSettings = oldModel.getSettings().copy();
                TileEntityModel model = (TileEntityModel) tile;
                model.getSettings().copy(oldSettings);

                if (Objects.equals(model.morph.get(), morph) && !force) {
                    return;
                }

                model.setMorph(morph);

                PacketModifyModelBlock message = new PacketModifyModelBlock(model.getPos(), model, true);

                mchorse.blockbuster.network.Dispatcher.DISPATCHER.get().sendToAll(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Enable or disable a model block at a given position in this world.
     * It only works when Blockbuster mod is installed.
     *
     * <pre>{@code
     *     function main(c)
     *     {
     *         var pos = c.getSubject().getPosition()
     *         var enabled = true;
     *
     *         c.getWorld().setModelBlockEnabled(pos.x, pos.y, pos.z, enabled);
     *     }
     * }</pre>
     *
     * @param x X coordinate of a model block.
     * @param y Y coordinate of a model block.
     * @param z Z coordinate of a model block.
     * @param enabled Whether to enable or disable the model block.
     */
    @Deprecated //see `getBBModelBlock(x, y, z)`
    public void setModelBlockEnabled(int x, int y, int z, boolean enabled) {
        if (!this.world.isBlockLoaded(this.pos.setPos(x, y, z))) {
            return;
        }
        if (Loader.isModLoaded("blockbuster")) {
            this.setModelBlockEnabledBlockbuster(x, y, z, enabled);
        }
    }

    @Deprecated //see `getBBModelBlock(x, y, z)`
    @net.minecraftforge.fml.common.Optional.Method(modid = "blockbuster")
    private void setModelBlockEnabledBlockbuster(int x, int y, int z, boolean enabled) {
        try {
            TileEntity tile = this.world.getTileEntity(new BlockPos(x, y, z));

            if (tile instanceof TileEntityModel) {
                TileEntityModel model = (TileEntityModel) tile;
                model.getSettings().setEnabled(enabled);

                PacketModifyModelBlock message = new PacketModifyModelBlock(model.getPos(), model, true);

                mchorse.blockbuster.network.Dispatcher.DISPATCHER.get().sendToAll(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check whether a model block at a given position in this world is enabled or not.
     * It only works when Blockbuster mod is installed.
     *
     * <pre>{@code
     *     c.send(c.getWorld().isModelBlockEnabled(0, 4, 0));
     * }</pre>
     *
     * @param x X coordinate of a model block.
     * @param y Y coordinate of a model block.
     * @param z Z coordinate of a model block.
     */
    @Deprecated //see `getBBModelBlock(x, y, z)`
    public boolean isModelBlockEnabled(int x, int y, int z) {
        if (!this.world.isBlockLoaded(this.pos.setPos(x, y, z))) {
            return false;
        }
        if (Loader.isModLoaded("blockbuster")) {
            return this.isModelBlockEnabledBlockbuster(x, y, z);
        }

        return false;
    }

    @Deprecated //see `getBBModelBlock(x, y, z)`
    @Optional.Method(modid = "blockbuster")
    private boolean isModelBlockEnabledBlockbuster(int x, int y, int z) {
        try {
            TileEntity tile = this.world.getTileEntity(new BlockPos(x, y, z));

            if (tile instanceof TileEntityModel) {
                TileEntityModel model = (TileEntityModel) tile;
                return model.getSettings().isEnabled();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Set morph (from NBT) to a model block at given position in this world.
     * It only works when Blockbuster mod is installed.
     *
     * <pre>{@code
     *     function main(c)
     *     {
     *         var pos = c.getSubject().getPosition()
     *         var nbt = '{Settings:{Hands:1b},Name:"blockbuster.fred"}';
     *
     *         c.getWorld().setModelBlock(nbt, pos.x, pos.y, pos.z, true);
     *     }
     * }</pre>
     *
     * @param nbt NBT of a morph to be replaced for given model block.
     * @param x X coordinate of a model block.
     * @param y Y coordinate of a model block.
     * @param z Z coordinate of a model block.
     * @param force Force update model block to update the morph even if the morph is same.
     */
    @Deprecated //see `getBBModelBlock(x, y, z)`
    public void setModelBlockMorph(String nbt, int x, int y, int z, boolean force) {
        if (!this.world.isBlockLoaded(this.pos.setPos(x, y, z))) {
            return;
        }
        if (Loader.isModLoaded("blockbuster")) {
            this.setModelBlockMorphBlockbuster(nbt, x, y, z, force);
        }
    }
}
