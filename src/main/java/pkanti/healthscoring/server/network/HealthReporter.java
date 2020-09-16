package pkanti.healthscoring.server.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import pkanti.healthscoring.HealthScoring;
import pkanti.healthscoring.data.HealthMap;
import pkanti.healthscoring.network.PacketHandler;
import pkanti.healthscoring.network.PacketHealth;

@Mod.EventBusSubscriber(modid = HealthScoring.MODID, value = Side.SERVER)
public class HealthReporter {

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent evt) {
        if (evt.player instanceof EntityPlayerMP) {
            int offset = 0;
            if (evt.player.getActivePotionEffect(MobEffects.POISON) != null)
                offset |= 0x1;
            if (evt.player.getActivePotionEffect(MobEffects.WITHER) != null)
                offset |= 0x2;
            if (evt.player.world.getWorldInfo().isHardcoreModeEnabled())
                offset |= 0x4;
            PacketHandler.INSTANCE.sendToAll(new PacketHealth(evt.player.getUniqueID(),
                    new HealthMap.HealthInfo(
                    (int) Math.ceil(evt.player.getHealth()),
                    (int) Math.ceil(evt.player.getAbsorptionAmount()),
                    offset,
                    true)));
        }
    }
}
