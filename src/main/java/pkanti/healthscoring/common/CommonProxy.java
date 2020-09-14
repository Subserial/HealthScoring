package pkanti.healthscoring.common;

import pkanti.healthscoring.HealthScoring;
import pkanti.healthscoring.data.HealthMap;
import pkanti.healthscoring.network.PacketHandler;

public class CommonProxy {
    public HealthMap map = null;

    public void commonSetup() {
        PacketHandler.registerPackets();
        HealthScoring.logInfo("Registered HealthInfo Packets");
    }
}
