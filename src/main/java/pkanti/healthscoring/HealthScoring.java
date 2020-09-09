package pkanti.healthscoring;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pkanti.healthscoring.common.CommonProxy;

@Mod(modid = HealthScoring.MODID,
        name = HealthScoring.NAME,
        version = "${GRADLE_VERSION}",
        acceptableRemoteVersions = "*",
        acceptedMinecraftVersions = "[1.12,1.13)",
        useMetadata = true)
public class HealthScoring
{
    public static final String MODID = "healthscoring";
    public static final String NAME = "Health Scoring";

    private static Logger logger = LogManager.getLogger("HealthScoring");

    @Instance(HealthScoring.MODID)
    public static HealthScoring INSTANCE;

    @SidedProxy(clientSide = "pkanti.healthscoring.client.ClientProxy", serverSide = "pkanti.healthscoring.server.ServerProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent evt) { proxy.preInit(); }

    @EventHandler
    public void init(FMLInitializationEvent evt) { proxy.init(); }

    @EventHandler
    public void postInit(FMLPostInitializationEvent evt) { proxy.postInit(); }

    public static void logInfo(String s) {
        logger.info(s);
    }
    public static void logError(String s) {
        logger.error(s);
    }

}
