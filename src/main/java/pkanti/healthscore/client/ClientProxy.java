package pkanti.healthscore.client;

import net.minecraftforge.common.MinecraftForge;
import pkanti.healthscore.HealthScore;
import pkanti.healthscore.ScoreboardRenderHelper;
import pkanti.healthscore.common.CommonProxy;
import pkanti.healthscore.data.HealthMap;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        map = new HealthMap();
        super.preInit();
    }

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(new ScoreboardRenderHelper());
        HealthScore.logInfo("Registered Scoreboard");
        MinecraftForge.EVENT_BUS.register(map);
        HealthScore.logInfo("Registered Map");
        super.init();
    }

    @Override
    public void postInit() { super.postInit(); }

}
