package mc.euro.extraction.nms.v1_7_R3;

import java.lang.reflect.Field;
import java.util.UUID;
import mc.euro.extraction.nms.Hostage;
import net.minecraft.server.v1_7_R3.Entity;
import net.minecraft.server.v1_7_R3.EntityAgeable;
import net.minecraft.server.v1_7_R3.EntityOwnable;
import net.minecraft.server.v1_7_R3.EntityVillager;
import net.minecraft.server.v1_7_R3.PathfinderGoalSelector;
import net.minecraft.server.v1_7_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.util.UnsafeList;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

/**
 *
 * @author Nikolai
 */
public class CraftHostage extends EntityVillager implements EntityOwnable, Hostage {
    
    private String owner;
    private String lastOwner;
    
    public CraftHostage(World w) {
        super(w);
        clearPathfinders();
        this.goalSelector.a(10, new PathfinderGoalFollowPlayer(this, 1.0D, 2.0F, 2.0F));
    }
    
    public CraftHostage(World w, int profession) {
        super(w, profession);
        clearPathfinders();
        this.goalSelector.a(10, new PathfinderGoalFollowPlayer(this, 1.0D, 2.0F, 2.0F));
    }
    
    private void clearPathfinders() {
        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            bField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
            bField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
    
    @Override
    public void stay() {
        this.lastOwner = owner;
        this.owner = null;
    }
    
    @Override
    public boolean isStopped() {
        return this.owner == null;
    }
    
    @Override
    public boolean isFollowing() {
        return this.owner != null;
    }
    
    @Override
    public void follow(Player p) {
        follow(p.getName());
    }
    
    @Override
    public void follow(String p) {
        this.owner = p;
    }
    
    @Override
    public void setOwner(Player p) {
        this.owner = p.getName();
    }
    
    @Override
    public void setOwner(String name) {
        this.owner = name;
    }

    @Override
    public String getOwnerName() {
        return this.owner;
    }

    @Override
    public Entity getOwner() {
        if (this.owner == null) return null;
        Player player = (Player) Bukkit.getPlayer(this.owner);
        int id = player.getEntityId();
        Entity E = (Entity) this.world.getEntity(id);
        return E;
    }
    
    @Override
    public Location getLocation() {
        Location loc = new Location(world.getWorld(), locX, locY, locZ, yaw, pitch);
        return loc;
    }
    
    @Override
    public void setLocation(Location loc) {
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float newYaw = loc.getYaw();
        float newPitch = loc.getPitch();
        setLocation(x, y, z, newYaw, newPitch);
    }

    @Override
    public void removeEntity() {
        world.removeEntity(this);
    }

    @Override
    public EntityAgeable createChild(EntityAgeable ea) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Villager.Profession getProfessionType() {
        int id = getProfession();
        return Villager.Profession.getProfession(id);
    }

    @Override
    public void setProfessionType(Villager.Profession x) {
        setProfession(x.getId());
    }

    @Override
    public void setHealth(double health) {
        setHealth((float) health);
    }

    @Override
    public String getOwnerUUID() {
        if (this.owner == null) {
            return null;
        }
        Player player = Bukkit.getPlayer(owner);
        UUID uuid = player.getUniqueId();
        return uuid.toString();
    }

    @Override
    public Player getRescuer() {
        String name = (owner == null) ? lastOwner : owner;
        Player rescuer = Bukkit.getPlayer(name);
        return rescuer;
    }
    
}
