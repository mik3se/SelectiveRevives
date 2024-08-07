package net.xumoye.SelectiveRevives.commands;

import net.xumoye.SelectiveRevives.ReviveCountHandler;
import net.xumoye.SelectiveRevives.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.math.BigInteger;

import static org.bukkit.Bukkit.getServer;

public class RemoveRevivesCommand implements CommandExecutor {
    public static void removeRevivesFromPlayer(Player target, Player setter, int removeAmount, ConsoleCommandSender console){
        CommandSender messageTo;
        String revivePluralTextRemove;
        String revivePluralTextAbsolute;
        if(setter == null){
            messageTo = getServer().getConsoleSender();
        }
        else{
            messageTo = setter;
        }
        if(removeAmount == 1){
            revivePluralTextRemove = " revive";
        }
        else{
            revivePluralTextRemove = " revives";
        }


        if(removeAmount < 1){
            messageTo.sendMessage(ChatColor.RED + "Integer must not be less than 1, found " + Integer.toString(removeAmount));
        }
        else if (target == null) {
            messageTo.sendMessage(ChatColor.RED + "No player was found");
        }
        else if (ReviveCountHandler.reviveCounts.get(target.getUniqueId()) - removeAmount < 0 && target == setter) {//Checks if amount of revives would exceed the signed 32-bit integer limit
            messageTo.sendMessage(ChatColor.RED + "You cannot remove this many revives from yourself, revive count would go less than zero");
        }
        else if (ReviveCountHandler.reviveCounts.get(target.getUniqueId()) - removeAmount < 0) {//Checks if amount of revives would exceed the signed 32-bit integer limit
            messageTo.sendMessage(ChatColor.RED + target.getName() + " cannot have this many revives removed, revive count would go less than zero");
        }
        else{
            if(ReviveCountHandler.reviveCounts.get(target.getUniqueId()) - removeAmount == 1){
                revivePluralTextAbsolute = " revive";
            }
            else{
                revivePluralTextAbsolute = " revives";
            }

            if(setter == null){
                ReviveCountHandler.reviveCounts.put(target.getUniqueId(), ReviveCountHandler.reviveCounts.get(target.getUniqueId()) - removeAmount);
                messageTo.sendMessage(target.getName() + " now has " + ReviveCountHandler.reviveCounts.get(target.getUniqueId()).toString() + " revives");
                if(target.isOp()){
                    target.sendMessage(ChatColor.RED + "Server removed " + removeAmount + revivePluralTextRemove + " from you");
                }
                Utils.tellReviveCount(target);
            }
            else if (target == setter) {
                ReviveCountHandler.reviveCounts.put(target.getUniqueId(), ReviveCountHandler.reviveCounts.get(target.getUniqueId()) - removeAmount);
                console.sendMessage(setter.getName() + " removed " + removeAmount + revivePluralTextRemove + " from themself");
                target.sendMessage(ChatColor.GREEN + "You now have " + ReviveCountHandler.reviveCounts.get(target.getUniqueId()).toString() + revivePluralTextAbsolute);
            }
            else {
                ReviveCountHandler.reviveCounts.put(target.getUniqueId(), ReviveCountHandler.reviveCounts.get(target.getUniqueId()) - removeAmount);
                console.sendMessage(setter.getName() + " removed " + removeAmount + revivePluralTextRemove + " from " + target.getName());
                messageTo.sendMessage(ChatColor.GREEN + target.getName() + " now has " + ReviveCountHandler.reviveCounts.get(target.getUniqueId()).toString() + revivePluralTextAbsolute);
                console.sendMessage(target.getName() + " now has " + ReviveCountHandler.reviveCounts.get(target.getUniqueId()).toString() + revivePluralTextAbsolute);
                if(target.isOp()){
                    target.sendMessage(ChatColor.RED + setter.getName() + " removed " + removeAmount + revivePluralTextRemove + " from you");
                }
                Utils.tellReviveCount(target);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        ConsoleCommandSender console = getServer().getConsoleSender();
        Player target;
        if(sender instanceof Player player && player.isOp()){
            if(args.length == 0){
                target = player;
            }
            else{
                String targetName = args[0];
                target = Bukkit.getServer().getPlayerExact(targetName);

                if(targetName.equals("@p") || targetName.equals("@s")){
                    target = player;
                }
                else if(targetName.equals("@e")){
                    player.sendMessage(ChatColor.RED + "Only players may be affected by this command, but the provided selector includes entities");
                }
                else if(targetName.equals("@r") || targetName.equals("@a")){
                    player.sendMessage(ChatColor.RED + "@a and @r selectors are not currently supported");
                }
            }
            int removeAmount;

            if(args.length < 2){
                removeAmount = 1;
                removeRevivesFromPlayer(target, player, removeAmount, console);
            }
            else{
                if(!Utils.isInteger(args[1])){
                    player.sendMessage(ChatColor.RED + "Expected integer, found '" + args[1] + "'");
                }
                else{
                    BigInteger bigIntGiveAmount = (new BigInteger(args[1]));
                    if(bigIntGiveAmount.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0 || bigIntGiveAmount.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0){//Checks if input would exceed signed 32-bit integer limit
                        player.sendMessage(ChatColor.RED + "Invalid integer '" + args[1] + "'");
                    }
                    else {
                        removeAmount = Integer.parseInt(args[1]);
                        removeRevivesFromPlayer(target, player, removeAmount, console);
                    }
                }
            }
        }
        else if(sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender){
            if(args.length == 0){
                console.sendMessage(ChatColor.RED + "Usage: " + command.getUsage().replace("<command>", "giverevives").replace("[<player>]", "<player>"));
            }
            else {
                String targetName = args[0];
                target = Bukkit.getServer().getPlayerExact(targetName);

                if(targetName.equals("@e")){
                    console.sendMessage(ChatColor.RED + "Only players may be affected by this command, but the provided selector includes entities");
                }
                else if(targetName.equals("@r") || targetName.equals("@a") || targetName.equals("@p") || targetName.equals("@s")){
                    console.sendMessage(ChatColor.RED + "@ selectors are not currently supported from within console");
                }
                int removeAmount;

                if (args.length < 2) {
                    removeAmount = 1;
                    removeRevivesFromPlayer(target, null, removeAmount, console);
                } else {
                    if (!Utils.isInteger(args[1])) {
                        console.sendMessage(ChatColor.RED + "Expected integer, found '" + args[1] + "'");
                    } else {
                        BigInteger bigIntGiveAmount = (new BigInteger(args[1]));
                        if (bigIntGiveAmount.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0 || bigIntGiveAmount.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0) {//Checks if input would exceed signed 32-bit integer limit
                            console.sendMessage(ChatColor.RED + "Invalid integer '" + args[1] + "'");
                        } else {
                            removeAmount = Integer.parseInt(args[1]);
                            removeRevivesFromPlayer(target, null, removeAmount, console);
                        }
                    }
                }
            }
        }
        return true;
    }
}
