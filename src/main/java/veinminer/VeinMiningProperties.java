package veinminer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class VeinMiningProperties implements IExtendedEntityProperties {

    public static final String IDENTIFIER = "VeinMiningData";

    private boolean enabled = false;

    private int maxAmount = 0;
    private int maxGap = 0;
    private boolean preciseMode = false;

    public static VeinMiningProperties get(EntityPlayer player) {
        return (VeinMiningProperties) player.getExtendedProperties(IDENTIFIER);
    }

    public static void register(EntityPlayer player) {
        player.registerExtendedProperties(IDENTIFIER, new VeinMiningProperties());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void toggle() {
        enabled = !enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public int getMaxGap() {
        return maxGap;
    }

    public void setMaxGap(int maxGap) {
        this.maxGap = maxGap;
    }

    public boolean isPreciseMode() {
        return preciseMode;
    }

    public void setPreciseMode(boolean preciseMode) {
        this.preciseMode = preciseMode;
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        compound.setBoolean("VeinMiningEnabled", enabled);
        compound.setInteger("VeinMiningMaxAmount", maxAmount);
        compound.setInteger("VeinMiningMaxGap", maxGap);
        compound.setBoolean("VeinMiningPrecise", preciseMode);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        enabled = compound.getBoolean("VeinMiningEnabled");
        maxAmount = compound.getInteger("VeinMiningMaxAmount");
        maxGap = compound.getInteger("VeinMiningMaxGap");
        preciseMode = compound.getBoolean("VeinMiningPrecise");
    }

    @Override
    public void init(Entity entity, World world) {}
}
