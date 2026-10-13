package unkn0wn.ae.client.gui.panels;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.utils.AbstractData;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.panels.GuiMappetDashboardPanel;
import mchorse.mappet.client.gui.scripts.GuiDocumentationOverlayPanel;
import mchorse.mappet.client.gui.scripts.GuiTextEditor;
import mchorse.mappet.client.gui.scripts.utils.SyntaxHighlighter;
import mchorse.mappet.client.gui.scripts.utils.SyntaxStyle;
import mchorse.mappet.client.gui.scripts.utils.documentation.DocClass;
import mchorse.mappet.client.gui.scripts.utils.documentation.DocMethod;
import mchorse.mappet.client.gui.utils.overlays.GuiOverlay;
import mchorse.mappet.client.gui.utils.overlays.GuiOverlayPanel;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiContextMenu;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.color.GuiColorPicker;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import unkn0wn.ae.api.shaders.ShaderData;
import unkn0wn.ae.client.gui.overlays.GuiShaderDocsOverlayPanel;
import unkn0wn.ae.client.gui.scripts.ShaderHighlighters;
import unkn0wn.ae.network.Dispatcher;
import unkn0wn.ae.network.packets.PacketShader;
import unkn0wn.ae.utils.ContentType;
import unkn0wn.ae.utils.shaders.ShaderRenderer;

import java.util.List;

/**
 * Dashboard panel for authoring GLSL shaders. Everything is saved locally to the
 * world's {@code mappet/shaders} folder (no server round-trip), and the shader
 * is applied on the client via {@link ShaderRenderer}.
 */
public class ShadersPanel extends GuiMappetDashboardPanel<AbstractData> {
    public GuiTextEditor vertex;
    public GuiTextEditor fragment;

    public GuiIconElement docs;
    public GuiIconElement run;
    public GuiIconElement stop;
    public GuiToggleElement showVertex;

    private String shaderError = null;
    private boolean applied = false;

    public ShadersPanel(Minecraft mc, GuiMappetDashboard dashboard) {
        super(mc, dashboard);

        this.namesList.setFileIcon(Icons.BLOCK);

        this.docs = new GuiIconElement(mc, Icons.FILE, this::openDocs);
        this.docs.tooltip(IKey.lang("ae.gui.shaders.docs.title"), Direction.LEFT);

        this.run = new GuiIconElement(mc, Icons.PLAY, this::runShader);
        this.run.tooltip(IKey.lang("ae.gui.shaders.run.title"), Direction.LEFT);

        this.stop = new GuiIconElement(mc, Icons.PAUSE, this::stopShader);
        this.stop.tooltip(IKey.lang("ae.gui.shaders.stop.title"), Direction.LEFT);

        this.vertex = new GuiTextEditor(mc, null);
        this.vertex.background().context(() -> createShadersContextMenu(this.mc, this.vertex));

        this.fragment = new GuiTextEditor(mc, null);
        this.fragment.background().context(() -> createShadersContextMenu(this.mc, this.fragment));

        /* GLSL syntax highlighting (stored as config/mappet/highlights/glsl.json) */
        SyntaxHighlighter highlighter = ShaderHighlighters.get();

        this.vertex.setHighlighter(highlighter);
        this.fragment.setHighlighter(highlighter);

        /* A small toggle to switch between editing the vertex / fragment stage. */
        this.showVertex = new GuiToggleElement(mc, IKey.lang("ae.gui.shaders.vertex"), false, (b) -> this.updateEditors());
        this.showVertex.flex().relative(this.editor).xy(10, 6).w(120).h(14);

        this.vertex.flex().relative(this.editor).xy(0.01F, 0.07F).wh(0.98F, 0.91F);
        this.fragment.flex().relative(this.editor).xy(0.01F, 0.07F).wh(0.98F, 0.91F);

        this.editor.add(this.showVertex, this.vertex, this.fragment);
        this.iconBar.add(this.docs, this.run, this.stop);

        this.updateEditors();
        this.fill(null);
    }

    @Override
    public void draw(GuiContext context) {
        super.draw(context);

        this.drawStatus();
    }

    private void drawStatus() {
        if (this.data == null) {
            return;
        }

        int y = this.editor.area.y + this.editor.area.h - 16;
        String text;
        int color;

        if (this.shaderError != null) {
            text = IKey.format("ae.gui.shaders.status.error", this.shaderError.split("\n")[0]).get();
            color = 0xff5555;
        } else if (this.applied) {
            text = IKey.lang("ae.gui.shaders.status.applied").get();
            color = 0x55ff55;
        } else {
            text = IKey.lang("ae.gui.shaders.status.ready").get();
            color = 0xaaaaaa;
        }

        this.font.drawStringWithShadow(text, this.editor.area.x + 10, y, color);
    }

