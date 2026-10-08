package unkn0wn.ae.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import unkn0wn.ae.AEMod;

public class PacketCameraOver implements IMessage {
    private boolean permission;

    public PacketCameraOver() {
    }

    public PacketCameraOver(boolean allow) { this.permission = allow; }

    public void fromBytes(ByteBuf buf) { this.permission = buf.readBoolean(); }

    public void toBytes(ByteBuf buf) { buf.writeBoolean(this.permission); }

    public static class Handler implements IMessageHandler<PacketCameraOver, IMessage> {
        public Handler() {
        }

        public IMessage onMessage(PacketCameraOver packet, MessageContext ctx) {
            if (ctx.side.isServer()) {
                return new PacketCameraOver(Minecraft.getMinecraft().getIntegratedServer().getEntityWorld().getWorldInfo().getGameType().getID() == 3);
            } else {
                if (packet.permission) {
                    Minecraft.getMinecraft().addScheduledTask(() -> {
                        AEMod.hasEyesHeight = true;
                        return true;
                    });
                }

                return null;
            }
        }
    }
}