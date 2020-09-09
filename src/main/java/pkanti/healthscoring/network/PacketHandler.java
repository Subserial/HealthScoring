package pkanti.healthscoring.network;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import pkanti.healthscoring.HealthScoring;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(HealthScoring.MODID);

    public static void registerPackets() {
        INSTANCE.registerMessage(PacketHealth.Handler.class, PacketHealth.class, 0, Side.CLIENT);
    }
}
