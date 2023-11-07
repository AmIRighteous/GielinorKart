package com.restingbuff;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private List<String> dreamPhrases = List.of("*zzzzzzzzzzzzzzzzzz*", "*One day I'll have that fire cape*", "*And I love you Nieve*");
	private WorldPoint lumbridgeStart = new WorldPoint(3223, 3223, 0);
	private WorldPoint alkharidGateEnd = new WorldPoint(3267, 3228, 0);
	private final int danceAnimation = 866;

	private Map<Integer, RestingBuffConfig.Emote> idToEmote = new HashMap<>() {{
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
		timer.tick();
		Player local = client.getLocalPlayer();
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
			timerOverlay.setCourseName("The Lum Bridge");
			timer.reset();
			overlayManager.add(timerOverlay);
//			log.info("Player is on lumbridge start tile!");
			int animationID = local.getAnimation();
			if (idToEmote.get(animationID) == config.emote()) {
				timer.start();
			}
		} else if (local.getWorldLocation().equals(alkharidGateEnd)) {
//			log.info("Player is at Al Kharid gate!");
			timer.stop();
		}
		else {
//			log.info("Player is on tile: " + local.getWorldLocation());
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
