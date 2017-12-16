package com.kotori316.transformer.block

import cofh.redstoneflux.api.{IEnergyProvider, IEnergyReceiver}
import com.kotori316.transformer.energy.{IC2Helper, MJStorage}
import ic2.api.energy.tile.{IEnergyAcceptor, IEnergyEmitter, IEnergySink, IEnergySource}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{EnumFacing, ITickable}
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.{CapabilityEnergy, IEnergyStorage}
import net.minecraftforge.fml.common.Optional.{Interface, Method}
import net.minecraftforge.fml.common.{Loader, ModAPIManager}

@Interface(iface = "cofh.redstoneflux.api.IEnergyReceiver", modid = "redstoneflux")
@Interface(iface = "cofh.redstoneflux.api.IEnergyProvider", modid = "redstoneflux")
@Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "ic2")
@Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "ic2")
class TileTrans extends TileEntity with ITickable
  with IEnergyReceiver with IEnergyProvider
  with IEnergySink with IEnergySource {
    tile =>

    var allEnergy = 0l
    var extractFacing: EnumFacing = EnumFacing.UP
    var lastRecieveTick = 0l
    var lastExtractTick = 0l
    private[this] val numElements = 400
    val recieveQueue = new java.util.ArrayDeque[Long](numElements)
    val extractQueue = new java.util.ArrayDeque[Long](numElements)
    val capacity = 1e18.toLong
    private[this] val hasBC = ModAPIManager.INSTANCE.hasAPI("BuildCraftAPI|core")
    private[this] val hasRF = Loader.isModLoaded("redstoneflux")
    private[this] val hasIC2 = Loader.isModLoaded("ic2")
    private[this] val mJStorages = EnumFacing.VALUES.+:(null).map(f => (f, MJStorage(hasBC, this, f))).toMap
    private[this] val iC2Helper = new IC2Helper(hasIC2, this)
    private[this] val energyStorages = EnumFacing.VALUES.map(f => (f, new ForgeEnergyFacing(f))).+:((null, ForgeEnergy)).toMap

    override def update() = {
        if (!getWorld.isRemote) {
            if (lastExtractTick != getWorld.getTotalWorldTime) {
                lastExtractTick = getWorld.getTotalWorldTime
                if (extractQueue.size() >= numElements) {
                    extractQueue.poll()
                }
                extractQueue.add(0l)
            }
            if (lastRecieveTick != getWorld.getTotalWorldTime) {
                lastRecieveTick = getWorld.getTotalWorldTime
                if (recieveQueue.size() >= numElements) {
                    recieveQueue.poll()
                }
                recieveQueue.add(0l)
            }
            val t = getWorld.getTileEntity(getPos.offset(extractFacing))
            if (t != null) {
                if (hasRF && isRFReciever(t)) {
                    transferRF(t, extractFacing.getOpposite)
                } else if (hasIC2 && isEUReciever(t)) {
                    transferEU(t, extractFacing.getOpposite)
                } else if (hasBC && MJStorage.isMJReciever(t, extractFacing.getOpposite)) {
                    mJStorages(extractFacing).tranferMJ(t, extractFacing.getOpposite)
                } else if (t.hasCapability(CapabilityEnergy.ENERGY, extractFacing.getOpposite)) {
                    val handler = t.getCapability(CapabilityEnergy.ENERGY, extractFacing.getOpposite)
                    if (handler.canReceive) {
                        val simulated = handler.receiveEnergy(ForgeEnergy.getEnergyStored, true)
                        if (simulated > 0) {
                            val accepted = handler.receiveEnergy(ForgeEnergy.getEnergyStored, false)
                            ForgeEnergy.extractEnergy(accepted, simulate = false)
                        }
                    }
                }
            }
        }
    }

    override def readFromNBT(compound: NBTTagCompound) = {
        super.readFromNBT(compound)
        extractFacing = EnumFacing.getFront(compound.getByte("facing"))
        allEnergy = compound.getLong("allenergy")
    }

    override def writeToNBT(compound: NBTTagCompound) = {
        compound.setByte("facing", extractFacing.getIndex.toByte)
        compound.setLong("allenergy", allEnergy)
        super.writeToNBT(compound)
    }

    override def onLoad(): Unit = {
        super.onLoad()
        iC2Helper.postLoadEvent()
    }

    override def onChunkUnload(): Unit = {
        super.onChunkUnload()
        iC2Helper.postUnloadEvent()
    }

    override def invalidate(): Unit = {
        super.invalidate()
        iC2Helper.postUnloadEvent()
    }

    override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = {
        if (capability == CapabilityEnergy.ENERGY) {
            true
        } else {
            (hasBC && mJStorages(facing).hasCapability(capability, facing)) || super.hasCapability(capability, facing)
        }
    }

    override def getCapability[T](capability: Capability[T], facing: EnumFacing): T = {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorages.getOrElse(facing, ForgeEnergy))
        }
        if (hasBC) {
            val t = mJStorages(facing).getCapability(capability, facing)
            if (t != null)
                return t.asInstanceOf[T]
        }
        super.getCapability(capability, facing)
    }

    def safeDivide(l1: Long, l2: Long): Int = {
        val l = l1 / l2
        if (l > Integer.MAX_VALUE) return Integer.MAX_VALUE
        if (l < Integer.MIN_VALUE) return Integer.MIN_VALUE
        l.toInt
    }

    def addEnergy(amount: Long): Unit = {
        allEnergy += amount
        if (lastRecieveTick == getWorld.getTotalWorldTime) {
            recieveQueue.addLast(recieveQueue.pollLast() + amount)
        } else {
            lastRecieveTick = getWorld.getTotalWorldTime
            if (recieveQueue.size() >= numElements) {
                recieveQueue.poll()
            }
            recieveQueue.addLast(amount)
        }
    }

    def minusEnergy(amount: Long): Unit = {
        allEnergy -= amount
        if (lastExtractTick == getWorld.getTotalWorldTime) {
            extractQueue.addLast(extractQueue.pollLast() + amount)
        } else {
            lastExtractTick = getWorld.getTotalWorldTime
            if (extractQueue.size() >= numElements) {
                extractQueue.poll()
            }
            extractQueue.addLast(amount)
        }
    }

    def getRecievedAverage: Long = {
        import scala.collection.JavaConverters._
        val sum = recieveQueue.asScala.sum
        if (sum == 0) return 0l
        sum / recieveQueue.size()
    }

    def getExtractedAverage: Long = {
        import scala.collection.JavaConverters._
        val sum = extractQueue.asScala.sum
        if (sum == 0) return 0l
        sum / extractQueue.size()
    }

    def acceptable: Long = Math.max(tile.capacity - tile.allEnergy, 0l)

    //Forge Energy
    val oneFEis = 1e5.toLong

    private class ForgeEnergyFacing(facing: EnumFacing) extends IEnergyStorage {

        override def canExtract = facing == extractFacing

        override def canReceive = facing != extractFacing

        override def getMaxEnergyStored = ForgeEnergy.getMaxEnergyStored

        override def getEnergyStored = ForgeEnergy.getEnergyStored

        override def receiveEnergy(maxReceive: Int, simulate: Boolean) =
            if (canReceive) ForgeEnergy.receiveEnergy(maxReceive, simulate, ())
            else 0

        override def extractEnergy(maxExtract: Int, simulate: Boolean) =
            if (canExtract) ForgeEnergy.extractEnergy(maxExtract, simulate, ())
            else 0
    }

    private object ForgeEnergy extends ForgeEnergyFacing(null) {
        override val canReceive: Boolean = false

        override def getEnergyStored: Int = safeDivide(tile.allEnergy, oneFEis)

        override def receiveEnergy(maxReceive: Int, simulate: Boolean): Int = 0

        def receiveEnergy(maxReceive: Int, simulate: Boolean, dummy: Unit): Int = {
            val acc = acceptable
            if (maxReceive * oneFEis > acc) {
                if (!simulate) {
                    addEnergy(maxReceive * oneFEis - acc)
                }
                ((maxReceive * oneFEis - acc) / oneFEis).toInt
            } else {
                if (!simulate) {
                    addEnergy(maxReceive * oneFEis)
                }
                maxReceive
            }
        }

        override val canExtract: Boolean = false

        override def extractEnergy(maxExtract: Int, simulate: Boolean): Int = 0

        def extractEnergy(maxExtract: Int, simulate: Boolean, dummy: Unit): Int = {
            val stored = tile.allEnergy
            if (stored < maxExtract * oneFEis) {
                if (!simulate) {
                    minusEnergy(maxExtract * oneFEis - stored)
                }
                ((maxExtract * oneFEis - stored) / oneFEis).toInt
            } else {
                if (!simulate) {
                    minusEnergy(maxExtract * oneFEis)
                }
                maxExtract
            }
        }

        override val getMaxEnergyStored: Int = safeDivide(tile.capacity, oneFEis)
    }

    //Redstone Flux
    val oneRFis = 1e5.toLong

    @Method(modid = "redstoneflux")
    override def receiveEnergy(from: EnumFacing, maxReceive: Int, simulate: Boolean) = energyStorages(from).receiveEnergy(maxReceive, simulate)

    @Method(modid = "redstoneflux")
    override def extractEnergy(from: EnumFacing, maxExtract: Int, simulate: Boolean) = energyStorages(from).extractEnergy(maxExtract, simulate)

    @Method(modid = "redstoneflux")
    override def getMaxEnergyStored(from: EnumFacing) = ForgeEnergy.getMaxEnergyStored

    @Method(modid = "redstoneflux")
    override def getEnergyStored(from: EnumFacing) = ForgeEnergy.getEnergyStored

    @Method(modid = "redstoneflux")
    override def canConnectEnergy(from: EnumFacing) = true

    @Method(modid = "redstoneflux")
    private def isRFReciever(t: TileEntity) = t.isInstanceOf[IEnergyReceiver]

    @Method(modid = "redstoneflux")
    private def transferRF(t: TileEntity, facing: EnumFacing): Unit = {
        val receiver = t.asInstanceOf[TileEntity with IEnergyReceiver]
        if (receiver.canConnectEnergy(facing)) {
            val simutlated = receiver.receiveEnergy(facing, getEnergyStored(extractFacing), true)
            if (simutlated > 0) {
                val accepted = receiver.receiveEnergy(facing, getEnergyStored(extractFacing), false)
                extractEnergy(extractFacing, accepted, simulate = false)
            }
        }
    }

    //IC2 EU
    val oneEUis = 2.5e5.toLong

    @Method(modid = "ic2")
    override def getDemandedEnergy = acceptable / oneEUis

    @Method(modid = "ic2")
    override def injectEnergy(directionFrom: EnumFacing, amount: Double, voltage: Double) = {
        if (directionFrom != extractFacing.getOpposite) {
            addEnergy((amount * oneEUis.toDouble).toLong)
            0
        } else {
            amount
        }
    }

    @Method(modid = "ic2")
    override def getSinkTier = 4

    @Method(modid = "ic2")
    override def drawEnergy(amount: Double) = {
        minusEnergy((amount * oneEUis.toDouble).toLong)
        if (tile.allEnergy < 0) tile.allEnergy = 0
    }

    @Method(modid = "ic2")
    override def getSourceTier = 1

    @Method(modid = "ic2")
    override def getOfferedEnergy = tile.allEnergy.toDouble / oneEUis.toDouble

    @Method(modid = "ic2")
    override def acceptsEnergyFrom(emitter: IEnergyEmitter, side: EnumFacing) = side != extractFacing

    @Method(modid = "ic2")
    override def emitsEnergyTo(receiver: IEnergyAcceptor, side: EnumFacing) = side == extractFacing

    @Method(modid = "ic2")
    def isEUReciever(t: TileEntity): Boolean = t.isInstanceOf[IEnergySink]

    @Method(modid = "ic2")
    def transferEU(t: TileEntity, facing: EnumFacing): Unit = {
        val receiver = t.asInstanceOf[TileEntity with IEnergySink]
        if (receiver.acceptsEnergyFrom(tile, facing) && receiver.getDemandedEnergy > 0) {
            val left = receiver.injectEnergy(facing, getOfferedEnergy, 1)
            drawEnergy(getOfferedEnergy - left)
        }
    }
}
