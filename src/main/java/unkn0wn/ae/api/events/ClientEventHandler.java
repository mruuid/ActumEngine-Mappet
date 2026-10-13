package unkn0wn.ae.api.events;

import mchorse.mappet.api.scripts.user.data.ScriptVector;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import unkn0wn.ae.api.scripts.code.ScriptVectorAngle;
import unkn0wn.ae.capabilities.camera.Camera;
import unkn0wn.ae.capabilities.hand.Hand;
import unkn0wn.ae.capabilities.hud.Hud;
import unkn0wn.ae.network.Dispatcher;
import unkn0wn.ae.network.packets.PacketEvent;
import unkn0wn.ae.utils.EventType;
import unkn0wn.ae.utils.shaders.ShaderRenderer;

import java.util.Map;

public class ClientEventHandler {
    /** Minimum interval (ms) between outgoing render-state packets (20 Hz). */
    private static final long PACKET_INTERVAL_MS = 50L;

    private long lastCameraPacket = 0L;
    private long lastHandPacket = 0L;
    private long lastHudPacket = 0L;

    @SubscribeEvent
    public void cameraSetup(EntityViewRenderEvent.CameraSetup event) {
        Camera camera = Camera.get(Minecraft.getMinecraft().player);
        if (camera == null) {
            return;
        }

        if (!camera.isCanceled()) {
            ScriptVectorAngle rotate = camera.getRotate();
            ScriptVector scale = camera.getScale();
            ScriptVector translate = camera.getPosition();

            GlStateManager.rotate((float)rotate.angle, (float)rotate.x, (float)rotate.y, (float)rotate.z);
            GlStateManager.scale(scale.x, scale.y, scale.z);
            GlStateManager.translate(translate.x, translate.y, translate.z);
        }

        /* Camera events fire every frame. Sending a packet every frame floods the
         * connection and causes severe stutter/flicker while the world is still
         * loading. Throttle to 20 Hz. */
        long now = System.currentTimeMillis();
        if (now - lastCameraPacket < PACKET_INTERVAL_MS) {
            return;
        }
        lastCameraPacket = now;

        NBTTagCompound data = new NBTTagCompound();
        Dispatcher.sendToServer(new PacketEvent(EventType.CAMERA, data));
    }

    @SubscribeEvent
    public void renderHandEvent(RenderSpecificHandEvent event) {
        Hand hand = Hand.get(Minecraft.getMinecraft().player);
        if (hand == null) {
            return;
        }

        hand.setTypeHand(event.getHand() == EnumHand.MAIN_HAND ? 0 : 1);

        ScriptVector position = hand.getPosition();
        ScriptVectorAngle rotate = hand.getRotate();
        boolean canceled = hand.isCanceled();

        boolean customMorph = !hand.morph.isEmpty();
        AbstractMorph morph = customMorph ? hand.morph.get() : null;
        boolean customSkin = hand.skinPath != null && !hand.skinPath.isEmpty();

        /* Take over vanilla arm rendering when the script installed a hand morph
         * or a custom skin (and didn't hide the hand explicitly). Only replace
         * the bare arm — when an item is held in this hand, vanilla item
         * rendering wins. */
        boolean renderCustom = !canceled && (customMorph || customSkin) && event.getItemStack().isEmpty();

        event.setCanceled(canceled || renderCustom);

        if(canceled && (position.x == 0 && position.y == 0 && position.z == 0 && rotate.angle == 0 && rotate.x == 0 && rotate.y == 0 && rotate.z == 0)){
            return;
        }

        GL11.glRotated(rotate.angle, rotate.x, rotate.y, rotate.z);
        GL11.glTranslated(position.x, position.y, position.z);

        if (renderCustom) {
            this.renderCustomHand(hand, event, customMorph ? morph : null);
        }

        long now = System.currentTimeMillis();
        if (now - lastHandPacket < PACKET_INTERVAL_MS) {
            return;
        }
        lastHandPacket = now;

        NBTTagCompound data = new NBTTagCompound();
        data.setFloat("partialTicks", event.getPartialTicks());
        data.setFloat("swingProgress", event.getSwingProgress());
        data.setFloat("equipProgress", event.getEquipProgress());
        data.setFloat("interpolatedPitch", event.getInterpolatedPitch());
        Dispatcher.sendToServer(new PacketEvent(EventType.HAND, data));
    }

    @SubscribeEvent
    public void renderHudPre(RenderGameOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getMinecraft();
        Hud hud = Hud.get(mc.player);
        if (hud == null) {
            return;
        }

        hud.setName(event.getType().name());
        boolean canceled = hud.isCanceled();
        ScriptVector scale = hud.getScale();
        ScriptVector pos = hud.getPosition();
        ScriptVectorAngle rotate = hud.getRotate();

        event.setCanceled(canceled);

        if (!canceled && event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            if (this.isDefaultHud(hud)) {
                RenderHelper.enableStandardItemLighting();
                GlStateManager.pushMatrix();
                RenderHelper.enableStandardItemLighting();
                GL11.glRotated(rotate.angle, rotate.x, rotate.y, rotate.z);
                GL11.glScaled(scale.x, scale.y, scale.z);
                GL11.glTranslated(pos.x, pos.y, pos.z);
            }
        }

        /* RenderGameOverlayEvent.Pre fires once per HUD element (~22x/frame).
         * Throttle the network send to 20 Hz so we don't flood the connection
         * while the world loads. */
        long now = System.currentTimeMillis();
        if (now - lastHudPacket < PACKET_INTERVAL_MS) {
            return;
        }
        lastHudPacket = now;

        NBTTagCompound data = new NBTTagCompound();
        data.setString("hudType", event.getType().name());
        Dispatcher.sendToServer(new PacketEvent(EventType.HUD, data));
    }

