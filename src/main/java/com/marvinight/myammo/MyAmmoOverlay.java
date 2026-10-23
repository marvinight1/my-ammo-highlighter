package com.marvinight.myammo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class MyAmmoOverlay extends Overlay
{
	private final Client client;
	private final MyAmmoPlugin plugin;
	private final MyAmmoConfig config;

	@Inject
	MyAmmoOverlay(Client client, MyAmmoPlugin plugin, MyAmmoConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D g)
	{
		Color color = config.tileColor();
		Color fill = config.fillTile()
			? new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(60, color.getAlpha()))
			: new Color(0, 0, 0, 0);

		// Combine stacks of the same ammo on one tile into one label.
		Map<Tile, Map<String, Integer>> byTile = new HashMap<>();
		for (MyAmmoPlugin.GroundAmmo ammo : plugin.getGroundAmmo())
		{
			if (ammo.getTile().getPlane() != client.getPlane())
			{
				continue;
			}
			byTile.computeIfAbsent(ammo.getTile(), t -> new HashMap<>())
				.merge(ammo.getName(), ammo.getItem().getQuantity(), Integer::sum);
		}

		for (Map.Entry<Tile, Map<String, Integer>> entry : byTile.entrySet())
		{
			LocalPoint lp = entry.getKey().getLocalLocation();
			if (lp == null)
			{
				continue;
			}

			Polygon poly = Perspective.getCanvasTilePoly(client, lp);
			if (poly != null)
			{
				OverlayUtil.renderPolygon(g, poly, color, fill, new BasicStroke(2f));
			}

			if (config.showText())
			{
				int z = 20;
				for (Map.Entry<String, Integer> item : entry.getValue().entrySet())
				{
					String text = item.getKey() + " (" + item.getValue() + ")";
					Point p = Perspective.getCanvasTextLocation(client, g, lp, text, z);
					if (p != null)
					{
						OverlayUtil.renderTextLocation(g, p, text, color);
					}
					z += 16;
				}
			}
		}
		return null;
	}
}
