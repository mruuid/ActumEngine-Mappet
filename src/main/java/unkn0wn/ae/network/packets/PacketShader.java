package unkn0wn.ae.network.packets;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import unkn0wn.ae.AEMod;
import unkn0wn.ae.api.shaders.ShaderData;
import unkn0wn.ae.network.Dispatcher;
import unkn0wn.ae.utils.shaders.ShaderRenderer;

/**
 * Applies or clears a screen shader. Sent from the client (dashboard "Run" /
 * "Stop") to the server, which then broadcasts to all players. Clients apply it
 * locally via {@link ShaderRenderer}.
 */
public class PacketShader implements IMessage {
    public String id;
    public boolean clear;

    public PacketShader() {
    }

    public PacketShader(String id, boolean clear) {
        this.id = id;
        this.clear = clear;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.clear = buf.readBoolean();
        this.id = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.clear);
        ByteBufUtils.writeUTF8String(buf, this.id == null ? "" : this.id);
    }

    public static class ClientHandler extends ClientMessageHandler<PacketShader> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketShader message) {
            if (message.clear) {
                ShaderRenderer.clear();
            } else {
                ShaderData data = AEMod.getShadersManager().load(message.id);
                ShaderRenderer.apply(data);
            }
        }
    }

    public static class ServerHandler extends ServerMessageHandler<PacketShader> {
        @Override
        public void run(EntityPlayerMP player, PacketShader message) {
            /* Broadcast the request to every player (including the sender). */
            Dispatcher.sendToAll(new PacketShader(message.id, message.clear));
        }
    }
}
