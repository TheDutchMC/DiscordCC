package nl.thedutchmc.discordCC.minecraftEvents;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import nl.thedutchmc.discordCC.Channel;
import nl.thedutchmc.discordCC.ConfigurationHandler;
import nl.thedutchmc.discordCC.DiscordCC;

public class PlayerJoinEventListener implements Listener {

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		DiscordCC.sendMessageToDiscord(ConfigurationHandler.playerJoinPrefix + "**" + event.getPlayer().getName() + "** joined the server!", Channel.CHAT);
	}
}
