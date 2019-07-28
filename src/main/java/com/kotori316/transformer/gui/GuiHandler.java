package com.kotori316.transformer.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.FMLPlayMessages;

import com.kotori316.transformer.TileSource;

import static com.kotori316.transformer.TileSource.GUI_ID;

public class GuiHandler {
    public static GuiScreen get(FMLPlayMessages.OpenContainer message) {
        BlockPos pos = message.getAdditionalData().readBlockPos();
        ResourceLocation id = message.getId();
        EntityPlayerSP player = Minecraft.getInstance().player;
        if (GUI_ID.equals(id.toString())) {
            TileSource tileSource = ((TileSource) Minecraft.getInstance().world.getTileEntity(pos));
            return new GuiSource(player, tileSource);
        }
        return null;
    }
}
