package net.xumoye.SelectiveRevives.commands;

import net.xumoye.SelectiveRevives.ReviveCountHandler;
import net.xumoye.SelectiveRevives.SelectiveRevives;
import net.xumoye.SelectiveRevives.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.math.BigInteger;
import java.util.Objects;

import static org.bukkit.Bukkit.getServer;

public class DefaultReviveCountCommand implements CommandExecutor {
    protected final SelectiveRevives plugin;

    public DefaultReviveCountCommand(SelectiveRevives plugin){
        this.plugin = plugin;
    }

    public static void setDefaultReviveCount(Player setter, int setAmount, ConsoleCommandSender console, Plugin plugin){
        CommandSender messageTo;
        if(setter == null){
            messageTo = getServer().getConsoleSender();
        }
        else{
            messageTo = setter;
        }

        if(setAmount < 0){
            messageTo.sendMessage(ChatColor.RED + "Integer must not be less than 0, found " + Integer.toString(setAmount));
        }
        else{
            Utils.startingReviveCount = setAmount;
            plugin.getConfig().set("starting-revives", setAmount);
            plugin.saveConfig();
            messageTo.sendMessage("Set default revive count to " + setAmount);
            if(setter != null){
                console.sendMessage(setter.getName() + " set the default revive count to " + setAmount);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        ConsoleCommandSender console = getServer().getConsoleSender();
        if(sender.isOp() && sender instanceof Player player){
            if(args.length > 1 && Objects.equals(args[0], "set")){
                if(!Utils.isInteger(args[1])){
                    player.sendMessage(ChatColor.RED + "Expected integer, found '" + args[1] + "'");
                }
                else{
                    int setAmount;
                    BigInteger bigIntGiveAmount = (new BigInteger(args[1]));
                    if(bigIntGiveAmount.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0 || bigIntGiveAmount.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0){//Checks if input would exceed signed 32-bit integer limit
                        player.sendMessage(ChatColor.RED + "Invalid integer '" + args[1] + "'");
                    }
                    else {
                        setAmount = Integer.parseInt(args[1]);
                        setDefaultReviveCount(player, setAmount, console, this.plugin);
                    }
                }
            }
            else if(args.length > 0 && Objects.equals(args[0], "get")){
                player.sendMessage("The current default revive count is " + this.plugin.getConfig().getInt("starting-revives"));
            }
            else {
                player.sendMessage(ChatColor.RED + "Usage: " + command.getUsage().replace("<command>", "defaultrevivecount"));
            }
        }
        else if(sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender){
            if(args.length > 1 && Objects.equals(args[0], "set")){
                if(!Utils.isInteger(args[1])){
                    console.sendMessage(ChatColor.RED + "Expected integer, found '" + args[1] + "'");
                }
                else{
                    int setAmount;
                    BigInteger bigIntGiveAmount = (new BigInteger(args[1]));
                    if(bigIntGiveAmount.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0 || bigIntGiveAmount.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0){//Checks if input would exceed signed 32-bit integer limit
                        console.sendMessage(ChatColor.RED + "Invalid integer '" + args[1] + "'");
                    }
                    else {
                        setAmount = Integer.parseInt(args[1]);
                        setDefaultReviveCount(null, setAmount, console, this.plugin);
                    }
                }
            }
            else if(args.length > 0 && Objects.equals(args[0], "get")){
                console.sendMessage("The current default revive count is " + this.plugin.getConfig().getInt("starting-revives"));
            }
            else {
                console.sendMessage(ChatColor.RED + "Usage: " + command.getUsage().replace("<command>", "defaultrevivecount"));
            }
        }
        return true;
    }
}
