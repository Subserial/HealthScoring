package pkanti.healthscore;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.scoreboard.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import pkanti.healthscore.data.HealthMap;
import slimeknights.mantle.Mantle;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = HealthScore.MODID, value = Side.CLIENT)
public class ScoreboardRenderHelper extends GuiPlayerTabOverlay {
    // taken from Mantle
    private static final ResourceLocation ICON_HEARTS = new ResourceLocation(Mantle.modId, "textures/gui/hearts.png");
    private static final ResourceLocation ICON_ABSORB = new ResourceLocation(Mantle.modId, "textures/gui/absorb.png");

    public static final Field FIELD_HEADER = ObfuscationReflectionHelper.findField(GuiPlayerTabOverlay.class,"field_175256_i");
    private final Ordering<NetworkPlayerInfo> ORDERING;

    private final Minecraft mc = Minecraft.getMinecraft();

    public ScoreboardRenderHelper() {
        super(Minecraft.getMinecraft(), Minecraft.getMinecraft().ingameGUI);
        Ordering<NetworkPlayerInfo> tmpORDERING = null;
        try {
            Field FIELD_ORDERING = ObfuscationReflectionHelper.findField(GuiPlayerTabOverlay.class, "field_175252_a");
            tmpORDERING = (Ordering<NetworkPlayerInfo>) FIELD_ORDERING.get(GuiPlayerTabOverlay.class);
        } catch (Exception ex) {
            HealthScore.logInfo("Error in loading reflection fields for scoreboard, disabling on client");
            tmpORDERING = null;
        }
        ORDERING = tmpORDERING;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderScoreboardHealth(RenderGameOverlayEvent.Pre evt) {
        if (evt.getType() != RenderGameOverlayEvent.ElementType.PLAYER_LIST || evt.isCanceled())
            return;

        Scoreboard scoreboard = mc.world.getScoreboard();
        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(0);
        if (objective == null || objective.getRenderType() != IScoreCriteria.EnumRenderType.HEARTS)
            return;

        NetHandlerPlayClient client = mc.player.connection;
        for (NetworkPlayerInfo net : client.getPlayerInfoMap()) {
            UUID uuid = net.getGameProfile().getId();
            String name = net.getGameProfile().getName();
            Score score = scoreboard.getOrCreateScore(name, objective);
            int displayScore = score.getScorePoints();
            HealthMap.HealthInfo health = HealthScore.proxy.map.get(uuid);
            if (health == null) {
                health = new HealthMap.HealthInfo(displayScore, 0, 0, false);
                HealthScore.proxy.map.update(uuid, health);
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
        if (evt.getType() != RenderGameOverlayEvent.ElementType.PLAYER_LIST || evt.isCanceled())
            return;

        Scoreboard scoreboard = mc.world.getScoreboard();
        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(0);
        if (objective == null || objective.getRenderType() != IScoreCriteria.EnumRenderType.HEARTS)
            return;


        // extra setup stuff
        int width = evt.getResolution().getScaledWidth();
        int updateCounter = mc.ingameGUI.getUpdateCounter();
        NetHandlerPlayClient client = mc.player.connection;
        ITextComponent header;
        List<NetworkPlayerInfo> players;
        players = ORDERING.sortedCopy(client.getPlayerInfoMap());
        try {
            header = (ITextComponent) FIELD_HEADER.get(mc.ingameGUI.getTabList());
        } catch (IllegalAccessException ex) {
            // debug mostly
            header = null;
        }


        mc.mcProfiler.startSection("scoreboardHealth");

        // mimic intermediate variables
        int maxTextWidth = 0;
        for (NetworkPlayerInfo inst : players) {
            int textWidth = mc.fontRenderer.getStringWidth(this.getPlayerName(inst));
            maxTextWidth = Math.max(maxTextWidth, textWidth);
        }
        players = players.subList(0, Math.min(players.size(), 80));
        int cols = (players.size() - 1) / 20 + 1;
        int rows = (int)Math.ceil((float)players.size() / cols);
        boolean encrypted = mc.getConnection().getNetworkManager().isEncrypted();
        int wrapWidth = width - 50;
        int displayLength = 90;
        int rectLength = Math.min((encrypted ? cols * 9 : 0) + maxTextWidth + displayLength + 13, wrapWidth) / cols;
        int sizeX = rectLength * cols + (cols - 1) * 5;
        int startX = width / 2 - sizeX / 2;
        int startY = 10;

        if (header != null)
        {
            List<String> headers = this.mc.fontRenderer.listFormattedStringToWidth(header.getFormattedText(), width - 50);

            for (String s : headers)
            {
                sizeX = Math.max(sizeX, this.mc.fontRenderer.getStringWidth(s));
                startY += this.mc.fontRenderer.FONT_HEIGHT;
            }

            startY++;
        }

        for (int i = 0; i < players.size(); i++) {
            int xindex = i / rows;
            int yindex = i % rows;
            int posX = startX + xindex * (rectLength + 5);
            int posY = startY + yindex * 9;

            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            NetworkPlayerInfo info = players.get(i);
            GameProfile profile = info.getGameProfile();

            if (encrypted) {
                posX += 9;
            }

            if (info.getGameType() != GameType.SPECTATOR) {
                int heartX = posX + maxTextWidth + 1;

                HealthMap.HealthInfo hinfo = HealthScore.proxy.map.get(info.getGameProfile().getId());

                // reset scoreboard values for compatibility
                scoreboard.getOrCreateScore(profile.getName(), objective).setScorePoints(hinfo.getHealthTotal());

                int health = hinfo.getHealth();
                int absorp = hinfo.getAbsorption();
                if (!HealthConfig.enableAbsorption) {
                    health += absorp;
                    absorp = 0;
                }

                hinfo.markDirty();
                int absorpCount = Math.min(absorp + 1, 20) / 2;
                int renderCount = 10 + absorpCount;
                float delta = Math.min(86.0F / renderCount, 9.0F);
                boolean blink = info.getHealthBlinkTime() > updateCounter && ((info.getHealthBlinkTime() - updateCounter) / 3) % 2 == 1;

                if (absorp == 0) {
                    // Basic render
                    drawHearts(heartX, posY, delta, health, 10, 0, hinfo.getOffset(), blink, false);
                } else {
                    // Expanded render (overlapping containers)
                    for (int segment = 0; segment < 10; segment++) {
                        drawHearts(heartX, posY, delta, health, 1, segment, hinfo.getOffset(), blink, false);
                    }
                    for (int segment = 0; segment < absorpCount; segment++) {
                        drawHearts(heartX + delta * 10, posY, delta, absorp, 1, segment, hinfo.getOffset(), blink, true);
                    }
                }
            }
        }

        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        mc.mcProfiler.endSection();
    }

    private void drawHearts(float posX, float posY, float delta, int health, int count, int segmentStart, int effect, boolean blink, boolean absorp) {
        int segmentEnd = segmentStart + count;

        // Parse offset value
        int vanillaXOffset = 0;
        int vanillaYOffset = 0;
        int mantleXOffset = 0;
        int mantleYOffset = 0;
        if ((effect & 0x1) != 0) { // poison (overrides wither)
            vanillaXOffset += 36;
            mantleYOffset += 9;
        } else if ((effect & 0x2) != 0) { // wither
            vanillaXOffset += 72;
            mantleYOffset += 18;
        }
        if ((effect & 0x4) != 0) { // hardcore
            vanillaYOffset += 45;
            mantleYOffset += 27;
        }

        // first pass: containers
        if (absorp && health > 20) {
            for (int segment = segmentStart; segment < segmentEnd; segment++) {
                if (health > 20 + segment * 2 + 1) {
                    this.mc.getTextureManager().bindTexture(ICON_ABSORB);
                    this.drawTexturedModalRect(posX + segment * delta, posY, 0, 54, 9, 9);
                } else {
                    this.mc.getTextureManager().bindTexture(ICONS);
                    this.drawTexturedModalRect(posX + segment * delta, posY, 16, vanillaYOffset, 9, 9);
                    if (health == 20 + segment * 2 + 1) {
                        this.mc.getTextureManager().bindTexture(ICON_ABSORB);
                        this.drawTexturedModalRect(posX + segment * delta, posY, 0, 54, 5, 9);
                    }
                }
            }
        } else {
            this.mc.getTextureManager().bindTexture(ICONS);
            for (int segment = segmentStart; segment < segmentEnd; segment++) {
                this.drawTexturedModalRect(posX + segment * delta, posY, blink ? 25 : 16, vanillaYOffset, 9, 9);
            }
        }

        // second pass: heart color
        if (absorp) {
            if (health <= 40) {
                this.mc.getTextureManager().bindTexture(ICONS);
                for (int segment = segmentStart; segment < segmentEnd; segment++) {
                    if (health == segment * 2 + 1) {
                        this.drawTexturedModalRect(posX + segment * delta, posY, 169 + vanillaXOffset, vanillaYOffset, 9, 9);
                    } else if (health > segment * 2) {
                        this.drawTexturedModalRect(posX + segment * delta, posY, 160 + vanillaXOffset, vanillaYOffset, 9, 9);
                    }
                }
            }
            if (health > 20) {
                this.mc.getTextureManager().bindTexture(ICON_ABSORB);
                int currColorHeart = (int) Math.ceil((float) (health - 20) / 20) % 11;
                int lastColorHeart = (int) Math.floor((float) (health - 20) / 20) % 11;
                for (int segment = segmentStart; segment < segmentEnd; segment++) {
                    // last color
                    if (lastColorHeart > 0 || health > 40) {
                        this.drawTexturedModalRect(posX + segment * delta, posY, 18 * (lastColorHeart - 1) + mantleXOffset, mantleYOffset, 9, 9);
                    }
                    // curr color (half heart and full heart)
                    if (health % 20 == segment * 2 + 1) {
                        this.drawTexturedModalRect(posX + segment * delta, posY, 18 * (currColorHeart - 1) + 9 + mantleXOffset, mantleYOffset, 9, 9);
                    } else if (health % 20 > segment * 2) {
                        this.drawTexturedModalRect(posX + segment * delta, posY, 18 * (currColorHeart - 1) + mantleXOffset, mantleYOffset, 9, 9);
                    }
                }
            }
        } else {
            if (health <= 40) {
                this.mc.getTextureManager().bindTexture(ICONS);
                for (int segment = segmentStart; segment < segmentEnd; segment++) {
                    if (health == segment * 2 + 1) {
                        this.drawTexturedModalRect(posX + segment * delta, posY, 61 + vanillaXOffset, vanillaYOffset, 9, 9);
                    } else if (health > segment * 2) {
                        this.drawTexturedModalRect(posX + segment * delta, posY, 52 + vanillaXOffset, vanillaYOffset, 9, 9);
                    }
                }
            }
            if (health > 20) {
                this.mc.getTextureManager().bindTexture(ICON_HEARTS);
                int currColorHeart = (int) Math.ceil((float) (health - 20) / 20) % 11;
                int lastColorHeart = (int) Math.floor((float) (health - 20) / 20) % 11;
                for (int segment = segmentStart; segment < segmentEnd; segment++) {
                    // last color
                    if (lastColorHeart > 0 || health > 40) {
                        this.drawTexturedModalRect(posX + segment * delta, posY, 18 * (lastColorHeart - 1) + mantleXOffset, mantleYOffset, 9, 9);
                    }
                    // curr color (half heart and full heart)
                    if (health % 20 == segment * 2 + 1) {
                        this.drawTexturedModalRect(posX + segment * delta, posY, 18 * (currColorHeart - 1) + 9 + mantleXOffset, mantleYOffset, 9, 9);
                    } else if (health % 20 > segment * 2) {
                        this.drawTexturedModalRect(posX + segment * delta, posY, 18 * (currColorHeart - 1) + mantleXOffset, mantleYOffset, 9, 9);
                    }
                }
            }
        }
    }
}
