package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration;

import org.bukkit.OfflinePlayer;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.Result;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.VaultAPI;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.difficulty.ExplorationDifficulty;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.TaskRepository;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.team.TaskTeam;

import java.util.List;

@Singleton
public class ExplorationService {
    @Inject
    private TaskRepository taskRepository;
    @Inject
    private VaultAPI vaultAPI;
    public Result create(TaskTeam taskTeam, ExplorationDifficulty explorationDifficulty) {
        if(taskTeam.getSelectedTask() != null) return new Result(false,"当前队伍已经选择了任务，无法再次创建。");
        double cost = explorationDifficulty.getWheatCost();
        List<OfflinePlayer> offlinePlayers = taskTeam.getOfflinePlayers();
        // 检查成员余额
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            if(vaultAPI.getEconomy(offlinePlayer) < cost) return new Result(false,"队伍中至少有一名成员无法支付本次任务费用。");
        }
        // 成员支付任务成本
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            vaultAPI.takeEconomy(offlinePlayer,cost);
        }
        // 创建任务
        ExplorationTask task = new ExplorationTask(taskTeam,explorationDifficulty);
        // 与队伍绑定
        taskTeam.setSelectedTask(task);
        taskRepository.insert(task);
        return new Result(true,"任务登记完成。");
    }
}
