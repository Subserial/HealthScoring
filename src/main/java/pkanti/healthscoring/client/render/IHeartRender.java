package pkanti.healthscoring.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.overlay.PlayerTabOverlayGui;

public interface IHeartRender {

    String getName();

    void drawHearts(MatrixStack stack, PlayerTabOverlayGui gui, int posX, int posY, int delta, int health, int heartCount, int segmentStart, int effect, boolean blink, boolean absorp);
}
