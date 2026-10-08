package unkn0wn.ae.utils.mixins.docs;

import com.google.gson.Gson;
import mchorse.mappet.client.gui.scripts.GuiDocumentationOverlayPanel;
import mchorse.mappet.client.gui.scripts.utils.documentation.DocMerger;
import mchorse.mappet.client.gui.scripts.utils.documentation.Docs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import unkn0wn.ae.AEMod;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

@Mixin(value = DocMerger.class, remap = false)
public abstract class MixinDocMerger {
    @Inject(
            method = "addAddonsDocs",
            at = @At(value = "TAIL"),
            remap = false,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void addDocs(Gson gson, List<Docs> docsList, CallbackInfo ci) {
        InputStream stream = GuiDocumentationOverlayPanel.class.getResourceAsStream("/assets/"+ AEMod.MOD_ID +"/docs.json");
        Scanner scanner = new Scanner(stream, "UTF-8");
        Docs actumEngineDocs = gson.fromJson(scanner.useDelimiter("\\A").next(), Docs.class);
        actumEngineDocs.source = "ActumEngine";
        actumEngineDocs.classes.forEach(clazz -> {
            clazz.source = actumEngineDocs.source;
            clazz.methods.forEach(method -> method.source = actumEngineDocs.source);
        });
        docsList.add(actumEngineDocs);
    }
}