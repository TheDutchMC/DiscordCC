package nl.thedutchmc.discordCC.minecraftEvents;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import nl.thedutchmc.discordCC.Channel;
import nl.thedutchmc.discordCC.DiscordCC;

public class AsyncPlayerChatEventListener implements Listener {

	@EventHandler
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
		DiscordCC.sendMessageToDiscord("[**" + event.getPlayer().getName() + "**] " + event.getMessage(), Channel.CHAT);
	}
	
	
}
