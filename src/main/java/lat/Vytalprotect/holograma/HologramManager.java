package lat.vytalprotect.hologram;

import lat.vytalprotect.VytalProtectPlugin;
import lat.vytalprotect.core.Protection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import java.util.*;

/** Hologramas propios usando ArmorStands invisibles, sin dependencias externas. */
public class HologramManager {
    private final VytalProtectPlugin plugin;
    private final Map<String, List<UUID>> spawned = new HashMap<String, List<UUID>>();
    public HologramManager(VytalProtectPlugin plugin){this.plugin=plugin;}
    public void spawn(Protection p) {
        remove(p.getId());
        if (!plugin.getConfig().getBoolean("settings.hologram.enabled", true) || !plugin.getHologramsConfig().getBoolean("enabled", true)) return;
        World world = Bukkit.getWorld(p.getWorld()); if(world==null) return;
        List<String> lines = plugin.getHologramsConfig().getStringList("lines");
        if(lines.isEmpty()) lines = plugin.getConfig().getStringList("settings.hologram.lines");
        double spacing = plugin.getHologramsConfig().getDouble("line-spacing",0.25);
        double offset = plugin.getHologramsConfig().getDouble("height-offset",2.2);
        List<UUID> ids = new ArrayList<UUID>();
        for(int i=0;i<lines.size();i++) {
            String line = format(lines.get(i),p);
            Location loc = new Location(world,p.getX()+0.5,p.getY()+offset+(lines.size()-1-i)*spacing,p.getZ()+0.5);
            ArmorStand stand = (ArmorStand)world.spawnEntity(loc, EntityType.ARMOR_STAND);
            stand.setVisible(false); stand.setGravity(false); stand.setCustomNameVisible(true); stand.setCustomName(color(line));
            try { stand.setMarker(true); } catch (Throwable ignored) { }
            stand.setMetadata("VytalProtectHologram",new FixedMetadataValue(plugin,p.getId())); ids.add(stand.getUniqueId());
        }
        spawned.put(p.getId(),ids);
    }
    private String format(String line, Protection p) {
        String owner = Bukkit.getOfflinePlayer(p.getOwner()).getName(); if(owner==null) owner="Desconocido";
        return line.replace("%owner%",owner).replace("%members%",String.valueOf(p.getMembers().size())).replace("%radius%",String.valueOf(p.getRadius())).replace("%type%",p.getId());
    }
    private String color(String s){return ChatColor.translateAlternateColorCodes('&',s);}
    public void remove(String id) {
        List<UUID> ids=spawned.remove(id); if(ids==null)return;
        for(World w:Bukkit.getWorlds()) for(Entity e:w.getEntities()) if(ids.contains(e.getUniqueId())) e.remove();
    }
    public void removeAll(){for(String id:new ArrayList<String>(spawned.keySet())) remove(id);}
    public void refresh(Protection p){spawn(p);}
}
