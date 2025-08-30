package veinminer.handler;

import static veinminer.VeinMiner.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.oredict.OreDictionary;

import com.github.bsideup.jabel.Desugar;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import veinminer.ItemStackWrapper;
import veinminer.VeinMiningProperties;
import veinminer.config.Config;
import veinminer.packet.PacketToggleVeinMine;

public class ServerHandler {

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP player) {
            network.sendTo(new PacketToggleVeinMine(Config.maxAmount, Config.maxGap, Config.preciseMode), player);
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof EntityPlayerMP playerMP)) return;
        VeinMiningProperties props = VeinMiningProperties.get(playerMP);
        if (props != null && props.isEnabled()) {
            clearConnectedBlocks(
                playerMP,
                event.x,
                event.y,
                event.z,
                props.getMaxAmount(),
                props.getMaxGap(),
                props.isPreciseMode());
        }
    }

    public void clearConnectedBlocks(EntityPlayerMP player, int x, int y, int z, int amount, int maxGap,
        boolean preciseMode) {
        if (player.getFoodStats()
            .getFoodLevel() <= 0
            && player.getFoodStats()
                .getSaturationLevel() <= 0f
            && !player.capabilities.isCreativeMode) {
            return;
        }
        World world = player.worldObj;
        ItemStack stack = player.getHeldItem();

        Queue<Node> queue = new ArrayDeque<>();
        Set<Long> visited = new HashSet<>();
        int cleared = 0;
        int blocksSinceHunger = 0;
        int toolMaxDamage = 0;
        int toolDamage = 0;

        if (stack != null) {
            toolMaxDamage = stack.getMaxDamage();
            toolDamage = stack.getItemDamage();
        }

        Block targetBlock = world.getBlock(x, y, z);
        int targetMeta = world.getBlockMetadata(x, y, z);

        List<ItemStack> allDrops = new ArrayList<>();

        queue.add(new Node(x, y, z, 0));

        while (!queue.isEmpty() && cleared < amount) {
            VeinMiningProperties props = VeinMiningProperties.get(player);
            if (!props.isEnabled()) break;

            Node node = queue.poll();
            int px = node.x, py = node.y, pz = node.z;
            int gap = node.gap;

            long key = (((long) px) & 0x3FFFFFF) << 38 | (((long) py) & 0xFFF) << 26 | (((long) pz) & 0x3FFFFFF);
            if (!visited.add(key)) continue;
            if (!world.blockExists(px, py, pz)) continue;

            Block block = world.getBlock(px, py, pz);
            int meta = world.getBlockMetadata(px, py, pz);
            float blockHardness = targetBlock.getBlockHardness(world, x, y, z);

            boolean matches = false;
            if (blockHardness >= 0) {
                if (block == targetBlock && (!preciseMode || meta == targetMeta)) {
                    matches = true;
                } else {
                    ItemStack stackAt = new ItemStack(block, 1, meta);
                    int[] oreIds = OreDictionary.getOreIDs(stackAt);
                    for (int id : oreIds) {
                        String name = OreDictionary.getOreName(id);
                        int[] targetIds = OreDictionary.getOreIDs(new ItemStack(targetBlock, 1, targetMeta));
                        for (int tid : targetIds) {
                            String tname = OreDictionary.getOreName(tid);
                            if (preciseMode) {
                                if (tname.equals(name)) {
                                    matches = true;
                                    break;
                                }
                            } else {
                                if ((name.startsWith("ore") && tname.startsWith("ore")) || tname.startsWith(name)) {
                                    matches = true;
                                    break;
                                }
                            }
                        }

                        if (matches) break;
                    }
                }
            }

            if (matches) {
                List<ItemStack> drops = removeBlockAndGetDrops(
                    player,
                    stack,
                    world,
                    px,
                    py,
                    pz,
                    block,
                    EnchantmentHelper.getSilkTouchModifier(player),
                    0);
                allDrops.addAll(drops);

                cleared++;
                blocksSinceHunger++;
                gap = 0;

                if (blocksSinceHunger >= 50) {
                    blocksSinceHunger = 0;
                    player.getFoodStats()
                        .addExhaustion(1f);
                }

                if (player.worldObj.rand.nextFloat() < 0.5f && !player.capabilities.isCreativeMode) {
                    if (toolMaxDamage > 0 && blockHardness > 0) {
                        if (toolDamage + 1 >= toolMaxDamage) {
                            world.playSoundEffect(player.posX, player.posY, player.posZ, "random.break", 1.0F, 1.0F);
                            if (stack.stackSize > 0) stack.stackSize--;
                            break;
                        } else {
                            stack.setItemDamage(toolDamage + 1);
                        }
                    }
                }

            } else {
                if (gap >= maxGap) continue;
                gap++;
            }

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) == 1) {
                            queue.add(new Node(px + dx, py + dy, pz + dz, gap));
                        }
                    }
                }
            }
        }

        if (blocksSinceHunger > 0) {
            player.getFoodStats()
                .addExhaustion(1f);
        }

        Map<ItemStackWrapper, Integer> merged = new HashMap<>();
        for (ItemStack drop : allDrops) {
            if (drop == null) continue;
            ItemStackWrapper key = new ItemStackWrapper(drop);
            merged.put(key, merged.getOrDefault(key, 0) + drop.stackSize);
        }
        for (Map.Entry<ItemStackWrapper, Integer> entry : merged.entrySet()) {
            ItemStack dropStack = entry.getKey()
                .stack()
                .copy();
            dropStack.stackSize = entry.getValue();
            dropItem(dropStack, world, player.posX, player.posY + 1, player.posZ);
        }
    }

    public List<ItemStack> removeBlockAndGetDrops(EntityPlayer player, ItemStack stack, World world, int x, int y,
        int z, Block block, boolean silk, int fortune) {
        List<ItemStack> drops = new ArrayList<>();
        if (!world.blockExists(x, y, z)) return drops;

        Block blk = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        if (blk == null || blk.isAir(world, x, y, z)) return drops;
        if (block != null && blk != block) return drops;

        float hardness = blk.getBlockHardness(world, x, y, z);
        if (hardness < 0) return drops;

        boolean canHarvest = true;
        String toolClass = blk.getHarvestTool(meta);
        int requiredLevel = blk.getHarvestLevel(meta);
        if (toolClass != null && stack != null) {
            int toolLevel = Objects.requireNonNull(stack.getItem())
                .getHarvestLevel(stack, toolClass);
            if (toolLevel < requiredLevel) {
                canHarvest = false;
            }
        }

        if (!player.capabilities.isCreativeMode && canHarvest) {
            blk.onBlockHarvested(world, x, y, z, meta, player);
            if (blk.removedByPlayer(world, player, x, y, z, true)) {
                blk.onBlockDestroyedByPlayer(world, x, y, z, meta);

                if (silk) {
                    ItemStack drop = blk
                        .getPickBlock(raytraceFromEntity(world, player, true, 10), world, x, y, z, player);
                    if (drop != null) drops.add(drop);
                } else {
                    drops.addAll(blk.getDrops(world, x, y, z, meta, fortune));
                }
            }
        } else {
            world.setBlockToAir(x, y, z);
        }
        return drops;
    }

    public static void dropItem(ItemStack drop, World world, double x, double y, double z) {
        float f = 0.7F;
        double d0 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
        double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
        double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
        EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, drop);
        entityitem.delayBeforeCanPickup = 10;
        world.spawnEntityInWorld(entityitem);
    }

    public static MovingObjectPosition raytraceFromEntity(World world, Entity player, boolean wut, double range) {
        float f = 1.0F;
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double d0 = player.prevPosX + (player.posX - player.prevPosX) * f;
        double d1 = player.prevPosY + (player.posY - player.prevPosY) * f;
        if (!world.isRemote && player instanceof EntityPlayer) d1 += 1.62D;
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * f;
        Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        Vec3 vec31 = vec3.addVector(f7 * range, f6 * range, f8 * range);
        return world.rayTraceBlocks(vec3, vec31, wut);
    }

    @Desugar
    private record Node(int x, int y, int z, int gap) {}
}
