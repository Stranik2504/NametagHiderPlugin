package stranik.dev.nametaghider;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class NametagHiderPlugin extends JavaPlugin implements Listener {
    // Constants
    private static final String DefaultDisplayName = "NametagHide";
    private static final String DefaultNameTeam = "nametagHide";
    private static final String DefaultNicknameFormat = "%nickname%";
    // Paths
    private static final String NameTeamPath = "scoreboardTeam.name";
    private static final String DisplayNamePath = "scoreboardTeam.displayName";
    private static final String NicknameFormatPath = "nicknameFormat";
    private static final String UseRightClickViewPath = "rightClickView";
    private static final String EnabledPath = "enabled";

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        var pluginManager = this.getServer().getPluginManager();

        pluginManager.registerEvents(this, this);

        prepareScoreboardTeam();
    }

    private void prepareScoreboardTeam() {
        var displayName = getDisplayName();
        var team = getTeam();
        
        team.setDisplayName(displayName);
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        team.setCanSeeFriendlyInvisibles(false);

        addAllUserToTeam(team);
    }
    
    private void addAllUserToTeam(Team team) {
        if (team == null)
            return;
        
        for (var player : getServer().getOnlinePlayers()) {
            if (!team.hasPlayer(player))
                team.addPlayer(player);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        // deleteTeam();
    }
    
    private void deleteTeam() {
        final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        var nameTeam = getNameTeam();

        Team team = scoreboard.getTeam(nameTeam);

        if (team == null)
            return;

        team.unregister();
    }

    private String getDisplayName() {
        var displayName = getConfig().getString(DisplayNamePath);

        if (displayName == null || displayName.trim().isEmpty())
            return DefaultDisplayName;

        return displayName;
    }

    private String getNameTeam() {
        var nameTeam = getConfig().getString(NameTeamPath);

        if (nameTeam == null || nameTeam.trim().isEmpty())
            return DefaultNameTeam;

        return nameTeam;
    }

    private String getNicknameFormat() {
        var nicknameFormat = getConfig().getString(NicknameFormatPath);

        if (nicknameFormat == null || nicknameFormat.trim().isEmpty())
            return DefaultNicknameFormat;

        return nicknameFormat;
    }

    private boolean getUseRightClickView() {
        return getConfig().getBoolean(UseRightClickViewPath);
    }

    private boolean getEnable() {
        return getConfig().getBoolean(EnabledPath);
    }
    
    private Team getTeam() {
        final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        var nameTeam = getNameTeam();

        Team team = scoreboard.getTeam(nameTeam);

        if (team == null) {
            team = scoreboard.registerNewTeam(nameTeam);
        }
        
        return team;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("nametagHider.commands"))
            return false;
        
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();

            sender.sendMessage("Config successfully reloaded");
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("set-rc")) {
            getConfig().set(UseRightClickViewPath, Boolean.parseBoolean(args[1]));
            saveConfig();

            sender.sendMessage("Use right click rule set to: " + getUseRightClickView());
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("set-rc")) {
            sender.sendMessage("Use right click rule: " + getUseRightClickView());
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("enabled")) {
            var enabled = Boolean.parseBoolean(args[1]);

            if (enabled && !getEnable())
                prepareScoreboardTeam();
            else if (!enabled && getEnable())
                deleteTeam();

            getConfig().set(EnabledPath, enabled);
            saveConfig();

            sender.sendMessage("Enabled set to: " + getEnable() + " successfully");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("enabled")) {
            sender.sendMessage("Enabled: " + getEnable());
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
            sender.sendMessage(
                    "Enabled: " + getEnable() + "\n" +
                    "Use right click rule: " + getUseRightClickView() + "\n");
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            if (args[0].trim().isEmpty())
                return List.of("reload", "set-rc", "enabled", "info");
            
            if ("reload".startsWith(args[0]) && !"reload".equals(args[0]))
                return List.of("reload");

            if ("set-rc".startsWith(args[0]) && !"set-rc".equals(args[0]))
                return List.of("set-rc");

            if ("enabled".startsWith(args[0]) && !"enabled".equals(args[0]))
                return List.of("enabled");

            if ("info".startsWith(args[0]) && !"info".equals(args[0]))
                return List.of("info");
        }
        
        if (args.length == 2 && (args[0].equalsIgnoreCase("set-rc") || args[0].equalsIgnoreCase("enabled"))) {
            if (args[1].trim().isEmpty())
                return List.of("true", "false");

            if ("true".startsWith(args[1]) && !"true".equals(args[1]))
                return List.of("true");

            if ("false".startsWith(args[1]) && !"false".equals(args[1]))
                return List.of("false");
        }
        
        return new ArrayList<>();
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!getEnable())
            return;
        
        var team = getTeam();
        
        team.addPlayer(e.getPlayer());
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if (!getUseRightClickView() || !getEnable())
            return;
        
        if (e.getRightClicked() instanceof Player target) {
            final Player source = e.getPlayer();
            final String message = getNicknameFormat().replace("%nickname%", target.getName());
            
            source.sendActionBar(() -> Component.text(message));
        }
    }
    
    
}
