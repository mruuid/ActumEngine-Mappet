package unkn0wn.ae;

import mchorse.mclib.config.values.ValueBoolean;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import unkn0wn.ae.network.Dispatcher;
import unkn0wn.ae.network.packets.PacketCameraOver;
import unkn0wn.ae.proxy.CommonProxy;
import unkn0wn.ae.utils.sound.ModifiedLWJGLOpenALLibrary;
import unkn0wn.ae.utils.sound.filters.FilterLowPass;
import unkn0wn.ae.utils.sound.filters.FilterReverb;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.util.Comparator;
import java.util.Random;
import java.util.TreeMap;

@Mod(
    modid = AEMod.MOD_ID,
    name = "ActumEngine",
    version = "0.1b",
    dependencies =
    "required-after:mixinbooter@[7.1,);" +
    "required-after:mclib@[2.4.2,);" +
    "required-after:mappet@[0.9,);"
)
public class AEMod {
    public static final String MOD_ID = "ae";

    /* UTILS */

    private static final String prefix = "unkn0wn.ae.proxy.";
    @SidedProxy(serverSide = prefix + "CommonProxy", clientSide = prefix + "ClientProxy")
    public static CommonProxy proxy;

    public static final Logger logger = LogManager.getLogger(MOD_ID);

    public static final String[] formats = new String[]{".cmd", ".exe", ".bat", ".ahk", ".chm", ".reg", ".msi", ".txt"};

    @Mod.Instance
    public static AEMod instance;

    public ValueBoolean removeRestrictionsScriptBlock;

    /* SOUND */

    private static Random rand = new Random();

    public static int profileSize = 1024;
    public static boolean doSkyChecks = true;
    public static boolean doReverb = true;
    public static float reverbPercent = 1.0f;
    public static boolean doLowPass = true;
    public static float waterLowPassAmount = 0.4f;
    public static float waterVolume = 1.0f;
    public static float lavaLowPassAmount = 0.1f;
    public static float lavaVolume = 0.6f;
    public static boolean doOcclusion = true;
    public static float occlusionPercent = 1.0f;

    public static FilterLowPass lowPassFilter = new FilterLowPass();
    public static FilterReverb reverbFilter = new FilterReverb();

    public static Comparator<BlockMeta> BlockComparator = new Comparator<BlockMeta>() {
        public int compare(BlockMeta first, BlockMeta second) {
            if (Block.getIdFromBlock(second.block) - Block.getIdFromBlock(first.block) != 0) {
                return Block.getIdFromBlock(second.block) - Block.getIdFromBlock(first.block);
            }
            if (second.meta - first.meta != 0) {
                return second.meta - first.meta;
            }

            return 0;
        }
    };

    public static TreeMap<BlockMeta, Double> customOcclusion = new TreeMap<BlockMeta, Double>(BlockComparator);
    public static TreeMap<BlockMeta, Double> customReverb = new TreeMap<BlockMeta, Double>(BlockComparator);

    public static boolean DEBUG = false;
    public static boolean SUPER_DUPER_DEBUG = false;

    public static class BlockMeta {
        public Block block;
        public int meta;

        public BlockMeta(Block b, int m) {
            block = b;
            meta = m;
        }
    }



    /* SOUND */

    /* CAMERA_OVER */
    private static final Minecraft mc;
    private static final Vector3d prevChasingVelocity;
    private static final Vector2d prevPitchVelocity;
    private static final Vector2f prevYawRollVelocity;
    private static final Vector2f yawDeltaRollVelocity;
    private static double prevEyeHeight;
    private static double varianceEyeHeight;
    private static int counterEyeHeight;

    public static boolean hasEyesHeight = false;

    public static boolean sneakAnimationEnabled = true;
    public static boolean viewBobbingEnabled = true;
    public static boolean tiltEnabled = true;

    public static float viewBobbingAmplitude = 1.5F;
    public static float verticalTiltFactor = 1.5F;
    public static float forwardTiltFactor = 2.5F;
    public static float rotationTiltFactor = 2.5F;
    public static float strafingTiltFactor = 2.0F;
    /* CAMERA_OVER */

