package veinminer.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

import cpw.mods.fml.client.config.GuiConfig;
import veinminer.VeinMiner;

public class ConfigGui extends GuiConfig {

    public ConfigGui(GuiScreen parentScreen) {
        super(
            parentScreen,
            new ConfigElement(Config.config.getCategory("veinminer")).getChildElements(),
            VeinMiner.MODID,
            false,
            false,
            GuiConfig.getAbridgedConfigPath(Config.config.toString()));
    }
}
