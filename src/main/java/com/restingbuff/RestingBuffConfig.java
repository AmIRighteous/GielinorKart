package com.restingbuff;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Units;

@ConfigGroup("RestingBuff")
public interface RestingBuffConfig extends Config
{
	@ConfigItem(
		keyName = "DreamText",
		name = "Dream Text",
		description = "Text that will appear above you when you dream."
	)
	default boolean dreamtext()
	{
		return true;
	}

	@ConfigItem(
			keyName = "countdown",
			name = "Countdown",
			description = "The time to countdown from based on in-game time"
	)
	@Units(Units.MINUTES)
	default int countdown()
	{
		return 0;
	}
}
