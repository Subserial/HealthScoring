package pkanti.healthscoring;


import org.apache.commons.lang3.tuple.Pair;
import net.minecraftforge.common.ForgeConfigSpec;

public class HealthConfig {

    public static class Common {
        public final ForgeConfigSpec.BooleanValue enableAbsorption;
        public final ForgeConfigSpec.BooleanValue enableEffects;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("enableAbsorption");
            enableAbsorption = builder
                    .comment("Enable extra absorption rendering when provided by server")
                    .define("enabled", true);
            builder.push("enableEffects");
            enableEffects = builder
                    .comment("Enable rendering health effects (poison, wither, hardcore) when provided by server")
                    .define("enabled", true);
        }
    }

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;
    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        COMMON_SPEC = specPair.getRight();
    }
}
