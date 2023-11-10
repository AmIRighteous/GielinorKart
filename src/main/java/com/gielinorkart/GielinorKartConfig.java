package com.gielinorkart;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(GielinorKartConfig.CONFIG_GROUP_NAME)
public interface GielinorKartConfig extends Config
{
	String CONFIG_GROUP_NAME = "gielinorkart";
	enum Emote {
		DANCE,
		RASPBERRY,
		JOG,
		BOW,
	}

	@ConfigItem(
			keyName = "emoteStart",
			name = "Emote Starter",
			description = "Determines the emote that you use on the start tile to begin a race."
	)
	default Emote emote() {
		return Emote.DANCE;
	}

	@ConfigItem(
			keyName = "showLines",
			name = "Show Start/Finish lines",
			description = "Determines if you show the start/finish tiles."
	)
	default boolean showLines() {
		return true;
	}


	@ConfigItem(
			keyName = "showArrow",
			name = "Show Hint Arrow",
			description = "Show arrow that points to next tile in race."
	)
	default boolean showArrow() {
		return true;
	}
}
