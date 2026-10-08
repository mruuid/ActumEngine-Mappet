package unkn0wn.ae.api.scripts.mixins;

import mchorse.blockbuster_pack.morphs.ImageMorph;
import mchorse.mappet.api.scripts.code.entities.ScriptEntity;
import mchorse.mappet.api.scripts.code.entities.ScriptPlayer;
import mchorse.metamorph.api.MorphAPI;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;
import net.optifine.shaders.Shaders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import unkn0wn.ae.AEMod;
import unkn0wn.ae.api.scripts.code.renderer.ScriptCamera;
import unkn0wn.ae.api.scripts.code.renderer.ScriptCameraOver;
import unkn0wn.ae.api.scripts.code.renderer.ScriptHandRender;
import unkn0wn.ae.api.scripts.code.renderer.ScriptHudRender;
import unkn0wn.ae.api.scripts.user.renderer.IScriptCamera;
import unkn0wn.ae.api.scripts.user.renderer.IScriptCameraOver;
import unkn0wn.ae.api.scripts.user.renderer.IScriptHandRender;
import unkn0wn.ae.api.scripts.user.renderer.IScriptHudRender;
import unkn0wn.ae.network.Dispatcher;
import unkn0wn.ae.network.packets.PacketClientData;
import unkn0wn.ae.network.packets.PacketSounds;
import unkn0wn.ae.network.packets.PacketTextures;
import unkn0wn.ae.utils.*;
import unkn0wn.ae.utils.mixins.utils.MixinTargetName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Mixin(value = ScriptPlayer.class, remap = false)
@MixinTargetName("mchorse.mappet.api.scripts.user.entities.IScriptPlayer")
public abstract class MixinScriptPlayer {
    @Shadow public abstract EntityPlayerMP getMinecraftPlayer();
    @Unique EntityPlayerMP player = getMinecraftPlayer();
    @Unique SkinUtils skinUtils = new SkinUtils(player);

    private final EntityPlayerMP players = this.getMinecraftPlayer();

    /**
     * Gets ping for player.
     * */
    public int getPing() { return this.player.ping; }

    /**
     * Gets the language that this player has set.
     *
     * @return the player's language code
     */
    public String getLanguage() {
        return ((EntityPlayer) this.players).getLanguage();
    }

    /**
     * Sends a packet to update sounds for this player.
     */
    public void soundUpdate(){
        Dispatcher.sendTo(new PacketSounds(AccessType.UPDATE), this.player);
    }

    /**
     * Sends a packet to update textures for this player.
     */
    public void textureUpdate(){
        Dispatcher.sendTo(new PacketTextures(AccessType.UPDATE), this.player);
    }

