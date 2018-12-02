package com.kotori316.transformer.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
    int dim;

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
        dim = p.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer p = new PacketBuffer(buf);
        p.writeBlockPos(pos).writeInt(amount);
        p.writeInt(dim);
    }

    public IMessage onReceive(MessageContext ctx) {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
            World world = ctx.getServerHandler().player.getEntityWorld();
            TileEntity entity = world.getTileEntity(pos);
            if (world.provider.getDimension() == dim && entity instanceof TileSource) {
                TileSource source = (TileSource) entity;
                source.amount_$eq(amount);
            }
        });
        return null;
    }
}
