package pkanti.healthscore;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pkanti.healthscore.common.CommonProxy;
import pkanti.healthscore.network.HealthReporter;
import pkanti.healthscore.network.PacketHandler;

@Mod(modid = HealthScore.MODID,
        name = HealthScore.NAME,
        version = HealthScore.VERSION,
        dependencies = "required-after:mantle@[1.3.3.55,)",
        acceptedMinecraftVersions = "[1.12,1.13)")
public class HealthScore
{
    public static final String MODID = "healthscore";
    public static final String NAME = "Health Score";
    public static final String VERSION = "1.0";

    private static Logger logger = LogManager.getLogger("HealthScore");

    @Instance(HealthScore.MODID)
    public static HealthScore INSTANCE;

    @SidedProxy(clientSide = "pkanti.healthscore.client.ClientProxy", serverSide = "pkanti.healthscore.server.ServerProxy")
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

}
