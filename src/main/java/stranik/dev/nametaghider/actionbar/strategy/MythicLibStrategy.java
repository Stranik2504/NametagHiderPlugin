package stranik.dev.nametaghider.actionbar.strategy;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import org.bukkit.entity.Player;

public class MythicLibStrategy implements ActionBarStrategy {

    @Override
    public void send(Player player, String message) {
        MMOPlayerData.get(player).getActionBar().show(message);
    }
}
