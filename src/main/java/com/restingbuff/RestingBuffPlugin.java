package com.restingbuff;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.List;

@Slf4j
@PluginDescriptor(
	name = "Resting Buff"
)
public class RestingBuffPlugin extends Plugin
{
	public static final String CONFIG_GROUP = "ingametimer";
	public static final String CONFIG_KEY_SECONDS_ELAPSED = "secondsElapsed";
	@Inject
	private Client client;
	@Inject
	private RestingBuffConfig config;
	@Inject
	private ConfigManager configManager;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private TimerOverlay timerOverlay;
	private int textTimer = 5;
	private int locationTimer = 5;
	private List<String> dreamPhrases = List.of("*zzzzzzzzzzzzzzzzzz*", "*One day I'll have that fire cape*", "*And I love you Nieve*");
	private WorldPoint lumbridgeStart = new WorldPoint(3223, 3223, 0);
	private WorldPoint alkharidGateEnd = new WorldPoint(3267, 3228, 0);
	private final int danceAnimation = 866;

//	@Override
//	protected void startUp() throws Exception
//	{
//		overlayManager.add(timerOverlay);
//	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(timerOverlay);
	}

	public void saveSecondsElapsed(long secondsElapsed) {
		configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY_SECONDS_ELAPSED, secondsElapsed);
	}

	public String getSavedSecondsElapsed() {
		return configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY_SECONDS_ELAPSED);
	}

	@Subscribe
	public void onOverlayMenuClicked(OverlayMenuClicked event)
	{
		if (event.getEntry() == TimerOverlay.PAUSE_ENTRY) {
			timerOverlay.pauseTimer();
		}

		if(event.getEntry() == TimerOverlay.START_ENTRY) {
			timerOverlay.resumeTimer();
		}

		if(event.getEntry() == TimerOverlay.RESET_ENTRY) {
			timerOverlay.reset();
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged gameStateChanged) {
		if(gameStateChanged.getGameState() == GameState.LOGGED_IN) {
			timerOverlay.setLoggedIn(true);
		}

		if(gameStateChanged.getGameState() == GameState.LOGIN_SCREEN) {
			timerOverlay.setLoggedIn(false);
		}
	}


	@Subscribe
	public void onGameTick(GameTick t) {
		Player local = client.getLocalPlayer();
		if (textTimer > 0) {
			textTimer--;
		}
		else if (config.dreamtext() && local.getAnimation() == 7627) {
			int dreamNum = (int) Math.floor(Math.random()*3);
			client.getLocalPlayer().setOverheadText(dreamPhrases.get(dreamNum));
			client.getLocalPlayer().setOverheadCycle(200);
			textTimer = 10;
		}
		if(locationTimer > 0) {
			locationTimer--;
		} else if (local.getWorldLocation().equals(lumbridgeStart)) {
			log.info("Player is on lumbridge start tile!");
			int animationID = local.getAnimation();
			if (animationID == danceAnimation) {
				overlayManager.add(timerOverlay);
//				timerOverlay.
			}
			log.info("Animation is currently id = " + animationID);
		} else if (local.getWorldLocation().equals(alkharidGateEnd)) {
			log.info("Player is at Al kharid gate!");
		}
		else {
			log.info("Player is on tile: " + local.getWorldLocation());
			locationTimer = 5;
		}

	}

//	@Subscribe
//	public void onAnimationChanged(AnimationChanged event) {
//		if (event.getActor() instanceof Player) {
//			Player player = (Player) event.getActor();
//			int animationID = player.getAnimation();
//			if (player == client.getLocalPlayer() && animationID == 7627) {
//				log.info("Found it!");
//				client.getLocalPlayer().setOverheadText("zzz");
//				client.getLocalPlayer().setOverheadCycle(200);
//			}
//		}
//	}
	@Provides
	RestingBuffConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RestingBuffConfig.class);
	}
}
