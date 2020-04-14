package com.kotori316.transformer.block

import com.kotori316.transformer.EnergyTransformer
import com.kotori316.transformer.gui.GuiHandler
import net.minecraft.block.material.Material
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.block.{BlockContainer, BlockDirectional}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{EntityLiving, EntityLivingBase}
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumBlockRenderType, EnumFacing, EnumHand}
import net.minecraft.world.{IBlockAccess, World}

object BlockTrans extends BlockContainer(Material.IRON) {
    val NAME = "energytransformer"
    lazy val FACING = BlockDirectional.FACING
    val instance = this

    setUnlocalizedName(NAME)
    setRegistryName(EnergyTransformer.modID, NAME)
    setCreativeTab(CreativeTabs.REDSTONE)
    setHardness(5.0f)
    val itemBlock = new ItemBlock(this)
    itemBlock.setRegistryName(EnergyTransformer.modID, NAME)
    setDefaultState(getBlockState.getBaseState.withProperty(FACING, EnumFacing.NORTH))

    override def createNewTileEntity(worldIn: World, meta: Int) = new TileTrans

    override def getStateForPlacement(world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float,
                                      meta: Int, placer: EntityLivingBase, hand: EnumHand): IBlockState =
        getDefaultState.withProperty(FACING, facing.getOpposite)

    override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand,
                                  facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
        if (!playerIn.isSneaking) {
            playerIn.openGui(EnergyTransformer.INSTANCE, GuiHandler.guiID_trans, worldIn, pos.getX, pos.getY, pos.getZ)
            return true
        }
        super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
    }

    override def onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack): Unit = {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
        if (!worldIn.isRemote) {
            worldIn.getTileEntity(pos) match {
                case t: TileTrans => t.extractFacing = state.getValue(FACING)
            }
        }
    }

    override def getMetaFromState(state: IBlockState): Int = state.getValue(FACING).getIndex

    override def getStateFromMeta(meta: Int): IBlockState = getDefaultState.withProperty(FACING, EnumFacing.getFront(meta))

    override def createBlockState(): BlockStateContainer = new BlockStateContainer(this, FACING)

    override def getRenderType(state: IBlockState) = EnumBlockRenderType.MODEL

    override def canCreatureSpawn(state: IBlockState, world: IBlockAccess, pos: BlockPos, t: EntityLiving.SpawnPlacementType) = false

}