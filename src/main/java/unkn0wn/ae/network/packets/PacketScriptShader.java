package unkn0wn.ae.network.packets;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import unkn0wn.ae.AEMod;
import unkn0wn.ae.api.shaders.ShaderData;
import unkn0wn.ae.utils.shaders.ShaderRenderer;

/**
 * Server -> client packet used by the scripting API ({@link unkn0wn.ae.api.scripts.user.renderer.IScriptShaders})
 * to open / close named screen shaders. Unlike {@link PacketShader} (which is
 * broadcast to everyone from the dashboard), this one targets a single player,
 * because scripts run on the server while {@link ShaderRenderer} lives on the
 * client.
 */
public class PacketScriptShader implements IMessage {
    public enum Operation {
        OPEN, CLOSE, CLOSE_ALL
    }

    public Operation operation;
    public String id;

    public PacketScriptShader() {
    }

    public PacketScriptShader(Operation operation, String id) {
        this.operation = operation;
        this.id = id == null ? "" : id;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.operation = Operation.valueOf(ByteBufUtils.readUTF8String(buf));
        this.id = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.operation.name());
        ByteBufUtils.writeUTF8String(buf, this.id);
    }

    public static class ClientHandler extends ClientMessageHandler<PacketScriptShader> {
        @Override
        @SideOnly(Side.CLIENT)
        public void run(EntityPlayerSP player, PacketScriptShader message) {
            switch (message.operation) {
                case OPEN:
                    ShaderData data = AEMod.getShadersManager().load(message.id);
                    ShaderRenderer.openShader(message.id, data);
                    break;
                case CLOSE:
                    ShaderRenderer.closeShader(message.id);
                    break;
                case CLOSE_ALL:
                    ShaderRenderer.closeAllShaders();
                    break;
            }
        }
    }
}
