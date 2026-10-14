package lat.vytalprotect.core;

import java.util.*;

public class Protection {
    private final String id;
    private final String world;
    private final int x, y, z, radius, height;
    private UUID owner;
    private final Set<UUID> members = new HashSet<UUID>();
    private final Set<UUID> banned = new HashSet<UUID>();
    private final Map<UUID, String> roles = new HashMap<UUID, String>();
    private final Map<String, Boolean> flags = new HashMap<String, Boolean>();

    public Protection(String id, String world, int x, int y, int z, int radius, int height, UUID owner) {
        this.id=id; this.world=world; this.x=x; this.y=y; this.z=z; this.radius=radius; this.height=height; this.owner=owner;
        roles.put(owner, "OWNER");
    }
    public String getId(){return id;} public String getWorld(){return world;} public int getX(){return x;} public int getY(){return y;} public int getZ(){return z;}
    public int getRadius(){return radius;} public int getHeight(){return height;} public UUID getOwner(){return owner;} public void setOwner(UUID owner){this.owner=owner; roles.put(owner,"OWNER");}
    public Set<UUID> getMembers(){return members;} public Set<UUID> getBanned(){return banned;} public Map<UUID,String> getRoles(){return roles;} public Map<String,Boolean> getFlags(){return flags;}
    public boolean contains(String worldName, int bx, int by, int bz) {
        return world.equalsIgnoreCase(worldName) && Math.abs(bx-x)<=radius && Math.abs(bz-z)<=radius && by>=y-height && by<=y+height;
    }
    public boolean overlaps(Protection other) {
        if (!world.equalsIgnoreCase(other.world)) return false;
        return x-radius <= other.x+other.radius && x+radius >= other.x-other.radius
            && z-radius <= other.z+other.radius && z+radius >= other.z-other.radius
            && y-height <= other.y+other.height && y+height >= other.y-other.height;
    }
    public boolean isOwner(UUID uuid){return owner.equals(uuid);}
    public boolean isMember(UUID uuid){return members.contains(uuid) || isOwner(uuid);}
  }
