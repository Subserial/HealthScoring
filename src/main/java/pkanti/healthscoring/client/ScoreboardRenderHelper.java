package pkanti.healthscoring.client;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.overlay.PlayerTabOverlayGui;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.scoreboard.*;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import pkanti.healthscoring.HealthConfig;
import pkanti.healthscoring.HealthScoring;
import pkanti.healthscoring.client.render.IHeartRender;
import pkanti.healthscoring.data.HealthMap;
import slimeknights.mantle.Mantle;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = HealthScoring.MODID, value = Dist.CLIENT)
public class ScoreboardRenderHelper extends PlayerTabOverlayGui {
    // resources from Mantle
    private static final ResourceLocation ICON_HEARTS = new ResourceLocation(Mantle.modId, "textures/gui/hearts.png");
    private static final ResourceLocation ICON_ABSORB = new ResourceLocation(Mantle.modId, "textures/gui/absorb.png");

    private final Minecraft mc = Minecraft.getInstance();
    private final IHeartRender renderSource;

    public ScoreboardRenderHelper(IHeartRender renderSource) {
        super(Minecraft.getInstance(), Minecraft.getInstance().ingameGUI);
        this.renderSource = renderSource;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderScoreboardHealth(RenderGameOverlayEvent.Pre evt) {
        if (renderSource == null || evt.getType() != RenderGameOverlayEvent.ElementType.PLAYER_LIST || evt.isCanceled())
            return;

        Scoreboard scoreboard = mc.world.getScoreboard();
        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(0);
        if (objective == null || objective.getRenderType() != ScoreCriteria.RenderType.HEARTS)
            return;

        ClientPlayNetHandler client = mc.player.connection;
        for (NetworkPlayerInfo net : client.getPlayerInfoMap()) {
            UUID uuid = net.getGameProfile().getId();
            String name = net.getGameProfile().getName();
            Score score = scoreboard.getOrCreateScore(name, objective);
            int displayScore = score.getScorePoints();
            HealthMap.HealthInfo health = HealthScoring.proxy.map.get(uuid);
            if (health == null) {
                health = HealthScoring.proxy.map.putLocal(uuid, displayScore);
            } else if (!health.isFromServer()) {
                health.update(displayScore);
            }
            score.setScorePoints(0);
            net.setDisplayHealth(0);
            if (health.getLastHealth() < health.getHealth())
                // trigger health gain blink
                net.setLastHealth(-1);
            if (health.getLastHealth() > health.getHealth())
                // trigger health loss blink
                net.setLastHealth(1);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderScoreboardHealthPost(RenderGameOverlayEvent.Post evt) {
        if (renderSource == null || evt.getType() != RenderGameOverlayEvent.ElementType.PLAYER_LIST || evt.isCanceled())
            return;

        Scoreboard scoreboard = mc.world.getScoreboard();
        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(0);
        if (objective == null || objective.getRenderType() != ScoreCriteria.RenderType.HEARTS)
            return;


        // extra setup stuff
        MatrixStack stack = evt.getMatrixStack();
        int width = evt.getWindow().getScaledWidth();
        int updateCounter = mc.ingameGUI.getTicks();
        ClientPlayNetHandler client = mc.player.connection;
        List<NetworkPlayerInfo> players;
        players = ENTRY_ORDERING.sortedCopy(client.getPlayerInfoMap());
        ITextComponent header = mc.ingameGUI.getTabList().header;


        mc.getProfiler().startSection("scoreboardHealth");

        // mimic intermediate variables
        int maxTextWidth = 0;
        for (NetworkPlayerInfo inst : players) {
            int textWidth = mc.fontRenderer.getStringPropertyWidth(this.getDisplayName(inst));
            maxTextWidth = Math.max(maxTextWidth, textWidth);
        }
        players = players.subList(0, Math.min(players.size(), 80));
        int cols = (players.size() - 1) / 20 + 1;
        int rows = (int) Math.ceil((float) players.size() / cols);
        boolean encrypted = mc.getConnection().getNetworkManager().isEncrypted();
        int wrapWidth = width - 50;
        int displayLength = 90;
        int rectLength = Math.min((encrypted ? cols * 9 : 0) + maxTextWidth + displayLength + 13, wrapWidth) / cols;
        int sizeX = rectLength * cols + (cols - 1) * 5;
        int startX = width / 2 - sizeX / 2;
        int startY = 10;

        if (header != null) {
            List<IReorderingProcessor> headers = this.mc.fontRenderer.trimStringToWidth(header, width - 50);

            for (IReorderingProcessor irp : headers) {
                sizeX = Math.max(sizeX, this.mc.fontRenderer.func_243245_a(irp));
                startY += this.mc.fontRenderer.FONT_HEIGHT;
            }

            startY++;
        }

        for (int i = 0; i < players.size(); i++) {
            int xindex = i / rows;
            int yindex = i % rows;
            int posX = startX + xindex * (rectLength + 5);
            int posY = startY + yindex * 9;

            NetworkPlayerInfo info = players.get(i);
            GameProfile profile = info.getGameProfile();

            if (encrypted) {
                posX += 9;
            }

            if (info.getGameType() != GameType.SPECTATOR) {
                int heartX = posX + maxTextWidth + 1;

                HealthMap.HealthInfo hinfo = HealthScoring.proxy.map.get(info.getGameProfile().getId());

                // reset scoreboard values for compatibility
                scoreboard.getOrCreateScore(profile.getName(), objective).setScorePoints(hinfo.getHealthTotal());

                int health = hinfo.getHealth();
                int absorp = hinfo.getAbsorption();
                if (!HealthConfig.COMMON.enableAbsorption.get()) {
                    health += absorp;
                    absorp = 0;
                }

                hinfo.markDirty();
                int absorpCount = Math.min(absorp + 1, 20) / 2;
                int renderCount = 10 + absorpCount;
                int delta = MathHelper.floor(Math.min(86.0F / renderCount, 9.0F));
                boolean blink = info.getHealthBlinkTime() > updateCounter && ((info.getHealthBlinkTime() - updateCounter) / 3) % 2 == 1;

                if (absorp == 0) {
                    // Basic render
                    renderSource.drawHearts(stack, this, heartX, posY, delta, health, 10, 0, hinfo.getOffset(), blink, false);
                } else {
                    // Expanded render (overlapping containers)
                    for (int segment = 0; segment < 10; segment++) {
                        renderSource.drawHearts(stack, this, heartX, posY, delta, health, 1, segment, hinfo.getOffset(), blink, false);
                    }
                    for (int segment = 0; segment < absorpCount; segment++) {
                        renderSource.drawHearts(stack, this, heartX + delta * 10, posY, delta, absorp, 1, segment, hinfo.getOffset(), blink, true);
                    }
                }
            }
        }
        mc.getProfiler().endSection();
    }
}