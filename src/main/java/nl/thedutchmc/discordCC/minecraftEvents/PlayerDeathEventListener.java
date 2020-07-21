package nl.thedutchmc.discordCC.minecraftEvents;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import nl.thedutchmc.discordCC.Channel;
import nl.thedutchmc.discordCC.DiscordCC;

public class PlayerDeathEventListener implements Listener {

	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event) {		
		DiscordCC.sendMessageToDiscord("**" + event.getDeathMessage() + "**", Channel.CHAT);
	}
}
