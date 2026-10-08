package unkn0wn.ae.network;

import mchorse.mclib.network.AbstractDispatcher;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import unkn0wn.ae.AEMod;
import unkn0wn.ae.network.packets.*;

public class Dispatcher {
    public static final AbstractDispatcher DISPATCHER = new AbstractDispatcher(AEMod.MOD_ID) {
        @Override
        public void register() {
            this.register(PacketClientData.class, PacketClientData.ClientHandler.class, Side.CLIENT);
            this.register(PacketClientData.class, PacketClientData.ServerHandler.class, Side.SERVER);

            this.register(PacketCapability.class, PacketCapability.ClientHandler.class, Side.CLIENT);

            this.register(PacketEvent.class, PacketEvent.ServerHandler.class, Side.SERVER);

            this.register(PacketUploadFile.class, PacketUploadFile.ServerHandler.class, Side.SERVER);

            this.register(PacketCameraOver.class, PacketCameraOver.Handler.class, Side.CLIENT);
            this.register(PacketCameraOver.class, PacketCameraOver.Handler.class, Side.SERVER);

            this.register(PacketSounds.class, PacketSounds.ClientHandler.class, Side.CLIENT);

            this.register(PacketTextures.class, PacketTextures.ClientHandler.class, Side.CLIENT);
        }
    };

    public static void sendTo(IMessage message, EntityPlayerMP player) {
        DISPATCHER.sendTo(message, player);
    }

    public static void sendToAll(IMessage message) {
        DISPATCHER.sendToAll(message);
    }

    public static void sendToServer(IMessage message) {
        DISPATCHER.sendToServer(message);
    }

    public static void register() {
        DISPATCHER.register();
    }
}