    @SubscribeEvent
    public void onRenderGuiPost(RenderGameOverlayEvent.Post event) {
        Hud hud = Hud.get(Minecraft.getMinecraft().player);

        hud.setName(event.getType().name());
        if (!event.isCanceled() && event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            if(this.isDefaultHud(hud)) {
                RenderHelper.disableStandardItemLighting();
                GlStateManager.popMatrix();
                RenderHelper.disableStandardItemLighting();
            }
        }
    }

    @SubscribeEvent
    public void mouseEvent(MouseEvent event) {
        NBTTagCompound data = new NBTTagCompound();

        data.setInteger("button", event.getButton());
        data.setInteger("x", event.getX());
        data.setInteger("y", event.getY());
        data.setInteger("dwheel", event.getDwheel());
        data.setInteger("dx", event.getDx());
        data.setInteger("dy", event.getDy());
        data.setBoolean("buttonState", event.isButtonstate());

        Dispatcher.sendToServer(new PacketEvent(EventType.MOUSE, data));
    }

    /**
     * Draw the active fullscreen shader on top of the world, right after the
     * world has been rendered into Minecraft's framebuffer but before it is
     * presented to the screen.
     */
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.getFramebuffer() == null) {
            return;
        }
        ShaderRenderer.render();
    }

    /**
     * Draw the first person hand replacement: the morph assigned with
     * {@code hand.setMorph(...)} or the bare arm textured with the skin
     * assigned with {@code hand.setSkinPath(...)} / {@code hand.setSkinType(...)}.
     *
     * <p>The transformation sequence mirrors vanilla
     * {@code ItemRenderer.renderArmFirstPerson} and the texture is bound on the
     * same spot vanilla binds the player skin, so the hand lands exactly where
     * the default arm would be. The morph is rendered through
     * {@code AbstractMorph.renderHand} — the same hook Metamorph uses in first
     * person — falling back to the textured arm when the morph doesn't
     * implement hand rendering.</p>
     */
    private void renderCustomHand(Hand hand, RenderSpecificHandEvent event, AbstractMorph morph) {
        Minecraft mc = Minecraft.getMinecraft();
        AbstractClientPlayer player = mc.player;

        if (player == null || player.isInvisible()) {
            return;
        }

        EnumHandSide side = event.getHand() == EnumHand.MAIN_HAND ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        boolean right = side != EnumHandSide.LEFT;
        float f = right ? 1.0F : -1.0F;

        float swing = event.getSwingProgress();
        float equip = event.getEquipProgress();

        GlStateManager.pushMatrix();

        float f1 = MathHelper.sqrt(swing);
        float f2 = -0.3F * MathHelper.sin(f1 * (float) Math.PI);
        float f3 = 0.4F * MathHelper.sin(f1 * ((float) Math.PI * 2F));
        float f4 = -0.4F * MathHelper.sin(swing * (float) Math.PI);

        GlStateManager.translate(f * (f2 + 0.64000005F), f3 + -0.6F + equip * -0.6F, f4 + -0.71999997F);
        GlStateManager.rotate(f * 45.0F, 0.0F, 1.0F, 0.0F);

        float f5 = MathHelper.sin(swing * swing * (float) Math.PI);
        float f6 = MathHelper.sin(f1 * (float) Math.PI);

        GlStateManager.rotate(f * f6 * 70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);

        ResourceLocation skin = null;

        if (hand.skinPath != null && !hand.skinPath.isEmpty()) {
            try {
                skin = new ResourceLocation(hand.skinPath);
            } catch (Exception e) {
                skin = null;
            }
        }

        if (skin == null) {
            skin = player.getLocationSkin();
        }

        mc.getTextureManager().bindTexture(skin);

        GlStateManager.translate(f * -1.0F, 3.6F, 3.5F);
        GlStateManager.rotate(f * 120.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * -135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(f * 5.6F, 0.0F, 0.0F);

        GlStateManager.disableCull();

        boolean handled = false;

        if (morph != null) {
            try {
                handled = morph.renderHand(player, event.getHand());
            } catch (Exception e) {
                handled = false;
            }
        }

        if (!handled) {
            RenderPlayer renderer = this.getHandRenderer(hand);

            if (renderer != null) {
                if (right) {
                    renderer.renderRightArm(player);
                }
                else {
                    renderer.renderLeftArm(player);
                }
            }
        }

        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    /**
     * Pick the player renderer matching the skin type set with
     * {@code hand.setSkinType(...)}: slim arms for "slim"/"alex", the default
     * wide arms otherwise.
     */
    private RenderPlayer getHandRenderer(Hand hand) {
        Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();
        RenderPlayer renderer = skinMap.get("slim".equalsIgnoreCase(hand.skinType) ? "slim" : "default");

        return renderer != null ? renderer : skinMap.get("default");
    }

    private boolean isDefaultHud(Hud hud) {
        ScriptVector scale = hud.getScale();
        ScriptVector pos = hud.getPosition();
        ScriptVectorAngle rotate = hud.getRotate();
        return !(pos.x == 0 && pos.y == 0 && pos.z == 0 && rotate.angle == 0 && rotate.x == 0 && rotate.y == 0 && rotate.z == 0 && scale.x == 1 && scale.y == 1 && scale.z == 0);
    }
}
