package com.kotori316.transformer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import com.kotori316.transformer.packets.EnergyUpdateMessage;
import com.kotori316.transformer.packets.PacketHandler;

public class TileSource extends TileEntity {

    public static final int INITIAL_CAPACITY = 1000;
    public static final String GUI_ID = EnergyTransformer.modID + ":gui_" + BlockSource.NAME;

    public TileSource() {
        super(EnergyTransformer.TYPE);
    }

    private Storage storage = new Storage(INITIAL_CAPACITY);

    @Override
    public NBTTagCompound write(NBTTagCompound compound) {
        compound.putInt("capacity", storage.getMaxEnergyStored());
        return super.write(compound);
    }

    @Override
    public void read(NBTTagCompound compound) {
        super.read(compound);
        int capacity = compound.getInt("capacity");
        storage = new Storage(capacity);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side) {
        if (cap == CapabilityEnergy.ENERGY) return CapabilityEnergy.ENERGY.orEmpty(cap, LazyOptional.of(() -> storage));
        return super.getCapability(cap, side);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return serializeNBT();
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        read(tag);
    }

    @OnlyIn(Dist.CLIENT)
    public IEnergyStorage getStorage() {
        return storage;
    }

    public void updateCapacity(int newCapacity) {
        storage = new Storage(newCapacity);
        if (hasWorld() && world.isRemote) {
            PacketHandler.sendToServer(new EnergyUpdateMessage(this, newCapacity));
        }
    }

    private static final class Storage implements IEnergyStorage {
        final int capacity;

        public Storage(int capacity) {
            this.capacity = capacity;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return Math.min(maxExtract, getMaxEnergyStored());
        }

        @Override
        public int getEnergyStored() {
            return getMaxEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return capacity;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    }
}
