package pkanti.healthscoring;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;

@Config(modid = HealthScoring.MODID)
public class HealthConfig {
    public static DisplayProperties display = new DisplayProperties();
    public static ScoreboardProperties scoreboard = new ScoreboardProperties();

    public static boolean displayAbsorption() { return display.enableAbsorption; }
    public static boolean displayEffects() { return display.enableEffects; }
    public static boolean initScoreboard() { return scoreboard.initializeScoreboard; }
    public static String initLabel() { return scoreboard.initialLabel; }
    public static boolean initCheckAgain() { return scoreboard.checkAgain; }

    static class DisplayProperties {

        @Comment("Enable extra absorption rendering when provided by server (disables sending on serverside)")
        @Name("enableAbsorption")
        public boolean enableAbsorption = true;

        @Comment("Enable rendering health effects (poison, wither, hardcore) when provided by server (disables sending on serverside)")
        @Name("enableEffects")
        public boolean enableEffects = true;
    }

    static class ScoreboardProperties {

        @Comment(" Enable creating a health objective and setting the tab display on world creation")
        @Name("initializeScoreboard")
        public boolean initializeScoreboard = true;

        @Comment(" Default label for a created objective")
        @Name("initialLabel")
        public String initialLabel = "HP";

        @Comment(" Check for an active health-based objective on every startup")
        @Name("checkAgain")
        public boolean checkAgain = true;
    }

}
