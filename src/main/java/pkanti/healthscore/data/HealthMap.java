package pkanti.healthscore.data;

import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import pkanti.healthscore.HealthScore;

import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(value = Side.CLIENT)
public class HealthMap {
    private Map<UUID, HealthInfo> health = Maps.newHashMap();
    private boolean serverEnabled = false;

    @SubscribeEvent
    public void onPlayerJoinServer(FMLNetworkEvent.ClientConnectedToServerEvent evt) {
        Minecraft.getMinecraft().addScheduledTask(
                () -> {
                    health.clear();
                    serverEnabled = false;
                    HealthScore.logInfo("Joined server!");
        });
    }

    public HealthInfo get(UUID uuid) {
        return health.get(uuid);
    }

    public void update(UUID uuid, HealthInfo info) {
        serverEnabled = true;
        HealthInfo stored = health.get(uuid);
        if (stored != null) {
            stored.update(info);
        } else {
            health.put(uuid, info);
        }
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
            this.offset = offset;
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

        public void markDirty() { this.lastTotal = this.health; }
    }
}
