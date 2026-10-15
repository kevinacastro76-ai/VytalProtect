package lat.vytal.vytalprotect;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MenuManager {
    private final VytalProtect plugin;
    private final Map<UUID, String> openedProtection = new HashMap<>();
    private final Map<UUID, String> menuType = new HashMap<>();

    public MenuManager(VytalProtect plugin) { this.plugin = plugin; }

    public void openMain(Player player, Protection p) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE + "VytalProtect | Protección");
        inv.setItem(10, item(Material.PLAYER_HEAD, "&dAñadir miembro", "&7Añade un jugador usando el chat."));
        inv.setItem(11, item(Material.BOOK, "&bGestionar miembros", "&7Ver, promover o quitar miembros."));
        inv.setItem(12, item(Material.SHIELD, "&cBanear jugador", "&7Bloquea a un jugador de esta protección."));
        inv.setItem(14, item(Material.REDSTONE, "&ePermisos", "&7Cambia permisos del rol miembro."));
        inv.setItem(15, item(Material.PAPER, "&aInformación", "&7Ver dueño, miembros y radio."));
        inv.setItem(16, item(Material.BARRIER, "&cCerrar", "&7Cerrar menú."));
        openedProtection.put(player.getUniqueId(), p.id); menuType.put(player.getUniqueId(), "main");
        player.openInventory(inv);
    }

    public void openMembers(Player player, Protection p) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "VytalProtect | Miembros");
        int slot = 0;
        for (Map.Entry<String, Protection.Role> entry : p.members.entrySet()) {
            if (slot >= 45) break;
            Material mat = entry.getValue() == Protection.Role.MANAGER ? Material.DIAMOND : entry.getValue() == Protection.Role.TRUSTED ? Material.EMERALD : Material.PLAYER_HEAD;
            inv.setItem(slot++, item(mat, "&f" + entry.getKey(), "&7Rol: &d" + entry.getValue().name(),
                    "&aClic: promover/degradar", "&cShift-clic: quitar"));
        }
        inv.setItem(49, item(Material.ARROW, "&eVolver", "&7Volver al menú principal."));
        openedProtection.put(player.getUniqueId(), p.id); menuType.put(player.getUniqueId(), "members");
        player.openInventory(inv);
    }

    public void openBans(Player player, Protection p) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE + "VytalProtect | Bloqueos");
        inv.setItem(11, item(Material.ANVIL, "&cBanear jugador", "&7Escribe el nombre en el chat."));
        int slot = 13;
        for (String name : p.banned) {
            if (slot >= 26) break;
            inv.setItem(slot++, item(Material.BARRIER, "&c" + name, "&7Clic para quitar el bloqueo."));
        }
        inv.setItem(22, item(Material.ARROW, "&eVolver", "&7Volver al menú principal."));
        openedProtection.put(player.getUniqueId(), p.id); menuType.put(player.getUniqueId(), "bans");
        player.openInventory(inv);
    }

    public void openPermissions(Player player, Protection p) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE + "VytalProtect | Permisos");
        inv.setItem(11, item(Material.IRON_PICKAXE, "&aConstrucción miembro", "&7Clic para alternar."));
        inv.setItem(13, item(Material.CHEST, "&bInteracción miembro", "&7Clic para alternar."));
        inv.setItem(15, item(Material.CRAFTING_TABLE, "&eGestión miembro", "&7Promueve a manager desde Miembros."));
        inv.setItem(22, item(Material.ARROW, "&eVolver", "&7Volver al menú principal."));
        openedProtection.put(player.getUniqueId(), p.id); menuType.put(player.getUniqueId(), "permissions");
        player.openInventory(inv);
    }

    public void handle(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player player = (Player)e.getWhoClicked();
        UUID id = player.getUniqueId();
        if (!openedProtection.containsKey(id)) return;
        String title = e.getView().getTitle();
        if (!title.startsWith(ChatColor.DARK_PURPLE + "VytalProtect")) return;
        e.setCancelled(true);
        Protection p = plugin.manager().getById(openedProtection.get(id));
        if (p == null || !plugin.manager().canManage(player, p)) { player.closeInventory(); return; }
        int slot = e.getRawSlot();
        String type = menuType.get(id);
        if ("main".equals(type)) {
            if (slot == 10) {
                player.closeInventory();
                plugin.pendingAdd().put(id, p.id);
                plugin.tell(player, "chat-add-member");
            } else if (slot == 11) openMembers(player, p);
            else if (slot == 12) openBans(player, p);
            else if (slot == 14) openPermissions(player, p);
            else if (slot == 15) {
                player.sendMessage(plugin.color(plugin.getPrefix() + plugin.color("&dDueño: &f" + plugin.manager().name(p.owner) + " &8| &dMiembros: &f" + p.members.size() + " &8| &dRadio: &f" + p.radius)));
                player.closeInventory();
            } else if (slot == 16) player.closeInventory();
        } else if ("members".equals(type)) {
            if (slot == 49) { openMain(player, p); return; }
            ItemStack clicked = e.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta() || clicked.getItemMeta().getDisplayName() == null) return;
            String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName()).toLowerCase(Locale.ROOT);
            if (!p.members.containsKey(name)) return;
            if (e.isShiftClick()) {
                plugin.manager().removeMember(p, name);
                plugin.tell(player, "member-removed");
            } else {
                Protection.Role old = p.members.get(name);
                Protection.Role next = old == Protection.Role.MEMBER ? Protection.Role.TRUSTED : old == Protection.Role.TRUSTED ? Protection.Role.MANAGER : Protection.Role.MEMBER;
                p.members.put(name, next); plugin.manager().save();
                plugin.tell(player, "member-promoted");
            }
            openMembers(player, p);
        } else if ("bans".equals(type)) {
            if (slot == 22) { openMain(player, p); return; }
            if (slot == 11) {
                player.closeInventory(); plugin.pendingAdd().put(id, "BAN:" + p.id);
                player.sendMessage(plugin.color("&eEscribe el nombre del jugador a bloquear. Escribe cancelar para abortar."));
            } else {
                ItemStack clicked = e.getCurrentItem();
                if (clicked != null && clicked.hasItemMeta()) {
                    String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName()).toLowerCase(Locale.ROOT);
                    plugin.manager().unban(p, name); plugin.tell(player, "player-unbanned"); openBans(player, p);
                }
            }
        } else if ("permissions".equals(type)) {
            if (slot == 22) { openMain(player, p); return; }
            // Persist role defaults in config.yml. These toggles apply to all members of that role.
            if (slot == 11) {
                boolean value = !plugin.getConfig().getBoolean("roles.member.build", false);
                plugin.getConfig().set("roles.member.build", value); plugin.saveConfig();
                player.sendMessage(plugin.color("&aConstrucción para miembros: " + value)); openPermissions(player, p);
            } else if (slot == 13) {
                boolean value = !plugin.getConfig().getBoolean("roles.member.interact", true);
                plugin.getConfig().set("roles.member.interact", value); plugin.saveConfig();
                player.sendMessage(plugin.color("&aInteracción para miembros: " + value)); openPermissions(player, p);
            }
        }
    }

    private ItemStack item(Material material, String name, String... lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.color(name));
            List<String> lines = new ArrayList<>();
            for (String line : lore) lines.add(plugin.color(line));
            meta.setLore(lines); stack.setItemMeta(meta);
        }
        return stack;
    }
                             }
