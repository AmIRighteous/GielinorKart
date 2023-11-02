package com.restingbuff;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class RestingBuffPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(RestingBuffPlugin.class);
		RuneLite.main(args);
	}
}