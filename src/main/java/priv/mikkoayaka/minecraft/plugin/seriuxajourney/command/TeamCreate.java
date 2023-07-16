package priv.mikkoayaka.minecraft.plugin.seriuxajourney.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.wolflink.common.ioc.Inject;
import org.wolflink.common.ioc.Singleton;
import org.wolflink.minecraft.wolfird.framework.bukkit.WolfirdCommand;
import priv.mikkoayaka.minecraft.plugin.seriuxajourney.team.TaskTeamService;

@Singleton
public class TeamCreate extends WolfirdCommand {

    @Inject
    private TaskTeamService taskTeamService;

    public TeamCreate() {
        super(true, false, true, "sj team create", "创建一个小队");
    }

    @Override
    protected void execute(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;
        taskTeamService.createTeam(player).show(player);
    }
}
