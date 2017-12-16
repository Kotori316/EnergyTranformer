package com.kotori316.transformer.energy

import buildcraft.api.mj.{IMjConnector, IMjPassiveProvider, IMjReadable, IMjRedstoneReceiver, MjAPI}
import com.kotori316.transformer.block.TileTrans
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.{Capability, ICapabilityProvider}
import net.minecraftforge.fml.common.Optional.{Interface, Method}

@Interface(iface = "buildcraft.api.mj.IMjRedstoneReceiver", modid = "BuildCraftAPI|core")
@Interface(iface = "buildcraft.api.mj.IMjReadable", modid = "BuildCraftAPI|core")
@Interface(iface = "buildcraft.api.mj.IMjPassiveProvider", modid = "BuildCraftAPI|core")
case class MJStorage(hasBC: Boolean, tile: TileTrans, facing: EnumFacing) extends ICapabilityProvider with IMjRedstoneReceiver with IMjReadable with IMjPassiveProvider {

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
    def constructMjHelper: ICapabilityProvider = new MjCapabilityHelper(this)

    @Method(modid = "BuildCraftAPI|core")
    override def receivePower(microJoules: Long, simulate: Boolean) = {
        if (tile.acceptable < microJoules) {
            val r = tile.acceptable
            if (!simulate) {
                tile.addEnergy(r)
            }
            microJoules - r
        } else {
            if (!simulate) {
                tile.addEnergy(microJoules)
            }
            0
        }
    }

    @Method(modid = "BuildCraftAPI|core")
    override def getPowerRequested = tile.acceptable

    @Method(modid = "BuildCraftAPI|core")
    override def getStored = tile.allEnergy

    @Method(modid = "BuildCraftAPI|core")
    override def getCapacity = tile.capacity

    @Method(modid = "BuildCraftAPI|core")
    override def canConnect(other: IMjConnector) = true

    @Method(modid = "BuildCraftAPI|core")
    override def extractPower(min: Long, max: Long, simulate: Boolean): Long = {
        if (getStored > max) {
            if (!simulate) {
                tile.minusEnergy(max)
            }
            max
        } else if (getStored >= min) {
            val e = getStored
            if (!simulate) {
                tile.minusEnergy(e)
            }
            e
        } else {
            0l
        }
    }

    @Method(modid = "BuildCraftAPI|core")
    def tranferMJ(t: TileEntity, facing: EnumFacing): Unit = {
        val reciever = t.getCapability(MjAPI.CAP_RECEIVER, facing)
        if (reciever.canReceive) {
            val required = Math.min(reciever.getPowerRequested, getStored)
            val e = reciever.receivePower(required, true)
            if (e != required) {
                val excess = reciever.receivePower(required, false)
                extractPower(required - excess, required - excess, simulate = false)
            }
        }
    }
}

object MJStorage {
    @Method(modid = "BuildCraftAPI|core")
    def isMJReciever(t: TileEntity, facing: EnumFacing): Boolean = t.hasCapability(MjAPI.CAP_RECEIVER, facing)
}
