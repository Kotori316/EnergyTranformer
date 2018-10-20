package com.kotori316.transformer.gui

import com.kotori316.transformer.EnergyTranformer
import com.kotori316.transformer.block.TileSource
import com.kotori316.transformer.network.{PacketHandler, SourceAmountMessage}
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import org.apache.commons.lang3.math.NumberUtils
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11

class GuiSource(player: EntityPlayer, tile: TileSource) extends GuiContainer(new ContainerSource(player, tile)) {
    val LOCATION = new ResourceLocation(EnergyTranformer.modID, "textures/gui/energytranformer.png")
    var text: GuiTextField = _

    override def initGui(): Unit = {
        super.initGui()
        Keyboard.enableRepeatEvents(true)
        text = new GuiTextField(0, this.fontRenderer, getGuiLeft + 18, getGuiTop + 18, 50, 10)
        text.setText(tile.amount.toString)
    }

    override def onGuiClosed(): Unit = {
        super.onGuiClosed()
        Keyboard.enableRepeatEvents(false)
        if (NumberUtils.isDigits(text.getText)) {
            val i = text.getText.toInt
            if (i > 0) {
                tile.amount = i
                PacketHandler.INSTANCE.sendToServer(new SourceAmountMessage(i, tile.getPos))
            }
        }
    }

    override def keyTyped(typedChar: Char, keyCode: Int): Unit = {
        text.textboxKeyTyped(typedChar, keyCode)
        super.keyTyped(typedChar, keyCode)
    }

    override def mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Unit = {
        text.mouseClicked(mouseX, mouseY, mouseButton)
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
        text.drawTextBox()
    }

    override def drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) = {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
        this.mc.getTextureManager.bindTexture(LOCATION)
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize)
    }

    override def drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int): Unit = {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
    }

}
