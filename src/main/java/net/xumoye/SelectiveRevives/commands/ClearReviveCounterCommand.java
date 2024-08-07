package net.xumoye.SelectiveRevives.commands;

import net.xumoye.SelectiveRevives.ReviveCountHandler;
import net.xumoye.SelectiveRevives.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class ClearReviveCounterCommand implements CommandExecutor {
    public static void clearPlayerRevives(Player target, Player clearer, ConsoleCommandSender console){
        CommandSender messageTo = clearer;
        String revivePluralText;
        if(clearer == null){
            messageTo = getServer().getConsoleSender();
        }
        else{
            messageTo = clearer;
        }

        if (target == null) {
            messageTo.sendMessage(ChatColor.RED + "No player was found");
        }
        else{
            if(clearer == null){
                ReviveCountHandler.reviveCounts.put(target.getUniqueId(), 0);
                messageTo.sendMessage(target.getName() + " now has 0 revives");
                if(target.isOp()){
                    target.sendMessage(ChatColor.RED + "Server cleared your revives");
                }
                else{
                    Utils.tellReviveCount(target);
                }
            }
            else if(target == clearer){
                ReviveCountHandler.reviveCounts.put(target.getUniqueId(), 0);
                console.sendMessage(clearer.getName() + " set their own revive count to 0");
                target.sendMessage(ChatColor.GREEN + "You now have 0 revives");
            }
            else {
                ReviveCountHandler.reviveCounts.put(target.getUniqueId(), 0);
                console.sendMessage(clearer.getName() + " set " + target.getName() + "'s revive count to 0");
                messageTo.sendMessage(ChatColor.GREEN + target.getName() + " now has 0 revives");
                if(target.isOp()){
                    target.sendMessage(ChatColor.RED + clearer.getName() + " set your revive count to 0");
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
        Player target;
        if(sender.isOp() && sender instanceof Player player){
            if(args.length < 1){
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
            clearPlayerRevives(target, player, console);
        }
        else if(sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender){
            if(args.length < 1){
                console.sendMessage(ChatColor.RED + "Usage: " + command.getUsage().replace("<command>", "clearrevivecounter").replace("[<player>]", "<player>"));
            }
            else{
                String targetName = args[0];
                target = Bukkit.getServer().getPlayerExact(targetName);

                if(targetName.equals("@e")){
                    console.sendMessage(ChatColor.RED + "Only players may be affected by this command, but the provided selector includes entities");
                }
                else if(targetName.equals("@r") || targetName.equals("@a") || targetName.equals("@p") || targetName.equals("@s")){
                    console.sendMessage(ChatColor.RED + "@ selectors are not currently supported from within console");
                    //todo: add @ selector support
                }
                clearPlayerRevives(target, null, console);
            }
        }
        return true;
    }
}
