package com.kotori316.transformer.gui

import com.kotori316.transformer.block.TileSource
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.{Container, Slot}
import net.minecraft.item.ItemStack

class ContainerSource(player: EntityPlayer, tile: TileSource) extends Container {
    final val oneBox = 18
    for (h <- 0 until 3; v <- 0 until 9)
        this.addSlotToContainer(new Slot(player.inventory, v + h * 9 + 9, 8 + v * oneBox, 84 + h * oneBox))

    for (vertical <- 0 until 9)
        this.addSlotToContainer(new Slot(player.inventory, vertical, 8 + vertical * oneBox, 142))

    override def transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack = ItemStack.EMPTY

    override def canInteractWith(playerIn: EntityPlayer) = tile.getWorld.getTileEntity(tile.getPos) == tile

}
