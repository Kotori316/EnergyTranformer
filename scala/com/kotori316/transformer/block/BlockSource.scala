package com.kotori316.transformer.block

import com.kotori316.transformer.EnergyTranformer
import com.kotori316.transformer.gui.GuiHandler
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumBlockRenderType, EnumFacing, EnumHand}
import net.minecraft.world.World

object BlockSource extends BlockContainer(Material.IRON) {
    final val NAME = "source"
    setRegistryName(EnergyTranformer.modID, NAME)
    setUnlocalizedName(NAME)
    setCreativeTab(CreativeTabs.REDSTONE)
    setHardness(4f)
    val itemBlock = new ItemBlock(this)
    itemBlock.setRegistryName(EnergyTranformer.modID, NAME)

    override def createNewTileEntity(worldIn: World, meta: Int): TileEntity = new TileSource

    override def getRenderType(state: IBlockState) = EnumBlockRenderType.MODEL

    override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer,
                                  hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
        if (!playerIn.isSneaking) {
            playerIn.openGui(EnergyTranformer.INSTANCE, GuiHandler.guiID_source, worldIn, pos.getX, pos.getY, pos.getZ)
            true
        } else
            super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
    }
}
