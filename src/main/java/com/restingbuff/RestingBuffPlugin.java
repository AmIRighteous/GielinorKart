package com.restingbuff;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Example"
)
public class RestingBuffPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private RestingBuffConfig config;


//	@Subscribe
//	public void onGameStateChanged(GameStateChanged gameStateChanged)
//	{
//		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
//		{
//			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
//		}
//	}


	@Subscribe
	public void onAnimationChanged(AnimationChanged event) {
		if (event.getActor() instanceof Player) {
			Player player = (Player) event.getActor();
			int animationID = player.getAnimation();
			log.info("player found = " + player.getName());
			log.info("Animation ID found = " + animationID);
			if (player == client.getLocalPlayer() && animationID == 7627) {
				log.info("Found it!");
				client.getLocalPlayer().setOverheadText("zzz");
				client.getLocalPlayer().setOverheadCycle(200);
			}
		}
	}
	@Provides
	RestingBuffConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RestingBuffConfig.class);
	}
}
