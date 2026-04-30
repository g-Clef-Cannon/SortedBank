package com.sortedbank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;

/**
 * Defines ordered groups of related items for the ghost item feature.
 * When a player owns at least one item from a group, ghosts are shown
 * for the remaining items in that group.
 */
public final class ItemGroup
{
	private static final List<int[]> GROUPS = new ArrayList<>();
	private static final Map<Integer, List<int[]>> ITEM_TO_GROUPS = new HashMap<>();

	static
	{
		// Ores
		addGroup(
			ItemID.COPPER_ORE, ItemID.TIN_ORE, ItemID.IRON_ORE, ItemID.COAL,
			ItemID.SILVER_ORE, ItemID.GOLD_ORE, ItemID.MITHRIL_ORE,
			ItemID.ADAMANTITE_ORE, ItemID.RUNITE_ORE, ItemID.AMETHYST
		);

		// Bars
		addGroup(
			ItemID.BRONZE_BAR, ItemID.IRON_BAR, ItemID.STEEL_BAR,
			ItemID.SILVER_BAR, ItemID.GOLD_BAR, ItemID.MITHRIL_BAR,
			ItemID.ADAMANTITE_BAR, ItemID.RUNITE_BAR
		);

		// Logs
		addGroup(
			ItemID.LOGS, ItemID.OAK_LOGS, ItemID.WILLOW_LOGS,
			ItemID.MAPLE_LOGS, ItemID.YEW_LOGS, ItemID.MAGIC_LOGS,
			ItemID.REDWOOD_LOGS
		);

		// Standard runes
		addGroup(
			ItemID.AIR_RUNE, ItemID.WATER_RUNE, ItemID.EARTH_RUNE, ItemID.FIRE_RUNE,
			ItemID.MIND_RUNE, ItemID.BODY_RUNE, ItemID.COSMIC_RUNE, ItemID.CHAOS_RUNE,
			ItemID.NATURE_RUNE, ItemID.LAW_RUNE, ItemID.DEATH_RUNE,
			ItemID.BLOOD_RUNE, ItemID.SOUL_RUNE, ItemID.WRATH_RUNE
		);

		// Uncut gems
		addGroup(
			ItemID.UNCUT_SAPPHIRE, ItemID.UNCUT_EMERALD, ItemID.UNCUT_RUBY,
			ItemID.UNCUT_DIAMOND, ItemID.UNCUT_DRAGONSTONE, ItemID.UNCUT_ONYX,
			ItemID.UNCUT_ZENYTE
		);

		// Cut gems
		addGroup(
			ItemID.SAPPHIRE, ItemID.EMERALD, ItemID.RUBY,
			ItemID.DIAMOND, ItemID.DRAGONSTONE, ItemID.ONYX,
			ItemID.ZENYTE
		);

		// Arrows
		addGroup(
			ItemID.BRONZE_ARROW, ItemID.IRON_ARROW, ItemID.STEEL_ARROW,
			ItemID.MITHRIL_ARROW, ItemID.ADAMANT_ARROW, ItemID.RUNE_ARROW,
			ItemID.DRAGON_ARROW
		);

		// Bolts
		addGroup(
			ItemID.BRONZE_BOLTS, ItemID.IRON_BOLTS, ItemID.STEEL_BOLTS,
			ItemID.MITHRIL_BOLTS, ItemID.ADAMANT_BOLTS, ItemID.RUNITE_BOLTS,
			ItemID.DRAGON_BOLTS
		);

		// Daggers
		addGroup(
			ItemID.BRONZE_DAGGER, ItemID.IRON_DAGGER, ItemID.STEEL_DAGGER,
			ItemID.BLACK_DAGGER, ItemID.MITHRIL_DAGGER, ItemID.ADAMANT_DAGGER,
			ItemID.RUNE_DAGGER, ItemID.DRAGON_DAGGER
		);

		// Scimitars
		addGroup(
			ItemID.BRONZE_SCIMITAR, ItemID.IRON_SCIMITAR, ItemID.STEEL_SCIMITAR,
			ItemID.BLACK_SCIMITAR, ItemID.MITHRIL_SCIMITAR, ItemID.ADAMANT_SCIMITAR,
			ItemID.RUNE_SCIMITAR, ItemID.DRAGON_SCIMITAR
		);

		// Full helms
		addGroup(
			ItemID.BRONZE_FULL_HELM, ItemID.IRON_FULL_HELM, ItemID.STEEL_FULL_HELM,
			ItemID.BLACK_FULL_HELM, ItemID.MITHRIL_FULL_HELM, ItemID.ADAMANT_FULL_HELM,
			ItemID.RUNE_FULL_HELM
		);

		// Platebodies
		addGroup(
			ItemID.BRONZE_PLATEBODY, ItemID.IRON_PLATEBODY, ItemID.STEEL_PLATEBODY,
			ItemID.BLACK_PLATEBODY, ItemID.MITHRIL_PLATEBODY, ItemID.ADAMANT_PLATEBODY,
			ItemID.RUNE_PLATEBODY
		);

		// Platelegs
		addGroup(
			ItemID.BRONZE_PLATELEGS, ItemID.IRON_PLATELEGS, ItemID.STEEL_PLATELEGS,
			ItemID.BLACK_PLATELEGS, ItemID.MITHRIL_PLATELEGS, ItemID.ADAMANT_PLATELEGS,
			ItemID.RUNE_PLATELEGS
		);

		// Kiteshields
		addGroup(
			ItemID.BRONZE_KITESHIELD, ItemID.IRON_KITESHIELD, ItemID.STEEL_KITESHIELD,
			ItemID.BLACK_KITESHIELD, ItemID.MITHRIL_KITESHIELD, ItemID.ADAMANT_KITESHIELD,
			ItemID.RUNE_KITESHIELD
		);

		// Herb seeds
		addGroup(
			ItemID.GUAM_SEED, ItemID.MARRENTILL_SEED, ItemID.TARROMIN_SEED,
			ItemID.HARRALANDER_SEED, ItemID.RANARR_SEED, ItemID.TOADFLAX_SEED,
			ItemID.IRIT_SEED, ItemID.AVANTOE_SEED, ItemID.KWUARM_SEED,
			ItemID.SNAPDRAGON_SEED, ItemID.CADANTINE_SEED, ItemID.LANTADYME_SEED,
			ItemID.DWARF_WEED_SEED, ItemID.TORSTOL_SEED
		);

		// Tree seeds
		addGroup(
			ItemID.ACORN, ItemID.WILLOW_SEED, ItemID.MAPLE_SEED,
			ItemID.YEW_SEED, ItemID.MAGIC_SEED
		);
	}

	private ItemGroup()
	{
	}

	private static void addGroup(int... itemIds)
	{
		GROUPS.add(itemIds);
		for (int id : itemIds)
		{
			ITEM_TO_GROUPS.computeIfAbsent(id, k -> new ArrayList<>()).add(itemIds);
		}
	}

	/**
	 * Returns all groups that contain the given item ID.
	 */
	public static List<int[]> getGroupsForItem(int itemId)
	{
		return ITEM_TO_GROUPS.getOrDefault(itemId, Collections.emptyList());
	}

	/**
	 * Returns all defined item groups.
	 */
	public static List<int[]> getAllGroups()
	{
		return Collections.unmodifiableList(GROUPS);
	}
}
