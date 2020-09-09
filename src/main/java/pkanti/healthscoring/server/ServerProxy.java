package pkanti.healthscoring.server;

import net.minecraftforge.common.MinecraftForge;
import pkanti.healthscoring.HealthScoring;
import pkanti.healthscoring.common.CommonProxy;
import pkanti.healthscoring.network.HealthReporter;

public class ServerProxy extends CommonProxy {

    public void preInit() { super.preInit(); }

    public void init() {
        MinecraftForge.EVENT_BUS.register(HealthReporter.class);
        HealthScoring.logInfo("Registered Reporter");
        super.init();
    }

    public void postInit() {super.postInit();}
}
