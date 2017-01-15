package me.waffle.GlitchPatch;

import me.waffle.GlitchPatch.command.CommandPatch;
import me.waffle.GlitchPatch.player.KnockbackPatch;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class GlitchPatch
  extends JavaPlugin
{
  public void onEnable()
  {
    saveDefaultConfig();
    
    PluginManager pm = Bukkit.getPluginManager();
    
    FileConfiguration config = getConfig();
    if (config.getBoolean("patches.knockback")) {
      pm.registerEvents(new KnockbackPatch(), this);
    }
    if (config.getBoolean("patches.command_spam")) {
      pm.registerEvents(new CommandPatch(), this);
    }
  }
}
