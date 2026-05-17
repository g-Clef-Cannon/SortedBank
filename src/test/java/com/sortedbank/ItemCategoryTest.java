package com.sortedbank;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ItemCategoryTest
{
	@Test
	public void categorizesKnownFailureCases()
	{
		assertCategory("Grimy cadantine", ItemCategory.HERB);
		assertCategory("Steel bar", ItemCategory.ORE);
		assertCategory("Bones", ItemCategory.PRAYER_ITEM);
		assertCategory("Big bones", ItemCategory.PRAYER_ITEM);
		assertCategory("Fishing bait", ItemCategory.TOOL);
		assertCategory("Feather", ItemCategory.FLETCHING_COMPONENT);
	}

	@Test
	public void categorizesCoreSkillingItems()
	{
		assertCategory("Knife", ItemCategory.TOOL);
		assertCategory("Hammer", ItemCategory.TOOL);
		assertCategory("Chisel", ItemCategory.TOOL);
		assertCategory("Vial", ItemCategory.CONTAINER);
		assertCategory("Vial of water", ItemCategory.CONTAINER);
		assertCategory("Leather", ItemCategory.PROCESSED_MATERIAL);
		assertCategory("Dynamite", ItemCategory.TOOL);
		assertCategory("Maple longbow (u)", ItemCategory.FLETCHING_COMPONENT);
		assertCategory("Willow shortbow (u)", ItemCategory.FLETCHING_COMPONENT);
		assertCategory("Bow string", ItemCategory.FLETCHING_COMPONENT);
		assertCategory("Bowstring", ItemCategory.FLETCHING_COMPONENT);
		assertCategory("Bronze bolts (unf)", ItemCategory.FLETCHING_COMPONENT);
		assertCategory("Javelin shaft", ItemCategory.FLETCHING_COMPONENT);
		assertCategory("Body talisman", ItemCategory.TOOL);
		assertCategory("Nature talisman", ItemCategory.TOOL);
		assertCategory("Red dragonhide", ItemCategory.PROCESSED_MATERIAL);
		assertCategory("Xerician fabric", ItemCategory.PROCESSED_MATERIAL);
		assertCategory("Thread", ItemCategory.PROCESSED_MATERIAL);
	}

	@Test
	public void categorizesCombatAndTeleportItems()
	{
		assertCategory("Water rune", ItemCategory.RUNE);
		assertCategory("Varrock teleport", ItemCategory.TELEPORT);
		assertCategory("Ring of dueling(8)", ItemCategory.TELEPORT);
		assertCategory("Ruby ring", ItemCategory.JEWELRY);
		assertCategory("Xeric's talisman", ItemCategory.TELEPORT);
		assertCategory("Bronze arrow", ItemCategory.AMMO);
		assertCategory("Bronze knife", ItemCategory.AMMO, "Wield");
		assertCategory("Bronze knives", ItemCategory.AMMO);
		assertCategory("Black warhammer", ItemCategory.MELEE_WEAPON, "Wield");
		assertCategory("Xerician top", ItemCategory.MAGIC_ARMOR, "Wear");
		assertCategory("Shayzien body (5)", ItemCategory.MELEE_ARMOR, "Wear");
		assertCategory("Beekeeper's gloves", ItemCategory.COSMETIC, "Wear");
		assertCategory("Zombie boots", ItemCategory.COSMETIC, "Wear");
		assertCategory("Camo helmet", ItemCategory.COSMETIC, "Wear");
	}

	@Test
	public void categorizesQuestAndClueItems()
	{
		assertCategory("Clue scroll (easy)", ItemCategory.QUEST_ITEM);
		assertCategory("Clue scroll (hard)", ItemCategory.QUEST_ITEM);
		assertCategory("Tattered page", ItemCategory.QUEST_ITEM);
		assertCategory("Tattered page 1", ItemCategory.QUEST_ITEM);
		assertCategory("Black bead", ItemCategory.QUEST_ITEM);
		assertCategory("Scroll box (beginner)", ItemCategory.QUEST_ITEM);
	}

	@Test
	public void categorizesConsumablesKeysAndPrayerItems()
	{
		assertCategory("Raw lobster", ItemCategory.FOOD);
		assertCategory("Beer", ItemCategory.FOOD, "Drink");
		assertCategory("Dwarven stout", ItemCategory.FOOD, "Drink");
		assertCategory("Superantipoison(4)", ItemCategory.POTION);
		assertCategory("Weapon poison(+)", ItemCategory.POTION);
		assertCategory("Super attack(4)", ItemCategory.POTION);
		assertCategory("Super strength(2)", ItemCategory.POTION);
		assertCategory("Super defence(4)", ItemCategory.POTION);
		assertCategory("Ranging potion(2)", ItemCategory.POTION);
		assertCategory("Giant key", ItemCategory.KEY);
		assertCategory("Grubby key", ItemCategory.KEY);
		assertCategory("Ensouled giant head", ItemCategory.PRAYER_ITEM);
		assertCategory("Long bone", ItemCategory.PRAYER_ITEM);
		assertCategory("Limpwurt root", ItemCategory.HERB);
		assertCategory("Red spiders' eggs", ItemCategory.HERB);
		assertCategory("Mushroom spore", ItemCategory.SEED);
		assertCategory("Mark of grace", ItemCategory.COLLECTIBLE);
		assertCategory("Frog token", ItemCategory.COLLECTIBLE);
		assertCategory("Ancient shard", ItemCategory.COLLECTIBLE);
		assertCategory("Lizardman fang", ItemCategory.COLLECTIBLE);
	}

	private static void assertCategory(String itemName, ItemCategory expectedCategory, String... actions)
	{
		assertEquals(itemName, expectedCategory, ItemCategory.categorize(itemName, actions));
	}
}
