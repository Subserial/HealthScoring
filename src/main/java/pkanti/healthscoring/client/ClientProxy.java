package pkanti.healthscoring.client;

import net.minecraftforge.common.MinecraftForge;
import pkanti.healthscoring.HealthScoring;
import pkanti.healthscoring.common.CommonProxy;
import pkanti.healthscoring.data.HealthMap;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        map = new HealthMap();
        super.preInit();
    }

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(new ScoreboardRenderHelper());
        HealthScoring.logInfo("Registered Scoreboard Renderer");
        MinecraftForge.EVENT_BUS.register(map);
        HealthScoring.logInfo("Registered Map");
        super.init();
    }

    @Override
    public void postInit() { super.postInit(); }

}
