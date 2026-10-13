package unkn0wn.ae.client.gui.overlays;

import mchorse.mappet.client.gui.scripts.utils.documentation.*;
import mchorse.mappet.client.gui.utils.overlays.GuiOverlayPanel;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiSearchListElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.Launch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A read-only overlay that documents the GLSL environment available to user
 * authored shaders: which uniforms are bound by {@code ShaderRenderer}, the
 * required vertex attributes, and the shader-type specific built-ins.
 */
public class GuiShaderDocsOverlayPanel extends GuiOverlayPanel {
    public static Docs docs;
    public static DocEntry top;
    public static DocEntry entry;
    public GuiDocEntrySearchList list;
    public GuiScrollElement documentations;

    public static List<DocClass> search(String text) {
        List<DocClass> list = new ArrayList<DocClass>();
        for (DocClass docClass : getDocs().classes) {
            if (docClass.getMethod(text) != null) {
                list.add(docClass);
            }
        }
        return list;
    }

    public static Docs getDocs() {
        parseDocs();
        return docs;
    }

    private static void parseDocs() {
        final boolean dev = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

        if (dev || docs == null) {
            docs = DocMerger.getMergedDocs();
            entry = null;

            Map<String, DocList> docLists = new HashMap<>();
            DocList topPackage = new DocList();
            DocList shaders = new DocList();

            docLists.put("topPackage", topPackage);
            docLists.put("shaders", shaders);
            mixinsHook();

            shaders.name = "ScriptShaders API";
            DocPackage shaderPackage = docs.getPackage("unkn0wn.ae.api.shaders.user");
            shaders.doc = shaderPackage == null ? "" : shaderPackage.doc;
            shaders.parent = topPackage;

            topPackage.entries.add(shaders);

            /* Distribute every shader API class into the "ScriptShaders API" list.
             * Without this loop the list renders empty even though the classes are
             * present in the merged documentation. */
            for (DocClass docClass : docs.classes) {
                if (docClass.name.startsWith("unkn0wn.ae.api.shaders.user")) {
                    docClass.setup();
                    docClass.parent = shaders;
                    shaders.entries.add(docClass);
                }
            }

            top = topPackage;
        }

    }

    public static void mixinsHook() {}

    public GuiShaderDocsOverlayPanel(Minecraft mc)
    {
        this(mc, null);
    }

    public GuiShaderDocsOverlayPanel(Minecraft mc, DocEntry entry) {
        super(mc, IKey.lang("ae.gui.shaders.docs.title"));

        this.list = new GuiDocEntrySearchList(mc, (l) -> this.pick(l.get(0)));
        this.list.label(IKey.lang("ae.gui.search"));
        this.list.flex().relative(this.content).w(240).h(1F);

        this.documentations = new GuiScrollElement(mc);
        this.documentations.flex().relative(this.content).x(240).w(1F, -240).h(1F).column(4).vertical().stretch().scroll().padding(10);

        this.content.add(this.list, this.documentations);

        this.icons.flex().row(0).reverse().resize().width(32).height(16);
        this.setupDocs(entry);
    }

    private void pick(DocEntry entryIn) {
        boolean isMethod = entryIn instanceof DocMethod;

        entryIn = entryIn.getEntry();
        List<DocEntry> entries = entryIn.getEntries();
        boolean wasSame = this.list.list.getList().size() >= 2 && this.list.list.getList().get(1).parent == entryIn.parent;

        if (entry == entryIn || !wasSame) {
            this.list.list.clear();
            if (entryIn.parent != null) {
                this.list.list.add(new DocDelegate(entryIn.parent));
            }
            this.list.list.add(entries);
            this.list.list.sort();
            if (isMethod) {
                this.list.list.setCurrentScroll(entryIn);
            }
        }
        this.fill(entryIn);
    }

    private void fill(DocEntry entryIn) {
        if (!(entryIn instanceof DocMethod)) {
            entry = entryIn;
        }

        this.documentations.scroll.scrollTo(0);
        this.documentations.removeAll();
        entryIn.fillIn(this.mc, this.documentations);

        this.resize();
    }

    private void setupDocs(DocEntry in) {
        parseDocs();

        if (in != null) {
            entry = in;
        } else if (entry == null) {
            entry = top;
        }
        this.pick(entry);
    }

    public static class GuiDocEntrySearchList extends GuiSearchListElement<DocEntry> {
        public GuiDocEntrySearchList(Minecraft mc, Consumer<List<DocEntry>> callback)
        {
            super(mc, callback);
        }

        @Override
        protected GuiListElement<DocEntry> createList(Minecraft minecraft, Consumer<List<DocEntry>> consumer) {
            return new GuiDocEntryList(minecraft, consumer);
        }
    }

    public static class GuiDocEntryList extends GuiListElement<DocEntry> {
        public GuiDocEntryList(Minecraft mc, Consumer<List<DocEntry>> callback) {
            super(mc, callback);
            this.scroll.scrollItemSize = 16;
            this.scroll.scrollSpeed *= 2;
        }

        @Override
        protected boolean sortElements() {
            this.list.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
            return true;
        }

        @Override
        protected String elementToString(DocEntry element)
        {
            return element.getName();
        }
    }
}
