package com.kotori316.transformer.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.kotori316.transformer.block.TileSource;

/**
 * To server.
 */
public class SourceAmountMessage implements IMessage {
    int amount;
    BlockPos pos;

    public SourceAmountMessage() {
    }

    public SourceAmountMessage(int amount, BlockPos pos) {
        this();
        this.amount = amount;
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer p = new PacketBuffer(buf);
        pos = p.readBlockPos();
        amount = p.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer p = new PacketBuffer(buf);
        p.writeBlockPos(pos).writeInt(amount);
    }

    public IMessage onReceive(MessageContext ctx) {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
            TileEntity entity = ctx.getServerHandler().player.getEntityWorld().getTileEntity(pos);
            if (entity instanceof TileSource) {
                TileSource source = (TileSource) entity;
                source.amount_$eq(amount);
            }
        });
        return null;
    }
}
