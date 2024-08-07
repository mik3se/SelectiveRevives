package net.xumoye.SelectiveRevives;

import net.xumoye.SelectiveRevives.commands.ReviveCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReviveCountHandler implements Listener {
    public static HashMap<UUID, Integer> reviveCounts = new HashMap<>();
    private final SelectiveRevives plugin;

    public ReviveCountHandler(SelectiveRevives plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent deathEvent) {
        if (ReviveCommand.reviveKilled.contains(deathEvent.getEntity().getUniqueId())){
            deathEvent.setDeathMessage(null);
            ReviveCommand.reviveKilled.remove(deathEvent.getEntity().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent joinEvent) {
        if(!ReviveCountHandler.reviveCounts.containsKey(joinEvent.getPlayer().getUniqueId())){
            if(Utils.startingReviveCount != null){
                ReviveCountHandler.reviveCounts.put(joinEvent.getPlayer().getUniqueId(), Utils.startingReviveCount);
            }
            else{
                ReviveCountHandler.reviveCounts.put(joinEvent.getPlayer().getUniqueId(), 0);
            }
        }
    }
}
