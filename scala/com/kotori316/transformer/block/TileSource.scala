package com.kotori316.transformer.block

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{EnumFacing, ITickable}
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.{CapabilityEnergy, IEnergyStorage}

class TileSource extends TileEntity with ITickable {
  var amount = 1000

  var f = (_: Long) => 1

  var T = 1

  val energyStorage = new IEnergyStorage {
    override def receiveEnergy(maxReceive: Int, simulate: Boolean): Int = 0

    override def extractEnergy(maxExtract: Int, simulate: Boolean): Int = amount

    override def getEnergyStored: Int = amount

    override def getMaxEnergyStored: Int = amount

    override def canExtract: Boolean = true

    override def canReceive: Boolean = false
  }

  override def update(): Unit = {
    if (world.isRemote) return
    for (facing <- EnumFacing.VALUES;
         tile <- Option(world.getTileEntity(pos.offset(facing)));
         handler <- Option(tile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite))) {
      val i = handler.receiveEnergy(amount * f(world.getTotalWorldTime / T), false)
      if (i > 0) {
        handler.receiveEnergy(i, true)
      }
    }
  }

  override def readFromNBT(compound: NBTTagCompound): Unit = {
    super.readFromNBT(compound)
    amount = compound.getInteger("amount")
    T = compound.getInteger("T")
    if (T <= 0) T = 1
  }

  override def writeToNBT(compound: NBTTagCompound): NBTTagCompound = {
    compound.setInteger("amount", amount)
    compound.setInteger("T", T)
    super.writeToNBT(compound)
  }

  override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = {
    capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing)
  }

  override def getCapability[T](capability: Capability[T], facing: EnumFacing): T = {
    if (capability == CapabilityEnergy.ENERGY) {
      CapabilityEnergy.ENERGY.cast(energyStorage)
    } else
      super.getCapability(capability, facing)
  }
}
