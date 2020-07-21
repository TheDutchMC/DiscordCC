package nl.thedutchmc.discordCC.discordEvents;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.vdurmont.emoji.EmojiParser;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import nl.thedutchmc.discordCC.DiscordCC;
import nl.thedutchmc.discordCC.JdaHandler;

public class MessageReceivedEventListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		
		final MessageChannel msgChannel = event.getChannel();
		
		//Messages from the chat channel should be send to all players
		if(msgChannel.equals(JdaHandler.chatChannel)) {
			
			if(event.getAuthor().isBot()) return;
			
			String discordMessage = event.getMessage().getContentDisplay();
			String msgEmojisTranslated = EmojiParser.parseToAliases(discordMessage);
			DiscordCC.sendMessageToPlayers(event.getAuthor().getName() + ": " + msgEmojisTranslated);
			
		//Messages from the console channel should be executed as commands
		} else if(msgChannel.equals(JdaHandler.consoleChannel)) {
			
			if(event.getAuthor().isBot()) return;
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), event.getMessage().getContentDisplay());
				}
			}.runTask(DiscordCC.INSTANCE);
			
		//Message came from a channel we're not interested in, so we just return.
		} else {
			return;
		}
	}
}
