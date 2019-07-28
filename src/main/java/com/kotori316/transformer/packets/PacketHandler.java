package com.kotori316.transformer.packets;

import java.util.function.Predicate;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import com.kotori316.transformer.EnergyTransformer;

public class PacketHandler {
    public static final String PROTOCOL = "1";
    private static final SimpleChannel WRAPPER = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(EnergyTransformer.modID, "main"))
        .networkProtocolVersion(() -> PROTOCOL)
        .clientAcceptedVersions(Predicate.isEqual(PROTOCOL))
        .serverAcceptedVersions(Predicate.isEqual(PROTOCOL))
        .simpleChannel();

    public static void init() {
        WRAPPER.registerMessage(0, EnergyUpdateMessage.class, EnergyUpdateMessage::toBytes, EnergyUpdateMessage::fromBytes, EnergyUpdateMessage::onReceive);
//        WRAPPER.registerMessage(ItemCountMessage::onReceive, ItemCountMessage.class, 0, Side.CLIENT);
    }

    public static void sendToServer(EnergyUpdateMessage message) {
        WRAPPER.sendToServer(message);
    }
}
