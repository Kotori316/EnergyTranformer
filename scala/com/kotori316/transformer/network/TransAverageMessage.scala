package com.kotori316.transformer.network

import com.kotori316.transformer.block.TileTrans
import com.kotori316.transformer.gui.ContainerTrans
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.network.PacketBuffer
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, MessageContext}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * To Client only
  */
class TransAverageMessage(tile: TileTrans) extends IMessage {
    def this() = {
        this(null)
    }

    var pos = BlockPos.ORIGIN
    var dimID = 0
    var receiveAverage = 0l
    var extractAverage = 0l
    if (tile != null) {
        pos = tile.getPos
        dimID = tile.getWorld.provider.getDimension
        receiveAverage = tile.getReceivedAverage
        extractAverage = tile.getExtractedAverage
    }

    override def toBytes(packet: ByteBuf): Unit = {
        val buf = new PacketBuffer(packet)
        buf.writeBlockPos(pos).writeInt(dimID).writeLong(receiveAverage).writeLong(extractAverage)
    }

    override def fromBytes(packet: ByteBuf): Unit = {
        val buf = new PacketBuffer(packet)
        pos = buf.readBlockPos()
        dimID = buf.readInt()
        receiveAverage = buf.readLong()
        extractAverage = buf.readLong()
    }

    @SideOnly(Side.CLIENT)
    def onReceive(ctx: MessageContext): IMessage = {
        Minecraft.getMinecraft.addScheduledTask(new Runnable {
            override def run(): Unit = {
                Minecraft.getMinecraft.player.openContainer match {
                    case c: ContainerTrans => c.receiveAve = receiveAverage; c.extractAve = extractAverage
                    case _ =>
                }
            }
        })
        null
    }
}
