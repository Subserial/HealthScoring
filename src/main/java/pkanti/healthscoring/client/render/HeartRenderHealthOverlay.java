package pkanti.healthscoring.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.overlay.PlayerTabOverlayGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import terrails.healthoverlay.HealthOverlay;

public class HeartRenderHealthOverlay implements IHeartRender {
    private static final ResourceLocation ICON_HEARTS = new ResourceLocation("healthoverlay:textures/health.png");
    private static final ResourceLocation ICON_ABSORP = new ResourceLocation("healthoverlay:textures/absorption.png");
    private static final ResourceLocation ICON_HALFHT = new ResourceLocation("healthoverlay:textures/half_heart.png");

    public String getName() {
        return "healthoverlay";
    }

    public void drawHearts(MatrixStack stack, PlayerTabOverlayGui gui, int posX, int posY, int delta, int health, int heartCount, int segmentStart, int effect, boolean blink, boolean absorp) {
        Minecraft mc = Minecraft.getInstance();
        int segmentEnd = segmentStart + heartCount;
        int containerEnd = Math.min(segmentEnd, health / 2);

        boolean poison = ((effect & 0x1) != 0);
        boolean wither = ((effect & 0x2) != 0);
        boolean hardcore = ((effect & 0x4) != 0);

        // Parse offset value
        int vanillaXOffset = 0;
        int vanillaYOffset = 0;
        int overlayXOffset = 0;
        int overlayYOffset = 0;
        int overlayDetailXOffset = 0;
        int overlayWitherYOffset = 0;
        int overlayHardcoreYOffset = 0;

        if (absorp) {
            vanillaXOffset = 160;
            overlayDetailXOffset = 45;
            overlayWitherYOffset = 18;
            overlayHardcoreYOffset = 9;
        } else {
            vanillaXOffset = 16;
            overlayDetailXOffset = 63;
            overlayWitherYOffset = 27;
            overlayHardcoreYOffset = 18;
            if (poison) { // overrides wither
                vanillaXOffset += 36;
                overlayXOffset = 18;
            } else if (wither) {
                vanillaXOffset += 72;
                overlayXOffset = 36;
            }
        }
        if (hardcore) {
            vanillaYOffset += 45;
            overlayYOffset = (absorp ? 18 : 45);
        }

        overlayWitherYOffset += overlayYOffset;
        overlayHardcoreYOffset += overlayYOffset;

        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);

        // first pass: containers
        if (absorp) {
            // containers match absorp hearts
            for (int segment = segmentStart; segment < containerEnd; segment++) {
                gui.blit(stack, posX + segment * delta, posY, blink ? 25 : 16, vanillaYOffset, 9, 9);
            }
            // draw half-heart container
            if (segmentStart <= containerEnd && health == containerEnd * 2 + 1) {
                mc.getTextureManager().bindTexture(ICON_HALFHT);
                gui.blit(stack, posX + containerEnd * delta, posY, blink ? 9 : 0, 0, 9, 9);
            }
        } else {
            // draw all containers
            for (int segment = segmentStart; segment < segmentEnd; segment++) {
                gui.blit(stack, posX + segment * delta, posY, blink ? 25 : 16, vanillaYOffset, 9, 9);
            }
        }

        // HealthOverlay: skip absorption hearts if poisoned or withered
        if (!(absorp && (poison || wither))) {

            // second pass: vanilla hearts
            mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
            if (health <= 20 + segmentEnd * 2) {
                for (int segment = segmentStart; segment < containerEnd; segment++) {
                    gui.blit(stack, posX + segment * delta, posY, vanillaXOffset + 36, vanillaYOffset, 9, 9);
                }
                if (containerEnd < segmentEnd && health == containerEnd * 2 + 1) {
                    gui.blit(stack, posX + containerEnd * delta, posY, vanillaXOffset + 45, vanillaYOffset, 9, 9);
                }
            }

            mc.getTextureManager().bindTexture(absorp ? ICON_ABSORP : ICON_HEARTS);

            // third pass: custom colors
            if (health > 20) {
                int lastIndex = Math.max(0, (int) Math.floor((float)(health - 40) / 20));
                int currIndex = Math.max(0, (int) Math.ceil((float)(health - 40) / 20));
                Color[] selection;
                Color currColor, lastColor;
                if (absorp) {
                    selection = HealthOverlay.absorptionColors;
                } else {
                    // "++" fixes an off-by-one.
                    if (poison) {
                        selection = HealthOverlay.poisonColors;
                        lastIndex++;
                        currIndex++;
                    } else if (wither) {
                        selection = HealthOverlay.witherColors;
                        lastIndex++;
                        currIndex++;
                    } else {
                        selection = HealthOverlay.healthColors;
                    }
                }
                lastColor = selection[lastIndex % selection.length];
                currColor = selection[currIndex % selection.length];

                int colorSplit = (health / 2) % 10;
                for (int segment = segmentStart; segment < segmentEnd; segment++) {
                    if (segment < colorSplit) {
                        setRenderColor(currColor);
                        gui.blit(stack, posX + segment * delta, posY, overlayXOffset, overlayYOffset, 9, 9);
                    } else if (health >= 40) {
                        setRenderColor(lastColor);
                        gui.blit(stack, posX + segment * delta, posY, overlayXOffset, overlayYOffset, 9, 9);
                    }
                    if (segment == colorSplit && health % 2 == 1) {
                        setRenderColor(currColor);
                        gui.blit(stack, posX + segment * delta, posY, overlayXOffset + 9, overlayYOffset, 9, 9);
                    }

                    // shading?
                    if (wither) {
                        setRenderFullAlpha(255);
                        gui.blit(stack, posX + segment * delta, posY, overlayXOffset, overlayWitherYOffset, 9, 9);
                    } else {
                        setRenderFullAlpha(56);
                        gui.blit(stack, posX + segment * delta, posY, overlayXOffset, overlayYOffset + 9, 9, 9);
                    }

                    // detail (eyes vs dot)
                    if (hardcore) {
                        setRenderFullAlpha(178);
                        gui.blit(stack, posX + segment * delta, posY, overlayXOffset, overlayHardcoreYOffset, 9, 9);
                    } else if (!(poison && health <= 20 + segment * 2) ) {
                        setRenderFullAlpha(255);
                        gui.blit(stack, posX + segment * delta, posY, overlayDetailXOffset, overlayYOffset, 9, 9);
                    }
                }
                setRenderFullAlpha(255);
            }
        }
        GlStateManager.disableBlend();
    }

    private static void setRenderFullAlpha(int alpha) {
        setRenderColorProps(Color.fromInt(0xFFFFFF), alpha);
    }

    private static void setRenderColor(Color color) {
        setRenderColorProps(color, 255);
    }

    private static void setRenderColorProps(Color color, int alpha) {
        int raw = color.getColor();
        GlStateManager.color4f(
                ((raw >> 16) & 0xFF) / 255.0F,
                ((raw >> 8) & 0xFF) / 255.0F,
                (raw & 0xFF) / 255.0F,
                alpha / 255.0F);
    }
}
