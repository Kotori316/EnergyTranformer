package com.kotori316.transformer.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import com.kotori316.transformer.EnergyTranformer;

public enum PacketHandler {
    INSTANCE;

    private final SimpleNetworkWrapper wrapper;

    PacketHandler() {
        this.wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(EnergyTranformer.modID);
    }

    public void init() {
        int i = 0;
        IMessageHandler<TransAverageMessage, IMessage> handler1 = TransAverageMessage::onRecieve;
        wrapper.registerMessage(handler1, TransAverageMessage.class, i++, Side.CLIENT);
        IMessageHandler<SourceAmountMessage, IMessage> handler2 = SourceAmountMessage::onReceive;
        wrapper.registerMessage(handler2, SourceAmountMessage.class, i++, Side.SERVER);
        IMessageHandler<SourceGUIMessage, IMessage> handler3 = SourceGUIMessage::onReceive;
        wrapper.registerMessage(handler3, SourceGUIMessage.class, i++, Side.SERVER);
        assert i > 0;
    }

    public void sendToPlayer(IMessage message, EntityPlayerMP player) {
        wrapper.sendTo(message, player);
    }

    public void sendToServer(IMessage message) {
        wrapper.sendToServer(message);
    }
}