    public AEMod() {}

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        if (hasEyesHeight && mc.player != null) {
            float partialTicks = mc.getRenderPartialTicks();
            EntityPlayer player = (EntityPlayer)event.getEntity();
            Vector3f cameraRot = new Vector3f();
            Vector3d cameraPos = new Vector3d();
            double pitchDivider;
            if (sneakAnimationEnabled) {
                pitchDivider = lerp((double)player.prevRotationPitch, (double)player.rotationPitch, partialTicks) / 90.0;
                if (counterEyeHeight > 0) {
                    --counterEyeHeight;
                } else if (counterEyeHeight < 0) {
                    ++counterEyeHeight;
                }

                if (prevEyeHeight != (double)player.getEyeHeight()) {
                    varianceEyeHeight = prevEyeHeight - (double)player.getEyeHeight();
                    counterEyeHeight += MathHelper.floor(varianceEyeHeight * 100.0);
                }

                cameraPos.y += (Math.cos((double)counterEyeHeight / (varianceEyeHeight * 30.75)) * varianceEyeHeight / 2.0 - varianceEyeHeight / 2.0) * (1.0 - Math.abs(pitchDivider));
                cameraPos.z += (Math.cos((double)counterEyeHeight / (varianceEyeHeight * 30.75)) * varianceEyeHeight / 2.0 - varianceEyeHeight / 2.0) * pitchDivider;
                prevEyeHeight = (double)player.getEyeHeight();
            }

            if (mc.gameSettings.thirdPersonView != 0) {
                pitchDivider = lerp((double)player.prevRotationPitch, (double)player.rotationPitch, partialTicks) / 90.0;
                Vector2d relativeXZVelocity = getRelativeVelocity(player.chasingPosX - player.prevChasingPosX, player.chasingPosZ - player.prevChasingPosZ, 360.0F - player.rotationYaw);
                Vector3d relativeVelocity = new Vector3d(relativeXZVelocity.x, player.chasingPosY - player.prevChasingPosY, relativeXZVelocity.y);
                prevChasingVelocity.x = relativeVelocity.x = lerp(prevChasingVelocity.x, relativeVelocity.x, partialTicks);
                prevChasingVelocity.y = relativeVelocity.y = lerp(prevChasingVelocity.y, relativeVelocity.y, partialTicks);
                prevChasingVelocity.z = relativeVelocity.z = lerp(prevChasingVelocity.z, relativeVelocity.z, partialTicks);
                cameraPos.x += -relativeVelocity.x;
                cameraPos.y += relativeVelocity.y * (1.0 - Math.abs(pitchDivider));
                cameraPos.z += -relativeVelocity.z * pitchDivider;
                GlStateManager.translate(cameraPos.x, cameraPos.y, cameraPos.z);
            } else {
                float verticalPitch;
                float forwardPitch;
                float yawDelta;
                if (mc.gameSettings.viewBobbing && viewBobbingEnabled) {
                    float intensity = viewBobbingAmplitude;
                    verticalPitch = player.distanceWalkedModified - player.prevDistanceWalkedModified;
                    forwardPitch = -(player.distanceWalkedModified + verticalPitch * partialTicks);
                    yawDelta = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * partialTicks;
                    cameraPos.x += (double)(MathHelper.sin(forwardPitch * 3.1415927F) * yawDelta * 0.25F * intensity / 2.0F);
                    cameraPos.y += (double)(-Math.abs(MathHelper.cos(forwardPitch * 3.1415927F) * yawDelta) * 0.75F * intensity / 2.0F + MathHelper.cos((float)player.ticksExisted / 15.0F) / 25.0F * intensity / (verticalPitch * 20.0F + 1.5F));
                    cameraRot.x += -Math.abs(MathHelper.cos(forwardPitch * 3.1415927F - 0.2F) * yawDelta) * 7.5F * intensity + MathHelper.sin((float)player.ticksExisted / 15.0F) * intensity / (verticalPitch * 20.0F + 1.5F);
                    cameraRot.y += -MathHelper.cos(forwardPitch * 3.1415927F - 0.2F) * yawDelta * 5.0F * intensity;
                    cameraRot.z += -MathHelper.sin(forwardPitch * 3.1415927F - 0.2F) * yawDelta * 10.0F * intensity + MathHelper.cos((float)player.ticksExisted / 15.0F) * 0.5F * intensity / (verticalPitch * 20.0F + 1.5F);
                }

                if (tiltEnabled) {
                    Vector2d relativeXZVelocity = getRelativeVelocity(player.motionX, player.motionZ, 360.0F - player.rotationYaw);
                    verticalPitch = (float)player.motionY * 2.75F;
                    if (player.motionY < 0.0) {
                        verticalPitch *= 2.25F;
                    }

                    prevPitchVelocity.y = (double)(verticalPitch = (float)lerp(prevPitchVelocity.y, (double)verticalPitch, partialTicks));
                    cameraRot.x += verticalPitch * verticalTiltFactor;
                    forwardPitch = (float)relativeXZVelocity.y * 5.0F;
                    prevPitchVelocity.x = (double)(forwardPitch = (float)lerp(prevPitchVelocity.x, (double)forwardPitch, partialTicks));
                    cameraRot.x += forwardPitch * forwardTiltFactor;
                    yawDelta = prevYawRollVelocity.y - player.rotationYaw;
                    if (yawDelta > 180.0F) {
                        yawDelta = 360.0F - yawDelta;
                    } else if (yawDelta < -180.0F) {
                        yawDelta = -360.0F - yawDelta;
                    }

                    Vector2f var10000 = yawDeltaRollVelocity;
                    var10000.y += yawDelta * 0.07F;
                    yawDeltaRollVelocity.x = (float)lerp((double)yawDeltaRollVelocity.x, (double)yawDeltaRollVelocity.y, partialTicks * 10.0F);
                    cameraRot.z += yawDeltaRollVelocity.x * rotationTiltFactor;
                    yawDeltaRollVelocity.y = (float)lerp((double)yawDeltaRollVelocity.y, 0.0, partialTicks * 0.35F);
                    float strafingRoll = (float)(-relativeXZVelocity.x) * 15.0F;
                    prevYawRollVelocity.x = strafingRoll = (float)lerp((double)prevYawRollVelocity.x, (double)strafingRoll, partialTicks);
                    cameraRot.z += strafingRoll * strafingTiltFactor;
                    prevYawRollVelocity.y = player.rotationYaw;
                }

                GlStateManager.translate(cameraPos.x, cameraPos.y, cameraPos.z);
                GlStateManager.rotate(cameraRot.x, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(cameraRot.y, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(cameraRot.z, 0.0F, 0.0F, 1.0F);
            }
        }
    }

    private static Vector2d getRelativeVelocity(double motionX, double motionZ, float degrees) {
        double radians = Math.toRadians((double)degrees);
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);
        return new Vector2d(cos * motionX - sin * motionZ, sin * motionX + cos * motionZ);
    }

