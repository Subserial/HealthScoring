package pkanti.healthscoring.common;

import pkanti.healthscoring.HealthScoring;
import pkanti.healthscoring.data.HealthMap;
import pkanti.healthscoring.network.PacketHandler;

public class CommonProxy {
    public HealthMap map = null;

    public void preInit() {
        PacketHandler.registerPackets();
        HealthScoring.logDebug("Registered HealthInfo Packets");
    }

    public void init() {}

    public void postInit() {}

    public void serverStart() {}
}