    /**
     * Download files from the specified world directory to the player's disk. If you don't specify a disk in the file path, you will start from the game folder.
     *
     * <pre>{@code
     *    function main(c) {
     *        const DimensionManager = Java.type('net.minecraftforge.common.DimensionManager')
     *        const worldDir = DimensionManager.getCurrentSaveRootDirectory().toPath()
     *        var filePath = worldDir.resolve('icon.png').toString()
     *
     *        // Loads the icon.png picture from the world folder, to the player in the config folder
     *        c.player.download(filePath, 'config/icon.png')
     *    }
     * }</pre>
     */
    public void download(String filePath, String path) throws IOException {
        Path pathFile = Paths.get(filePath);
        if(Files.isDirectory(pathFile)) {
            List<Path> filesList = Files.list(pathFile).collect(Collectors.toList());

            filesList.forEach((file) -> {
                try {
                    this.download(file.toString(), path +"/"+ file.getFileName());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            return;
        }

        if(Arrays.stream(AEMod.formats).noneMatch(path::endsWith)){
            NBTTagCompound data = new NBTTagCompound();

            data.setString("path", path);
            data.setByteArray("fileBytes", Files.readAllBytes(pathFile));
            data.setString("side", String.valueOf(DownloadType.SERVER_TO_CLIENT));
            data.setString("type", String.valueOf(DownloadType.DOWNLOAD));

            Dispatcher.sendTo(new PacketClientData(ClientData.DOWNLOAD, AccessType.SET, data), this.player);
        } else {
            throw new IllegalArgumentException("Invalid file format");
        }
    }

    /**
     * upload a file from the specified path on the player's disk to the specified path on the server.
     *
     * @param filePath the path of the file to upload
     * @param path the destination path on the server to save the file
     * @throws IllegalArgumentException if the file format is not valid
     */
    public void upload(String filePath, String path) {
        if(Arrays.stream(AEMod.formats).noneMatch(path::endsWith)){
            NBTTagCompound data = new NBTTagCompound();

            data.setString("filePath", filePath);
            data.setString("path", path);
            data.setString("side", String.valueOf(DownloadType.CLIENT_TO_SERVER));
            data.setString("type", String.valueOf(DownloadType.DOWNLOAD));

            Dispatcher.sendTo(new PacketClientData(ClientData.DOWNLOAD, AccessType.SET, data), this.player);
        } else {
            throw new IllegalArgumentException("Invalid file format");
        }
    }

    /**
     * Downloads a file from a URL and saves it to the specified file path on the player's disk.
     *
     * @param url the URL from which to download the file
     * @param path the destination path on the player's disk
     * @throws IllegalArgumentException if the file format is not valid
     */
    public void downloadFromURL(String url, String path) {
        if(Arrays.stream(AEMod.formats).noneMatch(path::endsWith)){
            NBTTagCompound data = new NBTTagCompound();

            data.setString("url", url);
            data.setString("path", path);
            data.setString("side", String.valueOf(DownloadType.SERVER_TO_CLIENT));
            data.setString("type", String.valueOf(DownloadType.URL));

            Dispatcher.sendTo(new PacketClientData(ClientData.DOWNLOAD, AccessType.SET, data), this.player);
        } else {
            throw new IllegalArgumentException("Invalid file format");
        }
    }

    /**
     * Reloads chameleon models to the player
     *
     * @throws IllegalArgumentException if the "chameleon" mod is not loaded
     */
    public void reloadModels() {
        if(Loader.isModLoaded("chameleon_morph")) {
            Dispatcher.sendTo(new PacketClientData(ClientData.CHAMELEON_MODELS, AccessType.USE), this.player);
        } else {
            throw new IllegalArgumentException("The chameleon mod is not loaded");
        }
    }

    /**
     * Gets the current time of the client.
     *
     * @param callback
     */
    public void getTime(Consumer<Object> callback) {
        UUID uniqueId = UUID.randomUUID();
        PacketClientData.callback.put(uniqueId, callback);

        Dispatcher.sendTo(new PacketClientData(ClientData.TIME, AccessType.GET, uniqueId), this.player);
    }

    /**
     * Gets a {@link IScriptCamera} for the current player.
     *
     * @return the camera instance
     */
    public IScriptCamera getCamera() {
        return new ScriptCamera(this.player);
    }

    /**
     * Gets a {@link IScriptCameraOver} for the current player
     *
     * @return the camera instance
     * */
    public IScriptCameraOver getCameraOver() { return new ScriptCameraOver(this.player); }

    /**
     * Gets a {@link IScriptHandRender} main or off - hand; 0 - main; 1 - off;.
     */
    public IScriptHandRender getHand(int hand) {
        return new ScriptHandRender(this.player, hand);
    }

    /**
     * Gets the {@link IScriptHudRender} with the specified name for the current player.
     *
     * @param name the name of the HUD render
     */
    public IScriptHudRender getHud(String name) {
        return new ScriptHudRender(this.player, name);
    }

    /**
     * Sets the skin (image morph) for the player. If null, the texture will become standard.
     */
    public void setSkin(AbstractMorph morph) throws Exception {
        if(morph instanceof ImageMorph) {
            this.skinUtils.setTexture((ImageMorph) morph);
        } else if(morph == null) {
            this.skinUtils.setTexture(null);
        } else {
            throw new IllegalArgumentException("morph is not a morph image");
        }
    }

    /**
     * Sets the skin type (steve or slim) for the player.
     */
    public void setSkinType(String type) {
        this.skinUtils.setType(type);
    }

    /**
     * Saves screenshots to the specified path with the specified name.
     *
     *<pre>{@code
     * function main(c)
     * {
     *     c.subject.saveScreenshot("\\config", "name")
     * }
     * }</pre>
     *
     * @param path
     * @param name
     *
     * @return void
     */
    public void saveScreenshot(String path, String name){
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.setString(ClientData.SCREEN.toString(), path);
        nbtTagCompound.setString("name", name);

        Dispatcher.sendTo(new PacketClientData(ClientData.SCREEN, AccessType.SET, nbtTagCompound), this.getMinecraftPlayer());
    }

    /**
     * Return player used shaderpack
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *      c.subject.getShaderPack(function(shader)
     *      {
     *          this.shader = shader.replace(".zip", "")
     *
     *          mappet.set("shader", this.shader)
     *      })
     *
     *      var shader = mappet.get("shader")
     *
     *      c.send(shader)
     *    }
     *  }</pre>
     */
    public void getShaderPack(Consumer<Object> callback) {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.setString(ClientData.SHADER.toString(), Shaders.getShaderPack().getName());

        PacketClientData.callback.put(this.getMinecraftPlayer().getUniqueID(), callback);
        Dispatcher.sendTo(new PacketClientData(ClientData.SHADER, AccessType.GET, nbtTagCompound), this.getMinecraftPlayer());

    }
}
