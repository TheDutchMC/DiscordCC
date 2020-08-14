package nl.thedutchmc.discordCC.discordEvents;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.vdurmont.emoji.EmojiParser;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import nl.thedutchmc.discordCC.Channel;
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
			
			String name = event.getMember().getEffectiveName();
			
			//we want to send the message to in-game players, but also to the console channel
			DiscordCC.sendMessageToPlayers(name + ": " + msgEmojisTranslated);
			
			final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			String currTime = formatter.format(new Date());
			DiscordCC.sendMessageToDiscord("[" + currTime + " INFO]: [DiscordCC] [Discord] " + name + ": " + msgEmojisTranslated, Channel.CONSOLE);
			
		//Messages from the console channel should be executed as commands
		} else if(msgChannel.equals(JdaHandler.consoleChannel)) {
			
			if(event.getAuthor().isBot()) return;
			
			String message = event.getMessage().getContentDisplay();
			
			//if it's a say message, we want to send this message to the chat channel as well
			//Though we have to strip off the "say" command itself
			//Not using String#replace() because the message itself might contain the word say
			if(message.startsWith("say")) {
				List<String> parts = new LinkedList<>(Arrays.asList(message.split(" ")));
				parts.remove(0);
				
				StringBuilder b = new StringBuilder();
				for(String p : parts)
					b.append(p + " ");

				String trimmedMessage = b.toString();
				DiscordCC.sendMessageToDiscord("**[Server]** " + trimmedMessage, Channel.CHAT);
			}
			
			//if its a broadcast, we want to pass the message to the chat channel as well.
			if(message.startsWith("broadcast")) {
				List<String> parts = new LinkedList<>(Arrays.asList(message.split(" ")));
				parts.remove(0);
				
				StringBuilder b = new StringBuilder();
				for(String p : parts)
					b.append(p + " ");
				
				String trimmedMessage = b.toString();
				DiscordCC.sendMessageToDiscord("**[Broadcast]** " + trimmedMessage, Channel.CHAT);
			}
			
			//Execute the command
			//In a BukkitRunnable because this event thread is not sync with the server's main thread
			new BukkitRunnable() {
				
				@Override
				public void run() {
					
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), message);
				}
			}.runTask(DiscordCC.INSTANCE);
			
		//Message came from a channel we're not interested in, so we just return.
		} else {
			return;
		}
	}
}