    private void updateEditors() {
        boolean vertexMode = this.showVertex.isToggled();

        this.vertex.setVisible(vertexMode);
        this.fragment.setVisible(!vertexMode);
    }

    private void openDocs(GuiIconElement element) {
        GuiShaderDocsOverlayPanel docsPanel = new GuiShaderDocsOverlayPanel(this.mc);
        GuiOverlay.addOverlay(GuiBase.getCurrent(), docsPanel, 0.9F, 0.9F);
    }

    private void runShader(GuiIconElement element) {
        if (this.data == null) {
            return;
        }

        this.preSave();
        this.save();

        ShaderData data = (ShaderData) this.data;
        String error = ShaderRenderer.validate(data.vertexSource, data.fragmentSource);

        if (error != null) {
            this.shaderError = error;
            this.applied = false;

            return;
        }

        this.shaderError = null;
        this.applied = true;

        ShaderRenderer.apply(data);

        Dispatcher.sendToServer(new PacketShader(data.getId(), false));
    }

    private void stopShader(GuiIconElement element) {
        this.applied = false;
        this.shaderError = null;

        ShaderRenderer.clear();
        Dispatcher.sendToServer(new PacketShader(this.data == null ? "" : this.data.getId(), true));
    }

    private void applyEditorToData() {
        if (this.data == null) {
            return;
        }

        ShaderData data = (ShaderData) this.data;

        data.vertexSource = this.vertex.getText();
        data.fragmentSource = this.fragment.getText();
    }

    public static GuiContextMenu createShadersContextMenu(Minecraft mc, GuiTextEditor editor) {
        GuiSimpleContextMenu menu = new GuiSimpleContextMenu(mc).action(Icons.MATERIAL, IKey.lang("mappet.gui.scripts.context.paste_colorRGB"), () -> openColorPicker(editor, false)).action(Icons.MATERIAL, IKey.lang("mappet.gui.scripts.context.paste_colorARGB"), () -> openColorPicker(editor, true));
        if (editor.isSelected()) { setupDocumentation(editor, menu); }
        return menu;
    }

    private static void setupDocumentation(GuiTextEditor editor, GuiSimpleContextMenu menu) {
        String text = editor.getSelectedText().replaceAll("[^\\w\\d_]+", "");
        List<DocClass> searched = GuiShaderDocsOverlayPanel.search(text);
        if (searched.isEmpty()) {
            return;
        }

        for (DocClass docClass : searched) {
            menu.action(Icons.SEARCH, IKey.format("mappet.gui.scripts.context.docs", docClass.getName()), () -> {
                searchDocumentation(editor, docClass.getMethod(text));
            });
        }
    }

    private static void searchDocumentation(GuiTextEditor editor, DocMethod method) {
        GuiShaderDocsOverlayPanel panel = new GuiShaderDocsOverlayPanel(Minecraft.getMinecraft(), method);
        GuiOverlay.addOverlay(GuiBase.getCurrent(), panel, 0.9F, 0.9F);
    }

    private static void openColorPicker(final GuiTextEditor editor, final boolean isArgb) {
        GuiOverlayPanel panel = new GuiOverlayPanel(Minecraft.getMinecraft(), IKey.lang("mappet.gui.scripts.context.paste_colorRGB"));

        GuiColorPicker picker = new GuiColorPicker(Minecraft.getMinecraft(), (color) -> {
            String hex = String.format(isArgb ? "#%08X" : "#%06X", color);

            editor.pasteText(hex);
        });

        if (isArgb) {
            picker.editAlpha();
        }

        picker.flex().relative(panel.content).xy(10, 10).wh(1F, -20).h(1F, -20);
        panel.content.add(picker);

        GuiOverlay.addOverlay(GuiBase.getCurrent(), panel, 220, 260);
    }

    @Override
    public ContentType getType() {
        return ContentType.SHADERS;
    }

    @Override
    public String getTitle() {
        return "ae.gui.panels.shaders";
    }

    @Override
    public void open() {
        super.open();

        this.updateStyle();
    }

    public void updateStyle() {
        SyntaxStyle style = Mappet.scriptEditorSyntaxStyle.get();

        if (this.fragment.getHighlighter().getStyle() != style) {
            this.fragment.getHighlighter().setStyle(style);
            this.fragment.resetHighlight();
            this.fragment.recalculate();
        }

        if (this.vertex.getHighlighter().getStyle() != style) {
            this.vertex.getHighlighter().setStyle(style);
            this.vertex.resetHighlight();
            this.vertex.recalculate();
        }
    }