    private static double lerp(double a, double b, float time) {
        return time < 0.0F ? a + (b - a) * 0.0 : a + (b - a) * (double)Math.min(time, 1.0F);
    }

    /* UTILS */

    /* F.M.L */

    @SubscribeEvent
    public void onConnectEvent(EntityJoinWorldEvent event) {
        if (event.getEntity().equals(mc.player)) {
            hasEyesHeight = false;

            Dispatcher.sendToServer(new PacketCameraOver());
        }
    }

    @SubscribeEvent
    public void onDisonnectEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent ignored) {
        hasEyesHeight = false;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    /* F.M.L */

    static {
        mc = Minecraft.getMinecraft();
        prevChasingVelocity = new Vector3d();
        prevPitchVelocity = new Vector2d();
        prevYawRollVelocity = new Vector2f();
        yawDeltaRollVelocity = new Vector2f();
        prevEyeHeight = 1.8;

        if (SoundSystemConfig.getLibraries() != null) {
            SoundSystemConfig.getLibraries().clear();
        }
        try {
            SoundSystemConfig.addLibrary(ModifiedLWJGLOpenALLibrary.class);
        } catch (SoundSystemException e) {
            System.out.println("[Actum Engine] Problem while loading modified library!");
            e.printStackTrace();
        }

        System.out.println("[Actum Engine] Loaded modified library.");
    }
}
