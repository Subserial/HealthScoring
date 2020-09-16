package pkanti.healthscoring.server;

import net.minecraftforge.common.MinecraftForge;
import pkanti.healthscoring.HealthScoring;
import pkanti.healthscoring.common.CommonProxy;
import pkanti.healthscoring.server.network.HealthReporter;

public class ServerProxy extends CommonProxy {

    @Override
    public void commonSetup() {
        MinecraftForge.EVENT_BUS.register(HealthReporter.class);
        HealthScoring.logDebug("Registered Health Reporter");
        MinecraftForge.EVENT_BUS.register(ScoreboardSetter.class);
        HealthScoring.logDebug("Registered Scoreboard Initializer");
        super.commonSetup();
    }
}
