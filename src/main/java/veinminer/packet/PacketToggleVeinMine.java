package veinminer.packet;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import io.netty.buffer.ByteBuf;
import veinminer.VeinMiningProperties;
import veinminer.config.Config;

public class PacketToggleVeinMine implements IMessage {

    private int maxAmount;
    private int maxGap;
    private boolean clientPrecise;

    public PacketToggleVeinMine() {}

    public PacketToggleVeinMine(int maxAmount, int maxGap, boolean precise) {
        this.maxAmount = maxAmount;
        this.maxGap = maxGap;
        this.clientPrecise = precise;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(maxAmount);
        buf.writeInt(maxGap);
        buf.writeBoolean(clientPrecise);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        maxAmount = buf.readInt();
        maxGap = buf.readInt();
        clientPrecise = buf.readBoolean();
    }

    public static class Handler implements IMessageHandler<PacketToggleVeinMine, IMessage> {

        @Override
        public IMessage onMessage(PacketToggleVeinMine msg, MessageContext ctx) {
            if (ctx.side.isServer()) {
                EntityPlayer player = ctx.getServerHandler().playerEntity;

                if (VeinMiningProperties.get(player) == null) {
                    VeinMiningProperties.register(player);
                }

                int maxAmount = Config.maxAmount;
                int maxGap = Config.maxGap;

                int finalAmount = Math.min(msg.maxAmount, maxAmount);
                int finalGap = Math.min(msg.maxGap, maxGap);

                boolean finalPrecise = msg.clientPrecise;

                VeinMiningProperties props = VeinMiningProperties.get(player);
                if (props != null) {
                    props.setMaxAmount(finalAmount);
                    props.setMaxGap(finalGap);
                    props.setPreciseMode(finalPrecise);
                }
            } else if (ctx.side.isClient()) {
                Config.serverMaxAmount = msg.maxAmount;
                Config.serverMaxGap = msg.maxGap;
            }
            return null;
        }
    }
}
