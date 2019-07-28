package com.kotori316.transformer.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSource extends Container {
    public ContainerSource(EntityPlayer player) {
        final int oneBox = 18;
        for (int h = 0; h < 3; ++h)
            for (int v = 0; v < 9; ++v)
                addSlot(new Slot(player.inventory, 9 + h * 9 + v, 8 + oneBox * v, 84 + oneBox * h));

        for (int v = 0; v < 9; ++v)
            addSlot(new Slot(player.inventory, v, 8 + oneBox * v, 142));
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
}
