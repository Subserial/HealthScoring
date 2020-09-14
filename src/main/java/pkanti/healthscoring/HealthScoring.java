package pkanti.healthscoring;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pkanti.healthscoring.client.ClientProxy;
import pkanti.healthscoring.common.CommonProxy;
import pkanti.healthscoring.server.ServerProxy;

@Mod(HealthScoring.MODID)
public class HealthScoring
{
    public static final String MODID = "healthscoring";
    public static final String NAME = "Health Scoring";

    private static Logger logger = LogManager.getLogger(NAME);

    public static HealthScoring INSTANCE;

    public static CommonProxy proxy;

    public HealthScoring() {
        INSTANCE = this;
        proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

        ModLoadingContext modContext = ModLoadingContext.get();
        modContext.registerConfig(ModConfig.Type.COMMON, HealthConfig.COMMON_SPEC);
        modContext.registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
                () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::commonSetup);
    }

    public void commonSetup(FMLCommonSetupEvent evt) { proxy.commonSetup(); }

    public static void logInfo(String s) {
        logger.info(s);
    }
    public static void logError(String s) {
        logger.error(s);
    }

}
