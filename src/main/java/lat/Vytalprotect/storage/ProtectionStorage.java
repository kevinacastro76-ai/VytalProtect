package lat.vytalprotect.storage;

import lat.vytalprotect.core.Protection;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ProtectionStorage {
    private final File file;
    public ProtectionStorage(File dataFolder) { this.file = new File(dataFolder, "protections.yml"); }
    public Map<String, Protection> load() {
        Map<String, Protection> result = new LinkedHashMap<String, Protection>();
        if (!file.exists()) return result;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = yaml.getConfigurationSection("protections");
        if (root == null) return result;
        for (String id : root.getKeys(false)) {
            String p = "protections." + id;
            try {
                Protection protection = new Protection(id, yaml.getString(p+".world"), yaml.getInt(p+".x"), yaml.getInt(p+".y"), yaml.getInt(p+".z"), yaml.getInt(p+".radius",8), yaml.getInt(p+".height",32), UUID.fromString(yaml.getString(p+".owner")));
                for (String s : yaml.getStringList(p+".members")) protection.getMembers().add(UUID.fromString(s));
                for (String s : yaml.getStringList(p+".banned")) protection.getBanned().add(UUID.fromString(s));
                ConfigurationSection roles = yaml.getConfigurationSection(p+".roles");
                if (roles != null) for (String key : roles.getKeys(false)) protection.getRoles().put(UUID.fromString(key), roles.getString(key));
                ConfigurationSection flags = yaml.getConfigurationSection(p+".flags");
                if (flags != null) for (String key : flags.getKeys(false)) protection.getFlags().put(key, flags.getBoolean(key));
                result.put(id, protection);
            } catch (Exception ignored) { }
        }
        return result;
    }
    public void save(Map<String, Protection> protections) {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Protection p : protections.values()) {
            String path = "protections." + p.getId();
            yaml.set(path+".world",p.getWorld()); yaml.set(path+".x",p.getX()); yaml.set(path+".y",p.getY()); yaml.set(path+".z",p.getZ());
            yaml.set(path+".radius",p.getRadius()); yaml.set(path+".height",p.getHeight()); yaml.set(path+".owner",p.getOwner().toString());
            List<String> members = new ArrayList<String>(); for(UUID u:p.getMembers()) members.add(u.toString()); yaml.set(path+".members",members);
            List<String> banned = new ArrayList<String>(); for(UUID u:p.getBanned()) banned.add(u.toString()); yaml.set(path+".banned",banned);
            for(Map.Entry<UUID,String> e:p.getRoles().entrySet()) yaml.set(path+".roles."+e.getKey().toString(),e.getValue());
            for(Map.Entry<String,Boolean> e:p.getFlags().entrySet()) yaml.set(path+".flags."+e.getKey(),e.getValue());
        }
        try { yaml.save(file); } catch (IOException e) { e.printStackTrace(); }
    }
        }
