package nl.thedutchmc.discordCC;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.md_5.bungee.api.ChatColor;
import nl.thedutchmc.discordCC.discordEvents.MessageReceivedEventListener;
import nl.thedutchmc.discordCC.minecraftEvents.AsyncPlayerChatEventListener;
import nl.thedutchmc.discordCC.minecraftEvents.PlayerDeathEventListener;
import nl.thedutchmc.discordCC.minecraftEvents.PlayerJoinEventListener;
import nl.thedutchmc.discordCC.minecraftEvents.PlayerQuitEventListener;

public class DiscordCC extends JavaPlugin {

	public static DiscordCC INSTANCE;
	public static ConfigurationHandler configHandler;
	public static JdaHandler jdaHandler;
	public static boolean POSTWORLD = false;
	
	private static final ConsoleAppender consoleAppender = new ConsoleAppender();
	private static final org.apache.logging.log4j.core.Logger rootLogger = (org.apache.logging.log4j.core.Logger) org.apache.logging.log4j.LogManager.getRootLogger();

	//org.apache.logging.log4j.core.Logger logger = (Logger) org.apache.logging.log4j.LogManager.getRootLogger();
	
	@Override
	public void onEnable() {
		//Assign all the static variables so they can be used throughout the plugin
		INSTANCE = this;
		configHandler = new ConfigurationHandler();
		jdaHandler = new JdaHandler();
		
		
		//Load and read the configuration file
		configHandler.loadConfig();

		if(!this.isEnabled()) return;

		//Set up the JDA instance
		jdaHandler.setupJda();
		
		//Send message to the console channel that we're hiding the startup-text, because ratelimits
		sendMessageToDiscord("**Server is starting!**", Channel.CONSOLE);
		sendMessageToDiscord("**Hiding server start console logs!**", Channel.CONSOLE);
		
		//Register JDA event listeners
		jdaHandler.getJda().addEventListener(new MessageReceivedEventListener());
		
		//Register the Minecraft event listeners
		Bukkit.getPluginManager().registerEvents(new AsyncPlayerChatEventListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerDeathEventListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoinEventListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerQuitEventListener(), this);
		
		rootLogger.addAppender(consoleAppender);
		
		sendMessageToDiscord(":ballot_box_with_check: **Server is loading!**", Channel.CHAT);
	}
	
	@Override
	public void onDisable() {
		sendMessageToDiscord(":octagonal_sign: **Server has stopped!**", Channel.CHAT, true);
		sendMessageToDiscord("**Server has stopped!**", Channel.CONSOLE, true);
	}
	
	//This method is used by JDA events to send a message to all players
	public static void sendMessageToPlayers(String message) {
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				String prefix = ChatColor.of("#" + ConfigurationHandler.ingamePrefixDiscordMessagesHexColor) + ConfigurationHandler.ingamePrefixDiscordMessages + ChatColor.RESET;
				
				for(Player p : Bukkit.getOnlinePlayers()) {
					p.sendMessage(prefix + " " + message);
				}
			}
		}.runTask(DiscordCC.INSTANCE);

	}
	
	//This method is used to send a message to a specific Discord channel
	//Can throw InterruptedIOException during shutdown
	public static void sendMessageToDiscord(String message, Channel channel, boolean... complete) {
		MessageChannel msgChannel = null;
				
		switch(channel) {
			case CONSOLE:
				msgChannel = JdaHandler.consoleChannel;
				break;
			case CHAT:
				msgChannel = JdaHandler.chatChannel;
				break;
		}
		
		if(msgChannel == null) {
			System.err.println("[DiscordCC] Critical error! a MessageChannel is null. Shutting down the plugin!");
			Bukkit.getPluginManager().disablePlugin(DiscordCC.INSTANCE);
		}
		
		if(complete.length > 0 && complete[0]) {
			msgChannel.sendMessage(message).complete();
		} else {
			msgChannel.sendMessage(message).queue();
		}
	}
}
