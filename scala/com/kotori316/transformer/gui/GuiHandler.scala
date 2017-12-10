package com.kotori316.transformer.gui

import com.kotori316.transformer.block.TileTrans
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

object GuiHandler extends IGuiHandler {
    val instance = this
    val guiID_trans = 0

    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int) = {
        val pos = new BlockPos(x, y, z)
        ID match {
            case 0 => new GuiTrans(world.getTileEntity(pos).asInstanceOf[TileTrans], player)
            case _ => null
        }
    }

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int) = {
        val pos = new BlockPos(x, y, z)
        ID match {
            case 0 => new ContainerTrans(world.getTileEntity(pos).asInstanceOf[TileTrans], player)
            case _ => null
        }
    }
}
