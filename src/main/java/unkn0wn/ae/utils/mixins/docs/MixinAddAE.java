package unkn0wn.ae.utils.mixins.docs;

import mchorse.mappet.Mappet;
import mchorse.mappet.client.gui.scripts.GuiDocumentationOverlayPanel;
import mchorse.mappet.client.gui.scripts.utils.documentation.DocClass;
import mchorse.mappet.client.gui.scripts.utils.documentation.DocEntry;
import mchorse.mappet.client.gui.scripts.utils.documentation.DocList;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Mixin(value = GuiDocumentationOverlayPanel.class, remap = false)
public abstract class MixinAddAE {
    @Inject(
            method = "parseDocs",
            at = @At(
                    value = "INVOKE",
                    target = "Lmchorse/mappet/client/gui/scripts/GuiDocumentationOverlayPanel;mixinsHook()V",
                    ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            remap = false
    )
    private static void parseDocs(CallbackInfo ci, boolean dev, Map docLists, DocList topPackage, DocList scripting, DocList entities, DocList nbt, DocList items, DocList blocks, DocList ui) {
        DocList trigger = new DocList();
        DocList renderer = new DocList();
        DocList mixins = new DocList();
        DocList data = new DocList();

        if (Mappet.scriptDocsNewStructure.get()) {
            trigger.name = "/ Triggers";
            trigger.doc = GuiDocumentationOverlayPanel.docs.getPackage("unkn0wn.ae.api.scripts.user.trigger").doc;
            trigger.parent = scripting;
            trigger.source = "ActumEngine";
            scripting.entries.add(trigger);

            renderer.name = "/ Renderer";
            renderer.doc = GuiDocumentationOverlayPanel.docs.getPackage("unkn0wn.ae.api.scripts.user.renderer").doc;
            renderer.parent = scripting;
            renderer.source = "ActumEngine";
            scripting.entries.add(renderer);

            mixins.name = "/ Mixins";
            mixins.doc = GuiDocumentationOverlayPanel.docs.getPackage("unkn0wn.ae.api.scripts.mixins").doc;
            mixins.parent = scripting;
            mixins.source = "ActumEngine";
            scripting.entries.add(mixins);

            data.name = "/ Data";
            data.doc = GuiDocumentationOverlayPanel.docs.getPackage("mchorse.mappet.api.scripts.user.data").doc;
            data.parent = scripting;
            data.source = "Mappet";
            scripting.entries.add(data);
        }
        docLists.put("trigger", trigger);
        docLists.put("renderer", renderer);
        docLists.put("mixins", mixins);
        docLists.put("data", data);
    }

    @Inject(
            method = "parseDocs",
            at = @At(
                    value = "INVOKE",
                    target = "Lmchorse/mappet/client/gui/scripts/GuiDocumentationOverlayPanel;mixinsHook()V",
                    ordinal = 1
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            remap = false
    )
    private static void mixinsHook(CallbackInfo ci, boolean dev, Map<String, DocList> docLists, DocList topPackage, DocList scripting, DocList entities, DocList nbt, DocList items, DocList blocks, DocList ui, boolean useNewStructure, List extraPackages, List extraDocLists, Iterator var12, DocClass docClass, List<Callable<Boolean>> functions, boolean added) {
        functions.add(() -> GuiDocumentationOverlayPanel.addWithNewStructure(input -> input.name.contains("trigger"), docClass, docLists.get("trigger")));
        functions.add(() -> GuiDocumentationOverlayPanel.addWithNewStructure(input -> input.name.contains("renderer"), docClass, docLists.get("renderer")));
        functions.add(() -> GuiDocumentationOverlayPanel.addWithNewStructure(input -> input.name.contains("mixins"), docClass, docLists.get("mixins")));
        functions.add(() -> GuiDocumentationOverlayPanel.addWithNewStructure(input -> input.name.contains("data"), docClass, docLists.get("data")));
    }
}
