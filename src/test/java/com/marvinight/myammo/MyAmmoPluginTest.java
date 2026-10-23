package com.marvinight.myammo;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

/** Run this main() (or ./gradlew run) to start RuneLite with the plugin loaded. */
public class MyAmmoPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(MyAmmoPlugin.class);
		RuneLite.main(args);
	}
}
