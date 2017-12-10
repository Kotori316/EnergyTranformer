package com.kotori316.transformer.energy

import com.kotori316.transformer.block.TileTrans
import ic2.api.energy.event.{EnergyTileLoadEvent, EnergyTileUnloadEvent}
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Optional.Method

class IC2Helper(val hasIC2: Boolean, tile: TileTrans) {

    var ic2Inited = false

    def postLoadEvent(): Unit = {
        if (!ic2Inited) {
            if (hasIC2 && !tile.getWorld.isRemote) {
                postLoadInternal()
            }
            ic2Inited = true
        }
    }

    @Method(modid = "ic2")
    private def postLoadInternal(): Unit = {
        MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(tile))
    }

    def postUnloadEvent(): Unit = {
        if (ic2Inited) {
            if (hasIC2 && !tile.getWorld.isRemote) {
                postUnloadInternal()
            }
            ic2Inited = false
        }
    }

    @Method(modid = "ic2")
    def postUnloadInternal(): Unit = {
        MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(tile))
    }
}
