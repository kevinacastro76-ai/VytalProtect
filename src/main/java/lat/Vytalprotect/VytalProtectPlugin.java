package lat.vytalprotect;

import lat.vytalprotect.command.VytalProtectCommand;
import lat.vytalprotect.core.Protection;
import lat.vytalprotect.core.ProtectionManager;
import lat.vytalprotect.hologram.HologramManager;
import lat.vytalprotect.listener.ProtectionListener;
import lat.vytalprotect.menu.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class VytalProtectPlugin extends JavaPlugin {
    private ProtectionManager protectionManager; private HologramManager holograms; private MenuManager menus;
    private YamlConfiguration permissionsConfig, hologramsConfig, menusConfig, languageConfig, eventsConfig;
    @Override public void onEnable(){
        saveDefaultConfig(); copyResource("messages.yml");copyResource("permissions.yml");copyResource("holograms.yml");copyResource("menus.yml");copyResource("language.yml");copyResource("events.yml");
        loadCustomConfigs(); protectionManager=new ProtectionManager(this); holograms=new HologramManager(this); menus=new MenuManager(this);
        getServer().getPluginManager().registerEvents(new ProtectionListener(this),this);
        getCommand("vytalprotect").setExecutor(new VytalProtectCommand(this));
        for(Protection p:protectionManager.all()) holograms.spawn(p);
        getLogger().info("VytalProtect habilitado. Protecciones cargadas: "+protectionManager.all().size());
    }
    @Override public void onDisable(){if(protectionManager!=null)protectionManager.save();if(holograms!=null)holograms.removeAll();}
    private void copyResource(String name){File f=new File(getDataFolder(),name);if(!f.exists())saveResource(name,false);}
    public void loadCustomConfigs(){permissionsConfig=YamlConfiguration.loadConfiguration(new File(getDataFolder(),"permissions.yml"));hologramsConfig=YamlConfiguration.loadConfiguration(new File(getDataFolder(),"holograms.yml"));menusConfig=YamlConfiguration.loadConfiguration(new File(getDataFolder(),"menus.yml"));languageConfig=YamlConfiguration.loadConfiguration(new File(getDataFolder(),"language.yml"));eventsConfig=YamlConfiguration.loadConfiguration(new File(getDataFolder(),"events.yml"));}
    public String message(String key){YamlConfiguration m=YamlConfiguration.loadConfiguration(new File(getDataFolder(),"messages.yml"));String prefix=m.getString("prefix", "&8[&dVytalProtect&8] ");return color(prefix+m.getString(key,"&cMissing message: "+key));}
    public String color(String s){return ChatColor.translateAlternateColorCodes('&',s);}
    public ProtectionManager getProtectionManager(){return protectionManager;} public HologramManager getHolograms(){return holograms;} public MenuManager getMenus(){return menus;}
    public YamlConfiguration getPermissionsConfig(){return permissionsConfig;} public YamlConfiguration getHologramsConfig(){return hologramsConfig;} public YamlConfiguration getMenusConfig(){return menusConfig;} public YamlConfiguration getLanguageConfig(){return languageConfig;} public YamlConfiguration getEventsConfig(){return eventsConfig;}
}