    @Override
    public void appear() {
        this.update = false;
        this.save = true;

        this.requestDataNames();

        if (this.data != null) {
            this.fill(this.getType().getManager().load(this.data.getId()));
        }
    }

    @Override
    public void requestDataNames() {
        List<String> names = new java.util.ArrayList<>(this.getType().getManager().getKeys());

        this.fillNames(names);
    }

    @Override
    public void pickData(String id) {
        this.save();

        this.fill(this.getType().getManager().load(id));
    }

    @Override
    public void save() {
        if (!this.update && this.data != null && this.editor.isEnabled()) {
            this.preSave();

            this.getType().getManager().save(this.data.getId(), this.data.serializeNBT());
        }
    }

    @Override
    protected void preSave() {
        this.applyEditorToData();
    }

    @Override
    protected void addNewData(String name, AbstractData data) {
        if (this.namesList.hasInHierarchy(name)) {
            return;
        }

        this.save();

        this.namesList.addFile(name);

        if (data == null) {
            data = this.getType().getManager().create(name);
            this.fillDefaultData((AbstractData) data);
        } else {
            data.setId(name);
        }

        this.getType().getManager().save(name, data.serializeNBT());
        this.fill(data);
    }

    @Override
    protected void dupeData(String name) {
        if (this.namesList.hasInHierarchy(name)) {
            return;
        }

        this.save();

        NBTTagCompound tag = this.data.serializeNBT();
        AbstractData data = this.getType().getManager().create(name, tag);

        this.getType().getManager().save(name, tag);
        this.namesList.addFile(name);
        this.fill(data);
    }

    @Override
    protected void renameData(String name) {
        if (this.namesList.hasInHierarchy(name)) {
            return;
        }

        String path = this.getDataPath();
        String oldId = this.data.getId();
        String newId = path + name;

        this.save();

        this.getType().getManager().rename(oldId, newId);

        this.namesList.removeFile(oldId);
        this.namesList.addFile(newId);
        this.data.setId(newId);
    }

    @Override
    protected void removeData(boolean confirm) {
        if (confirm && this.data != null) {
            String id = this.data.getId();

            this.getType().getManager().delete(id);
            this.namesList.removeFile(id);

            this.applied = false;
            this.shaderError = null;

            this.fill(null);
        }
    }

    @Override
    protected void fillDefaultData(AbstractData data) {
        ShaderData shader = (ShaderData) data;

        shader.vertexSource = DEFAULT_VERTEX;
        shader.fragmentSource = DEFAULT_FRAGMENT;
    }

    @Override
    public void fill(AbstractData data, boolean allowed) {
        ShaderData last = (ShaderData) this.data;
        String lastVertex = last == null ? null : last.vertexSource;
        String lastFragment = last == null ? null : last.fragmentSource;

        super.fill(data, allowed);

        boolean hasData = data != null;

        if (!hasData) {
            this.shaderError = null;
        }

        this.editor.setVisible(hasData);
        this.docs.setVisible(hasData && allowed);
        this.run.setVisible(hasData && allowed);
        this.stop.setVisible(hasData && allowed);
        this.showVertex.setVisible(hasData && allowed);

        if (hasData) {
            ShaderData shaderData = (ShaderData) data;

            if (!shaderData.vertexSource.equals(lastVertex)) {
                this.vertex.setText(shaderData.vertexSource);
                this.vertex.recalculate();
            }

            if (!shaderData.fragmentSource.equals(lastFragment)) {
                this.fragment.setText(shaderData.fragmentSource);
                this.fragment.recalculate();
            }
        }
    }

    public static final String DEFAULT_VERTEX = "#version 120\n" +
            "void main()\n" +
            "{\n" +
            "    gl_TexCoord[0] = gl_MultiTexCoord0;\n" +
            "    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n" +
            "}";

    public static final String DEFAULT_FRAGMENT = "#version 120\n" +
            "\n" +
            "uniform sampler2D tex;\n" +
            "uniform float iTime;\n" +
            "uniform vec2 iResolution;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    vec2 uv = gl_TexCoord[0].xy;\n" +
            "    vec3 color = texture2D(tex, uv).rgb;\n" +
            "\n" +
            "    /* Simple animated vignette demo */\n" +
            "    float d = distance(uv, vec2(0.5));\n" +
            "    color *= smoothstep(0.8, 0.3, d);\n" +
            "    color += 0.05 * vec3(sin(iTime), 0.0, cos(iTime));\n" +
            "\n" +
            "    gl_FragColor = vec4(color, 1.0);\n" +
            "}";
}
