package veinminer.packet;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import io.netty.buffer.ByteBuf;
import veinminer.VeinMiningProperties;
import veinminer.config.Config;

public class PacketToggleVeinMine implements IMessage {

    private int clientMaxAmount;
    private int clientMaxGap;
    private boolean clientPrecise;

    public PacketToggleVeinMine() {}

    public PacketToggleVeinMine(int maxAmount, int maxGap, boolean precise) {
        this.clientMaxAmount = maxAmount;
        this.clientMaxGap = maxGap;
        this.clientPrecise = precise;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(clientMaxAmount);
        buf.writeInt(clientMaxGap);
        buf.writeBoolean(clientPrecise);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        clientMaxAmount = buf.readInt();
        clientMaxGap = buf.readInt();
        clientPrecise = buf.readBoolean();
    }

    public static class Handler implements IMessageHandler<PacketToggleVeinMine, IMessage> {

        @Override
        public IMessage onMessage(PacketToggleVeinMine msg, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            int serverMaxAmount = Config.maxAmount;
            int serverMaxGap = Config.maxGap;

            int finalAmount = Math.min(msg.clientMaxAmount, serverMaxAmount);
            int finalGap = Math.min(msg.clientMaxGap, serverMaxGap);

            boolean finalPrecise = msg.clientPrecise;

            VeinMiningProperties props = VeinMiningProperties.get(player);
            if (props != null) {
                props.setMaxAmount(finalAmount);
                props.setMaxGap(finalGap);
                props.setPreciseMode(finalPrecise);
            }
            return null;
        }
    }
}
