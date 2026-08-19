package stranik.dev.nametaghider.actionbar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import stranik.dev.nametaghider.NametagHiderPlugin;
import stranik.dev.nametaghider.actionbar.strategy.ActionBarStrategy;
import stranik.dev.nametaghider.actionbar.strategy.MythicLibStrategy;
import stranik.dev.nametaghider.actionbar.strategy.VanillaStrategy;

public class ActionBarSender {
    public static void send(Player player, String message) {
        getStrategy().send(player, message);
    }

    private static ActionBarStrategy getStrategy() {
        boolean mythicLibsEnabled = Bukkit.getPluginManager().isPluginEnabled("MythicLib");

        if (mythicLibsEnabled && NametagHiderPlugin.getInstance().getMythicLibIntegration()) {
            return MythicLibStrategy.INSTANCE;
        }

        return VanillaStrategy.INSTANCE;
    }
}
