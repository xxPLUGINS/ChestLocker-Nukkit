package dev.xxplugins.chestlocker.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import dev.xxplugins.chestlocker.Loader;

public class LockChestCommand extends Command {

    public LockChestCommand() {
        super("lockchest");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (commandSender.hasPermission("cmd.lockchest")) {
            if (commandSender instanceof Player) {
                if (Loader.getInstance().getCommandStatus(commandSender.getName()) == 0 || Loader.getInstance().getCommandStatus(commandSender.getName()) == 2) {
                    Loader.getInstance().setCommandStatus(1, commandSender.getName());
                    commandSender.sendMessage(Loader.getInstance().translateColors('&', Loader.PREFIX + "&2" + Loader.ITEM_NAME + " lock command enabled. Click the " + Loader.ITEM_NAME_2 + " to lock."));
                } else {
                    Loader.getInstance().setCommandStatus(0, commandSender.getName());
                    commandSender.sendMessage(Loader.getInstance().translateColors('&', Loader.PREFIX + "&4" + Loader.ITEM_NAME + " lock command disabled."));
                }
            } else {
                commandSender.sendMessage(Loader.getInstance().translateColors('&', Loader.PREFIX + "&cYou can only perform this command as a player"));
            }
        } else {
            commandSender.sendMessage(Loader.getInstance().translateColors('&', "&cYou don't have permissions to use this command"));
        }
        return false;
    }
}
