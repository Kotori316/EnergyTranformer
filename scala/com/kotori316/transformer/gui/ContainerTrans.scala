package com.kotori316.transformer.gui

import com.kotori316.transformer.block.TileTrans
import com.kotori316.transformer.network.{PacketHandler, TransAverageMessage}
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.inventory.{Container, Slot}
import net.minecraft.item.ItemStack

class ContainerTrans(tile: TileTrans, player: EntityPlayer) extends Container {
    val oneBox = 18
    var recieveAve = tile.getRecievedAverage
    var extractAve = tile.getExtractedAverage

    for (h <- 0 until 3)
        for (v <- 0 until 9)
            this.addSlotToContainer(new Slot(player.inventory, v + h * 9 + 9, 8 + v * oneBox, 84 + h * oneBox))

    for (vertical <- 0 until 9)
        this.addSlotToContainer(new Slot(player.inventory, vertical, 8 + vertical * oneBox, 142))

    override def transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack = ItemStack.EMPTY

    override def canInteractWith(playerIn: EntityPlayer) = tile.getWorld.getTileEntity(tile.getPos) == tile

    override def detectAndSendChanges(): Unit = {
        super.detectAndSendChanges()
        if (!tile.getWorld.isRemote) {
            PacketHandler.INStANCE.sendToPlayer(new TransAverageMessage(tile), player.asInstanceOf[EntityPlayerMP])
        }
    }
}
