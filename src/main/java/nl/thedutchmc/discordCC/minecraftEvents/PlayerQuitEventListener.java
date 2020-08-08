package nl.thedutchmc.discordCC.minecraftEvents;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import nl.thedutchmc.discordCC.Channel;
import nl.thedutchmc.discordCC.ConfigurationHandler;
import nl.thedutchmc.discordCC.DiscordCC;

public class PlayerQuitEventListener implements Listener {

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		DiscordCC.sendMessageToDiscord(ConfigurationHandler.playerLeavePrefix + "**" + event.getPlayer().getName() + "** left the server!", Channel.CHAT);
	}
	
}
