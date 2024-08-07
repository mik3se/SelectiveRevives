//todo:
//- add saving reviveCounts to file
//- add killing functionality

package net.xumoye.SelectiveRevives;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.xumoye.SelectiveRevives.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class SelectiveRevives extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        Utils.startingReviveCount = getConfig().getInt("starting-revives");

        File reviveCountsSaveFile = new File(getDataFolder().getPath(), "revivecounts-uuid.json");
        ObjectMapper mapper = new ObjectMapper();

        if(reviveCountsSaveFile.exists() && !reviveCountsSaveFile.isDirectory()) {
            try{
                ReviveCountHandler.reviveCounts = new HashMap<UUID, Integer>(mapper.readValue(reviveCountsSaveFile, new TypeReference<>() {}));
            }
            catch (IOException error){
                error.printStackTrace();
            }
        }

        getServer().getPluginManager().registerEvents(new ReviveCountHandler(this), this);

        getServer().getPluginManager().registerEvents(this, this);
        getCommand("revive").setExecutor(new ReviveCommand());
        getCommand("giverevives").setExecutor(new GiveRevivesCommand());
        getCommand("setrevivecounter").setExecutor(new SetReviveCounterCommand());
        getCommand("getrevivecounter").setExecutor(new GetReviveCounterCommand());
        getCommand("removerevives").setExecutor(new RemoveRevivesCommand());
        getCommand("clearrevivecounter").setExecutor(new ClearReviveCounterCommand());
        getCommand("defaultrevivecount").setExecutor(new DefaultReviveCountCommand(this));
    }

    @Override
    public void onDisable() {
    }
}