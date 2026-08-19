package stranik.dev.nametaghider.actionbar.strategy;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;


public class VanillaStrategy implements ActionBarStrategy {
    public static final VanillaStrategy INSTANCE = new VanillaStrategy();

    @Override
    public void send(Player player, String message) {
        player.sendActionBar(() -> Component.text(message));
    }
}
