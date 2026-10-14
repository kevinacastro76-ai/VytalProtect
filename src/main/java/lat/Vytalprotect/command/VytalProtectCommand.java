package lat.vytalprotect.command;

import lat.vytalprotect.VytalProtectPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;

public class VytalProtectCommand implements CommandExecutor {
    private final VytalProtectPlugin plugin;
    public VytalProtectCommand(VytalProtectPlugin plugin){this.plugin=plugin;}
    @Override public boolean onCommand(CommandSender sender,Command command,String label,String[] args){
        if(args.length==0){
            sender.sendMessage(plugin.color("&d&lVytalProtect &7v"+plugin.getDescription().getVersion()));
            sender.sendMessage(plugin.color("&f/vp menu &7- Abrir el menú de tu protección"));
            sender.sendMessage(plugin.color("&f/vp give <jugador> [cantidad] &7- Entregar bloques"));
            sender.sendMessage(plugin.color("&f/vp reload &7- Recargar configuración"));return true;
        }
        if(args[0].equalsIgnoreCase("menu")){if(!(sender instanceof Player)){sender.sendMessage(plugin.message("player-only"));return true;}plugin.getMenus().openCurrent((Player)sender);return true;}
        if(args[0].equalsIgnoreCase("give")){
            if(!sender.hasPermission("vytalprotect.give")){sender.sendMessage(plugin.message("no-permission"));return true;}
            if(args.length<2){sender.sendMessage(plugin.color("&cUso: /vp give <jugador> [cantidad]"));return true;}
            Player target=Bukkit.getPlayer(args[1]);if(target==null){sender.sendMessage(plugin.message("player-not-found"));return true;}
            int amount=1;if(args.length>=3)try{amount=Math.max(1,Math.min(64,Integer.parseInt(args[2])));}catch(NumberFormatException ignored){}
            Material material;try{material=Material.valueOf(plugin.getConfig().getString("settings.protection-block","OBSIDIAN").toUpperCase());}catch(Exception ex){material=Material.OBSIDIAN;}
            ItemStack item=new ItemStack(material,amount);ItemMeta meta=item.getItemMeta();meta.setDisplayName(plugin.color(plugin.getConfig().getString("settings.protection-item-name","&d&lVytalProtect")));List<String> lore=new ArrayList<String>();for(String line:plugin.getConfig().getStringList("settings.protection-item-lore"))lore.add(plugin.color(line));meta.setLore(lore);item.setItemMeta(meta);target.getInventory().addItem(item);
            target.sendMessage(plugin.message("item-received"));sender.sendMessage(plugin.message("item-given").replace("%player%",target.getName()));return true;
        }
        if(args[0].equalsIgnoreCase("reload")){if(!sender.hasPermission("vytalprotect.admin")){sender.sendMessage(plugin.message("no-permission"));return true;}plugin.reloadConfig();plugin.loadCustomConfigs();sender.sendMessage(plugin.message("reload"));return true;}
        if(args[0].equalsIgnoreCase("info")){if(!(sender instanceof Player)){sender.sendMessage(plugin.message("player-only"));return true;}lat.vytalprotect.core.Protection p=plugin.getProtectionManager().at(((Player)sender).getLocation());if(p==null){sender.sendMessage(plugin.message("protection-not-found"));return true;}sender.sendMessage(plugin.color("&dVytalProtect &7ID: &f"+p.getId()+" &7Dueño: &f"+Bukkit.getOfflinePlayer(p.getOwner()).getName()+" &7Miembros: &f"+p.getMembers().size()+" &7Radio: &f"+p.getRadius()));return true;}
        sender.sendMessage(plugin.color("&cSubcomando desconocido. Usa /vp"));return true;
    }
                                                                                                                                                                                                                }
