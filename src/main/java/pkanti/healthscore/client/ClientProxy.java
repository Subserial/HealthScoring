package pkanti.healthscore.client;

import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import pkanti.healthscore.HealthScore;
import pkanti.healthscore.ScoreboardRenderHelper;
import pkanti.healthscore.common.CommonProxy;
import pkanti.healthscore.data.HealthMap;

import java.lang.reflect.Field;

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
