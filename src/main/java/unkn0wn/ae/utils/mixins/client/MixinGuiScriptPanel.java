package unkn0wn.ae.utils.mixins.client;

import mchorse.mappet.client.gui.panels.GuiScriptPanel;
import mchorse.mappet.client.gui.scripts.GuiTextEditor;
import mchorse.mclib.client.gui.framework.elements.context.GuiContextMenu;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import unkn0wn.ae.client.gui.scripts.ScriptBeautifier;

/**
 * Wires up the js-beautify based {@link unkn0wn.ae.client.renderer.Beautifier}
 * into Mappet's script editor by adding a "Beautify" entry to the script
 * editor's right-click context menu. When invoked it reformats either the
 * current selection or the whole file in-place.
 */
@Mixin(value = GuiScriptPanel.class, remap = false)
public abstract class MixinGuiScriptPanel {
    @Inject(
            method = "createScriptContextMenu",
            at = @At("RETURN"),
            remap = false
    )
    private static void addBeautifyAction(Minecraft mc, GuiTextEditor editor, CallbackInfoReturnable<GuiContextMenu> cir) {
        GuiContextMenu menu = cir.getReturnValue();

        if (menu instanceof GuiSimpleContextMenu) {
            ((GuiSimpleContextMenu) menu).action(
                    Icons.EDIT,
                    IKey.lang("ae.gui.scripts.context.beautify"),
                    () -> beautifyEditor(editor)
            );
        }
    }

    private static void beautifyEditor(GuiTextEditor editor) {
        boolean selected = editor.isSelected();
        String code = selected ? editor.getSelectedText() : editor.getText();
        String result = ScriptBeautifier.beautify(code);

        if (result == null || result.equals(code)) {
            return;
        }

        if (selected) {
            editor.deleteSelection();
            editor.writeString(result);
        } else {
            editor.setText(result);
        }
    }
}
