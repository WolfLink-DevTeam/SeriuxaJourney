package priv.mikkoayaka.minecraft.plugin.seriuxajourney.task.common.stage;

import lombok.Getter;
import org.wolflink.minecraft.wolfird.framework.gamestage.stage.Stage;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.utils.Notifier;

public abstract class TaskStage extends Stage {

    @Getter
    private final TaskLinearStageHolder stageHolder;
    public TaskStage(String displayName, TaskLinearStageHolder stageHolder) {
        super(displayName, stageHolder);
        this.stageHolder = stageHolder;
    }

    @Override
    protected void onEnter() {
        Notifier.debug("任务"+getStageHolder().getTask().getTaskId()+"进入"+getDisplayName()+"阶段");
    }

    @Override
    protected void onLeave() {
        Notifier.debug("任务"+getStageHolder().getTask().getTaskId()+"离开"+getDisplayName()+"阶段");
    }
}
