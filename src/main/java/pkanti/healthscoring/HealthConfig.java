package pkanti.healthscoring;


import org.apache.commons.lang3.tuple.Pair;
import net.minecraftforge.common.ForgeConfigSpec;

public class HealthConfig {

    public static class Common {
        public final ForgeConfigSpec.BooleanValue enableAbsorption;
        public final ForgeConfigSpec.BooleanValue enableEffects;
        public final ForgeConfigSpec.ConfigValue<String> preferredRender;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("Display Properties");
            enableAbsorption = builder
                    .comment(" Enable extra absorption rendering when provided by server")
                    .define("enableAbsorption", true);
            enableEffects = builder
                    .comment(" Enable rendering health effects (poison, wither, hardcore) when provided by server")
                    .define("enableEffects", true);
            preferredRender = builder
                    .comment(" ID of mod to render hearts. Ignored if mod is not installed.")
                    .define("preferredRender", "");
            builder.pop();
        }
    }

    public static class Server {
        public final ForgeConfigSpec.BooleanValue initializeScoreboard;
        public final ForgeConfigSpec.ConfigValue<String> initialLabel;
        public final ForgeConfigSpec.BooleanValue checkAgain;

        public Server(ForgeConfigSpec.Builder builder) {
            builder.push("Scoreboard Properties");
            initializeScoreboard = builder
                    .comment(" Enable creating a health objective and setting the tab display on world creation")
                    .define("initializeScoreboard", true);
            initialLabel = builder
                    .comment(" Default label for a created objective")
                    .define("initialLabel", "HP");
            checkAgain = builder
                    .comment(" Check for an active health-based objective on every startup")
                    .define("checkAgain", false);
            builder.pop();
        }
    }

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Server SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;
    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        COMMON_SPEC = specPair.getRight();
        final Pair<Server, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER = specPair2.getLeft();
        SERVER_SPEC = specPair2.getRight();
    }
}
