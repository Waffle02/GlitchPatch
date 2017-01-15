package me.waffle.GlitchPatch.command;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandPatch
  implements Listener
{
  private ArrayList<String> trackedCommands = new ArrayList();
  
  public CommandPatch()
  {
    this.trackedCommands.add("/lightning");
    this.trackedCommands.add("/tnt");
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void onCommand(PlayerCommandPreprocessEvent e)
  {
    String lowerCaseCommand = e.getMessage().toLowerCase();
    if (lowerCaseCommand.contains(" "))
    {
      String[] split = lowerCaseCommand.split(" ");
      if (this.trackedCommands.contains(split[0]))
      {
        e.setCancelled(true);
        e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lDo not try to spam this command"));
      }
    }
  }
}
