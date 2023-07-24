package org.wolflink.minecraft.plugin.siriuxa.sculkinfection;

import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.api.ISwitchable;
import org.wolflink.minecraft.plugin.siriuxa.api.world.BlockAPI;
import org.wolflink.minecraft.plugin.siriuxa.file.Config;
import org.wolflink.minecraft.plugin.siriuxa.file.ConfigProjection;
import org.wolflink.minecraft.wolfird.framework.bukkit.scheduler.SubScheduler;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
public class SculkInfection implements ISwitchable {
    /**
     * 感染值
     */
    private final Map<UUID,Integer> infectionMap = new ConcurrentHashMap<>();

    private final SubScheduler subScheduler = new SubScheduler();

    @Inject
    private BlockAPI blockAPI;
    @Inject
    private Config config;

    /**
     * 增加感染值
     */
    private void addInfectionValue(Player player,int value) {
        UUID pUuid = player.getUniqueId();
        int oldValue = getInfectionValue(player.getUniqueId());
        int newValue = oldValue + value;
        if(newValue < 0) newValue = 0;
        infectionMap.put(pUuid,newValue);
    }
    public int getInfectionValue(UUID uuid) {
        return infectionMap.getOrDefault(uuid,0);
    }

    /**
     * 刷新玩家的感染值
     * 玩家站在潜声方块上，每秒获得 40 点感染值
     * 每秒获得 附近8格内潜声方块数量 x 4 点感染值
     * 如果不处在附近，则每秒 -40 点感染值
     * 牛奶可以减少 100 点感染值
     *
     * 轻度感染 达到 100 点 间歇性虚弱+间歇性挖掘疲劳+走过的方块有概率变成潜声方块
     * 中度感染 达到 200 点 虚弱+挖掘疲劳+缓慢+走过的方块有概率变成潜声方块
     * 重度感染 达到 300 点 虚弱+挖掘疲劳+走过的方块有概率变成潜声方块+凋零+缓慢+失明
     */
    private void updateInfectionValue(Player player) {
        // 不在任务世界
        if(!(player.getWorld().getName().equals(config.get(ConfigProjection.EXPLORATION_TASK_WORLD_NAME)))) return;
        // 不是生存模式
        if(player.getGameMode() != GameMode.SURVIVAL) return;
        UUID pUuid = player.getUniqueId();
        List<Location> nearbySculks = blockAPI.searchBlock(Material.SCULK,player.getLocation(),8);
        int sculkAmount = nearbySculks.size();
        addInfectionValue(player,sculkAmount * 4 - 25);
        int value = getInfectionValue(pUuid);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double randDouble = random.nextDouble();
        Bukkit.getScheduler().runTask(Siriuxa.getInstance(),()->{
            if(value >= 300) {
                player.playSound(player.getLocation(), Sound.BLOCK_SCULK_CHARGE,1f,1f);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent("§c§l你被幽匿方块严重感染了！"));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,40,0,false,false,false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,40,0,false,false,false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS,40,0,false,false,false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,40,0,false,false,false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER,40,0,false,false,false));
                if(randDouble <= 0.4) {
                    Material material;
                    if(random.nextDouble() <= 0.2) material = Material.SCULK_CATALYST;
                    else material = Material.SCULK;
                    player.getLocation().clone().add(0,-1,0).getBlock().setType(material);
                }
            }
            else if (value >= 200) {
                player.playSound(player.getLocation(), Sound.BLOCK_SCULK_CHARGE,1f,1f);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent("§5§l你变得寸步难行..."));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,40,0,false,false,false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,40,0,false,false,false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,40,0,false,false,false));
                if(randDouble <= 0.2) {
                    Material material;
                    if(random.nextDouble() <= 0.2) material = Material.SCULK_CATALYST;
                    else material = Material.SCULK;
                    player.getLocation().clone().add(0,-1,0).getBlock().setType(material);
                }
            }
            else if (value >= 100) {
                player.playSound(player.getLocation(), Sound.BLOCK_SCULK_CHARGE,1f,1f);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent("§e§l你感到有些不适..."));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,10,0,false,false,false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,10,0,false,false,false));
                if(randDouble <= 0.1) {
                    Material material;
                    if(random.nextDouble() <= 0.2) material = Material.SCULK_CATALYST;
                    else material = Material.SCULK;
                    player.getLocation().clone().add(0,-1,0).getBlock().setType(material);
                }
            }
        });
    }

    @Override
    public void enable() {
        subScheduler.runTaskTimerAsync(()->{
            Bukkit.getOnlinePlayers().forEach(this::updateInfectionValue);
        },20,20);
    }

    @Override
    public void disable() {
        subScheduler.cancelAllTasks();
    }
}
