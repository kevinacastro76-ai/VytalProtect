package lat.vytalprotect.core;

import lat.vytalprotect.VytalProtectPlugin;
import lat.vytalprotect.storage.ProtectionStorage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.*;

public class ProtectionManager {
    private final VytalProtectPlugin plugin;
    private final Map<String, Protection> protections = new LinkedHashMap<String, Protection>();
    private final ProtectionStorage storage;
    public ProtectionManager(VytalProtectPlugin plugin){this.plugin=plugin;this.storage=new ProtectionStorage(plugin.getDataFolder()); protections.putAll(storage.load());}
    public Collection<Protection> all(){return Collections.unmodifiableCollection(protections.values());}
    public Protection at(Location loc){if(loc==null||loc.getWorld()==null)return null; for(Protection p:protections.values())if(p.contains(loc.getWorld().getName(),loc.getBlockX(),loc.getBlockY(),loc.getBlockZ()))return p;return null;}
    public Protection byId(String id){return protections.get(id);}
    public int countOwned(UUID uuid){int n=0;for(Protection p:protections.values())if(p.isOwner(uuid))n++;return n;}
    public boolean overlaps(Protection candidate){for(Protection p:protections.values())if(p.overlaps(candidate))return true;return false;}
    public Protection create(Location loc, Player owner) {
        int radius=plugin.getConfig().getInt("settings.default-radius",8), height=plugin.getConfig().getInt("settings.default-height",32);
        String id=UUID.randomUUID().toString().substring(0,8);
        Protection p=new Protection(id,loc.getWorld().getName(),loc.getBlockX(),loc.getBlockY(),loc.getBlockZ(),radius,height,owner.getUniqueId());
        if(overlaps(p))return null;
        protections.put(id,p);save();return p;
    }
    public void remove(Protection p){protections.remove(p.getId());plugin.getHolograms().remove(p.getId());save();}
    public void save(){storage.save(protections);}
    public boolean hasPermission(Protection p, UUID uuid, String permission) {
        if(plugin.getConfig().getBoolean("settings.admins-bypass-protection",true)) {
            Player player=org.bukkit.Bukkit.getPlayer(uuid); if(player!=null&&player.hasPermission("vytalprotect.admin"))return true;
        }
        if(p.isOwner(uuid))return true;
        if(p.getBanned().contains(uuid))return false;
        Boolean flag=p.getFlags().get(permission+":"+uuid.toString()); if(flag!=null)return flag.booleanValue();
        Boolean override=p.getFlags().get(permission+":*"); if(override!=null)return override.booleanValue();
        String role=p.getRoles().get(uuid);
        if(role==null) role=p.isMember(uuid)?"MEMBER":plugin.getPermissionsConfig().getString("default-role","VISITOR");
        List<String> perms=plugin.getPermissionsConfig().getStringList("roles."+role+".permissions");
        return perms.contains(permission);
    }
}
