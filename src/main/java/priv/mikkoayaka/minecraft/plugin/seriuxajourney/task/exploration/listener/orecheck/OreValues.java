package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.listener.orecheck;

import org.bukkit.Material;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.OreCache;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 矿物价值类
 */
@Singleton
public class OreValues {
    /**
     * 材质 - 相对于铜块的价值
     */
    private final static Map<Material,Double> valueMap = new HashMap<>();
    static {
        valueMap.put(Material.COPPER_BLOCK,1.0);
        valueMap.put(Material.COAL_BLOCK,1.15);
        valueMap.put(Material.IRON_BLOCK,1.5);
        valueMap.put(Material.GOLD_BLOCK,1.8);
        valueMap.put(Material.LAPIS_BLOCK,0.6);
        valueMap.put(Material.REDSTONE_BLOCK,0.6);
        valueMap.put(Material.DIAMOND_BLOCK,6.65);
        valueMap.put(Material.EMERALD_BLOCK,5.0);
    }

    @Inject
    private OreCache oreCache;
    /**
     * 矿物数量历史记录
     */
    private final Map<Material,Integer> cacheMap = new HashMap<>();
    private final int totalCache;
    /**
     * 今日矿物数量记录
     */
    private final Map<Material,Integer> todayMap = new HashMap<>();
    /**
     * 整体价值倍率
     */
    private final double multiple = 8.0;
    /**
     * 最少记录池数量
     */
    private final int minPoolSize = 100;
    /**
     * 最大记录天数
     */
    private final int maxRecordDate = 30;
    public OreValues() {
        int totalCache = 0;
        Calendar calendar = Calendar.getInstance();
        for (Material material : getOreMaterials()) {
            int count = 0;
            for (int i = 0; i < maxRecordDate; i++) {
                count += oreCache.getOreCache(calendar,material);
                calendar.add(Calendar.DATE,-1);
            }
            Notifier.debug("获取到矿物记录数据："+material.name().toLowerCase()+"，数量总计："+count);
            cacheMap.put(material,count);
            totalCache += count;
        }
        this.totalCache = totalCache;
    }
    public void record(Material material) {
        todayMap.put(material,todayMap.get(material)+1);
    }
    public void save() {
        for (Map.Entry<Material,Integer> entry : todayMap.entrySet()) {
            oreCache.addOreCacheToday(entry.getKey(),entry.getValue());
        }
    }
    public Set<Material> getOreMaterials() {
        return valueMap.keySet();
    }

    /**
     * 获取当前价值百分比(只根据历史数据进行判定) [0.2,1.2]
     */
    public double getValuePercent(Material material) {
        if(totalCache <= minPoolSize) return 1 - (1.0/getOreMaterials().size());
        return 1.2 - (cacheMap.get(material) / (double) totalCache);
    }

    public double getOreValue(Material material) {
        return ( valueMap.get(material) * multiple ) * getValuePercent(material);
    }
}
