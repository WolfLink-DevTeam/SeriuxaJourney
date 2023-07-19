package org.wolflink.minecraft.plugin.siriuxa.monster;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.wolflink.minecraft.plugin.siriuxa.Siriuxa;
import org.wolflink.minecraft.plugin.siriuxa.task.common.Task;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 任务怪物生成器
 * 主要负责处理与任务相关的怪物生成
 */
public class TaskMonsterSpawner {

    @NonNull
    private final Task task;

    private boolean enabled = false;

    private final SpawnerAttribute spawnerAttribute;

    private int spawnTaskId = -1;

    public void setEnabled(boolean value) {
        if (enabled == value) return;
        enabled = value;
        if (enabled) startSpawnMob();
        else stopSpawnMob();
    }

    public TaskMonsterSpawner(@NonNull Task task) {
        this.task = task;
        spawnerAttribute = new SpawnerAttribute(task.getTaskDifficulty());
    }

    private final int MIN_RADIUS = 10;
    private final int MAX_RADIUS = 20;

    private void startSpawnMob() {
        if (spawnTaskId == -1) {
            spawnTaskId = spawnMobTask(MIN_RADIUS, MAX_RADIUS).getTaskId();
        }
    }

    private void stopSpawnMob() {
        if (spawnTaskId != -1) {
            Bukkit.getScheduler().cancelTask(spawnTaskId);
            spawnTaskId = -1;
        }
    }

    private @NonNull BukkitTask spawnMobTask(int minRadius, int maxRadius) {
        Plugin plugin = Siriuxa.getInstance();
        return Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            task.getPlayers().forEach(p -> spawnMobAroundPlayer(minRadius, maxRadius, p));
        }, 20 * 15, 20 * 15);
    }

    /**
     * 在玩家周围生成一只怪物
     */
    private void spawnMobAroundPlayer(int minRadius, int maxRadius, @NonNull Player player) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Location loc = player.getLocation();
        World world = loc.getWorld();
        assert world != null;
        if (isMobCountOverLimit(maxRadius, loc)) return;
        double r = random.nextInt(minRadius, maxRadius);
        double x = loc.getX() + random.nextDouble() * r * 2 - r;
        double z = loc.getZ() + random.nextDouble() * r * 2 - r;
        double y = world.getHighestBlockYAt((int) x, (int) z);
        Location spawnLoc = new Location(world, x, y, z);
        if (spawnLoc.getBlock().isLiquid()) return;
        EntityType entityType = spawnerAttribute.randomType();
        Monster monster = (Monster) world.spawnEntity(spawnLoc, entityType);
        AttributeInstance maxHealth = monster.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        AttributeInstance movementSpeed = monster.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        AttributeInstance attackDamage = monster.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (maxHealth != null) {
            maxHealth.setBaseValue(maxHealth.getBaseValue() * spawnerAttribute.getHealthMultiple());
            monster.setHealth(maxHealth.getBaseValue());
        }
        if (movementSpeed != null) movementSpeed.setBaseValue(movementSpeed.getBaseValue() * spawnerAttribute.getMovementMultiple());
        if (attackDamage != null) attackDamage.setBaseValue(attackDamage.getBaseValue() * spawnerAttribute.getDamageMultiple());
    }

    // 必须同步计算
    private static boolean isMobCountOverLimit(double radius, @NonNull Location center) {
        int mobCount = Objects.requireNonNull(center.getWorld())
                .getNearbyEntities(center, radius, radius, radius, Monster.class::isInstance)
                .size();
        // 示例:
        // 半径:15, 怪物上限~50
        // 半径:20, 怪物上限~80
        // 半径:25, 怪物上限~120
        // 不考虑半径太大的情况
        return mobCount > 15 + (radius * radius / 6.0);
    }
}
