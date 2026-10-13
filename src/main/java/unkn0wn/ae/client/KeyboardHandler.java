package unkn0wn.ae.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;
import unkn0wn.ae.api.shaders.ShaderData;
import unkn0wn.ae.client.gui.panels.ShadersPanel;
import unkn0wn.ae.utils.shaders.ShaderRenderer;

public class KeyboardHandler {
    public KeyBinding rasShaders;

    public KeyboardHandler() {
        String prefix = "key.ae.";
        this.rasShaders = new KeyBinding(prefix + "ranShaders", Keyboard.KEY_F7, prefix + "category");

        ClientRegistry.registerKeyBinding(this.rasShaders);
    }

    @SubscribeEvent
    public void onKeyPress(KeyInputEvent event) {
        if (this.rasShaders.isPressed()) {

        }
    }
}
