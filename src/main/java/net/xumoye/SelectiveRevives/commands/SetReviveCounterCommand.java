package net.xumoye.SelectiveRevives.commands;

import net.xumoye.SelectiveRevives.ReviveCountHandler;
import net.xumoye.SelectiveRevives.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.math.BigInteger;

import static org.bukkit.Bukkit.getServer;

public class SetReviveCounterCommand implements CommandExecutor {
    public static void setPlayerRevives(Player target, Player setter, int setAmount, ConsoleCommandSender console){
        CommandSender messageTo = setter;
        String revivePluralText;
        if(setter == null){
            messageTo = getServer().getConsoleSender();
        }
        else{
            messageTo = setter;
        }

        if(setAmount == 1){
            revivePluralText = " revive";
        }
        else{
            revivePluralText = " revives";
        }


        if(setAmount < 0){
            messageTo.sendMessage(ChatColor.RED + "Integer must not be less than 0, found " + Integer.toString(setAmount));
        }
        else if (target == null) {
            messageTo.sendMessage(ChatColor.RED + "No player was found");
        }
        else{
            if(setter == null){
                ReviveCountHandler.reviveCounts.put(target.getUniqueId(), setAmount);
                messageTo.sendMessage(target.getName() + " now has " + setAmount + revivePluralText);
                if(target.isOp()){
                    target.sendMessage(ChatColor.GREEN + "Server set your revive count to " + setAmount);
                }
                else{
                    Utils.tellReviveCount(target);
                }
            }
            else if(target == setter){
                ReviveCountHandler.reviveCounts.put(target.getUniqueId(), setAmount);
                console.sendMessage(setter.getName() + " set their own revive count to " + setAmount);
                target.sendMessage(ChatColor.GREEN + "You now have " + setAmount + revivePluralText);
            }
            else {
                ReviveCountHandler.reviveCounts.put(target.getUniqueId(), setAmount);
                console.sendMessage(setter.getName() + " set " + target.getName() + "'s revive count to  " + setAmount);
                messageTo.sendMessage(ChatColor.GREEN + target.getName() + " now has " + setAmount + revivePluralText);
                if(target.isOp()){
                    target.sendMessage(ChatColor.GREEN + setter.getName() + " set your revive count to " + setAmount);
                }
                else{
                    Utils.tellReviveCount(target);
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        ConsoleCommandSender console = getServer().getConsoleSender();
        if(sender.isOp() && sender instanceof Player player){
            if(args.length < 2){
                player.sendMessage(ChatColor.RED + "Usage: " + command.getUsage().replace("<command>", "setrevivecounter"));
            }
            else{
                String targetName = args[0];
                Player target = Bukkit.getServer().getPlayerExact(targetName);
                int setAmount;

                if(targetName.equals("@p") || targetName.equals("@s")){
                    target = player;
                }
                else if(targetName.equals("@e")){
                    player.sendMessage(ChatColor.RED + "Only players may be affected by this command, but the provided selector includes entities");
                }
                else if(targetName.equals("@r") || targetName.equals("@a")){
                    player.sendMessage(ChatColor.RED + "@a and @r selectors are not currently supported");
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
                        setAmount = Integer.parseInt(args[1]);
                        setPlayerRevives(target, player, setAmount, console);
                    }
                }
            }
        }
        else if(sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender){
            if(args.length < 2){
                console.sendMessage(ChatColor.RED + "Usage: " + command.getUsage().replace("<command>", "setrevives"));
            }
            else{
                String targetName = args[0];
                Player target = Bukkit.getServer().getPlayerExact(targetName);
                int setAmount;

                if(targetName.equals("@e")){
                    console.sendMessage(ChatColor.RED + "Only players may be affected by this command, but the provided selector includes entities");
                }
                else if(targetName.equals("@r") || targetName.equals("@a") || targetName.equals("@p") || targetName.equals("@s")){
                    console.sendMessage(ChatColor.RED + "@ selectors are not currently supported from within console");
                }
                if(!Utils.isInteger(args[1])){
                    console.sendMessage(ChatColor.RED + "Expected integer, found '" + args[1] + "'");
                }
                else{
                    BigInteger bigIntGiveAmount = (new BigInteger(args[1]));
                    if(bigIntGiveAmount.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0 || bigIntGiveAmount.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0){//Checks if input would exceed signed 32-bit integer limit
                        console.sendMessage(ChatColor.RED + "Invalid integer '" + args[1] + "'");
                    }
                    else {
                        setAmount = Integer.parseInt(args[1]);
                        setPlayerRevives(target, null, setAmount, console);
                    }
                }
            }
        }
        return true;
    }
}
