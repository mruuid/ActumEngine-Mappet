package unkn0wn.ae.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import unkn0wn.ae.utils.sound.SoundEventHandler;
import unkn0wn.ae.utils.sound.SoundTickHandler;

/**
 * Mod support Script's [JS 5.1], [HTML and CSS], [GLSL] and [Kotlin]
 * */
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    public ClientProxy() {}

    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

    }

    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    public void registerTickHandlers()
    {
        FMLCommonHandler.instance().bus().register(new SoundTickHandler());
    }

    public void registerEventHandlers()
    {
        MinecraftForge.EVENT_BUS.register(new SoundEventHandler());
    }
}
