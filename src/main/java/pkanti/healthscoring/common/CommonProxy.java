package pkanti.healthscoring.common;

import net.minecraftforge.fml.ModList;
import pkanti.healthscoring.HealthScoring;
import pkanti.healthscoring.client.render.IHeartRender;
import pkanti.healthscoring.data.HealthMap;
import pkanti.healthscoring.network.PacketHandler;

public class CommonProxy {
    public HealthMap map = null;
    public String assetSource = "";

    public void commonSetup() {
        PacketHandler.registerPackets();
        HealthScoring.logDebug("Registered HealthInfo Packets");
    }

    public void imcEnqueue() {}
    public void imcProcess() {}
}
