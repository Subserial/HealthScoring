package pkanti.healthscoring.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import pkanti.healthscoring.HealthScoring;

import java.util.function.Predicate;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";

    // Lack of communication is okay
    public static Predicate<String> matchOrAbsent = (String a) -> {
        return (a.equals(NetworkRegistry.ABSENT)
                || a.equals(PROTOCOL_VERSION));
    };

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(HealthScoring.MODID, "chan"),
            () -> PROTOCOL_VERSION,
            matchOrAbsent,
            matchOrAbsent);


    public static void registerPackets() {
        int id = 0;
        INSTANCE.registerMessage(id++, PacketHealth.class, PacketHealth::encode, PacketHealth::decode, PacketHealth::handle);
    }
}
