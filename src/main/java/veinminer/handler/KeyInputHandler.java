package veinminer.handler;

import static veinminer.VeinMiner.*;
import static veinminer.config.Config.*;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentTranslation;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import veinminer.config.Config;
import veinminer.key.KeyBindings;
import veinminer.packet.PacketEnableVeinMining;
import veinminer.packet.PacketToggleVeinMine;

@SideOnly(Side.CLIENT)
public class KeyInputHandler {

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (KeyBindings.toggleVeinMine.getIsKeyPressed()) {
            network.sendToServer(new PacketToggleVeinMine(Config.maxAmount, Config.maxGap, Config.preciseMode));
            network.sendToServer(new PacketEnableVeinMining(true));
        } else {
            network.sendToServer(new PacketEnableVeinMining(false));
        }
        if (KeyBindings.increaseMax.isPressed()) {
            Config.maxAmount += 1000;
            Config.maxAmount = Math.max(0, Math.min(serverMaxAmount, Config.maxAmount));
            network.sendToServer(new PacketToggleVeinMine(Config.maxAmount, Config.maxGap, Config.preciseMode));
            mc.thePlayer.addChatMessage(new ChatComponentTranslation("veinminer.amount", Config.maxAmount));
        }
        if (KeyBindings.decreaseMax.isPressed()) {
            Config.maxAmount -= 1000;
            Config.maxAmount = Math.max(0, Math.min(serverMaxAmount, Config.maxAmount));
            network.sendToServer(new PacketToggleVeinMine(Config.maxAmount, Config.maxGap, Config.preciseMode));
            mc.thePlayer.addChatMessage(new ChatComponentTranslation("veinminer.amount", Config.maxAmount));
        }
        if (KeyBindings.increaseGap.isPressed()) {
            Config.maxGap += 1;
            Config.maxGap = Math.max(0, Math.min(serverMaxGap, Config.maxGap));
            network.sendToServer(new PacketToggleVeinMine(Config.maxAmount, Config.maxGap, Config.preciseMode));
            mc.thePlayer.addChatMessage(new ChatComponentTranslation("veinminer.gap", Config.maxGap));
        }
        if (KeyBindings.decreaseGap.isPressed()) {
            Config.maxGap -= 1;
            Config.maxGap = Math.max(0, Math.min(serverMaxGap, Config.maxGap));
            network.sendToServer(new PacketToggleVeinMine(Config.maxAmount, Config.maxGap, Config.preciseMode));
            mc.thePlayer.addChatMessage(new ChatComponentTranslation("veinminer.gap", Config.maxGap));
        }
        if (KeyBindings.togglePrecise.isPressed()) {
            Config.preciseMode = !Config.preciseMode;
            network.sendToServer(new PacketToggleVeinMine(Config.maxAmount, Config.maxGap, Config.preciseMode));
            mc.thePlayer.addChatMessage(
                new ChatComponentTranslation("veinminer.precise" + (Config.preciseMode ? "On" : "Off")));
        }
    }
}
