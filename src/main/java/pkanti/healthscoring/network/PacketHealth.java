package pkanti.healthscoring.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import pkanti.healthscoring.HealthScoring;
import pkanti.healthscoring.data.HealthMap;

import java.util.UUID;
import java.util.function.Supplier;

public class PacketHealth {
    private UUID id;
    private HealthMap.HealthInfo info;

    public PacketHealth() {}

    public PacketHealth(UUID id, HealthMap.HealthInfo info) {
        this.id = id;
        this.info = info;
    }

    public static PacketHealth decode(PacketBuffer buf) {
        UUID id = buf.readUniqueId();
        int normalHealth = buf.readVarInt();
        int absorptionHealth = buf.readVarInt();
        int effect = buf.readVarInt();
        return new PacketHealth(id,
                new HealthMap.HealthInfo(normalHealth, absorptionHealth, effect, true));
    }

    public static void encode(PacketHealth message, PacketBuffer buf) {
        buf.writeUniqueId(message.id);
        buf.writeVarInt(message.info.getHealth());
        buf.writeVarInt(message.info.getAbsorption());
        buf.writeVarInt(message.info.getOffset());
    }

    public static void handle(PacketHealth message, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient())
            ctx.get().enqueueWork(() -> {
                HealthScoring.proxy.map.update(message.id, message.info);
            });
        ctx.get().setPacketHandled(true);
    }
}
