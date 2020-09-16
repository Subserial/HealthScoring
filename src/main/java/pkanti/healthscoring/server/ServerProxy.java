package pkanti.healthscoring.server;

import net.minecraftforge.common.MinecraftForge;
import pkanti.healthscoring.HealthScoring;
import pkanti.healthscoring.common.CommonProxy;
import pkanti.healthscoring.server.network.HealthReporter;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit() { super.preInit(); }

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(HealthReporter.class);
        HealthScoring.logDebug("Registered Health Reporter");
        MinecraftForge.EVENT_BUS.register(ScoreboardSetter.class);
        HealthScoring.logDebug("Registered Scoreboard Initializer");
        HealthScoring.logInfo("Registered Reporter");
        super.init();
    }

    @Override
    public void postInit() { super.postInit(); }

    @Override
    public void serverStart() {
        ScoreboardSetter.prepareScoreboard();
        super.serverStart();
    }
}
