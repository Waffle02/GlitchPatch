package me.waffle.GlitchPatch.player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class KnockbackPatch
  implements Listener
{
  private static Method NMS_PLAYER_GETHANDLE = null;
  private static Field NMS_PLAYER_CONNECTION = null;
  private static Method NMS_PLAYER_CONNECTION_SEND_PACKET = null;
  private Class<?> NMS_PACKET = null;
  private Constructor<?> VELOCITY_PACKET;
  
  public KnockbackPatch()
  {
    String cbVersion = org.bukkit.Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    try
    {
      Class nmsPlayer = Class.forName("net.minecraft.server." + cbVersion + ".EntityPlayer");
      Class velocityPacket = Class.forName("net.minecraft.server." + cbVersion + ".PacketPlayOutEntityVelocity");
      Class playerConn = Class.forName("net.minecraft.server." + cbVersion + ".PlayerConnection");
      
      this.NMS_PACKET = Class.forName("net.minecraft.server." + cbVersion + ".Packet");
      this.VELOCITY_PACKET = velocityPacket.getConstructor(new Class[] { Integer.TYPE, Double.TYPE, Double.TYPE, Double.TYPE });
    }
    catch (ClassNotFoundException localClassNotFoundException) {}catch (NoSuchMethodException e)
    {
      e.printStackTrace();
    }
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
  {
    if ((!(event.getEntity() instanceof Player)) || (!(event.getDamager() instanceof Player))) {
      return;
    }
    if (event.isCancelled()) {
      return;
    }
    Player damaged = (Player)event.getEntity();
    Player damager = (Player)event.getDamager();
    if (damaged.getNoDamageTicks() > damaged.getMaximumNoDamageTicks() / 2.0D) {
      return;
    }
    double horMultiplier = 1.3D;
    double verMultiplier = 1.3D;
    double sprintMultiplier = damager.isSprinting() ? 0.8D : 0.5D;
    double kbMultiplier = damager.getItemInHand() == null ? 0.0D : damager.getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK) * 0.2D;
    
    double airMultiplier = damaged.isOnGround() ? 1.0D : 0.5D;
    
    Vector knockback = damager.getLocation().getDirection().normalize();
    knockback.setX((knockback.getX() * sprintMultiplier + kbMultiplier) * horMultiplier);
    knockback.setY(0.35D * airMultiplier * verMultiplier);
    knockback.setZ((knockback.getZ() * sprintMultiplier + kbMultiplier) * horMultiplier);
    try
    {
      Object packet = this.VELOCITY_PACKET.newInstance(new Object[] { Integer.valueOf(damaged.getEntityId()), Double.valueOf(knockback.getX()), Double.valueOf(knockback.getY()), Double.valueOf(knockback.getZ()) });
      sendPacket(damaged, packet);
    }
    catch (SecurityException|IllegalArgumentException|IllegalAccessException|InvocationTargetException|InstantiationException localSecurityException) {}
  }
  
  private void sendPacket(Player p, Object packet)
  {
    if (NMS_PLAYER_GETHANDLE == null) {
      try
      {
        NMS_PLAYER_GETHANDLE = p.getClass().getMethod("getHandle", new Class[0]);
      }
      catch (NoSuchMethodException|SecurityException e)
      {
        e.printStackTrace();
      }
    }
    try
    {
      Object nms_player = NMS_PLAYER_GETHANDLE.invoke(p, new Object[0]);
      if (NMS_PLAYER_CONNECTION == null) {
        NMS_PLAYER_CONNECTION = nms_player.getClass().getField("playerConnection");
      }
      Object player_con = NMS_PLAYER_CONNECTION.get(nms_player);
      if (NMS_PLAYER_CONNECTION_SEND_PACKET == null) {
        NMS_PLAYER_CONNECTION_SEND_PACKET = player_con.getClass().getMethod("sendPacket", new Class[] { this.NMS_PACKET });
      }
      NMS_PLAYER_CONNECTION_SEND_PACKET.invoke(player_con, new Object[] { packet });
    }
    catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException|NoSuchFieldException|SecurityException|NoSuchMethodException e)
    {
      e.printStackTrace();
    }
  }
}
