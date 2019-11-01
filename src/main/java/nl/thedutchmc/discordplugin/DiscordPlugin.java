package nl.thedutchmc.discordplugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDA.Status;
import net.dv8tion.jda.api.JDABuilder;
import net.md_5.bungee.api.ChatColor;

public class DiscordPlugin extends JavaPlugin {

	private JDA jda;
	
	private String token = "";
	public String chatLinkChannelId = "";
	public String botCommandChannelId = "";
	public String commandPrefix = "";
	public String whitelistRoleId = "";
	public String botReportChannelId = "";
	
	List<String> profanityList = new ArrayList<String>();
	
	public boolean enableWhitelisting, restrictBotCommandsToChannel, requireWhitelistRole, enableBotReport, enableChatMonitoring;
	
	public static DiscordPlugin instance;
	
	DiscordListener dl = new DiscordListener(this);
	
	Plugin plugin = this;
    
	private File customConfigFile;
    private FileConfiguration customConfig;
	
    private File profanityListFile;
    private FileConfiguration profanityListConfigurator;
	
    public JDA getJda() {
		return jda;
	}
	
	@Override
	public void onEnable() {
		System.out.println("[DiscordPlugin] Plugin starting...");
		
		configHandler();
		
		profanityListHandler();
		
		try {
			jda = new JDABuilder(AccountType.BOT).setToken(token).build();
			
			boolean settingUp = true;
			while(settingUp) {
				if(jda.awaitStatus(Status.CONNECTED ) != null) {
					jda.addEventListener(dl);
					dl.sendToDiscord(":white_check_mark: **Server has started!**", "SERVER_CHAT","");
					settingUp = false;
					System.out.println("[DiscordPlugin] Bot is logged in.");
				}
			}

		} catch (LoginException e) {
			System.err.println("[DiscordPlugin] Failed to log in!");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		getServer().getPluginManager().registerEvents(new EventHandlers(dl, this), this);
	}
	
	@Override
	public void onDisable() {
		dl.sendToDiscord(":octagonal_sign: **Server has stopped!**", "SERVER_CHAT","");
	}
	
    public FileConfiguration getCustomConfig() {
        return this.customConfig;
    }
	
    public FileConfiguration getProfanityList() {
    	return this.profanityListConfigurator;
    }
	public void configHandler() {
        customConfigFile = new File(getDataFolder(), "config.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
            System.out.println("Saving config file...");
         }

        customConfig= new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        
        token = customConfig.getString("BotToken");
        chatLinkChannelId = customConfig.getString("ChatLinkChannelId");
        botCommandChannelId = customConfig.getString("BotCommandChannelId");
        commandPrefix = customConfig.getString("CommandPrefix");
        whitelistRoleId = customConfig.getString("WhitelistRoleId");
        enableWhitelisting = customConfig.getBoolean("EnableWhitelisting");
        restrictBotCommandsToChannel = customConfig.getBoolean("RestrictBotCommandsToChannel");
        requireWhitelistRole = customConfig.getBoolean("RequireWhitelistRole");
        botReportChannelId = customConfig.getString("BotReportChannelId");
        enableBotReport = customConfig.getBoolean("EnableBotReport");
        enableChatMonitoring = customConfig.getBoolean("EnableChatMonitoring");
        
        
        if(token.equals("0")) {
        	System.err.println("[DiscordPlugin] Bot Token cannot be 0! Shutting Down....");
        	plugin.getServer().shutdown();

        }
	}
	
	@SuppressWarnings("unchecked")
	public void profanityListHandler() {
		profanityListFile = new File(getDataFolder(), "list.yml");
		if(!profanityListFile.exists()) {
            saveResource("list.yml", false);
		}
		
		profanityListConfigurator = new YamlConfiguration();
		try {
			profanityListConfigurator.load(profanityListFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		profanityList = (List<String>) profanityListConfigurator.getList("profanity");
		
	}
	
	
	public void sendToServer(String message, String author) {
		Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.BLUE + author + ChatColor.GRAY + "] " + ChatColor.WHITE + message);
	}
	
	public void runCommand(String command) {
		Bukkit.getScheduler().callSyncMethod(this, () -> Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command));
	}
}
