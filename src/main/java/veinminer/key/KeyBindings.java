package veinminer.key;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;

public class KeyBindings {

    public static KeyBinding toggleVeinMine;
    public static KeyBinding increaseMax;
    public static KeyBinding decreaseMax;
    public static KeyBinding increaseGap;
    public static KeyBinding decreaseGap;
    public static KeyBinding togglePrecise;

    public static void init() {
        toggleVeinMine = new KeyBinding("key.veinmine.toggle", Keyboard.KEY_V, "key.categories.veinminer");
        increaseMax = new KeyBinding("key.veinminer.increaseMax", Keyboard.KEY_UP, "key.categories.veinminer");
        decreaseMax = new KeyBinding("key.veinminer.decreaseMax", Keyboard.KEY_DOWN, "key.categories.veinminer");
        increaseGap = new KeyBinding("key.veinminer.increaseGap", Keyboard.KEY_RIGHT, "key.categories.veinminer");
        decreaseGap = new KeyBinding("key.veinminer.decreaseGap", Keyboard.KEY_LEFT, "key.categories.veinminer");
        togglePrecise = new KeyBinding("key.veinminer.precise", Keyboard.KEY_P, "key.categories.veinminer");

        ClientRegistry.registerKeyBinding(toggleVeinMine);
        ClientRegistry.registerKeyBinding(increaseMax);
        ClientRegistry.registerKeyBinding(decreaseMax);
        ClientRegistry.registerKeyBinding(increaseGap);
        ClientRegistry.registerKeyBinding(decreaseGap);
        ClientRegistry.registerKeyBinding(togglePrecise);
    }
}
