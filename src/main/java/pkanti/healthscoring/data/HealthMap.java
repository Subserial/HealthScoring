package pkanti.healthscoring.data;

import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import pkanti.healthscoring.HealthConfig;

import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(value = Side.CLIENT)
public class HealthMap {
    private Map<UUID, HealthInfo> health = Maps.newHashMap();

    @SubscribeEvent
    public void onPlayerJoinServer(FMLNetworkEvent.ClientConnectedToServerEvent evt) {
        Minecraft.getMinecraft().addScheduledTask(
                () -> {
                    health.clear();
        });
    }

    public HealthInfo get(UUID uuid) {
        return health.get(uuid);
    }

    public void update(UUID uuid, HealthInfo info) {
        HealthInfo stored = health.get(uuid);
        if (stored != null) {
            stored.update(info);
        } else {
            health.put(uuid, info);
        }
    }

    public HealthInfo putLocal(UUID uuid, int newHealth) {
        HealthInfo stored = health.get(uuid);
        if (stored != null) {
            stored.update(newHealth);
        } else {
            stored = new HealthInfo(newHealth, 0, 0, false);
            health.put(uuid, stored);
        }
        return stored;
    }

    public static class HealthInfo {
        private int health;
        private int absorption;
        private int lastTotal;
        private int offset;
        private boolean fromServer;

        public HealthInfo(int health, int absorption, int offset, boolean fromServer) {
            this.health = health;
            this.absorption = absorption;
            this.offset = HealthConfig.enableEffects ? offset : 0;
            this.fromServer = fromServer;
            markDirty();
        }

        public int getHealth() { return health; }
        public int getAbsorption() { return absorption; }
        public int getLastHealth() { return lastTotal; }
        public int getHealthTotal() { return health + absorption; }
        public int getOffset() { return offset; }
        public boolean isFromServer() { return fromServer; }

        public void update(HealthInfo info) {
            this.health = info.getHealth();
            this.absorption = info.getAbsorption();
            this.offset = info.getOffset();
        }

        public void update(int newHealth) {
            this.health = newHealth;
        }

        public void markDirty() { this.lastTotal = this.health; }
    }
}
