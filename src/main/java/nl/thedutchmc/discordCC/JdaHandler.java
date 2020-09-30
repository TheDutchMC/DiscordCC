package nl.thedutchmc.discordCC;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class JdaHandler {

	private static JDA jda;
	
	public static MessageChannel consoleChannel, chatChannel;
	
	public void setupJda() {

		//Set up the JDA instance and connect it to Discord
		try {
			
			//Specify the GatewayIntents we want to use
			List<GatewayIntent> intents = new ArrayList<>();
			intents.add(GatewayIntent.GUILD_MESSAGES);
			
			jda = JDABuilder.createDefault(ConfigurationHandler.botToken)
					.setActivity(Activity.playing("Administering"))
					.enableIntents(intents)
					.build();

		} catch (LoginException e) {
			DiscordCC.logWarn("[DiscordCC] Oei, we can't log in to Discord! Is your token valid? Disabling the plugin");
			Bukkit.getPluginManager().disablePlugin(DiscordCC.INSTANCE);
		}
		
		//We want to wait for JDA to be ready until we continue
		try {
			jda.awaitReady();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Setup the Text channels
		consoleChannel = jda.getTextChannelById(ConfigurationHandler.consoleChannel);
		chatChannel = jda.getTextChannelById(ConfigurationHandler.chatChannel);
	}
	
	public JDA getJda() {
		return jda;
	}
	
	public void setJda(JDA jda) {
		JdaHandler.jda = jda;
	}
	
	public static void shutdownJda() throws Exception {
		jda.shutdownNow();
	}
}
