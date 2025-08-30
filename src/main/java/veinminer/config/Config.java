package veinminer.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

// spotless:off
public class Config {

    // Vein Miner
    public static int maxAmount = 327670;
    public static int maxGap = 32;
    public static boolean preciseMode = true;

    public static Configuration config;

    public static void init(File configFile) {
        if (config == null) {
            config = new Configuration(configFile);
            loadConfig();
        }

    }

    public static void reloadConfig() {
        if (config.hasChanged()) {
            config.save();
        }
        if (config != null) {
            config.load();
            loadConfig();
        }
    }

    public static void loadConfig() {
        maxAmount = config.get("veinminer", "MaxAmount", maxAmount, "VeinMiner can miner max amount")
            .getInt(maxAmount);

        maxGap = config.get("veinminer", "MaxGap", maxGap, "VeinMiner can miner max gap")
            .getInt(maxGap);

        preciseMode = config.get("veinminer", "PreciseMode", preciseMode, "VeinMiner need check same meta")
            .getBoolean(preciseMode);

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static Configuration getConfiguration() {
        return config;
    }

}
