package com.marvinight.myammo;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(MyAmmoConfig.GROUP)
public interface MyAmmoConfig extends Config
{
	String GROUP = "myammohighlighter";

	@ConfigItem(
		keyName = "onlyMine",
		name = "Only my drops",
		description = "Only highlight ammo the game says belongs to you (not other players' bolts)",
		position = 1
	)
	default boolean onlyMine()
	{
		return true;
	}

	@ConfigItem(
		keyName = "trackThrown",
		name = "Include thrown weapons",
		description = "Also highlight darts, knives, javelins etc. you have wielded",
		position = 2
	)
	default boolean trackThrown()
	{
		return true;
	}

	@ConfigItem(
		keyName = "extraItems",
		name = "Extra items",
		description = "Comma-separated item names to always highlight, wildcards allowed (e.g. *bolts*, Rune arrow)",
		position = 3
	)
	default String extraItems()
	{
		return "";
	}

	@Alpha
	@ConfigItem(
		keyName = "tileColor",
		name = "Tile color",
		description = "Outline color for tiles with your ammo",
		position = 4
	)
	default Color tileColor()
	{
		return new Color(0, 255, 140, 220);
	}

	@ConfigItem(
		keyName = "fillTile",
		name = "Fill tile",
		description = "Shade the tile as well as outlining it",
		position = 5
	)
	default boolean fillTile()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showText",
		name = "Show name and count",
		description = "Draw the ammo name and quantity above the tile",
		position = 6
	)
	default boolean showText()
	{
		return true;
	}
}
