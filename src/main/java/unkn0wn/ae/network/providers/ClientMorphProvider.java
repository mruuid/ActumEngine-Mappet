package unkn0wn.ae.network.providers;

import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.nbt.NBTTagCompound;
import unkn0wn.ae.utils.ClientData;
import unkn0wn.ae.utils.RenderingHandler;

/**
 * Client-side provider for the {@link ClientData#CLIENT_MORPH} packet. It reads
 * the morph NBT sent by {@code setMorph(morph, true)} and assigns it to the
 * first-person morph ({@link RenderingHandler#clientMorph}) which is drawn from
 * the player's own point of view.
 */
public class ClientMorphProvider implements IClientDataProvider {
    @Override
    public void setData(NBTTagCompound value) {
        NBTTagCompound morphTag = value.getCompoundTag(ClientData.CLIENT_MORPH.name());
        AbstractMorph morph = MorphManager.INSTANCE.morphFromNBT(morphTag);

        RenderingHandler.clientMorph.setDirect(morph);
    }
}
