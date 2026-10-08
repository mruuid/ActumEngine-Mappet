package unkn0wn.ae.api.events;

import mchorse.mappet.CommonProxy;
import mchorse.mappet.api.ui.components.UIComponent;
import mchorse.mappet.api.utils.factory.MapFactory;
import mchorse.mappet.events.RegisterUIComponentEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.lwjgl.input.Keyboard;
import unkn0wn.ae.AEMod;
import unkn0wn.ae.api.ui.components.UIColorPickerComponent;
import unkn0wn.ae.api.ui.components.UIScriptEditorComponent;
import unkn0wn.ae.capabilities.CapabilitiesType;
import unkn0wn.ae.capabilities.camera.Camera;
import unkn0wn.ae.capabilities.camera.CameraProvider;
import unkn0wn.ae.capabilities.camera.ICamera;
import unkn0wn.ae.capabilities.hand.Hand;
import unkn0wn.ae.capabilities.hand.HandProvider;
import unkn0wn.ae.capabilities.hand.IHand;
import unkn0wn.ae.capabilities.hud.Hud;
import unkn0wn.ae.capabilities.hud.HudProvider;
import unkn0wn.ae.capabilities.hud.IHud;
import unkn0wn.ae.capabilities.skin.ISkin;
import unkn0wn.ae.capabilities.skin.Skin;
import unkn0wn.ae.capabilities.skin.SkinProvider;
import unkn0wn.ae.network.Dispatcher;
import unkn0wn.ae.network.packets.PacketCapability;
import unkn0wn.ae.network.packets.PacketEvent;
import unkn0wn.ae.utils.EventType;


public class EventHandler {
    public static final ResourceLocation CAMERA = new ResourceLocation(AEMod.MOD_ID, "camera");
    public static final ResourceLocation HAND = new ResourceLocation(AEMod.MOD_ID, "hand");
    public static final ResourceLocation HUD = new ResourceLocation(AEMod.MOD_ID, "hud");
    public static final ResourceLocation SKIN = new ResourceLocation(AEMod.MOD_ID, "skin");

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(CAMERA, new CameraProvider());
            event.addCapability(HAND, new HandProvider());
            event.addCapability(HUD, new HudProvider());
            event.addCapability(SKIN, new SkinProvider());
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;

        final ICamera camera = Camera.get(player);
        final IHand hand = Hand.get(player);
        final IHud hud = Hud.get(player);
        final ISkin skin = Skin.get(player);

        Dispatcher.sendTo(new PacketCapability(camera.serializeNBT(), CapabilitiesType.CAMERA), (EntityPlayerMP) player);
        Dispatcher.sendTo(new PacketCapability(hand.serializeNBT(), CapabilitiesType.HAND), (EntityPlayerMP) player);
        Dispatcher.sendTo(new PacketCapability(hud.serializeNBT(), CapabilitiesType.HUD), (EntityPlayerMP) player);
        Dispatcher.sendTo(new PacketCapability(skin.serializeNBT(), CapabilitiesType.SKIN), (EntityPlayerMP) player);
    }

    @SubscribeEvent
    public void keyInputEvent(InputEvent.KeyInputEvent event) {
        NBTTagCompound data = new NBTTagCompound();

        data.setInteger("keyCode", Keyboard.getEventKey());
        data.setBoolean("keyState", Keyboard.getEventKeyState());

        Dispatcher.sendToServer(new PacketEvent(EventType.KEYBOARD, data));
    }

    @SubscribeEvent
    public void onRegisterUIComponentEvent(RegisterUIComponentEvent event) {
        ((MapFactory<UIComponent>)CommonProxy.getUiComponents())
                .register("scriptEditor", UIScriptEditorComponent.class, 0xffffff)
                .register("colorPicker", UIColorPickerComponent.class, 0xffffff);
    }
}
