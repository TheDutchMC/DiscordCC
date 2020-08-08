package nl.thedutchmc.discordCC;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;

import net.md_5.bungee.api.ChatColor;

public class ConsoleAppender extends AbstractAppender {
	

	@SuppressWarnings("deprecation")
	public ConsoleAppender() {
		super("consoleAppender", null, null);
		start();
		
	}
	
	@Override
	public void append(LogEvent event) {
		LogEvent log = event.toImmutable();
		
		final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		
		final String finalLogMessage = ChatColor.stripColor(
				"[" + formatter.format(new Date(event.getTimeMillis())) 
				+ " " + event.getLevel().toString() + "] "
				+ log.getMessage().getFormattedMessage());
		
		if(!DiscordCC.INSTANCE.isEnabled()) return;
		
		if(log.getMessage().getFormattedMessage().contains("Done") && !DiscordCC.POSTWORLD) {
			
			DiscordCC.POSTWORLD = true;
			DiscordCC.sendMessageToDiscord(":white_check_mark: **Server has started!**", Channel.CHAT);
		}
		
		if(!DiscordCC.POSTWORLD) return;
		
		DiscordCC.sendMessageToDiscord(finalLogMessage, Channel.CONSOLE);
	}
	
	public void shutdown() {
		Logger rootLogger = (Logger) LogManager.getRootLogger();
		rootLogger.removeAppender(this);
	}
}
