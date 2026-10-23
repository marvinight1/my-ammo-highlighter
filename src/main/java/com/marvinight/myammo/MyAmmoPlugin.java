package com.marvinight.myammo;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import lombok.Value;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.WildcardMatcher;

@PluginDescriptor(
	name = "My Ammo Highlighter",
	description = "Highlights your own ammo (bolts, arrows, darts...) lying on the ground",
	tags = {"ammo", "bolts", "arrows", "darts", "ranged", "ground", "highlight"}
)
public class MyAmmoPlugin extends Plugin
{
	/** Equipment ("worn") item container id. */
	private static final int WORN_CONTAINER = 94;

	private static final String[] THROWN_KEYWORDS = {
		"dart", "knife", "javelin", "thrownaxe", "chinchompa", "toktz-xil-ul"
	};

	@Inject private Client client;
	@Inject private ClientThread clientThread;
	@Inject private ItemManager itemManager;
	@Inject private OverlayManager overlayManager;
	@Inject private MyAmmoOverlay overlay;
	@Inject private MyAmmoConfig config;

	/** Every ammo id you've had equipped this session, so piles still highlight after your quiver runs dry. */
	private final Set<Integer> ammoIds = new HashSet<>();
	private final Map<TileItem, GroundAmmo> groundAmmo = new LinkedHashMap<>();
	private List<String> extraPatterns = new ArrayList<>();

	@Value
	static class GroundAmmo
	{
		Tile tile;
		TileItem item;
		String name;
	}

	@Provides
	MyAmmoConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MyAmmoConfig.class);
	}

	@Override
	protected void startUp()
	{
		parseExtraPatterns();
		overlayManager.add(overlay);
		clientThread.invokeLater(this::refreshEquipment);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		groundAmmo.clear();
		ammoIds.clear();
	}

	Collection<GroundAmmo> getGroundAmmo()
	{
		return groundAmmo.values();
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() == WORN_CONTAINER)
		{
			refreshEquipment();
		}
	}

	@Subscribe
	public void onItemSpawned(ItemSpawned event)
	{
		TileItem item = event.getItem();
		if (isMyAmmo(item))
		{
			String name = itemManager.getItemComposition(item.getId()).getName();
			groundAmmo.put(item, new GroundAmmo(event.getTile(), item, name));
		}
	}

	@Subscribe
	public void onItemDespawned(ItemDespawned event)
	{
		groundAmmo.remove(event.getItem());
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		// Items re-fire ItemSpawned after a region load, so start clean.
		if (event.getGameState() == GameState.LOADING || event.getGameState() == GameState.LOGIN_SCREEN)
		{
			groundAmmo.clear();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (MyAmmoConfig.GROUP.equals(event.getGroup()))
		{
			parseExtraPatterns();
		}
	}

	private void refreshEquipment()
	{
		ItemContainer worn = client.getItemContainer(WORN_CONTAINER);
		if (worn == null)
		{
			return;
		}

		Item ammo = worn.getItem(EquipmentInventorySlot.AMMO.getSlotIdx());
		if (ammo != null && ammo.getId() > 0)
		{
			ammoIds.add(ammo.getId());
		}

		if (config.trackThrown())
		{
			Item weapon = worn.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
			if (weapon != null && weapon.getId() > 0 && isThrownWeapon(weapon.getId()))
			{
				ammoIds.add(weapon.getId());
			}
		}
	}

	private boolean isThrownWeapon(int itemId)
	{
		String name = itemManager.getItemComposition(itemId).getName().toLowerCase();
		for (String keyword : THROWN_KEYWORDS)
		{
			if (name.contains(keyword))
			{
				return true;
			}
		}
		return false;
	}

	private boolean isMyAmmo(TileItem item)
	{
		if (config.onlyMine() && item.getOwnership() != TileItem.OWNERSHIP_SELF)
		{
			return false;
		}

		if (ammoIds.contains(item.getId()))
		{
			return true;
		}

		if (!extraPatterns.isEmpty())
		{
			String name = itemManager.getItemComposition(item.getId()).getName();
			for (String pattern : extraPatterns)
			{
				if (WildcardMatcher.matches(pattern, name))
				{
					return true;
				}
			}
		}
		return false;
	}

	private void parseExtraPatterns()
	{
		List<String> patterns = new ArrayList<>();
		for (String part : config.extraItems().split(","))
		{
			String trimmed = part.trim();
			if (!trimmed.isEmpty())
			{
				patterns.add(trimmed);
			}
		}
		extraPatterns = patterns;
	}
}
