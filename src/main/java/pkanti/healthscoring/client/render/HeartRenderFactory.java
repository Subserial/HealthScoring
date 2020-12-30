package pkanti.healthscoring.client.render;

import java.util.HashMap;
import java.util.function.Supplier;

public class HeartRenderFactory {

    private static HashMap<String, Supplier<IHeartRender>> factoryMap;
    static {
        factoryMap = new HashMap<>();
        registerRender("mantle", HeartRenderMantle::new);
        registerRender("healthoverlay", HeartRenderHealthOverlay::new);
    }

    public static void registerRender(String id, Supplier<IHeartRender> render) {
        factoryMap.put(id, render);
    }

    public static IHeartRender getHeartRender(String id) {
        Supplier<IHeartRender> supplier = factoryMap.getOrDefault(id, null);
        return supplier != null ? supplier.get() : null;
    }
}
