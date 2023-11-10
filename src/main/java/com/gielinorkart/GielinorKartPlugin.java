package com.gielinorkart;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(
	name = "Gielinor Kart"
)
public class GielinorKartPlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private GielinorKartConfig config;
	@Inject
	private ConfigManager configManager;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private TimerOverlay timerOverlay;
	@Inject
	private TileMarkersOverlay tileMarkersOverlay;
	@Inject
	private TileArrowOverlay tileArrowOverlay;
	@Getter
	private TrackTimer timer = new TrackTimer();
	private WorldPoint lumbridgeStart = new WorldPoint(3223, 3223, 0);
	private WorldPoint alkharidGateEnd = new WorldPoint(3267, 3228, 0);

	private WorldPoint lummyCastleStart = new WorldPoint(3234, 3217, 0);
	private WorldPoint lummyCastleEnd = new WorldPoint(3234, 3224, 0);
	private WorldPoint lummyCastleCheckOne = new WorldPoint(3206, 3211, 0);
	private WorldPoint lummyCastleCheckTwo = new WorldPoint(3208, 3238, 0);
	private Race currRace;
	private List<WorldPoint> checkpointsVisited = new ArrayList<>();

	public final List<Race> races = List.of(
			new RaceBuilder().courseName("The Lum Bridge").start(lumbridgeStart).end(alkharidGateEnd).buildRace(),
			new RaceBuilder().courseName("The Lumbridge Castle").start(lummyCastleStart).end(lummyCastleEnd).checkpoint(List.of(lummyCastleCheckOne, lummyCastleCheckTwo)).buildRace());
	@Getter
	private WorldPoint playerLocation;
	@Getter
	private final Map<WorldPoint, String> startToTitle = new HashMap<>() {{
		put(lumbridgeStart, "The Lum Bridge");
		put(lummyCastleStart, "The Lumbridge Castle");
	}};
	private final Map<Race, Long> raceHiScores = new HashMap<>();
	private final Map<Integer, GielinorKartConfig.Emote> idToEmote = new HashMap<>() {{
		put(866, GielinorKartConfig.Emote.DANCE);
		put(2110, GielinorKartConfig.Emote.RASPBERRY);
		put(858, GielinorKartConfig.Emote.BOW);
		put(2764, GielinorKartConfig.Emote.JOG);
	}};

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(timerOverlay);
		overlayManager.remove(tileMarkersOverlay);
		overlayManager.remove(tileArrowOverlay);
	}

	@Subscribe
	public void onGameTick(GameTick t) {
		Player local = client.getLocalPlayer();
		playerLocation = local.getWorldLocation();
		timer.tick();
		if (currRace != null) {
			if (currRace.getCheckpoints().contains(playerLocation) && !checkpointsVisited.contains(playerLocation)) {
				checkpointsVisited.add(playerLocation);
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Checkpoint "+ checkpointsVisited.size() + ", Split: " + timerOverlay.formatTime(timer.getRealTime().getSeconds()), null);
			} else if (playerLocation.equals(currRace.getEnd())) {
				if (checkpointsVisited.size() == currRace.getCheckpoints().size()) {
					timer.stop();
					sendEndRaceMsg(currRace.getCourseName());
					updateHiScores();
					currRace = null;
					checkpointsVisited.clear();
				}
			}
		} else {
			List<Race> tempRace = races.stream().filter(r -> r.getStart().equals(local.getWorldLocation())).collect(Collectors.toList());
			if (tempRace.size() != 1 || timer.isActive()) return;
			else {
				Race r = tempRace.get(0);
				timer.reset(); //TODO I'm not convinced this is the best place to put a timer reset, but not sure where it should go
				int animationID = local.getAnimation();
				if (idToEmote.get(animationID) == config.emote())
				{
					timer.start();
					currRace = r;
				}
			}
		}
	}

	private void updateHiScores() {
		long t = timer.getRealTime().getSeconds();
		if (!raceHiScores.containsKey(currRace) || raceHiScores.get(currRace) > t) {
			raceHiScores.put(currRace, timer.getRealTime().getSeconds());
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "New Record! Time: " + timerOverlay.formatTime(t), null);
		} else if (raceHiScores.get(currRace) <= t) {
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Personal Best: " + timerOverlay.formatTime(raceHiScores.get(currRace)), null);
		}
	}

	private void sendEndRaceMsg(String courseName) {
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Course Completed! "+ courseName+", Time: " + timerOverlay.formatTime(timer.getRealTime().getSeconds()), null);
	}

	public WorldPoint getNextTile() {
		if (currRace == null) {
			return null;
		} else if (checkpointsVisited.size() < currRace.getCheckpoints().size()) {
			return currRace.getCheckpoints().get(checkpointsVisited.size());
		} else {
			return currRace.getEnd();
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged g) {
		if (g.getGameState() == GameState.LOGGED_IN) {
			log.info("overlay has been added");
			overlayManager.add(timerOverlay);
			overlayManager.add(tileMarkersOverlay);
			overlayManager.add(tileArrowOverlay);
			loadHiScores();
		} else {
			saveHiScores();
			log.info("overlay has been removed");
			overlayManager.remove(timerOverlay);
			overlayManager.remove(tileMarkersOverlay);
			overlayManager.remove(tileArrowOverlay);
		}
	}

	private void loadHiScores() {
		for (Race r: races) {
			Long hiscore = configManager.getRSProfileConfiguration(GielinorKartConfig.CONFIG_GROUP_NAME, r.getCourseName(), long.class);
			if (hiscore != null) {
				raceHiScores.put(r, hiscore);
			}
		}
		log.info("Race hiscores = " + raceHiScores);
	}

	private void saveHiScores() {
		for (Race r: raceHiScores.keySet()) {
			configManager.setRSProfileConfiguration(GielinorKartConfig.CONFIG_GROUP_NAME, r.getCourseName(), raceHiScores.get(r));
		}
	}


	@Provides
	GielinorKartConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GielinorKartConfig.class);
	}
}
