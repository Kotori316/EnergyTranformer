package com.kotori316.transformer.block

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{EnumFacing, ITickable}
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.{CapabilityEnergy, IEnergyStorage}

class TileSource extends TileEntity with ITickable {
    var amount = 1000

    override def update(): Unit = {
        if (world.isRemote) return
        for (f <- EnumFacing.VALUES;
             tile <- Option(world.getTileEntity(pos.offset(f)));
             handler <- Option(tile.getCapability(CapabilityEnergy.ENERGY, f.getOpposite))) {
            val i = handler.receiveEnergy(amount, false)
            if (i > 0) {
                handler.receiveEnergy(i, true)
            }
        }
    }

    override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = {
        capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing)
    }

    override def getCapability[T](capability: Capability[T], facing: EnumFacing): T = {
        if (capability == CapabilityEnergy.ENERGY) {
            CapabilityEnergy.ENERGY.cast(new IEnergyStorage {
                override def receiveEnergy(maxReceive: Int, simulate: Boolean): Int = 0

                override def extractEnergy(maxExtract: Int, simulate: Boolean): Int = amount

                override def getEnergyStored: Int = amount

                override def getMaxEnergyStored: Int = amount

                override def canExtract: Boolean = true

                override def canReceive: Boolean = false
            })
        } else
            super.getCapability(capability, facing)
    }
}
