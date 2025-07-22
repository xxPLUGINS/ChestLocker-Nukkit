package dev.xxplugins.chestlocker.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import dev.xxplugins.chestlocker.Loader;

public class Commands extends Command {

    public Commands() {
        super("chestlocker");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (strings.length == 1) {
            if (strings[0].equalsIgnoreCase("help")) {
                if (commandSender.hasPermission("cmd.chestlocker.help")) {
                    commandSender.sendMessage(Loader.getInstance().translateColors('&', "&c|| &8Available Commands &c||"));
                    commandSender.sendMessage(Loader.getInstance().translateColors('&', "&c/chlock reload &8> Reload the config"));
                    commandSender.sendMessage(Loader.getInstance().translateColors('&', "&c/lockchest &8> Lock a " + Loader.ITEM_NAME_2));
                    commandSender.sendMessage(Loader.getInstance().translateColors('&', "&c/unlockchest &8> Unlock a " + Loader.ITEM_NAME_2));
                } else {
                    commandSender.sendMessage(Loader.getInstance().translateColors('&', "&cYou don't have permissions to use this command"));
                }
            } else if (strings[0].equalsIgnoreCase("reload")) {
                if (commandSender.hasPermission("cmd.chestlocker.reload")) {
                    Loader.getInstance().reloadConfig();
                    commandSender.sendMessage(Loader.getInstance().translateColors('&', Loader.PREFIX + "&aConfiguration reloaded."));
                } else {
                    commandSender.sendMessage(Loader.getInstance().translateColors('&', "&cYou don't have permissions to use this command"));
                }
            } else {
                if (commandSender.hasPermission("cmd.chestlocker.help")) {
                    commandSender.sendMessage(Loader.getInstance().translateColors('&', "&c|| &8Available Commands &c||"));
                    commandSender.sendMessage(Loader.getInstance().translateColors('&', "&c/chlock reload &8> Reload the config"));
                    commandSender.sendMessage(Loader.getInstance().translateColors('&', "&c/lockchest &8> Lock a " + Loader.ITEM_NAME_2));
                    commandSender.sendMessage(Loader.getInstance().translateColors('&', "&c/unlockchest &8> Unlock a " + Loader.ITEM_NAME_2));
                } else {
                    commandSender.sendMessage(Loader.getInstance().translateColors('&', "&cYou don't have permissions to use this command"));
                }
            }
        }
        return false;
    }
}
