package priv.mikkoayaka.minecraft.plugin.seriuxajourney.file;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum ConfigProjection {
    WHEAT_LOSS_BASE("WheatLoss.Base",1.0), // 麦穗每秒流失量(基础值)
    LOBBY_WORLD_NAME("Lobby.WorldName","kg"), // 任务大厅世界
    LOBBY_LOCATION("Lobby.Location","-140 91 10") // 任务大厅坐标
    ;
    @Getter
    private final String path;
    @Getter
    private final Object defaultValue;
    ConfigProjection(String path,Object defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }
    public static Map<String,Object> asMap() {
        Map<String,Object> map = new HashMap<>();
        for (ConfigProjection configProjection : ConfigProjection.values()) {
            map.put(configProjection.path,configProjection.defaultValue);
        }
        return map;
    }
}
