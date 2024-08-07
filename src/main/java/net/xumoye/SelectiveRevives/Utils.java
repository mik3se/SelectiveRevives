package net.xumoye.SelectiveRevives;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Utils {
    public static Integer startingReviveCount;

    public static void tellReviveCount(Player player){
        if (ReviveCountHandler.reviveCounts.get(player.getUniqueId()) > 1) {
            player.sendMessage(ChatColor.GREEN + "You now have " + ReviveCountHandler.reviveCounts.get(player.getUniqueId()).toString() + " revives");
        }
        else if (ReviveCountHandler.reviveCounts.get(player.getUniqueId()) == 1) {
            player.sendMessage(ChatColor.GREEN + "You now have 1 revive");
        }
        else if (ReviveCountHandler.reviveCounts.get(player.getUniqueId()) <= 0) {
            player.sendMessage(ChatColor.RED + "You don't have any revives left");
        }
    }
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < 48 || c > 57) { //Quick unicode char value check
                return false;
            }
        }
        return true;
    }
    public static void setAndWriteCounter(UUID key, Integer value, HashMap<UUID, Integer> map, ObjectMapper mapper){
        map.put(key, value);
        try{
            mapper.writeValueAsString(map);
        }
        catch (JsonProcessingException error){
            error.printStackTrace();
        }
    }
}
