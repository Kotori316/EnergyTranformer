package com.kotori316.transformer.network;

import java.io.IOException;
import java.util.Optional;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.kotori316.transformer.block.TileSource;

public class SourceGUIMessage implements IMessage {

    NBTTagCompound compound;
    BlockPos pos;
    int dim;

    public SourceGUIMessage() {
    }

    public SourceGUIMessage(TileSource source) {
        this();
        this.compound = source.serializeNBT();
        this.pos = source.getPos();
        this.dim = source.getWorld().provider.getDimension();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer p = new PacketBuffer(buf);
        dim = p.readInt();
        pos = p.readBlockPos();
        try {
            compound = p.readCompoundTag();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer p = new PacketBuffer(buf);
        p.writeInt(dim);
        p.writeBlockPos(pos);
        p.writeCompoundTag(compound);
    }

    @SideOnly(Side.CLIENT)
    public IMessage onReceive(MessageContext ctx) {
        WorldClient world = Minecraft.getMinecraft().world;
        if (world.provider.getDimension() == dim) {
            Optional.ofNullable(world.getTileEntity(pos))
                .filter(TileSource.class::isInstance)
                .map(TileSource.class::cast)
                .ifPresent(source ->
                    FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
                        source.readFromNBT(compound)));
        }
        return null;
    }
}
