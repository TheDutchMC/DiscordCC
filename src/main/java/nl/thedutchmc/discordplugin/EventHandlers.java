package nl.thedutchmc.discordplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.md_5.bungee.api.ChatColor;

public class EventHandlers implements Listener {

	private DiscordListener dl;
	private DiscordPlugin dp;
	
	public EventHandlers(DiscordListener dl, DiscordPlugin dp) {
		this.dl = dl;
		this.dp = dp;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		dl.sendToDiscord(":heavy_plus_sign: **" + event.getPlayer().getName() + "** joined the server!", "SERVER_CHAT","");
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		dl.sendToDiscord(":heavy_minus_sign: **" + event.getPlayer().getName() + "** left the server!", "SERVER_CHAT","");
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		dl.sendToDiscord("**" + event.getDeathMessage() + "**", "SERVER_CHAT","");
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		dl.sendToDiscord("[**" + event.getPlayer().getName() + "**] " + event.getMessage(), "SERVER_CHAT","");
		
		String message = event.getMessage();
		String[] words = message.split(" ");
		
		if(!event.getPlayer().isOp()) {
	        for(String word : words) {
	            if(dp.profanityList.contains(word.toLowerCase())) {
	            	dl.sendToDiscord("**[!]** Detected profanity by **" + event.getPlayer().getName() + "**! Word: **" + word + "**", "BOT_REPORT_CHAT", "");
					dl.sendToDiscord("**[!]** Profanity! See Reports!", "SERVER_CHAT", "");
					event.getPlayer().sendMessage(ChatColor.RED + "Profanity detected! This will be reported!");
					event.setCancelled(true);
	            }
	        }
		}
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		dl.sendToDiscord("**" + event.getPlayer().getName() + "**got kicked with reason: **" + event.getReason() + "**", "SERVER_CHAT","");
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		String eventMessage = event.getMessage();
		String executor = event.getPlayer().getName();

		String[] args = eventMessage.split(" ", 0);
		
		if(args[0].equals("/msg")) {
			String target = args[1];
			
			args[0] = "";
			args[1] = "";
			
			String commandMessage = String.join(" ", args);
			
			dl.sendToDiscord("**[MSG]** from **" + executor + "** to **" + target + "** with message: " + commandMessage, "SERVER_CHAT","");
		}
	}
}
