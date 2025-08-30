package veinminer;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import veinminer.handler.KeyInputHandler;
import veinminer.key.KeyBindings;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        KeyBindings.init();

        FMLCommonHandler.instance()
            .bus()
            .register(new KeyInputHandler());
    }
}
