package pkanti.healthscoring.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.overlay.PlayerTabOverlayGui;

public class HeartRenderHealthOverlay implements IHeartRender {

    public String getName() {
        return "healthoverlay";
    }

    public void drawHearts(MatrixStack stack, PlayerTabOverlayGui gui, int posX, int posY, int delta, int health, int heartCount, int segmentStart, int effect, boolean blink, boolean absorp) {

    }
}
