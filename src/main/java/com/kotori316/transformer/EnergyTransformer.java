package com.kotori316.transformer;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.kotori316.transformer.gui.GuiHandler;
import com.kotori316.transformer.packets.PacketHandler;

@SuppressWarnings("SpellCheckingInspection")
@Mod(EnergyTransformer.modID)
public class EnergyTransformer {
    public static final String modID = "energytransformer";
    public static final BlockSource BLOCK_SOURCE = new BlockSource();
    public static final TileEntityType<TileSource> TYPE = TileEntityType.Builder.create(TileSource::new).build(null);

    public EnergyTransformer() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.GUIFACTORY, () -> GuiHandler::get);
        PacketHandler.init();
    }

    @SubscribeEvent
    public void block(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(BLOCK_SOURCE);
    }

    @SubscribeEvent
    public void item(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(BLOCK_SOURCE.itemBlock);
    }

    @SubscribeEvent
    public void tile(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TYPE.setRegistryName(modID, "tile_" + BlockSource.NAME));
    }
}
