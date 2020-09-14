package pkanti.healthscoring.network;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import pkanti.healthscoring.HealthScoring;
import pkanti.healthscoring.data.HealthMap;
import net.minecraft.potion.Effects;

@Mod.EventBusSubscriber(modid = HealthScoring.MODID, value = Dist.DEDICATED_SERVER)
public class HealthReporter {

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent evt) {
        if (evt.side == LogicalSide.SERVER) {
            int offset = 0;
            if (evt.player.getActivePotionEffect(Effects.POISON) != null)
                offset |= 0x1;
            if (evt.player.getActivePotionEffect(Effects.WITHER) != null)
                offset |= 0x2;
            if (evt.player.world.getWorldInfo().isHardcore())
                offset |= 0x4;
            PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(),
                    new PacketHealth(evt.player.getUniqueID(),
                        new HealthMap.HealthInfo(
                        (int) Math.ceil(evt.player.getHealth()),
                        (int) Math.ceil(evt.player.getAbsorptionAmount()),
                        offset,
                       true)));
        }
    }
}
