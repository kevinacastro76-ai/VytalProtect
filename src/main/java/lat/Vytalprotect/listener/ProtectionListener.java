package lat.vytalprotect.listener;

import lat.vytalprotect.VytalProtectPlugin;
import lat.vytalprotect.core.Protection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Iterator;

public class ProtectionListener implements Listener {
    private final VytalProtectPlugin plugin;
    public ProtectionListener(VytalProtectPlugin plugin){this.plugin=plugin;}
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onPlace(BlockPlaceEvent e){
        Player player=e.getPlayer(); Block b=e.getBlockPlaced(); ItemStack hand=e.getItemInHand();
        if(isProtectionItem(hand)){
            if(plugin.getProtectionManager().countOwned(player.getUniqueId())>=plugin.getConfig().getInt("settings.max-protections-per-player",5)&&!player.hasPermission("vytalprotect.admin")){e.setCancelled(true);player.sendMessage(plugin.message("protection-limit"));return;}
            Protection p=plugin.getProtectionManager().create(b.getLocation(),player);
            if(p==null){e.setCancelled(true);player.sendMessage(plugin.message("protection-overlap"));return;}
            plugin.getHolograms().spawn(p);player.sendMessage(plugin.message("protection-created"));return;
        }
        Protection p=plugin.getProtectionManager().at(b.getLocation());
        if(p!=null&&!plugin.getProtectionManager().hasPermission(p,player.getUniqueId(),"BUILD")){e.setCancelled(true);if(p.getBanned().contains(player.getUniqueId()))player.sendMessage(plugin.message("player-banned"));}
    }
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBreak(BlockBreakEvent e){
        Protection p=plugin.getProtectionManager().at(e.getBlock().getLocation());if(p==null)return;
        if(e.getBlock().getX()==p.getX()&&e.getBlock().getY()==p.getY()&&e.getBlock().getZ()==p.getZ()){
            if(!p.isOwner(e.getPlayer().getUniqueId())&&!e.getPlayer().hasPermission("vytalprotect.admin")){e.setCancelled(true);e.getPlayer().sendMessage(plugin.message("protection-not-owner"));return;}
            plugin.getProtectionManager().remove(p);e.getPlayer().sendMessage(plugin.message("protection-removed"));return;
        }
        if(!plugin.getProtectionManager().hasPermission(p,e.getPlayer().getUniqueId(),"BUILD")){e.setCancelled(true);if(p.getBanned().contains(e.getPlayer().getUniqueId()))e.getPlayer().sendMessage(plugin.message("player-banned"));}
    }
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onInteract(PlayerInteractEvent e){if(e.getClickedBlock()==null)return;Protection p=plugin.getProtectionManager().at(e.getClickedBlock().getLocation());if(p==null)return;
        if(e.getClickedBlock().getX()==p.getX()&&e.getClickedBlock().getY()==p.getY()&&e.getClickedBlock().getZ()==p.getZ()&&e.getAction().name().contains("RIGHT_CLICK")){e.setCancelled(true);if(p.isOwner(e.getPlayer().getUniqueId())||p.isMember(e.getPlayer().getUniqueId())||e.getPlayer().hasPermission("vytalprotect.admin"))plugin.getMenus().openMain(e.getPlayer(),p);else e.getPlayer().sendMessage(plugin.message("protection-not-owner"));return;}
        if(p.getBanned().contains(e.getPlayer().getUniqueId())&&!p.isOwner(e.getPlayer().getUniqueId())){e.setCancelled(true);e.getPlayer().sendMessage(plugin.message("player-banned"));return;}
        Material type=e.getClickedBlock().getType();String name=type.name();boolean container=name.contains("CHEST")||name.contains("FURNACE")||name.contains("DISPENSER")||name.contains("DROPPER")||name.contains("HOPPER")||name.contains("BREWING")||name.contains("ENDER");
        String perm=container?"CONTAINERS":"INTERACT";if(!plugin.getProtectionManager().hasPermission(p,e.getPlayer().getUniqueId(),perm))e.setCancelled(true);
    }
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBucketEmpty(PlayerBucketEmptyEvent e){Protection p=plugin.getProtectionManager().at(e.getBlockClicked().getRelative(e.getBlockFace()).getLocation());if(p!=null&&!plugin.getProtectionManager().hasPermission(p,e.getPlayer().getUniqueId(),"BUILD"))e.setCancelled(true);}
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBucketFill(PlayerBucketFillEvent e){Protection p=plugin.getProtectionManager().at(e.getBlockClicked().getLocation());if(p!=null&&!plugin.getProtectionManager().hasPermission(p,e.getPlayer().getUniqueId(),"BUILD"))e.setCancelled(true);}
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onDamage(EntityDamageByEntityEvent e){Entity damager=e.getDamager();if(!(damager instanceof Player))return;Player player=(Player)damager;Protection p=plugin.getProtectionManager().at(e.getEntity().getLocation());if(p!=null&&!plugin.getProtectionManager().hasPermission(p,player.getUniqueId(),"PVP"))e.setCancelled(true);}
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityExplode(EntityExplodeEvent e){filterExplosion(e.blockList());}
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockBurn(BlockBurnEvent e){if(plugin.getProtectionManager().at(e.getBlock().getLocation())!=null)e.setCancelled(true);}
    private void filterExplosion(java.util.List<Block> blocks){Iterator<Block> it=blocks.iterator();while(it.hasNext())if(plugin.getProtectionManager().at(it.next().getLocation())!=null)it.remove();}
    @EventHandler public void onChat(AsyncPlayerChatEvent e){final Player p=e.getPlayer();final String message=e.getMessage();if(plugin.getMenus().hasPending(p)){e.setCancelled(true);Bukkit.getScheduler().runTask(plugin,new Runnable(){public void run(){plugin.getMenus().handleChat(p,message);}});}}
    @EventHandler public void onMove(PlayerMoveEvent e){if(e.getTo()==null||e.getFrom().getBlockX()==e.getTo().getBlockX()&&e.getFrom().getBlockY()==e.getTo().getBlockY()&&e.getFrom().getBlockZ()==e.getTo().getBlockZ())return;Protection from=plugin.getProtectionManager().at(e.getFrom()),to=plugin.getProtectionManager().at(e.getTo());if(from==to)return;
        if(from!=null&&plugin.getEventsConfig().getBoolean("show-leave-message",true)){String owner=Bukkit.getOfflinePlayer(from.getOwner()).getName();e.getPlayer().sendMessage(plugin.color(plugin.getEventsConfig().getString("leave","").replace("%owner%",owner==null?"Desconocido":owner)));}
        if(to!=null){if(to.getBanned().contains(e.getPlayer().getUniqueId())&&!to.isOwner(e.getPlayer().getUniqueId())){e.setTo(e.getFrom());e.getPlayer().sendMessage(plugin.message("player-banned"));return;}if(plugin.getEventsConfig().getBoolean("show-enter-message",true)){String owner=Bukkit.getOfflinePlayer(to.getOwner()).getName();e.getPlayer().sendMessage(plugin.color(plugin.getEventsConfig().getString("enter","").replace("%owner%",owner==null?"Desconocido":owner)));}}
    }
    private boolean isProtectionItem(ItemStack item){if(item==null||item.getType()==Material.AIR||!item.hasItemMeta())return false;ItemMeta meta=item.getItemMeta();if(!meta.hasDisplayName())return false;String expected=ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("settings.protection-item-name","&d&lVytalProtect"));return item.getType().name().equalsIgnoreCase(plugin.getConfig().getString("settings.protection-block","OBSIDIAN"))&&meta.getDisplayName().equals(expected);}
          }
                                                                                                                                                                                                                                                                                   
