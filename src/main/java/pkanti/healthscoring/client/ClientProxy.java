package pkanti.healthscoring.client;

import net.minecraftforge.common.MinecraftForge;
import pkanti.healthscoring.HealthScoring;
import pkanti.healthscoring.common.CommonProxy;
import pkanti.healthscoring.data.HealthMap;

public class ClientProxy extends CommonProxy {

    @Override
    public void commonSetup() {
        map = new HealthMap();
        MinecraftForge.EVENT_BUS.register(new ScoreboardRenderHelper());
        HealthScoring.logDebug("Registered Scoreboard Renderer");
        MinecraftForge.EVENT_BUS.register(map);
        HealthScoring.logDebug("Registered Health Record Map");
        super.commonSetup();
    }

}
