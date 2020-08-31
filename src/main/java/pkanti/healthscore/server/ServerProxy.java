package pkanti.healthscore.server;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import pkanti.healthscore.HealthScore;
import pkanti.healthscore.ScoreboardRenderHelper;
import pkanti.healthscore.common.CommonProxy;
import pkanti.healthscore.network.HealthReporter;
import pkanti.healthscore.network.PacketHandler;

public class ServerProxy extends CommonProxy {

    public void preInit() { super.preInit(); }

    public void init() {
        MinecraftForge.EVENT_BUS.register(HealthReporter.class);
        HealthScore.logInfo("Registered Reporter");
        super.init();
    }

    public void postInit() {super.postInit();}
}
