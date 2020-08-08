package nl.thedutchmc.discordCC;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigurationHandler {

	public static String botToken, consoleChannel, chatChannel, 
		ingamePrefixDiscordMessages, ingamePrefixDiscordMessagesHexColor,
		playerJoinPrefix, playerLeavePrefix;
	public static boolean consoleEnabled, chatEnabled;
	
	private File file;
	private FileConfiguration config;
	
	public FileConfiguration getConfig() {
		return config;
	}
	
	public void loadConfig() {
		file = new File(DiscordCC.INSTANCE.getDataFolder(), "config.yml");
		
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			DiscordCC.INSTANCE.saveResource("config.yml", false);
		}
		 
		config = new YamlConfiguration();
		
		try {
			config.load(file);
			readConfig();
		} catch (IOException e) {
			System.err.println("[DiscordCC] Oei, we've got an IOException! Disabling the plugin. The stacktrace is as follows:");
			Bukkit.getPluginManager().disablePlugin(DiscordCC.INSTANCE);
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			System.err.println("[DiscordCC] Ouch, you've got an error in your config! Disabling the plugin.");
			Bukkit.getPluginManager().disablePlugin(DiscordCC.INSTANCE);
		}
	}
	
	public void readConfig() {
		botToken = this.getConfig().getString("botToken");
		consoleChannel = this.getConfig().getString("consoleChannel");
		chatChannel = this.getConfig().getString("chatChannel");
		ingamePrefixDiscordMessages = this.getConfig().getString("ingamePrefixDiscordMessages");
		ingamePrefixDiscordMessagesHexColor = this.getConfig().getString("ingamePrefixDiscordMessagesHexColor");
		
		playerJoinPrefix = this.getConfig().getString("playerJoinPrefix");
		playerLeavePrefix = this.getConfig().getString("playerLeavePrefix");
		
		consoleEnabled = Boolean.valueOf(this.getConfig().getString("consoleEnabled"));
		chatEnabled = Boolean.valueOf(this.getConfig().getString("chatEnabled"));
		
		//Validate the config
		if(botToken == null || botToken == "") invalidConfig();
		if(consoleEnabled && (consoleChannel == null || consoleChannel == "")) invalidConfig();
		if(chatEnabled && (chatChannel == null || chatChannel == "")) invalidConfig();
			
	}
	
	void invalidConfig() {
		System.err.println("[DiscordCC] It seems a field is empty in config.yml! Disabling plugin");
		Bukkit.getPluginManager().disablePlugin(DiscordCC.INSTANCE);
	}
}
