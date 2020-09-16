package pkanti.healthscoring.server;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.server.FMLServerHandler;
import pkanti.healthscoring.HealthConfig;
import pkanti.healthscoring.HealthScoring;

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
    public void readFromNBT(NBTTagCompound compound) {
        scoreboardChecked = compound.getBoolean("scoreboard_init_check");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("scoreboard_init_check", true);
        return compound;
    }

    public static ScoreboardSetter get(World world) {
        ScoreboardSetter setter = (ScoreboardSetter)world.getMapStorage().getOrLoadData(ScoreboardSetter.class, DATA_NAME);
        if (setter == null) {
            setter = new ScoreboardSetter();
            world.getMapStorage().setData(DATA_NAME, setter);
        }
        return setter;
    }

    public static void prepareScoreboard() {
        World world = FMLServerHandler.instance().getServer().getWorld(0);
        ScoreboardSetter setter = get(world);
        if (HealthConfig.initScoreboard()
                && (!setter.scoreboardChecked
                    || HealthConfig.initCheckAgain())) {
            Scoreboard scoreboard = world.getScoreboard();
            ScoreObjective healthObjective = scoreboard.getObjective("hsHealth");
            if (healthObjective != null && healthObjective.getRenderType() != IScoreCriteria.EnumRenderType.HEARTS) {
                HealthScoring.logError("hsHealth objective is misconfigured. What are you trying to pull here?");
                scoreboard.removeObjective(healthObjective);
                healthObjective = null;
            }

            ScoreObjective firstSlot = scoreboard.getObjectiveInDisplaySlot(0);
            if (firstSlot != null && firstSlot.getRenderType() != IScoreCriteria.EnumRenderType.HEARTS) {
                HealthScoring.logInfo("Objective in tab display is taken, skipping initialization");
                healthObjective = firstSlot;
            }

            if (healthObjective == null) {
                HealthScoring.logInfo("Creating default scoreboard for health tracking");
                healthObjective = scoreboard.addScoreObjective(
                        "hsHealth",
                        ScoreCriteria.HEALTH);
                healthObjective.setDisplayName(HealthConfig.initLabel());
                healthObjective.setRenderType(IScoreCriteria.EnumRenderType.HEARTS);
            }
            scoreboard.setObjectiveInDisplaySlot(0, healthObjective);
            setter.scoreboardChecked = true;
            setter.markDirty();
        }
    }
}
