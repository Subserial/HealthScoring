package pkanti.healthscore;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;

@Config(modid = HealthScore.MODID)
public class HealthConfig {

    @Comment({"Enable appending absorption hearts to scoreboard values. (default: true)",
              "Absorption hearts increase the render complexity due to overlapping vanilla/modded textures,",
              "but only when a player has any absorption hearts.",
              "Disabling this will meld absorption into the total health displayed."})
    @Name("Enable Absorption Hearts on Scoreboard")
    public static boolean enableAbsorption = true;

    @Comment({"Enable displaying effects such as regen, posion, wither. (default: true)",
            "On the client, this enables showing effects received.",
            "On the server, this enables sending the players status information."})
    @Name("Enable Displaying Effects")
    public static boolean enableEffects = true;
}
