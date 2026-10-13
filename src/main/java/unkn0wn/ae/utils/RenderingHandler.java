package unkn0wn.ae.utils;

import mchorse.mappet.client.morphs.WorldMorph;
import mchorse.metamorph.api.Morph;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class RenderingHandler {
    public static Morph clientMorph = new Morph();
    public static List<WorldMorph> worldMorphs = new ArrayList();
    private static Minecraft mc = Minecraft.getMinecraft();

    /**
     * Tick the first-person morph's animator.
     *
     * <p>The body (third-person) morph is ticked by Metamorph through the
     * {@code Morphing} capability, but the first-person morph
     * ({@link #clientMorph}) is a standalone object that is only rendered in
     * {@link #onRenderLast(RenderWorldLastEvent)}. Without ticking it, its
     * Chameleon {@code Animator} never advances
     * ({@code ActionPlayback.ticks += speed}), so animations played via
     * {@code playAnimation(..., true)} stay frozen on the first frame. This
     * handler does exactly what Metamorph does for the body morph.</p>
     */
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || clientMorph.isEmpty()) {
            return;
        }

        EntityPlayerSP player = mc.player;

        if (player == null || player.isSpectator()) {
            return;
        }

        AbstractMorph morph = clientMorph.get();

        if (morph != null) {
            morph.update(player);
        }
    }

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
        EntityRenderer render = mc.entityRenderer;
        Minecraft.getMinecraft().entityRenderer.enableLightmap();

        for(WorldMorph morph : worldMorphs) {
            morph.render(event.getPartialTicks());
        }

        Minecraft.getMinecraft().entityRenderer.disableLightmap();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        if (!clientMorph.isEmpty() && mc.gameSettings.thirdPersonView == 0 && !mc.player.isSpectator()) {
            AbstractClientPlayer player = mc.player;
            RenderHelper.enableStandardItemLighting();
            setLightmap(player);
            render.enableLightmap();
            float partialTicks = event.getPartialTicks();
            float angle = (1.0F - partialTicks) * player.prevRenderYawOffset + partialTicks * player.renderYawOffset;
            Vector3d offset = new Vector3d(0.35 * Math.sin(Math.toRadians((double)angle)), (double)0.0F, -(0.35 * Math.cos(Math.toRadians((double)angle))));
            clientMorph.get().render(player, offset.x, offset.y, offset.z, 0.0F, partialTicks);
            RenderHelper.disableStandardItemLighting();
            render.disableLightmap();
            RenderManager renderManager = mc.getRenderManager();
            if (renderManager.options != null && renderManager.options.entityShadows && renderManager.isRenderShadow()) {
                renderShadow(mc.player, renderManager);
            }
        }

    }

    public static void setLightmap(AbstractClientPlayer player) {
        int i = mc.world.getCombinedLight(new BlockPos(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ), 0);
        float f = (float)(i & '\uffff');
        float f1 = (float)(i >> 16);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
    }

    public static void renderShadow(EntityPlayerSP player, RenderManager renderManager) {
        float partialTicks = mc.getRenderPartialTicks();
        double distanceToCamera = renderManager.getDistanceToCamera(player.posX, player.posY, player.posZ);
        double alpha = ((double)1.0F - distanceToCamera / (double)256.0F) * (double)1.0F;
        if (!(alpha <= (double)0.0F)) {
            float shadowSize = 0.6F;
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            renderManager.renderEngine.bindTexture(new ResourceLocation("textures/misc/shadow.png"));
            World world = renderManager.world;
            GlStateManager.depthMask(false);
            double interpX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
            double interpY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
            double interpZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;
            Vector3d position = new Vector3d(interpX, interpY, interpZ);
            int minX = MathHelper.floor(position.x - (double)shadowSize);
            int maxX = MathHelper.floor(position.x + (double)shadowSize);
            int minY = MathHelper.floor(position.y - (double)shadowSize);
            int maxY = MathHelper.floor(position.y);
            int minZ = MathHelper.floor(position.z - (double)shadowSize);
            int maxZ = MathHelper.floor(position.z + (double)shadowSize);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

            for(BlockPos blockpos : BlockPos.getAllInBoxMutable(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ))) {
                IBlockState blockState = world.getBlockState(blockpos.down());
                if (blockState.getRenderType() != EnumBlockRenderType.INVISIBLE && world.getLightFromNeighbors(blockpos) > 3) {
                    float playerYaw = (1.0F - partialTicks) * player.prevRenderYawOffset + partialTicks * player.renderYawOffset;
                    double offsetX = 0.35 * Math.sin(Math.toRadians((double)playerYaw));
                    double offsetZ = -(0.35 * Math.cos(Math.toRadians((double)playerYaw)));
                    Vector3d offset = new Vector3d(offsetX, (double)0.0F, offsetZ);
                    renderShadowSingle(renderManager, position, offset, blockState, blockpos, (float)alpha, (double)shadowSize);
                }
            }

            tessellator.draw();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
        }
    }

    private static void renderShadowSingle(RenderManager renderManager, Vector3d playerPos, Vector3d offset, IBlockState state, BlockPos blockPos, float shadowAlpha, double shadowSize) {
        if (state.isFullBlock()) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            double alpha = ((double)shadowAlpha - ((double)blockPos.getY() - playerPos.y) / (double)2.0F) * (double)0.5F * (double)renderManager.world.getLightBrightness(blockPos);
            if (alpha >= (double)0.0F) {
                if (alpha > (double)1.0F) {
                    alpha = (double)1.0F;
                }

                AxisAlignedBB blockBounds = state.getBoundingBox(renderManager.world, blockPos);
                double minX = (double)blockPos.getX() + blockBounds.minX - playerPos.x;
                double maxX = (double)blockPos.getX() + blockBounds.maxX - playerPos.x;
                double minY = (double)blockPos.getY() + blockBounds.minY - playerPos.y + (double)0.015625F;
                double minZ = (double)blockPos.getZ() + blockBounds.minZ - playerPos.z;
                double maxZ = (double)blockPos.getZ() + blockBounds.maxZ - playerPos.z;
                float texMinX = (float)(minX / (double)2.0F / shadowSize + (double)0.5F - offset.x);
                float texMaxX = (float)(maxX / (double)2.0F / shadowSize + (double)0.5F - offset.x);
                float texMinZ = (float)(minZ / (double)2.0F / shadowSize + (double)0.5F - offset.z);
                float texMaxZ = (float)(maxZ / (double)2.0F / shadowSize + (double)0.5F - offset.z);
                bufferbuilder.pos(minX, minY, minZ).tex((double)texMinX, (double)texMinZ).color(1.0F, 1.0F, 1.0F, (float)(-alpha)).endVertex();
                bufferbuilder.pos(minX, minY, maxZ).tex((double)texMinX, (double)texMaxZ).color(1.0F, 1.0F, 1.0F, (float)(-alpha)).endVertex();
                bufferbuilder.pos(maxX, minY, maxZ).tex((double)texMaxX, (double)texMaxZ).color(1.0F, 1.0F, 1.0F, (float)(-alpha)).endVertex();
                bufferbuilder.pos(maxX, minY, minZ).tex((double)texMaxX, (double)texMinZ).color(1.0F, 1.0F, 1.0F, (float)(-alpha)).endVertex();
            }
        }

    }
}
