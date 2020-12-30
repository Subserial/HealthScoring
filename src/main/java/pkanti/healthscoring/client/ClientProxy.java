package pkanti.healthscoring.client;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import pkanti.healthscoring.HealthScoring;
import pkanti.healthscoring.client.render.HeartRenderFactory;
import pkanti.healthscoring.client.render.IHeartRender;
import pkanti.healthscoring.common.CommonProxy;
import pkanti.healthscoring.data.HealthMap;

import java.util.stream.Stream;

public class ClientProxy extends CommonProxy {
    String source = "";

    @Override
    public void commonSetup() {
        map = new HealthMap();
        if (ModList.get().isLoaded("mantle"))
            source = "mantle";
        else if (ModList.get().isLoaded("healthoverlay"))
            source = "healthoverlay";

        IHeartRender renderSource = HeartRenderFactory.getHeartRender(source);
        if (renderSource != null) {
            HealthScoring.logInfo("Using " + renderSource.getName() + " as an asset source.");
        } else {
            HealthScoring.logError("No asset source detected! Render will be disabled.");
            HealthScoring.logError("Currently implemented sources are Mantle and HealthOverlay.");
        }

        MinecraftForge.EVENT_BUS.register(new ScoreboardRenderHelper(renderSource));
        HealthScoring.logDebug("Registered Scoreboard Renderer");
        MinecraftForge.EVENT_BUS.register(map);
        HealthScoring.logDebug("Registered Health Record Map");
        super.commonSetup();
    }

}
