package dev.xxplugins.chestlocker;

import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import dev.xxplugins.chestlocker.commands.Commands;
import dev.xxplugins.chestlocker.commands.LockChestCommand;
import dev.xxplugins.chestlocker.commands.UnlockChestCommand;
import dev.xxplugins.chestlocker.listener.EventListener;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Loader extends PluginBase {
    //Instance
    private static Loader instance;

    // Prefix
    public static final String PREFIX = "&8[&cChestLocker&8]&r ";

    // File / Directory
    public static final String _FILE = ".";
    public static final String _DIRECTORY = "chests" + File.separator;

    // Item Info
    public static final String ITEM_NAME = "Chest";
    public static final String ITEM_NAME_2 = "chest";
    public static final int BLOCK_ID = BlockID.CHEST;

    // Variablen
    private final Map<String, Integer> status = new HashMap<>();
    private File dataFolderFile;

    @Override
    public void onEnable() {
        instance = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        File chestDir = new File(getDataFolder(), _DIRECTORY);
        if (!chestDir.exists()) {
            chestDir.mkdirs();
        }

        saveDefaultConfig();

        this.dataFolderFile = getDataFolder();

        Server.getInstance().getPluginManager().registerEvents(new EventListener(), this);
        Server.getInstance().getCommandMap().register("chestlocker", new Commands());
        Server.getInstance().getCommandMap().register("lockchest", new LockChestCommand());
        Server.getInstance().getCommandMap().register("unlockchest", new UnlockChestCommand());
    }

    public static Loader getInstance() {
        return instance;
    }

    public String translateColors(char symbol, String message) {
        return message
                .replace(symbol + "0", TextFormat.BLACK.toString())
                .replace(symbol + "1", TextFormat.DARK_BLUE.toString())
                .replace(symbol + "2", TextFormat.DARK_GREEN.toString())
                .replace(symbol + "3", TextFormat.DARK_AQUA.toString())
                .replace(symbol + "4", TextFormat.DARK_RED.toString())
                .replace(symbol + "5", TextFormat.DARK_PURPLE.toString())
                .replace(symbol + "6", TextFormat.GOLD.toString())
                .replace(symbol + "7", TextFormat.GRAY.toString())
                .replace(symbol + "8", TextFormat.DARK_GRAY.toString())
                .replace(symbol + "9", TextFormat.BLUE.toString())
                .replace(symbol + "a", TextFormat.GREEN.toString())
                .replace(symbol + "b", TextFormat.AQUA.toString())
                .replace(symbol + "c", TextFormat.RED.toString())
                .replace(symbol + "d", TextFormat.LIGHT_PURPLE.toString())
                .replace(symbol + "e", TextFormat.YELLOW.toString())
                .replace(symbol + "f", TextFormat.WHITE.toString())
                .replace(symbol + "k", TextFormat.OBFUSCATED.toString())
                .replace(symbol + "l", TextFormat.BOLD.toString())
                .replace(symbol + "m", TextFormat.STRIKETHROUGH.toString())
                .replace(symbol + "n", TextFormat.UNDERLINE.toString())
                .replace(symbol + "o", TextFormat.ITALIC.toString())
                .replace(symbol + "r", TextFormat.RESET.toString());
    }

    public void setCommandStatus(int intValue, String playerName) {
        if (intValue >= 0 && intValue <= 3) {
            status.put(playerName.toLowerCase(Locale.ROOT), intValue);
        }
    }

    public int getCommandStatus(String playerName) {
        return status.getOrDefault(playerName.toLowerCase(Locale.ROOT), 0);
    }

    public void endCommandSession(String playerName) {
        status.remove(playerName.toLowerCase(Locale.ROOT));
    }

    public boolean isChestRegistered(String level, double x, double y, double z) {
        File f = new File(
                dataFolderFile,
                _DIRECTORY + level.toLowerCase(Locale.ROOT) + File.separator +
                        (x + _FILE + y + _FILE + z + ".yml").toLowerCase(Locale.ROOT)
        );
        return f.exists();
    }

    public String getChestOwner(String level, double x, double y, double z) {
        File f = new File(
                dataFolderFile,
                _DIRECTORY + level.toLowerCase(Locale.ROOT) + File.separator +
                        (x + _FILE + y + _FILE + z + ".yml").toLowerCase(Locale.ROOT)
        );
        if (f.exists()) {
            Config chest = new Config(f, Config.YAML);
            return chest.getString("player", "").toLowerCase(Locale.ROOT);
        }
        return null; //Failed: Chest not registered
    }

    public boolean lockChest(String level, double x, double y, double z, String playerName) {
        File dir = new File(dataFolderFile, _DIRECTORY + level.toLowerCase(Locale.ROOT));
        if (!dir.exists()) dir.mkdirs();

        File f = new File(dir, (x + _FILE + y + _FILE + z + ".yml").toLowerCase(Locale.ROOT));
        if (f.exists()) {
            return false; //Error: Chest already registered
        }
        Config chest = new Config(f, Config.YAML);
        chest.set("player", playerName);
        chest.save();
        return true; //Success!
    }

    /**
     * @return 2 = success, 1 = Player is not owner of chest, 0 = Chest not registered
     */
    public int unlockChest(String level, double x, double y, double z, String playerName) {
        File f = new File(
                dataFolderFile,
                _DIRECTORY + level.toLowerCase(Locale.ROOT) + File.separator +
                        (x + _FILE + y + _FILE + z + ".yml").toLowerCase(Locale.ROOT)
        );
        if (!f.exists()) {
            return 0;
        }

        Config chest = new Config(f, Config.YAML);
        String owner = chest.getString("player", "");
        if (owner.equalsIgnoreCase(playerName)) {
            if (f.delete()) {
                return 2; //Success!
            }
        } else {
            return 1; //Failed: Player is not owner of chest
        }
        return 0; //Failed: Chest not registered
    }
}