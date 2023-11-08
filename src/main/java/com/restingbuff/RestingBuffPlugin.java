package com.restingbuff;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.*;

@Slf4j
@PluginDescriptor(
	name = "Gielinor Kart"
)
public class RestingBuffPlugin extends Plugin
{
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
	@Getter
	private TrackTimer timer = new TrackTimer();
	private int textTimer = 5;
	private int locationTimer = 5;
	private WorldPoint lumbridgeStart = new WorldPoint(3223, 3223, 0);
	private WorldPoint alkharidGateEnd = new WorldPoint(3267, 3228, 0);
	public List<Race> races = List.of(new RaceBuilder().start(lumbridgeStart).end(alkharidGateEnd).buildRace());
	@Getter
	private WorldPoint playerLocation;
	@Getter
	private final Map<WorldPoint, String> startToTitle = new HashMap<>() {{
		put(lumbridgeStart, "The Lum Bridge");
	}};
	private final Map<Integer, RestingBuffConfig.Emote> idToEmote = new HashMap<>() {{
		put(866, RestingBuffConfig.Emote.DANCE);
		put(2110, RestingBuffConfig.Emote.RASPBERRY);
		put(858, RestingBuffConfig.Emote.BOW);
		put(2764, RestingBuffConfig.Emote.JOG);
	}};

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(timerOverlay);
	}

	@Subscribe
	public void onGameTick(GameTick t) {
		Player local = client.getLocalPlayer();
		playerLocation = local.getWorldLocation();
		timer.tick();
//		if (textTimer > 0) {
//			textTimer--;
//		}
//		else if (config.dreamtext() && local.getAnimation() == 7627) {
//			int dreamNum = (int) Math.floor(Math.random()*3);
//			client.getLocalPlayer().setOverheadText(dreamPhrases.get(dreamNum));
//			client.getLocalPlayer().setOverheadCycle(200);
//			textTimer = 10;
//		}
 		if(locationTimer > 0) {
			locationTimer--;
		} else if (local.getWorldLocation().equals(lumbridgeStart)) {
//			timerOverlay.setCourseName("The Lum Bridge");
			timer.reset();

			int animationID = local.getAnimation();
			if (idToEmote.get(animationID) == config.emote()) {
				timer.start();
			}
		} else if (local.getWorldLocation().equals(alkharidGateEnd) && timer.isActive()) {
			timer.stop();
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Course Completed! The Lum Bridge, Time: " + timerOverlay.formatTime(timer.getRealTime().getSeconds()), null);
		}
		else {
			locationTimer = 5;
		}

	}

	public void renderTile() {
		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged g) {
		if (g.getGameState() == GameState.LOGGED_IN) {
			log.info("overlay has been added");
			overlayManager.add(timerOverlay);
		} else {
			log.info("overlay has been removed");
			overlayManager.remove(timerOverlay);
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
