package veinminer.handler;

import static veinminer.VeinMiner.*;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import veinminer.config.Config;
import veinminer.key.KeyBindings;
import veinminer.packet.PacketEnableVeinMining;
import veinminer.packet.PacketToggleVeinMine;

public class KeyInputHandler {

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (KeyBindings.toggleVeinMine.isPressed()) {
            network.sendToServer(new PacketToggleVeinMine(Config.maxAmount, Config.maxGap, Config.preciseMode));
            network.sendToServer(new PacketEnableVeinMining(true));
        } else {
            network.sendToServer(new PacketEnableVeinMining(false));
        }
        if (KeyBindings.increaseMax.isPressed()) {
            Config.maxAmount += 1000;
            mc.thePlayer.addChatMessage(new ChatComponentText("VeinMiner MaxAmount = " + Config.maxAmount));
        }
        if (KeyBindings.decreaseMax.isPressed()) {
            Config.maxAmount = Math.max(1, Config.maxAmount - 1000);
            mc.thePlayer.addChatMessage(new ChatComponentText("VeinMiner MaxAmount = " + Config.maxAmount));
        }
        if (KeyBindings.increaseGap.isPressed()) {
            Config.maxGap += 1;
            mc.thePlayer.addChatMessage(new ChatComponentText("VeinMiner MaxGap = " + Config.maxGap));
        }
        if (KeyBindings.decreaseGap.isPressed()) {
            Config.maxGap = Math.max(0, Config.maxGap - 1);
            mc.thePlayer.addChatMessage(new ChatComponentText("VeinMiner MaxGap = " + Config.maxGap));
        }
        if (KeyBindings.togglePrecise.isPressed()) {
            Config.preciseMode = !Config.preciseMode;
            mc.thePlayer.addChatMessage(new ChatComponentText("VeinMiner PreciseMode = " + Config.preciseMode));
        }
    }
}
