package unkn0wn.ae.network.packets;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import unkn0wn.ae.AEMod;

public class PacketSoundLoop implements IMessage {
    public String sound = "";
    public float volume = 1.0F;
    public float pitch = 1.0F;
    public int ticks = 1;
    public boolean enabled = true;

    public PacketSoundLoop() {
    }

    public PacketSoundLoop(String sound, float volume, float pitch, int ticks, boolean enabled) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.ticks = ticks;
        this.enabled = enabled;
    }

    public PacketSoundLoop(String sound, boolean enabled) {
        this.sound = sound;
        this.volume = this.volume;
        this.pitch = this.pitch;
        this.ticks = this.ticks;
        this.enabled = enabled;
    }

    public void fromBytes(ByteBuf buf) {
        this.sound = ByteBufUtils.readUTF8String(buf);
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
        this.ticks = buf.readInt();
        this.enabled = buf.readBoolean();
    }

    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.sound);
        buf.writeFloat(this.volume);
        buf.writeFloat(this.pitch);
        buf.writeInt(this.ticks);
        buf.writeBoolean(this.enabled);
    }

    @SideOnly(Side.CLIENT)
    public static class ClientHandler extends ClientMessageHandler<PacketSoundLoop> {
        public void run(EntityPlayerSP player, PacketSoundLoop message) {
            if (message.enabled) {
                ResourceLocation rl = new ResourceLocation(message.sound);

                for(int i = AEMod.listSounds.size() - 1; i >= 0; --i) {
                    ISound existingSound = (ISound)AEMod.listSounds.get(i);
                    if (existingSound.getSoundLocation().toString().equals(rl.toString())) {
                        AEMod.listSounds.remove(i);
                        AEMod.listTicks.remove(i);
                        AEMod.listFirstTicks.remove(i);
                    }
                }

                ISound masterRecord = new PositionedSoundRecord(rl, SoundCategory.MASTER, message.volume, message.pitch, false, 0, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
                AEMod.listSounds.add(masterRecord);
                AEMod.listTicks.add(message.ticks);
                AEMod.listFirstTicks.add(message.ticks);
                Minecraft.getMinecraft().getSoundHandler().playSound(masterRecord);
            } else if (!message.enabled) {
                for(int i = AEMod.listSounds.size() - 1; i >= 0; --i) {
                    if (((ISound)AEMod.listSounds.get(i)).getSoundLocation().toString().equals(message.sound)) {
                        Minecraft.getMinecraft().getSoundHandler().stopSound((ISound)AEMod.listSounds.get(i));
                        AEMod.listSounds.remove(i);
                        AEMod.listFirstTicks.remove(i);
                        AEMod.listTicks.remove(i);
                    }
                }
            }
        }
    }
}
