package unkn0wn.ae.network.packets;

import io.netty.buffer.ByteBuf;
import mchorse.chameleon.animation.ActionConfig;
import mchorse.chameleon.animation.Animator;
import mchorse.chameleon.metamorph.ChameleonMorph;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.metamorph.api.models.IMorphProvider;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.Morphing;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import unkn0wn.ae.utils.ReflectionUtilsAnimation;
import unkn0wn.ae.utils.RenderingHandler;

import java.util.UUID;

public class PacketPlayAnimation implements IMessage {
    String animation;
    String uuid;
    boolean firstperson;

    public PacketPlayAnimation(String animation, String uuid, boolean firstperson) {
        this.animation = animation;
        this.uuid = uuid;
        this.firstperson = firstperson;
    }

    public PacketPlayAnimation() {
    }

    public void fromBytes(ByteBuf buf) {
        this.animation = ByteBufUtils.readUTF8String(buf);
        this.uuid = ByteBufUtils.readUTF8String(buf);
        this.firstperson = buf.readBoolean();
    }

    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.animation);
        ByteBufUtils.writeUTF8String(buf, this.uuid);
        buf.writeBoolean(this.firstperson);
    }

    @SideOnly(Side.CLIENT)
    public static class ClientHandler extends ClientMessageHandler<PacketPlayAnimation> {
        public void run(EntityPlayerSP player, PacketPlayAnimation message) {
            player.world.getLoadedEntityList().stream().filter((entity) -> entity.getUniqueID().equals(UUID.fromString(message.uuid))).forEach((entity) -> {
                AbstractMorph morph = this.getMorph(entity, message.firstperson);
                if (morph instanceof ChameleonMorph) {
                    ChameleonMorph chameleonMorph = (ChameleonMorph)morph;
                    Animator animator = (Animator) ReflectionUtilsAnimation.getAndInvokeMethod(ChameleonMorph.class, "getAnimator", chameleonMorph);
                    ActionConfig config = chameleonMorph.actions.getConfig(message.animation);
                    animator.addAction(animator.createAction(animator.animation, config, false));
                }

            });
        }

        public AbstractMorph getMorph(Entity entity, boolean firstperson) {
            if (entity instanceof IMorphProvider) {
                return ((IMorphProvider)entity).getMorph();
            } else if (entity instanceof EntityPlayer) {
                return firstperson ? RenderingHandler.clientMorph.get() : Morphing.get((EntityPlayer)entity).getCurrentMorph();
            } else {
                return null;
            }
        }
    }
}
