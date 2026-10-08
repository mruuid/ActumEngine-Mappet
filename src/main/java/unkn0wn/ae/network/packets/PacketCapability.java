package unkn0wn.ae.network.packets;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import unkn0wn.ae.capabilities.CapabilitiesType;
import unkn0wn.ae.capabilities.camera.Camera;
import unkn0wn.ae.capabilities.hand.Hand;
import unkn0wn.ae.capabilities.hud.Hud;
import unkn0wn.ae.capabilities.skin.Skin;

public class PacketCapability implements IMessage {
    NBTTagCompound profile;
    CapabilitiesType type;
    public PacketCapability() {
    }

    public PacketCapability(NBTTagCompound profile, CapabilitiesType type) {
        this.profile = profile;
        this.type = type;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.profile = ByteBufUtils.readTag(buf);
        this.type = CapabilitiesType.valueOf(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, this.profile);
        ByteBufUtils.writeUTF8String(buf, this.type.toString());
    }


    public static class ClientHandler extends ClientMessageHandler<PacketCapability> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP entityPlayerSP, PacketCapability message) {
            CapabilitiesType type = message.type;
            EntityPlayerSP player = Minecraft.getMinecraft().player;

            switch (type){
                case CAMERA:
                    Camera.get(player).deserializeNBT(message.profile);
                    break;
                case HAND:
                    Hand.get(player).deserializeNBT(message.profile);
                    break;
                case HUD:
                    Hud.get(player).deserializeNBT(message.profile);
                    break;
                case SKIN:
                    Skin.get(player).deserializeNBT(message.profile);
                    break;
            }
        }
    }
}