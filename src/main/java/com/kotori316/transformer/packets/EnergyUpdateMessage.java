package com.kotori316.transformer.packets;

import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import com.kotori316.transformer.TileSource;

/**
 * To Client Only
 */
public class EnergyUpdateMessage {
    BlockPos pos;
    int dim;
    private int capacity;

    @SuppressWarnings("unused")
    //Accessed via reflection
    public EnergyUpdateMessage() {
    }

    public EnergyUpdateMessage(TileSource tile, int capacity) {
        pos = tile.getPos();
        dim = Optional.ofNullable(tile.getWorld()).map(World::getDimension).map(Dimension::getType).map(DimensionType::getId).orElse(0);
        this.capacity = capacity;
    }

    public static EnergyUpdateMessage fromBytes(PacketBuffer p) {
        EnergyUpdateMessage message = new EnergyUpdateMessage();
        message.pos = p.readBlockPos();
        message.dim = p.readInt();
        message.capacity = p.readInt();
        return message;
    }

    public void toBytes(PacketBuffer p) {
        p.writeBlockPos(pos).writeInt(dim);
        p.writeInt(capacity);
    }

    @OnlyIn(Dist.CLIENT)
    void onReceive(Supplier<NetworkEvent.Context> ctx) {
        Optional.ofNullable(ctx.get().getSender()).map(EntityPlayerMP::getServerWorld)
            .ifPresent(world -> {
                TileEntity entity = world.getTileEntity(pos);
                if (world.getDimension().getType().getId() == dim && entity instanceof TileSource) {
                    TileSource tile = (TileSource) entity;
                    ctx.get().enqueueWork(() -> tile.updateCapacity(capacity));
                    ctx.get().setPacketHandled(true);
                }
            });
    }
}
