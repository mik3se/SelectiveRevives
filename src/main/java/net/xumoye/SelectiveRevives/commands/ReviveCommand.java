package net.xumoye.SelectiveRevives.commands;

import net.xumoye.SelectiveRevives.ReviveCountHandler;
import net.xumoye.SelectiveRevives.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class ReviveCommand implements CommandExecutor {
    public static List<UUID> reviveKilled = new ArrayList<>();

    static void revivePlayer(Player target, Player revivor){
        target.setGameMode(GameMode.SURVIVAL);
        reviveKilled.add(target.getUniqueId());
        target.setHealth(0);
        target.setRespawnLocation(null);
        target.spigot().respawn();
        if(revivor == null){
            Bukkit.broadcastMessage(target.getName() + " was revived");
            if(target.isOp()){
                target.sendMessage(ChatColor.GREEN + "You have been revived by the server");
            }
            else{
                target.sendMessage(ChatColor.GREEN + "You have been revived");
            }
        }
        else if(target != revivor){
            Bukkit.broadcastMessage(revivor.getName() + " revived " + target.getName());
            if(target.isOp()){
                target.sendMessage(ChatColor.GREEN + "You have been revived by " + revivor.getName());
            }
            else{
                target.sendMessage(ChatColor.GREEN + "You have been revived");
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ConsoleCommandSender console = getServer().getConsoleSender();
        String targetName;
        Player target;
        boolean updateCounter = false;

        if(sender instanceof Player player){
            if(args.length == 0 && !player.isOp()){
                player.sendMessage(ChatColor.RED + "Usage: " + command.getUsage().replace("<command>", "revive"));
            }
            else{
                if(args.length == 0){
                    target = player;
                }
                else {
                    targetName = args[0];
                    target = Bukkit.getServer().getPlayerExact(targetName);

                    if(args.length > 1 && args[1].equalsIgnoreCase("updatecounter")){
                        updateCounter = true;
                    }

                    if (targetName.equals("@p") || targetName.equals("@s")) {
                        target = player;
                    }
                    else if (targetName.equals("@e")) {
                        player.sendMessage(ChatColor.RED + "Only players may be affected by this command, but the provided selector includes entities");
                    }
                    else if (targetName.equals("@r") || targetName.equals("@a")) {
                        player.sendMessage(ChatColor.RED + "@a and @r selectors are not currently supported");
                    }
                    else if(target == null){
                        player.sendMessage(ChatColor.RED + "No player was found");
                    }
                }
                if(target != null){
                    if(target == player && !player.isOp()){
                        player.sendMessage(ChatColor.RED + "You must be a server operator to revive yourself");
                    }
                    else if(target == player && updateCounter){
                        player.sendMessage(ChatColor.RED + "You cannot revive yourself with updateCounter argument");
                    }
                    else if(target.getGameMode() == GameMode.SPECTATOR || target.isDead()) {
                        if(player.isOp() && !updateCounter){
                            if(target == player){
                                player.sendMessage(ChatColor.GREEN + "You've revived yourself");
                            }
                            else{
                                player.sendMessage(ChatColor.GREEN + "Successfully revived " + target.getName());
                            }
                            revivePlayer(target, player);
                        }
                        else if(ReviveCountHandler.reviveCounts.get(player.getUniqueId()) > 0){
                            player.sendMessage(ChatColor.GREEN + "Successfully revived " + target.getName());
                            ReviveCountHandler.reviveCounts.put(player.getUniqueId(), ReviveCountHandler.reviveCounts.get(player.getUniqueId()) - 1);
                            Utils.tellReviveCount(player);
                            revivePlayer(target, player);
                        }
                        else{
                            player.sendMessage(ChatColor.RED + "You don't have any revives");
                        }
                    }
                    else if(player.isOp() && target == player){
                        player.sendMessage(ChatColor.RED + "You are not dead");
                    }
                    else{
                        player.sendMessage(ChatColor.RED + "This player is not dead");
                    }
                }
            }
        }
        else if(sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender){
            if(args.length == 0){
                console.sendMessage(ChatColor.RED + "Usage: " + command.getUsage().replace("<command>", "revive"));
            }
            else{
                targetName = args[0];
                target = Bukkit.getServer().getPlayerExact(targetName);

                if(targetName.equals("@e")){
                    console.sendMessage(ChatColor.RED + "Only players may be affected by this command, but the provided selector includes entities");
                }
                else if(targetName.equals("@r") || targetName.equals("@a") || targetName.equals("@p") || targetName.equals("@s")){
                    console.sendMessage(ChatColor.RED + "@ selectors are not currently supported from within console");
                }
                if(target == null){
                    console.sendMessage(ChatColor.RED + "No player was found");
                }
                else{
                    if(target.getGameMode() == GameMode.SPECTATOR || target.isDead()){
                        revivePlayer(target, null);
                    }
                    else{
                        console.sendMessage(ChatColor.RED + "This player is not dead");
                    }
                }
            }
            }
        return true;
    }
}