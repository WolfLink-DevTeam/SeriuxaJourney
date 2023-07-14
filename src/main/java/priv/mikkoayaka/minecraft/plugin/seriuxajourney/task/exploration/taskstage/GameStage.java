package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.exploration.taskstage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.wolflink.common.ioc.IOC;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdListener;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.SeriuxaJourney;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.Config;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.file.ConfigProjection;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.EvacuationZone;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.Task;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.region.SquareRegion;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.region.TaskRegion;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.stage.TaskLinearStageHolder;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.stage.TaskStage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

import java.util.HashSet;
import java.util.Set;

public class GameStage extends TaskStage {
    private final Config config;
    private final Task task;
    public GameStage(TaskLinearStageHolder stageHolder) {
        super("正在进行", stageHolder);
        task = stageHolder.getTask();
        config = IOC.getBean(Config.class);
    }
    @Override
    protected void onEnter() {
        String worldName = config.get(ConfigProjection.EXPLORATION_TASK_WORLD_NAME);
        World world = Bukkit.getWorld(worldName);
        getStageHolder().getTask().start(new SquareRegion(
                getStageHolder().getTask(),
                world,
                0,
                0,
                500
        ));
    }
    @Override
    protected void onLeave() {

    }
}
