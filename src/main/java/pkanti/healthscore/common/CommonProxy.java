package pkanti.healthscore.common;

import net.minecraftforge.fml.common.Loader;
import pkanti.healthscore.HealthScore;
import pkanti.healthscore.data.HealthMap;
import pkanti.healthscore.network.PacketHandler;

public class CommonProxy {
    public HealthMap map = null;
    protected boolean isMantleLoaded;

    public void preInit() {
        PacketHandler.registerPackets();
        HealthScore.logInfo("Registered packets");
    }

    public void init() {}

    public void postInit() {}
}
