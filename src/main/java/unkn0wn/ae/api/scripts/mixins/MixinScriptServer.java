package unkn0wn.ae.api.scripts.mixins;

import mchorse.mappet.api.scripts.code.ScriptServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import unkn0wn.ae.AEMod;
import unkn0wn.ae.api.scripts.code.ScriptFancyWorld;
import unkn0wn.ae.api.scripts.user.IScriptFancyWorld;
import unkn0wn.ae.utils.mixins.utils.MixinTargetName;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@Mixin(value = ScriptServer.class, remap = false)
@MixinTargetName("mchorse.mappet.api.scripts.user.IScriptServer")
public abstract class MixinScriptServer {
    @Shadow(remap = false) private MinecraftServer server;

    /**
     * download the file from the url to the specified path on the server.
     *
     * <pre>{@code
     *    function main(c) {
     *        c.player.downloadFromURL('https://drive.google.com/file/d/19kMtDmVhzrGMO4J0f_vjVKTZlVZ5VCdq/view?usp=sharing', 'config/icon.png')
     *    }
     * }</pre>
     *
     * @param url url
     * @param path the destination path on the server to save the file
     * @throws IllegalArgumentException if the file format is not valid
     */
    public void downloadFromURL(String url, String path) {
        if(Arrays.stream(AEMod.formats).noneMatch(path::endsWith)) {
            if (url.contains("https://drive.google.com")) {
                url = url.replace("file/d/", "uc?id=").replace("/view?usp=sharing", "&export=download");
            }

            if (url.contains("https://dropbox.com")) {
                url = url.replace("www.dropbox.com", "dl.dropboxusercontent.com");
            }

            try {
                InputStream is = new URL(url).openStream();

                Files.copy(is, Paths.get(path));

                is.close();
            } catch (IOException ignored) {}
        } else {
            throw new IllegalArgumentException("Invalid file format");
        }
    }

    /**
     * Get fancy world at dimension ID.
     *
     * <pre>{@code
     *    var overworld = c.getServer().getFancyWorld(0);
     *
     *    // Do something with the world...
     * }</pre>
     */
    public IScriptFancyWorld getFancyWorld(int dimension) {
        return new ScriptFancyWorld(this.server.getWorld(dimension));
    }
}
