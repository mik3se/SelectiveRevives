package net.xumoye.SelectiveRevives.commands;

import net.xumoye.SelectiveRevives.ReviveCountHandler;
import net.xumoye.SelectiveRevives.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.math.BigInteger;

import static org.bukkit.Bukkit.getServer;

public class GiveRevivesCommand implements CommandExecutor {
    public static void giveRevivesToPlayer(Player target, Player giver, int giveAmount, ConsoleCommandSender console, boolean updateCounter){
        CommandSender messageTo;
        String revivePluralTextGive;
        String revivePluralTextAbsolute;
        if(giver == null){
            messageTo = getServer().getConsoleSender();
        }
        else{
            messageTo = giver;
        }
        if(giveAmount == 1){
            revivePluralTextGive = " revive";
        }
        else{
            revivePluralTextGive = " revives";
        }


        if(giveAmount < 1){
            messageTo.sendMessage(ChatColor.RED + "Integer must not be less than 1, found " + Integer.toString(giveAmount));
        }
        else if (target == null) {
            messageTo.sendMessage(ChatColor.RED + "No player was found");
        }
        else if (target == giver) {
            if(giver.isOp()){
                ReviveCountHandler.reviveCounts.put(target.getUniqueId(), ReviveCountHandler.reviveCounts.get(target.getUniqueId()) + giveAmount);
                if(giveAmount == 1){
                    target.sendMessage(ChatColor.GREEN + "You've given yourself a revive");
                    console.sendMessage(giver.getName() + " gave themself a revive");
                }
                else{
                    target.sendMessage(ChatColor.GREEN + "You've given yourself " + giveAmount + revivePluralTextGive);
                    console.sendMessage(giver.getName() + " gave themself " + giveAmount + revivePluralTextGive);
                }
                Utils.tellReviveCount(target);
            }
            else{
                messageTo.sendMessage(ChatColor.RED + "You must be a server operator to give yourself revives");
            }
        }
        else if (target == giver && updateCounter) {
            messageTo.sendMessage(ChatColor.RED + "You cannot give yourself revives with updateCounter argument");
        }
        else if ((long) ReviveCountHandler.reviveCounts.get(target.getUniqueId()) + (long)giveAmount > Integer.MAX_VALUE) {//Checks if amount of revives would exceed the signed 32-bit integer limit
            messageTo.sendMessage(ChatColor.RED + target.getName() + " cannot receive this many revives, revive count would exceed integer limit");
        }
        else{
            if(ReviveCountHandler.reviveCounts.get(target.getUniqueId()) + giveAmount == 1){
                revivePluralTextAbsolute = " revive";
            }
            else{
                revivePluralTextAbsolute = " revives";
            }

            if(giver == null){
                ReviveCountHandler.reviveCounts.put(target.getUniqueId(), ReviveCountHandler.reviveCounts.get(target.getUniqueId()) + giveAmount);
                messageTo.sendMessage(target.getName() + " now has " + ReviveCountHandler.reviveCounts.get(target.getUniqueId()).toString() + " revives");
                if(target.isOp()){
                    if(giveAmount == 1){
                        target.sendMessage(ChatColor.GREEN + "Server gave you a revive");
                    }
                    else{
                        target.sendMessage(ChatColor.GREEN + "Server gave you " + giveAmount + revivePluralTextGive);
                    }
                }
                Utils.tellReviveCount(target);
            }
            else if (giver.isOp() && !updateCounter) {
                ReviveCountHandler.reviveCounts.put(target.getUniqueId(), ReviveCountHandler.reviveCounts.get(target.getUniqueId()) + giveAmount);
                console.sendMessage(giver.getName() + " gave " + target.getName() + " " + giveAmount + revivePluralTextGive);
                messageTo.sendMessage(ChatColor.GREEN + target.getName() + " now has " + ReviveCountHandler.reviveCounts.get(target.getUniqueId()).toString() + revivePluralTextAbsolute);
                console.sendMessage(target.getName() + " now has " + ReviveCountHandler.reviveCounts.get(target.getUniqueId()).toString() + revivePluralTextAbsolute);
                Utils.tellReviveCount(target);
            }
            else if (ReviveCountHandler.reviveCounts.get(giver.getUniqueId()) >= giveAmount) {
                ReviveCountHandler.reviveCounts.put(target.getUniqueId(), ReviveCountHandler.reviveCounts.get(target.getUniqueId()) + giveAmount);
                ReviveCountHandler.reviveCounts.put(giver.getUniqueId(), ReviveCountHandler.reviveCounts.get(giver.getUniqueId()) - giveAmount);
                if(giveAmount == 1){
                    messageTo.sendMessage(ChatColor.GREEN + "Successfully gave " + target.getName() + " a revive");
                }
                else{
                    messageTo.sendMessage(ChatColor.GREEN + "Successfully gave " + target.getName() + " " + giveAmount + revivePluralTextGive);
                }
                console.sendMessage(giver.getName() + " gave " + target.getName() + " " + giveAmount + revivePluralTextGive);
                if(giveAmount == 1){
                    target.sendMessage(ChatColor.GREEN + giver.getName() + " gave you a revive");
                }
                else{
                    target.sendMessage(ChatColor.GREEN + giver.getName() + " gave you " + giveAmount + revivePluralTextGive);
                }
                console.sendMessage(target.getName() + " now has " + ReviveCountHandler.reviveCounts.get(target.getUniqueId()).toString() + revivePluralTextAbsolute);
                Utils.tellReviveCount(giver);
                Utils.tellReviveCount(target);
            }
            else {
                messageTo.sendMessage(ChatColor.RED + "You do not have enough revives to do this");
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        ConsoleCommandSender console = getServer().getConsoleSender();
        Player target;
        if(sender instanceof Player player){
            if(args.length == 0  && player.isOp()){
                target = player;
            }
            if(args.length == 0  && !player.isOp()){
                player.sendMessage(ChatColor.RED + "Usage: " + command.getUsage().replace("<command>", "giverevives"));
            }
            else{
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
                        //todo: add @a and @r support
                    }
                }
                int giveAmount;

                if(args.length < 2){
                    giveAmount = 1;
                    giveRevivesToPlayer(target, player, giveAmount, console, false);
                }
                else{
                    boolean updateCounter;
                    if(args.length > 2 && args[2].equalsIgnoreCase("updatecounter")){
                        updateCounter = true;
                    }
                    else{
                        updateCounter = false;
                    }
                    if(!Utils.isInteger(args[1])){
                        player.sendMessage(ChatColor.RED + "Expected integer, found '" + args[1] + "'");
                    }
                    else{
                        BigInteger bigIntGiveAmount = (new BigInteger(args[1]));
                        if(bigIntGiveAmount.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0 || bigIntGiveAmount.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0){//Checks if input would exceed signed 32-bit integer limit
                            player.sendMessage(ChatColor.RED + "Invalid integer '" + args[1] + "'");
                        }
                        else {
                            giveAmount = Integer.parseInt(args[1]);
                            giveRevivesToPlayer(target, player, giveAmount, console, updateCounter);
                        }
                    }
                }
            }
        }
        else if(sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender){
            if(args.length == 0){
                console.sendMessage(ChatColor.RED + "Usage: " + command.getUsage().replace("<command>", "giverevives").replace("[<player>]", "<player>"));
            }
            else if(args.length == 1){
                String targetName = args[0];
                target = Bukkit.getServer().getPlayerExact(targetName);

                if(targetName.equals("@e")){
                    console.sendMessage(ChatColor.RED + "Only players may be affected by this command, but the provided selector includes entities");
                }
                else if(targetName.equals("@r") || targetName.equals("@a") || targetName.equals("@p") || targetName.equals("@s")){
                    console.sendMessage(ChatColor.RED + "@ selectors are not currently supported from within console");
                    //todo: add @ selector support
                }
                else{
                    giveRevivesToPlayer(target, null, 1, console, false);
                }
            }
            else{
                String targetName = args[0];
                target = Bukkit.getServer().getPlayerExact(targetName);
                int giveAmount;


                if(!Utils.isInteger(args[1])){
                    console.sendMessage(ChatColor.RED + "Expected integer, found '" + args[1] + "'");
                }
                else{
                    BigInteger bigIntGiveAmount = (new BigInteger(args[1]));
                    if(bigIntGiveAmount.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0 || bigIntGiveAmount.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0){//Checks if input would exceed signed 32-bit integer limit
                        console.sendMessage(ChatColor.RED + "Invalid integer '" + args[1] + "'");
                    }
                    else {
                        giveAmount = Integer.parseInt(args[1]);

                        if(targetName.equals("@e")){
                            console.sendMessage(ChatColor.RED + "Only players may be affected by this command, but the provided selector includes entities");
                        }
                        else if(targetName.equals("@r") || targetName.equals("@a") || targetName.equals("@p") || targetName.equals("@s")){
                            console.sendMessage(ChatColor.RED + "@ selectors are not currently supported from within console");
                            //todo: add @a and @r support
                        }
                        else{
                            giveRevivesToPlayer(target, null, giveAmount, console, false);
                        }
                    }
                }
            }
        }
        return true;
    }
}
