package com.kotori316.transformer;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import com.kotori316.transformer.gui.ContainerSource;

public class BlockSource extends BlockContainer {
    public static final String NAME = "source";
    public final ItemBlock itemBlock;

    public BlockSource() {
        super(Block.Properties.create(Material.ANVIL));
        setRegistryName(EnergyTransformer.modID, NAME);
        itemBlock = new ItemBlock(this, new Item.Properties().group(ItemGroup.REDSTONE));
        itemBlock.setRegistryName(EnergyTransformer.modID, NAME);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player,
                                    EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!player.isSneaking()) {
//            TileSource source = ((TileSource) worldIn.getTileEntity(pos));
//            System.out.println((!worldIn.isRemote ? "Server " : "Client ") + source.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(-1));
            if (!worldIn.isRemote) {
                NetworkHooks.openGui(((EntityPlayerMP) player), InteractionObject.OBJECT, pos);
            }
            return true;
        }
        return super.onBlockActivated(state, worldIn, pos, player, hand, side, hitX, hitY, hitZ);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return EnergyTransformer.TYPE.create();
    }

    @SuppressWarnings("deprecation")
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    private enum InteractionObject implements IInteractionObject {
        OBJECT;

        @Override
        public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
            return new ContainerSource(playerIn);
        }

        @Override
        public String getGuiID() {
            return TileSource.GUI_ID;
        }

        @SuppressWarnings("NoTranslation")
        @Override
        public ITextComponent getName() {
            return new TextComponentTranslation("block.energytransformer.source");
        }

        @Override
        public boolean hasCustomName() {
            return false;
        }

        @Nullable
        @Override
        public ITextComponent getCustomName() {
            return null;
        }
    }
}
