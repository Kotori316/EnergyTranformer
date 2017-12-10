package com.kotori316.transformer.energy

import buildcraft.api.mj.{IMjConnector, IMjReadable, IMjRedstoneReceiver, MjCapabilityHelper}
import com.kotori316.transformer.block.TileTrans
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.{Capability, ICapabilityProvider}
import net.minecraftforge.fml.common.Optional.{Interface, Method}

@Interface(iface = "buildcraft.api.mj.IMjRedstoneReceiver", modid = "BuildCraftAPI|core")
@Interface(iface = "buildcraft.api.mj.IMjReadable", modid = "BuildCraftAPI|core")
class MJStorage(hasBC: Boolean, tile: TileTrans) extends ICapabilityProvider with IMjRedstoneReceiver with IMjReadable {

    val mjhelper: ICapabilityProvider = {
        if (hasBC) {
            constructMjHelper
        } else null
    }

    override def getCapability[T](capability: Capability[T], facing: EnumFacing) = {
        if (hasBC) {
            mjhelper.getCapability(capability, facing)
        } else {
            null.asInstanceOf[T]
        }
    }

    override def hasCapability(capability: Capability[_], facing: EnumFacing) = {
        if (hasBC) {
            mjhelper.hasCapability(capability, facing)
        } else {
            false
        }
    }

    @Method(modid = "BuildCraftAPI|core")
    def constructMjHelper: ICapabilityProvider = {
        new MjCapabilityHelper(this)
    }

    @Method(modid = "BuildCraftAPI|core")
    override def receivePower(microJoules: Long, simulate: Boolean) = {
        if (tile.acceptable < microJoules) {
            val r = tile.acceptable
            if (!simulate) {
                tile.addEnergy(r)
            }
            r
        } else {
            if (!simulate) {
                tile.addEnergy(microJoules)
            }
            microJoules
        }
    }

    @Method(modid = "BuildCraftAPI|core")
    override def getPowerRequested = {
        tile.acceptable
    }

    @Method(modid = "BuildCraftAPI|core")
    override def getStored = tile.allEnergy

    @Method(modid = "BuildCraftAPI|core")
    override def getCapacity = tile.capacity

    @Method(modid = "BuildCraftAPI|core")
    override def canConnect(other: IMjConnector) = true

}
