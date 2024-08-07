package net.xumoye.SelectiveRevives.commands;

import net.xumoye.SelectiveRevives.ReviveCountHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class GetReviveCounterCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        ConsoleCommandSender console = getServer().getConsoleSender();
        String targetName;
        Player target;
        String revivePluralText;
        if(sender instanceof Player player){
            if(args.length == 0){
                target = player;
            }
            else{
                targetName = args[0];
                target = Bukkit.getServer().getPlayerExact(targetName);

                if(targetName.equals("@p") || targetName.equals("@s")){
                    target = player;
                }
                else if(targetName.equals("@e")){
                    player.sendMessage(ChatColor.RED + "Only players may be affected by this command, but the provided selector includes entities");
                }
                else if(targetName.equals("@a")){
                    player.sendMessage(ChatColor.RED + "Only one player is allowed, but the provided selector allows more than one");
                }
                else if(targetName.equals("@r")){
                    player.sendMessage(ChatColor.RED + "@r selectors is not currently supported");
                    //todo: add @r support
                }
                else if(target == null){
                    player.sendMessage(ChatColor.RED + "No player was found");
                }
            }
            if(target != null){
                if(ReviveCountHandler.reviveCounts.get(target.getUniqueId()) == 1){
                    revivePluralText = " revive";
                }
                else{
                    revivePluralText = " revives";
                }
                if(player != target && player.isOp()){
                    player.sendMessage(ChatColor.GREEN + target.getName() + " has " + ReviveCountHandler.reviveCounts.get(target.getUniqueId()).toString() + revivePluralText);
                }
                else if(player != target){
                    player.sendMessage(ChatColor.RED + "You must be a server operator to get the revive count of another player");
                }
                else{
                    if (ReviveCountHandler.reviveCounts.get(player.getUniqueId()) > 0) {
                        player.sendMessage(ChatColor.GREEN + "You have " + ReviveCountHandler.reviveCounts.get(player.getUniqueId()).toString() + revivePluralText);
                    }
                    else {
                        player.sendMessage(ChatColor.RED + "You don't have any revives");
                    }
                }
            }
        }
        else if(sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender){
            if(args.length == 0){
                console.sendMessage(ChatColor.RED + "Usage: " + command.getUsage().replace("<command>", "getrevivecounter").replace("[<player>]", "<player>"));
                target = null;
            }
            else{
                targetName = args[0];
                target = Bukkit.getServer().getPlayerExact(targetName);

                if(targetName.equals("@e")){
                    console.sendMessage(ChatColor.RED + "Only players may be affected by this command, but the provided selector includes entities");
                }
                else if(targetName.equals("@a")){
                    console.sendMessage(ChatColor.RED + "Only one player is allowed, but the provided selector allows more than one");
                }
                else if(targetName.equals("@r") || targetName.equals("@p") || targetName.equals("@s")){
                    console.sendMessage(ChatColor.RED + "@ selectors are not currently supported from within console");
                    //todo: add @ selector support
                }
                else if(target == null){
                    console.sendMessage(ChatColor.RED + "No player was found");
                }
            }
            if(target != null){
                if(ReviveCountHandler.reviveCounts.get(target.getUniqueId()) == 1){
                    revivePluralText = " revive";
                }
                else{
                    revivePluralText = " revives";
                }
                console.sendMessage(target.getName() + " has " + ReviveCountHandler.reviveCounts.get(target.getUniqueId()).toString() + revivePluralText);
            }
        }
        return true;
    }
}
