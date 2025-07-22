package dev.xxplugins.chestlocker.listener;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import dev.xxplugins.chestlocker.Loader;

import java.util.Objects;

public class EventListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Loader.getInstance().setCommandStatus(0, event.getPlayer().getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Loader.getInstance().endCommandSession(event.getPlayer().getName());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (block.getId() == Loader.BLOCK_ID) {
            BlockEntity chest = player.getLevel().getBlockEntity(block.getLocation());
            if (chest instanceof BlockEntityChest) {
                //Check Command status
                //0
                if (Loader.getInstance().getCommandStatus(player.getName()) == 0) {
                    if (!player.hasPermission("chestlocker.bypass")) {
                        BlockEntityChest paired = ((BlockEntityChest) chest).getPair();

                        boolean chestRegistered = Loader.getInstance().isChestRegistered(
                                chest.getLocation().getLevel().getFolderName(),
                                chest.getLocation().getX(),
                                chest.getLocation().getY(),
                                chest.getLocation().getZ()
                        );
                        boolean chestNotOwned = !Objects.equals(
                                Loader.getInstance().getChestOwner(
                                        chest.getLocation().getLevel().getFolderName(),
                                        chest.getLocation().getX(),
                                        chest.getLocation().getY(),
                                        chest.getLocation().getZ()
                                ),
                                player.getName().toLowerCase()
                        );

                        boolean pairedRegistered = false;
                        boolean pairedNotOwned = false;
                        if (paired != null) {
                            pairedRegistered = Loader.getInstance().isChestRegistered(
                                    paired.getLocation().getLevel().getFolderName(),
                                    paired.getLocation().getX(),
                                    paired.getLocation().getY(),
                                    paired.getLocation().getZ()
                            );
                            pairedNotOwned = !Objects.equals(
                                    Loader.getInstance().getChestOwner(
                                            paired.getLocation().getLevel().getFolderName(),
                                            paired.getLocation().getX(),
                                            paired.getLocation().getY(),
                                            paired.getLocation().getZ()
                                    ),
                                    player.getName().toLowerCase()
                            );
                        }

                        if ((chestRegistered && chestNotOwned) || (pairedRegistered && pairedNotOwned)) {
                            event.setCancelled(true);
                            player.sendMessage(Loader.getInstance().translateColors('&',
                                    Loader.PREFIX + "&4You aren't the owner of this " + Loader.ITEM_NAME_2 + "."));
                        }
                    }
                }

                //1
                if (Loader.getInstance().getCommandStatus(player.getName()) == 1) {
                    BlockEntityChest paired = ((BlockEntityChest) chest).getPair();

                    String w = chest.getLocation().getLevel().getFolderName();
                    int x = chest.getLocation().getFloorX();
                    int y = chest.getLocation().getFloorY();
                    int z = chest.getLocation().getFloorZ();

                    boolean registered = Loader.getInstance().isChestRegistered(w, x, y, z);
                    boolean pairedRegistered = false;
                    boolean pairedOwnedByOther = false;

                    if (paired != null) {
                        String w2 = paired.getLocation().getLevel().getFolderName();
                        int x2 = paired.getLocation().getFloorX();
                        int y2 = paired.getLocation().getFloorY();
                        int z2 = paired.getLocation().getFloorZ();
                        pairedRegistered = Loader.getInstance().isChestRegistered(w2, x2, y2, z2);
                        if (pairedRegistered) {
                            String pairedOwner = Loader.getInstance().getChestOwner(w2, x2, y2, z2);
                            if (!player.getName().equalsIgnoreCase(pairedOwner)) {
                                pairedOwnedByOther = true;
                            }
                        }
                    }

                    if (registered) {
                        String owner = Loader.getInstance().getChestOwner(w, x, y, z);
                        if (!player.getName().equalsIgnoreCase(owner) || pairedOwnedByOther) {
                            player.sendMessage(Loader.getInstance().translateColors('&', Loader.PREFIX + "&4You aren't the owner of this " + Loader.ITEM_NAME_2 + "."));
                        } else {
                            player.sendMessage(Loader.getInstance().translateColors('&', Loader.PREFIX + "&2" + Loader.ITEM_NAME + " already locked"));
                        }
                    } else {
                        Loader.getInstance().lockChest(w, x, y, z, player.getName());
                        player.sendMessage(Loader.getInstance().translateColors('&', Loader.PREFIX + "&2" + Loader.ITEM_NAME + " locked"));

                        if (paired != null && !pairedRegistered) {
                            String w2 = paired.getLocation().getLevel().getFolderName();
                            int x2 = paired.getLocation().getFloorX();
                            int y2 = paired.getLocation().getFloorY();
                            int z2 = paired.getLocation().getFloorZ();
                            Loader.getInstance().lockChest(w2, x2, y2, z2, player.getName());
                        }
                    }

                    event.setCancelled(true);
                    Loader.getInstance().setCommandStatus(0, player.getName());
                    return;
                }

                //2
                if (Loader.getInstance().getCommandStatus(player.getName()) == 2) {
                    boolean chestRegistered = Loader.getInstance().isChestRegistered(
                            chest.getLocation().getLevel().getFolderName(),
                            chest.getLocation().getX(),
                            chest.getLocation().getY(),
                            chest.getLocation().getZ()
                    );

                    if (chestRegistered) {
                        String owner = Loader.getInstance().getChestOwner(
                                chest.getLocation().getLevel().getFolderName(),
                                chest.getLocation().getX(),
                                chest.getLocation().getY(),
                                chest.getLocation().getZ()
                        );

                        if (!player.hasPermission("chestlocker.bypass")
                                && !player.getName().equalsIgnoreCase(owner)) {
                            player.sendMessage(Loader.getInstance().translateColors('&',
                                    Loader.PREFIX + "&4You aren't the owner of this " + Loader.ITEM_NAME_2 + "."));
                        } else {
                            player.sendMessage(Loader.getInstance().translateColors('&',
                                    Loader.PREFIX + "&2" + Loader.ITEM_NAME + " unlocked"));

                            Loader.getInstance().unlockChest(
                                    chest.getLocation().getLevel().getFolderName(),
                                    chest.getLocation().getX(),
                                    chest.getLocation().getY(),
                                    chest.getLocation().getZ(),
                                    owner
                            );

                            BlockEntityChest paired = ((BlockEntityChest) chest).getPair();
                            if (paired != null) {
                                boolean pairedRegistered = Loader.getInstance().isChestRegistered(
                                        paired.getLocation().getLevel().getFolderName(),
                                        paired.getLocation().getX(),
                                        paired.getLocation().getY(),
                                        paired.getLocation().getZ()
                                );
                                if (pairedRegistered) {
                                    String pairedOwner = Loader.getInstance().getChestOwner(
                                            paired.getLocation().getLevel().getFolderName(),
                                            paired.getLocation().getX(),
                                            paired.getLocation().getY(),
                                            paired.getLocation().getZ()
                                    );
                                    Loader.getInstance().unlockChest(
                                            paired.getLocation().getLevel().getFolderName(),
                                            paired.getLocation().getX(),
                                            paired.getLocation().getY(),
                                            paired.getLocation().getZ(),
                                            pairedOwner
                                    );
                                }
                            }
                        }
                    } else {
                        player.sendMessage(Loader.getInstance().translateColors('&',
                                Loader.PREFIX + "&2" + Loader.ITEM_NAME + " not registered"));
                    }

                    event.setCancelled(true);
                    Loader.getInstance().setCommandStatus(0, player.getName());
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getId() == Loader.BLOCK_ID) {
            BlockEntity tile = block.getLevel().getBlockEntity(block.getLocation());
            if (tile instanceof BlockEntityChest) {
                BlockEntityChest chest = (BlockEntityChest) tile;
                String level = chest.getLocation().getLevel().getFolderName();
                int x = chest.getLocation().getFloorX();
                int y = chest.getLocation().getFloorY();
                int z = chest.getLocation().getFloorZ();

                BlockEntityChest paired = chest.getPair();

                if (Loader.getInstance().isChestRegistered(level, x, y, z)) {
                    boolean notOwnerMain = Loader.getInstance().isChestRegistered(level, x, y, z)
                            && !Loader.getInstance().getChestOwner(level, x, y, z)
                            .equalsIgnoreCase(player.getName());
                    boolean notOwnerPaired = paired != null
                            && Loader.getInstance().isChestRegistered(
                            paired.getLocation().getLevel().getFolderName(),
                            paired.getLocation().getFloorX(),
                            paired.getLocation().getFloorY(),
                            paired.getLocation().getFloorZ())
                            && !Loader.getInstance().getChestOwner(
                                    paired.getLocation().getLevel().getFolderName(),
                                    paired.getLocation().getFloorX(),
                                    paired.getLocation().getFloorY(),
                                    paired.getLocation().getFloorZ())
                            .equalsIgnoreCase(player.getName());

                    if (!player.hasPermission("chestlocker.bypass") && (notOwnerMain || notOwnerPaired)) {
                        player.sendMessage(Loader.getInstance().translateColors('&',
                                Loader.PREFIX + "&4You aren't the owner of this " + Loader.ITEM_NAME_2 + "."));
                        event.setCancelled(true);
                    } else {
                        String ownerMain = Loader.getInstance().getChestOwner(level, x, y, z);
                        Loader.getInstance().unlockChest(level, x, y, z, ownerMain);

                        if (paired != null && Loader.getInstance().isChestRegistered(
                                paired.getLocation().getLevel().getFolderName(),
                                paired.getLocation().getFloorX(),
                                paired.getLocation().getFloorY(),
                                paired.getLocation().getFloorZ())) {

                            String pairedOwner = Loader.getInstance().getChestOwner(
                                    paired.getLocation().getLevel().getFolderName(),
                                    paired.getLocation().getFloorX(),
                                    paired.getLocation().getFloorY(),
                                    paired.getLocation().getFloorZ());
                            Loader.getInstance().unlockChest(
                                    paired.getLocation().getLevel().getFolderName(),
                                    paired.getLocation().getFloorX(),
                                    paired.getLocation().getFloorY(),
                                    paired.getLocation().getFloorZ(),
                                    pairedOwner);
                        }
                    }
                }
            }
        }
    }
}
