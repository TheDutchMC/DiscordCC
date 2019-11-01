package nl.thedutchmc.discordplugin;

import java.util.List;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;


public class DiscordListener implements EventListener {
	
	JDA jda;
	DiscordPlugin dp;
	
	public DiscordListener(DiscordPlugin dp) {
		this.dp = dp;
	}
	
	@Override
	public void onEvent(GenericEvent event) {
		
		if(event instanceof MessageReceivedEvent) {
			//Had to do all this to get the nickname, instead of event.getAuthor().getName(), which gives the user's discord name, not their server nickname
			String author = "";
			List<Member> members = ((MessageReceivedEvent) event).getGuild().getMembers();
			for(Member m : members) {
				if(!m.getUser().isBot()) {
					if(m.getId().equals(((MessageReceivedEvent) event).getAuthor().getId())) {
						if(m.getNickname() != null) {
							author = m.getNickname();
						} else {
							author = ((MessageReceivedEvent) event).getAuthor().getName();
						}
						break;
					}
				}
			}
			
			String message = ((MessageReceivedEvent) event).getMessage().getContentDisplay();
			if(!(((MessageReceivedEvent) event).getAuthor().isBot())) {
				if(((MessageReceivedEvent) event).getChannel().getId().contentEquals(dp.chatLinkChannelId)) {
			        //Add text sending to server
					dp.sendToServer(message, author);
				} if(dp.restrictBotCommandsToChannel) { //run if the bot commands are restricted to one channel
					if(((MessageReceivedEvent) event).getChannel().getId().contentEquals(dp.botCommandChannelId)) {
						if(message.equalsIgnoreCase(dp.commandPrefix + "whitelist") && dp.enableWhitelisting) {
							if(dp.requireWhitelistRole) {
								for(Role r : ((MessageReceivedEvent) event).getMember().getRoles()) {
									if(r.getId().equals(dp.whitelistRoleId)) {
										processCommand(event, message, author, "whitelist");
										break;
									}
								}
							} else {
								processCommand(event, message, author, "whitelist");

							}
						} else if(message.equalsIgnoreCase(dp.commandPrefix + "help")) {
							processCommand(event, message, author, "help");
						}
					}
				} else { //Run when the bot commands are not restricted to one channel
					if(message.equalsIgnoreCase(dp.commandPrefix + "whitelist") && dp.enableWhitelisting) {
						if(dp.requireWhitelistRole) {
							for(Role r : ((MessageReceivedEvent) event).getMember().getRoles()) {
								if(r.getId().equals(dp.whitelistRoleId)) {
									processCommand(event, message, author, "whitelist");
									break;
								}
							}
						} else {
							processCommand(event, message, author, "whitelist");

						}
					} else if(message.equalsIgnoreCase(dp.commandPrefix + "help")) {
						processCommand(event, message, author,"help");
					}
				}
			}
		}
	}
	
	public void processCommand(GenericEvent event, String message, String sender, String command) {
		switch(command) {
		case "whitelist":
			//run command
			sendToDiscord("Added " + sender + " to the whitelist!", "", ((MessageReceivedEvent) event).getChannel().getId());
			dp.runCommand("whitelist add " + sender);
			
			System.err.println(dp.botReportChannelId);
			
			if(dp.enableBotReport) {
				sendToDiscord("Added " + sender + " to the whitelist!", "BOT_REPORT_CHAT", "");
			}
			break;
		case "help":
			sendToDiscord("**Help Page:**\n"
					+ "> **- whitelist**: Add yourself to the whitelist, the command uses your **discord nickname**, so make sure it is the same as your in-game name! This only works if it is enabled in config!\n"
					+ "> **- help**: Gives this page.", "", ((MessageReceivedEvent) event).getChannel().getId());
			break;
		}
	}
	 
	public void sendToDiscord(String message, String channelName, String originChannelId) {
		String channelId = "0";
		
		jda = dp.getJda();
		
		switch(channelName) {
		case "SERVER_CHAT":
			channelId = dp.chatLinkChannelId;
			break;
		case "BOT_COMMAND_CHAT":
			channelId = dp.botCommandChannelId;
			break;
		case "BOT_REPORT_CHAT":
			channelId = dp.botReportChannelId;
			break;
		case "":
			channelId = originChannelId;
			break;
		}
		
		jda.getTextChannelById(channelId).sendMessage(message).queue();
	}
}