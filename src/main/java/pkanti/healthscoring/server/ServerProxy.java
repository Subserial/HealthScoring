package pkanti.healthscoring.server;

import net.minecraftforge.common.MinecraftForge;
import pkanti.healthscoring.HealthScoring;
import pkanti.healthscoring.common.CommonProxy;
import pkanti.healthscoring.network.HealthReporter;

public class ServerProxy extends CommonProxy {

    @Override
    public void commonSetup() {
        MinecraftForge.EVENT_BUS.register(HealthReporter.class);
        HealthScoring.logInfo("Registered Health Reporter");
        super.commonSetup();
    }
}
