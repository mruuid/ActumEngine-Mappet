package unkn0wn.ae.api.shaders;

import mchorse.mappet.api.utils.manager.BaseManager;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class ShadersManager extends BaseManager<ShaderData> {
    private String activeShaderName = null;
    private ShaderData activeShader = null;

    public ShadersManager(File folder) {
        super(folder);
    }

    public void apply(String id) {
        this.activeShader = this.load(id);
        this.activeShaderName = id;
    }

    public void clear() {
        this.activeShader = null;
        this.activeShaderName = null;
    }

    protected ShaderData createData(String id, NBTTagCompound tag) {
        ShaderData data = new ShaderData();
        if (tag != null) {
            data.deserializeNBT(tag);
        }

        return data;
    }

    public File getVertexFile(String id) {
        return this.folder == null ? null : new File(this.folder, id + ".vert");
    }

    public File getFragmentFile(String id) {
        return this.folder == null ? null : new File(this.folder, id + ".frag");
    }

    public ShaderData load(String id) {
        ShaderData data = (ShaderData)super.load(id);
        if (data == null) {
            data = new ShaderData();
        }

        File vert = this.getVertexFile(id);
        File frag = this.getFragmentFile(id);

        try {
            if (vert != null && vert.isFile()) {
                data.vertexSource = FileUtils.readFileToString(vert, "UTF-8");
            }

            if (frag != null && frag.isFile()) {
                data.fragmentSource = FileUtils.readFileToString(frag, "UTF-8");
            }
        } catch (Exception var6) {
        }

        data.setId(id);
        return data;
    }
}
