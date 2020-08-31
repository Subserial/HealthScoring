package pkanti.healthscore.network;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import pkanti.healthscore.HealthScore;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(HealthScore.MODID);

    public static void registerPackets() {
        INSTANCE.registerMessage(PacketHealth.Handler.class, PacketHealth.class, 0, Side.CLIENT);
    }
}
