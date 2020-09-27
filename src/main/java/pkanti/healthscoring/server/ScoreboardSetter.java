package pkanti.healthscoring.server;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import pkanti.healthscoring.HealthConfig;
import pkanti.healthscoring.HealthScoring;

@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER)
public class ScoreboardSetter extends WorldSavedData {
    private static final String DATA_NAME = HealthScoring.MODID + "_check";

    private boolean scoreboardChecked = false;

    public ScoreboardSetter() {
        super(DATA_NAME);
    }

    public ScoreboardSetter(String s) {
        super(s);
    }

    @Override
    public void read(CompoundNBT compound) {
        if (compound.contains("scoreboard_init_check")) {
            scoreboardChecked = compound.getBoolean("scoreboard_init_check");
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putBoolean("scoreboard_init_check", true);
        return compound;
    }

    public static ScoreboardSetter get(World world) {
        return ((ServerWorld)world).getSavedData().getOrCreate(ScoreboardSetter::new, DATA_NAME);
    }

    @SubscribeEvent
    public static void prepareScoreboard(FMLServerStartedEvent evt) {
        MinecraftServer server = evt.getServer();
        ScoreboardSetter setter = get(server.getWorld(World.OVERWORLD));
        if (HealthConfig.SERVER.initializeScoreboard.get()
                && (!setter.scoreboardChecked
                    || HealthConfig.SERVER.checkAgain.get())) {
            if (HealthConfig.SERVER.initializeScoreboard.get()) {
                Scoreboard scoreboard = server.getScoreboard();
                ScoreObjective healthObjective = scoreboard.getObjective("hsHealth");
                if (healthObjective != null && healthObjective.getRenderType() != ScoreCriteria.RenderType.HEARTS) {
                    HealthScoring.logError("hsHealth objective is misconfigured. What are you trying to pull here?");
                    scoreboard.removeObjective(healthObjective);
                    healthObjective = null;
                }

                ScoreObjective firstSlot = scoreboard.getObjectiveInDisplaySlot(0);
                if (firstSlot != null && firstSlot.getRenderType() != ScoreCriteria.RenderType.HEARTS) {
                    HealthScoring.logInfo("Objective in tab display is taken, skipping initialization");
                    healthObjective = firstSlot;
                }

                if (healthObjective == null) {
                    HealthScoring.logInfo("Creating default scoreboard for health tracking");
                    healthObjective = scoreboard.addObjective(
                            "hsHealth",
                            ScoreCriteria.HEALTH,
                            new StringTextComponent(HealthConfig.SERVER.initialLabel.get()),
                            ScoreCriteria.RenderType.HEARTS);
                }
                scoreboard.setObjectiveInDisplaySlot(0, healthObjective);
            }
            setter.scoreboardChecked = true;
            setter.markDirty();
        }
    }
}
