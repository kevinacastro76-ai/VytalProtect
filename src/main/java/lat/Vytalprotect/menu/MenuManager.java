package lat.vytalprotect.menu;

import lat.vytalprotect.VytalProtectPlugin;
import lat.vytalprotect.core.Protection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;

public class MenuManager {
    private final VytalProtectPlugin plugin;
    private final Map<UUID,String> selectedProtection=new HashMap<UUID,String>();
    private final Map<UUID,String> pendingAction=new HashMap<UUID,String>();
    public MenuManager(VytalProtectPlugin plugin){this.plugin=plugin;}
    public void openMain(Player player, Protection p){
        selectedProtection.put(player.getUniqueId(),p.getId());
        Inventory inv=Bukkit.createInventory(null,27, color(plugin.getMenusConfig().getString("main.title","&8Protección: &d%owner%").replace("%owner%",name(p.getOwner()))));
        ItemStack filler=item(Material.STAINED_GLASS_PANE, (short) purplePane(), " ", Collections.<String>emptyList());
        for(int i=0;i<27;i++)inv.setItem(i,filler);
        inv.setItem(slot("main.members-slot",10),item(Material.SKULL_ITEM,(short)3,"&d&lMiembros",Arrays.asList("&7Gestionar miembros","&eClic para abrir")));
        inv.setItem(slot("main.permissions-slot",12),item(Material.BOOK,(short)0,"&d&lPermisos",Arrays.asList("&7Consultar permisos de la zona","&eClic para abrir")));
        inv.setItem(slot("main.bans-slot",14),item(Material.BARRIER,(short)0,"&c&lJugadores bloqueados",Arrays.asList("&7Bloquear o desbloquear jugadores","&eClic para abrir")));
        inv.setItem(slot("main.stats-slot",16),item(Material.PAPER,(short)0,"&b&lEstadísticas",Arrays.asList("&7Dueño: &f"+name(p.getOwner()),"&7Miembros: &f"+p.getMembers().size(),"&7Radio: &f"+p.getRadius(),"&7Altura: &f"+p.getHeight())));
        inv.setItem(slot("main.close-slot",22),item(Material.REDSTONE,(short)0,"&cCerrar",Collections.singletonList("&7Cerrar este menú")));
        player.openInventory(inv);
    }
    private int purplePane(){return 10;}
    private int slot(String path,int def){return plugin.getMenusConfig().getInt(path,def);}
    private String name(UUID id){String n=Bukkit.getOfflinePlayer(id).getName();return n==null?"Desconocido":n;}
    private String color(String s){return plugin.color(s);}
    private ItemStack item(Material m,short data,String title,List<String> lore){ItemStack i=new ItemStack(m,1,data);ItemMeta meta=i.getItemMeta();meta.setDisplayName(color(title));List<String> l=new ArrayList<String>();for(String line:lore)l.add(color(line));meta.setLore(l);i.setItemMeta(meta);return i;}
    public boolean isOurMenu(String title){return title.contains("Protección:")||title.equals(color("&8Miembros de la protección"))||title.equals(color("&8Permisos de la protección"))||title.equals(color("&8Jugadores bloqueados"));}
    public void handleClick(InventoryClickEvent e){
        if(!(e.getWhoClicked() instanceof Player))return; Player player=(Player)e.getWhoClicked(); String title=e.getView().getTitle();
        if(!isOurMenu(title))return; e.setCancelled(true); if(e.getCurrentItem()==null||!e.getCurrentItem().hasItemMeta())return;
        String display=e.getCurrentItem().getItemMeta().getDisplayName(); String id=selectedProtection.get(player.getUniqueId()); Protection p=plugin.getProtectionManager().byId(id);if(p==null){player.closeInventory();return;}
        if(display.equals(color("&cCerrar"))){player.closeInventory();return;}
        if(title.contains("Protección:")){
            if(display.contains("Miembros"))openMembers(player,p);
            else if(display.contains("Permisos"))openPermissions(player,p);
            else if(display.contains("bloqueados"))openBans(player,p);
        } else if(title.equals(color("&8Miembros de la protección"))){
            if(display.contains("Volver")){openMain(player,p);return;}
            if(display.contains("Añadir miembro")){begin(player,p,"add");}
            else if(display.contains("Eliminar miembro")){begin(player,p,"remove");}
            else if(display.contains("Promover miembro")){begin(player,p,"promote");}
        } else if(title.equals(color("&8Permisos de la protección"))){if(display.contains("Volver")){openMain(player,p);return;} String perm=org.bukkit.ChatColor.stripColor(display); if(perm!=null&&perm.matches("[A-Z_]+")){if(!p.isOwner(player.getUniqueId())&&!player.hasPermission("vytalprotect.admin")){player.sendMessage(plugin.message("protection-not-owner"));return;}String key=perm+":*";boolean next=!p.getFlags().containsKey(key)||!p.getFlags().get(key).booleanValue();p.getFlags().put(key,next);plugin.getProtectionManager().save();openPermissions(player,p);plugin.getHolograms().refresh(p);}
        } else if(title.equals(color("&8Jugadores bloqueados"))){
            if(display.contains("Volver")){openMain(player,p);return;}
            if(display.contains("Bloquear jugador"))begin(player,p,"ban");else if(display.contains("Desbloquear jugador"))begin(player,p,"unban");
        }
    }
    private void openMembers(Player player,Protection p){Inventory inv=Bukkit.createInventory(null,27,color("&8Miembros de la protección"));int slot=10;for(UUID u:p.getMembers()){if(slot>25)break;inv.setItem(slot++,item(Material.SKULL_ITEM,(short)3,"&f"+name(u),Arrays.asList("&7Rango: &f"+p.getRoles().get(u))));}inv.setItem(2,item(Material.EMERALD,(short)0,"&aAñadir miembro",Collections.singletonList("&7Escribe el nombre por el chat")));inv.setItem(4,item(Material.IRON_INGOT,(short)0,"&eEliminar miembro",Collections.singletonList("&7Escribe el nombre por el chat")));inv.setItem(6,item(Material.GOLD_INGOT,(short)0,"&bPromover miembro",Collections.singletonList("&7Escribe el nombre por el chat")));inv.setItem(22,item(Material.ARROW,(short)0,"&7Volver",Collections.<String>emptyList()));player.openInventory(inv);}
    private void openPermissions(Player player,Protection p){Inventory inv=Bukkit.createInventory(null,27,color("&8Permisos de la protección"));String[] perms={"BUILD","CONTAINERS","INTERACT","PVP","MANAGE_MEMBERS","MANAGE_PERMISSIONS","BAN_PLAYERS"};int slot=10;for(String perm:perms){Boolean override=p.getFlags().get(perm+":*");String state=override==null?"&ePor rango":(override.booleanValue()?"&aPermitido":"&cDenegado");inv.setItem(slot++,item(Material.BOOK,(short)0,"&d"+perm,Arrays.asList("&7Estado: "+state,"&eClic para alternar en esta protección")));if(slot==17)slot=19;}inv.setItem(22,item(Material.ARROW,(short)0,"&7Volver",Collections.<String>emptyList()));player.openInventory(inv);}
    private void openBans(Player player,Protection p){Inventory inv=Bukkit.createInventory(null,27,color("&8Jugadores bloqueados"));int slot=10;for(UUID u:p.getBanned()){if(slot>25)break;inv.setItem(slot++,item(Material.BARRIER,(short)0,"&c"+name(u),Collections.singletonList("&7Actualmente bloqueado")));}inv.setItem(2,item(Material.REDSTONE,(short)0,"&cBloquear jugador",Collections.singletonList("&7Escribe el nombre por el chat")));inv.setItem(6,item(Material.SUGAR,(short)0,"&aDesbloquear jugador",Collections.singletonList("&7Escribe el nombre por el chat")));inv.setItem(22,item(Material.ARROW,(short)0,"&7Volver",Collections.<String>emptyList()));player.openInventory(inv);}
    private void begin(Player player,Protection p,String action){if(!p.isOwner(player.getUniqueId())&&!player.hasPermission("vytalprotect.admin")){player.sendMessage(plugin.message("protection-not-owner"));return;}pendingAction.put(player.getUniqueId(),p.getId()+":"+action);player.closeInventory();player.sendMessage(color("&dVytalProtect &8» &fEscribe el nombre del jugador en el chat. Escribe &ccancelar &fpara cancelar."));}
    public boolean handleChat(Player player,String text){String pending=pendingAction.remove(player.getUniqueId());if(pending==null)return false;if(text.equalsIgnoreCase("cancelar")){player.sendMessage(color("&cAcción cancelada."));return true;}String[] parts=pending.split(":",2);Protection p=plugin.getProtectionManager().byId(parts[0]);if(p==null)return true;OfflinePlayer target=Bukkit.getOfflinePlayer(text);UUID uuid=target.getUniqueId();String action=parts[1];
        if(action.equals("add")){p.getMembers().add(uuid);p.getRoles().put(uuid,"MEMBER");player.sendMessage(plugin.message("member-added").replace("%player%",text));}
        else if(action.equals("remove")){p.getMembers().remove(uuid);p.getRoles().remove(uuid);player.sendMessage(plugin.message("member-removed").replace("%player%",text));}
        else if(action.equals("promote")){if(!p.getMembers().contains(uuid)){p.getMembers().add(uuid);}p.getRoles().put(uuid,"ADMIN");player.sendMessage(color("&a"+text+" ahora tiene el rango ADMIN en esta protección."));}
        else if(action.equals("ban")){p.getBanned().add(uuid);p.getMembers().remove(uuid);p.getRoles().remove(uuid);player.sendMessage(plugin.message("member-banned").replace("%player%",text));}
        else if(action.equals("unban")){p.getBanned().remove(uuid);player.sendMessage(plugin.message("member-unbanned").replace("%player%",text));}
        plugin.getProtectionManager().save();plugin.getHolograms().refresh(p);openMain(player,p);return true;
    }
    public boolean hasPending(Player player){return pendingAction.containsKey(player.getUniqueId());}
    public void openCurrent(Player player){Protection p=plugin.getProtectionManager().at(player.getLocation());if(p==null){player.sendMessage(plugin.message("protection-not-found"));return;}if(!p.isOwner(player.getUniqueId())&&!p.isMember(player.getUniqueId())&&!player.hasPermission("vytalprotect.admin")){player.sendMessage(plugin.message("protection-not-owner"));return;}openMain(player,p);}
                           }
                                                                    
