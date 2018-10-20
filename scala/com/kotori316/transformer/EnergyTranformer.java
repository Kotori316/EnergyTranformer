package com.kotori316.transformer;

import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kotori316.transformer.block.BlockSource;
import com.kotori316.transformer.block.BlockSource$;
import com.kotori316.transformer.block.BlockTrans;
import com.kotori316.transformer.block.TileSource;
import com.kotori316.transformer.block.TileTrans;
import com.kotori316.transformer.gui.GuiHandler;
import com.kotori316.transformer.network.PacketHandler;

@Mod(name = EnergyTranformer.MOD_NAME, modid = EnergyTranformer.modID, version = "${version}")
public class EnergyTranformer {

    public static final EnergyTranformer INSTANCE;
    public static final String MOD_NAME = "EnergyTranformer";
    public static final String modID = "kotori_energytrans";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    static {
        INSTANCE = new EnergyTranformer();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        PacketHandler.INSTANCE.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(EnergyTranformer.instance(), GuiHandler.instance());
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(BlockTrans.instance());
        event.getRegistry().register(BlockSource$.MODULE$);
        TileEntity.register(modID + ":" + BlockTrans.NAME(), TileTrans.class);
        TileEntity.register(modID + ":" + BlockSource.NAME(), TileSource.class);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(BlockTrans.itemBlock());
        event.getRegistry().register(BlockSource.itemBlock());
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(BlockTrans.itemBlock(), 0,
            new ModelResourceLocation(Objects.requireNonNull(BlockTrans.itemBlock().getRegistryName()), "inventory"));
        ModelLoader.setCustomModelResourceLocation(BlockSource.itemBlock(), 0,
            new ModelResourceLocation(BlockSource.getRegistryName(), "inventory"));
    }

    @Mod.InstanceFactory
    public static EnergyTranformer instance() {
        return INSTANCE;
    }
}