package pkanti.healthscoring.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.overlay.PlayerTabOverlayGui;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.Mantle;

public class HeartRenderMantle implements IHeartRender {
    private static final ResourceLocation ICON_HEARTS = new ResourceLocation(Mantle.modId, "textures/gui/hearts.png");
    private static final ResourceLocation ICON_ABSORP = new ResourceLocation(Mantle.modId, "textures/gui/absorb.png");

    public String getName() {
        return "mantle";
    }

    // V1
    // This code was copied from the previous version and is MUCH older than that of HealthOverlay. I may rewrite this.
    public void drawHearts(MatrixStack stack, PlayerTabOverlayGui gui, int posX, int posY, int delta, int health, int heartCount, int segmentStart, int effect, boolean blink, boolean absorp) {
        Minecraft mc = Minecraft.getInstance();
        int segmentEnd = segmentStart + heartCount;

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
                    mc.getTextureManager().bindTexture(ICON_ABSORP);
                    gui.blit(stack, posX + segment * delta, posY, 0, 54, 9, 9);
                } else {
                    mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
                    gui.blit(stack, posX + segment * delta, posY, 16, vanillaYOffset, 9, 9);
                    if (health == 20 + segment * 2 + 1) {
                        mc.getTextureManager().bindTexture(ICON_ABSORP);
                        gui.blit(stack, posX + segment * delta, posY, 0, 54, 5, 9);
                    }
                }
            }
        } else {
            mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
            for (int segment = segmentStart; segment < segmentEnd; segment++) {
                gui.blit(stack, posX + segment * delta, posY, blink ? 25 : 16, vanillaYOffset, 9, 9);
            }
        }

        // second pass: heart color
        if (absorp) {
            if (health <= 40) {
                mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
                for (int segment = segmentStart; segment < segmentEnd; segment++) {
                    if (health == segment * 2 + 1) {
                        gui.blit(stack, posX + segment * delta, posY, 169 + vanillaXOffset, vanillaYOffset, 9, 9);
                    } else if (health > segment * 2) {
                        gui.blit(stack, posX + segment * delta, posY, 160 + vanillaXOffset, vanillaYOffset, 9, 9);
                    }
                }
            }
            if (health > 20) {
                mc.getTextureManager().bindTexture(ICON_ABSORP);
                int currColorHeart = (int) Math.ceil((float) (health - 40) / 20) % 11;
                int lastColorHeart = (int) Math.floor((float) (health - 40) / 20) % 11;
                for (int segment = segmentStart; segment < segmentEnd; segment++) {
                    // last color
                    if (health > 40) {
                        gui.blit(stack, posX + segment * delta, posY, 18 * lastColorHeart + mantleXOffset, mantleYOffset, 9, 9);
                    }
                    // curr color (half heart and full heart)
                    if (health % 20 == segment * 2 + 1) {
                        gui.blit(stack, posX + segment * delta, posY, 18 * currColorHeart + 9 + mantleXOffset, mantleYOffset, 9, 9);
                    } else if (health % 20 > segment * 2) {
                        gui.blit(stack, posX + segment * delta, posY, 18 * currColorHeart + mantleXOffset, mantleYOffset, 9, 9);
                    }
                }
            }
        } else {
            if (health <= 40) {
                mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
                for (int segment = segmentStart; segment < segmentEnd; segment++) {
                    if (health == segment * 2 + 1) {
                        gui.blit(stack, posX + segment * delta, posY, 61 + vanillaXOffset, vanillaYOffset, 9, 9);
                    } else if (health > segment * 2) {
                        gui.blit(stack, posX + segment * delta, posY, 52 + vanillaXOffset, vanillaYOffset, 9, 9);
                    }
                }
            }
            if (health > 20) {
                mc.getTextureManager().bindTexture(ICON_HEARTS);
                int currColorHeart = (int) Math.ceil((float) (health - 40) / 20) % 11;
                int lastColorHeart = (int) Math.floor((float) (health - 40) / 20) % 11;
                for (int segment = segmentStart; segment < segmentEnd; segment++) {
                    // last color
                    if (health > 40) {
                        gui.blit(stack, posX + segment * delta, posY, 18 * lastColorHeart + mantleXOffset, mantleYOffset, 9, 9);
                    }
                    // curr color (half heart and full heart)
                    if (health % 20 == segment * 2 + 1) {
                        gui.blit(stack, posX + segment * delta, posY, 18 * currColorHeart + 9 + mantleXOffset, mantleYOffset, 9, 9);
                    } else if (health % 20 > segment * 2) {
                        gui.blit(stack, posX + segment * delta, posY, 18 * currColorHeart + mantleXOffset, mantleYOffset, 9, 9);
                    }
                }
            }
        }
    }
}
