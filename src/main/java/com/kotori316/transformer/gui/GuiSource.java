package com.kotori316.transformer.gui;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.kotori316.transformer.EnergyTransformer;
import com.kotori316.transformer.TileSource;

public class GuiSource extends GuiContainer {
    private static final ResourceLocation LOCATION = new ResourceLocation(EnergyTransformer.modID, "textures/gui/" + EnergyTransformer.modID + ".png");
    private final TileSource tileSource;
    private GuiTextField text;

    public GuiSource(EntityPlayer player, TileSource tileSource) {
        super(new ContainerSource(player));
        this.tileSource = tileSource;
    }

    @Override
    public IGuiEventListener getFocused() {
        return this.text.isFocused() ? this.text : null;
    }

    @Override
    protected void initGui() {
        super.initGui();
        mc.keyboardListener.enableRepeatEvents(true);
        text = new GuiTextField(0, this.fontRenderer, getGuiLeft() + 18, getGuiTop() + 18, 50, 10);
        text.setText(String.valueOf(tileSource.getStorage().getMaxEnergyStored()));
        text.setTextAcceptHandler(this::updateCapacity);
        text.setValidator(this::predicateString);
        this.children.add(text);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        this.text.drawTextField(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(LOCATION);
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    private void updateCapacity(int id, String text) {
        final int newCapacity;
        if (StringUtils.isNullOrEmpty(text)) {
            newCapacity = 0;
        } else {
            int i;
            try {
                i = Integer.parseInt(text);
            } catch (NumberFormatException ignore) {
                i = 0;
            }
            newCapacity = i;
        }
        tileSource.updateCapacity(newCapacity);
    }

    private boolean predicateString(String text) {
        return StringUtils.isNullOrEmpty(text) || NumberUtils.isDigits(text);
    }
}
