package veinminer.packet;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import veinminer.VeinMiningProperties;

public class PacketEnableVeinMining implements IMessage {

    private boolean clientEnable;

    public PacketEnableVeinMining() {}

    public PacketEnableVeinMining(boolean enable) {
        this.clientEnable = enable;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(clientEnable);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        clientEnable = buf.readBoolean();
    }

    public static class Handler implements IMessageHandler<PacketEnableVeinMining, IMessage> {

        @Override
        public IMessage onMessage(PacketEnableVeinMining msg, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            VeinMiningProperties props = VeinMiningProperties.get(player);
            if (props != null) {
                props.setEnabled(msg.clientEnable);
            }

            return null;
        }
    }
}
