package com.kotori316.transformer.energy

import buildcraft.api.mj.{IMjConnector, IMjPassiveProvider, IMjReadable, IMjRedstoneReceiver, MjAPI}
import com.kotori316.transformer.block.TileTrans
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.{Capability, ICapabilityProvider}
import net.minecraftforge.fml.common.Optional.{Interface, Method}

@Interface(iface = "buildcraft.api.mj.IMjRedstoneReceiver", modid = MJStorage.bcId)
@Interface(iface = "buildcraft.api.mj.IMjReadable", modid = MJStorage.bcId)
@Interface(iface = "buildcraft.api.mj.IMjPassiveProvider", modid = MJStorage.bcId)
case class MJStorage(hasBC: Boolean, tile: TileTrans, facing: EnumFacing) extends ICapabilityProvider with IMjRedstoneReceiver with IMjReadable with IMjPassiveProvider {

    val mjHelper: ICapabilityProvider = {
        if (hasBC) {
            constructMjHelper
        } else null
    }

    override def getCapability[T](capability: Capability[T], facing: EnumFacing) = {
        if (hasBC) {
            mjHelper.getCapability(capability, facing)
        } else {
            null.asInstanceOf[T]
        }
    }

    override def hasCapability(capability: Capability[_], facing: EnumFacing) = {
        if (hasBC) {
            mjHelper.hasCapability(capability, facing)
        } else {
            false
        }
    }

    @Method(modid = MJStorage.bcId)
    def constructMjHelper: ICapabilityProvider = new MjCapabilityHelper(this)

    @Method(modid = MJStorage.bcId)
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

    @Method(modid = MJStorage.bcId)
    override def getPowerRequested = tile.acceptable

    @Method(modid = MJStorage.bcId)
    override def getStored = tile.allEnergy

    @Method(modid = MJStorage.bcId)
    override def getCapacity = tile.capacity

    @Method(modid = MJStorage.bcId)
    override def canConnect(other: IMjConnector) = true

    @Method(modid = MJStorage.bcId)
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

    @Method(modid = MJStorage.bcId)
    def transferMJ(t: TileEntity, facing: EnumFacing): Unit = {
        val receiver = t.getCapability(MjAPI.CAP_RECEIVER, facing)
        if (receiver.canReceive) {
            val required = Math.min(receiver.getPowerRequested, getStored)
            val e = receiver.receivePower(required, true)
            if (e != required) {
                val excess = receiver.receivePower(required, false)
                extractPower(required - excess, required - excess, simulate = false)
            }
        }
    }
}

object MJStorage {
    final val bcId = "buildcraftlib"

    @Method(modid = MJStorage.bcId)
    def isMJReceiver(t: TileEntity, facing: EnumFacing): Boolean = t.hasCapability(MjAPI.CAP_RECEIVER, facing)
}
