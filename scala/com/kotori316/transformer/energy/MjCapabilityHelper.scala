package com.kotori316.transformer.energy

import buildcraft.api.mj.MjAPI
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.{Capability, ICapabilityProvider}
import net.minecraftforge.fml.common.Optional.Method

final class MjCapabilityHelper(storage: MJStorage) extends ICapabilityProvider {
    override def getCapability[T](capability: Capability[T], facing: EnumFacing) = getCapabilityImpl(capability, facing)

    @Method(modid = "BuildCraftAPI|core")
    private def getCapabilityImpl[T](capability: Capability[T], facing: EnumFacing): T = {
        capability match {
            case MjAPI.CAP_CONNECTOR => MjAPI.CAP_CONNECTOR.cast(storage)
            case MjAPI.CAP_RECEIVER if facing != storage.tile.extractFacing => MjAPI.CAP_RECEIVER.cast(storage)
            case MjAPI.CAP_REDSTONE_RECEIVER if facing != storage.tile.extractFacing => MjAPI.CAP_REDSTONE_RECEIVER.cast(storage)
            case MjAPI.CAP_READABLE if facing != storage.tile.extractFacing => MjAPI.CAP_READABLE.cast(storage)
            case MjAPI.CAP_PASSIVE_PROVIDER if facing == storage.tile.extractFacing => MjAPI.CAP_PASSIVE_PROVIDER.cast(storage)
            case _ => null.asInstanceOf[T]
        }
    }

    override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = hasCapabilityImpl(capability, facing)

    @Method(modid = "BuildCraftAPI|core")
    private def hasCapabilityImpl(capability: Capability[_], facing: EnumFacing): Boolean = {
        if (facing != storage.tile.extractFacing) {
            capability match {
                case MjAPI.CAP_CONNECTOR
                     | MjAPI.CAP_RECEIVER
                     | MjAPI.CAP_REDSTONE_RECEIVER
                     | MjAPI.CAP_READABLE => true
                case _ => false
            }
        } else {
            capability match {
                case MjAPI.CAP_CONNECTOR
                     | MjAPI.CAP_PASSIVE_PROVIDER => true
                case _ => false
            }
        }
    }
}
