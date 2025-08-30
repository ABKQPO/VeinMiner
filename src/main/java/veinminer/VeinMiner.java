package veinminer;

import static veinminer.Tags.MODID;
import static veinminer.Tags.MODNAME;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import veinminer.config.Config;
import veinminer.config.ReloadConfig;
import veinminer.handler.ServerHandler;
import veinminer.packet.PacketEnableVeinMining;
import veinminer.packet.PacketToggleVeinMine;

@Mod(
    modid = MODID,
    version = Tags.VERSION,
    name = MODNAME,
    acceptableRemoteVersions = "*",
    guiFactory = "veinminer.config.ConfigGuiFactory",
    acceptedMinecraftVersions = "1.7.10")

public class VeinMiner {

    @Mod.Instance
    public static VeinMiner instance;
    public static final String MODID = "veinminer";
    public static final String MODNAME = "VeinMiner";
    public static final String VERSION = Tags.VERSION;
    public static final String Arthor = "HFstudio";
    public static final String RESOURCE_ROOT_ID = "veinminer";

    static {
        File configDir = new File("config");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        File mainConfigFile = new File(configDir, "VeinMiner.cfg");
        Config.init(mainConfigFile);
    }

    @SidedProxy(clientSide = "veinminer.ClientProxy", serverSide = "veinminer.CommonProxy")
    public static CommonProxy proxy;
    public static SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandReloadConfig());
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        proxy.preInit(event);

        Config.init(event.getSuggestedConfigurationFile());

        FMLCommonHandler.instance()
            .bus()
            .register(new ConfigEventHandler());

        network.registerMessage(PacketToggleVeinMine.Handler.class, PacketToggleVeinMine.class, 0, Side.SERVER);
        network.registerMessage(PacketToggleVeinMine.Handler.class, PacketToggleVeinMine.class, 1, Side.CLIENT);
        network.registerMessage(PacketEnableVeinMining.Handler.class, PacketEnableVeinMining.class, 2, Side.SERVER);
        MinecraftForge.EVENT_BUS.register(new ServerHandler());
        FMLCommonHandler.instance()
            .bus()
            .register(new ServerHandler());
    }

    public static class ConfigEventHandler {

        @SubscribeEvent
        public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.modID.equals(VeinMiner.MODID)) {
                ReloadConfig.reloadConfig();
            }
        }
    }
}
