package pkanti.healthscoring.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import pkanti.healthscoring.HealthScoring;
import pkanti.healthscoring.data.HealthMap;

import java.util.UUID;

public class PacketHealth implements IMessage {
    private UUID id;
    private HealthMap.HealthInfo info;

    public PacketHealth() {}

    public PacketHealth(UUID id, HealthMap.HealthInfo info) {
        this.id = id;
        this.info = info;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        id = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        int normalHealth = ByteBufUtils.readVarInt(buf, 5);
        int absorptionHealth = ByteBufUtils.readVarInt(buf,5);
        int effect = ByteBufUtils.readVarInt(buf, 5);
        info = new HealthMap.HealthInfo(normalHealth, absorptionHealth, effect, true);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, id.toString());
        ByteBufUtils.writeVarInt(buf, info.getHealth(), 5);
        ByteBufUtils.writeVarInt(buf, info.getAbsorption(), 5);
        ByteBufUtils.writeVarInt(buf, info.getOffset(), 5);
    }

    public static class Handler implements IMessageHandler<PacketHealth, IMessage> {
        @Override
        public IMessage onMessage(PacketHealth message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(
                    () -> HealthScoring.proxy.map.update(message.id, message.info)
            );
            return null;
        }
    }

}
