package com.kotori316.transformer.gui

import com.kotori316.transformer.EnergyTranformer
import com.kotori316.transformer.block.TileTrans
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
class GuiTrans(tile: TileTrans, player: EntityPlayer) extends GuiContainer(new ContainerTrans(tile, player)) {

    val LOCATION = new ResourceLocation(EnergyTranformer.modID, "textures/gui/energytranformer.png")
    lazy val trans = inventorySlots.asInstanceOf[ContainerTrans]

    override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    override def drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) = {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
        this.mc.getTextureManager.bindTexture(LOCATION)
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize)
    }

    override def drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int): Unit = {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
        val s = I18n.format("tile.energytransformer.name")
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752)
        val ext = (trans.extractAve.toDouble / 1e6).formatted("%.4f")
        this.fontRenderer.drawString(ext, 20, 20, 4210752)
        val rec = (trans.recieveAve.toDouble / 1e6).formatted("%.4f")
        this.fontRenderer.drawString(rec, 20, 34, 4210752)
    }
}
