package pkanti.healthscoring;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;

@Config(modid = HealthScoring.MODID)
public class HealthConfig {

    @Comment("Enable extra absorption rendering when provided by server (disables sending on serverside)")
    @Name("enableAbsorption")
    public static boolean enableAbsorption = true;

    @Comment("Enable rendering health effects (poison, wither, hardcore) when provided by server (disables sending on serverside)")
    @Name("enableEffects")
    public static boolean enableEffects = true;
}
