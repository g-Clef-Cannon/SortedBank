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
		assertCategory("Bolt mould", ItemCategory.TOOL);
		assertCategory("Ring mould", ItemCategory.TOOL);
		assertCategory("Necklace mould", ItemCategory.TOOL);
		assertCategory("Bracelet mould", ItemCategory.TOOL);
		assertCategory("Holy mould", ItemCategory.TOOL);
		assertCategory("Sickle mould", ItemCategory.TOOL);
		assertCategory("Tiara mould", ItemCategory.TOOL);
		assertCategory("Glassblowing pipe", ItemCategory.TOOL);
		assertCategory("Lobster pot", ItemCategory.TOOL);
		assertCategory("Lockpick", ItemCategory.TOOL);
		assertCategory("Bait pack", ItemCategory.TOOL);
		assertCategory("Fish offcuts", ItemCategory.TOOL);
		assertCategory("Bruma torch (off-hand)", ItemCategory.TOOL);
		assertCategory("Empty candle lantern", ItemCategory.TOOL);
		assertCategory("Empty oil lamp", ItemCategory.TOOL);
		assertCategory("Vial", ItemCategory.CONTAINER);
		assertCategory("Vial of water", ItemCategory.CONTAINER);
		assertCategory("Empty fishbowl", ItemCategory.CONTAINER);
		assertCategory("Basket", ItemCategory.CONTAINER);
		assertCategory("Sack pack", ItemCategory.CONTAINER);
		assertCategory("Leather", ItemCategory.PROCESSED_MATERIAL);
		assertCategory("Ball of wool", ItemCategory.PROCESSED_MATERIAL);
		assertCategory("Flax", ItemCategory.PROCESSED_MATERIAL);
		assertCategory("Seaweed", ItemCategory.PROCESSED_MATERIAL);
		assertCategory("Soda ash", ItemCategory.PROCESSED_MATERIAL);
		assertCategory("Molten glass", ItemCategory.PROCESSED_MATERIAL);
		assertCategory("Bucket of sand", ItemCategory.PROCESSED_MATERIAL);
		assertCategory("Steel studs", ItemCategory.PROCESSED_MATERIAL);
		assertCategory("Dynamite", ItemCategory.TOOL);
		assertCategory("Maple longbow (u)", ItemCategory.FLETCHING_COMPONENT);
		assertCategory("Willow shortbow (u)", ItemCategory.FLETCHING_COMPONENT);
		assertCategory("Bow string", ItemCategory.FLETCHING_COMPONENT);
		assertCategory("Bowstring", ItemCategory.FLETCHING_COMPONENT);
		assertCategory("Bronze bolts (unf)", ItemCategory.FLETCHING_COMPONENT);
		assertCategory("Javelin shaft", ItemCategory.FLETCHING_COMPONENT);
		assertCategory("Dragon dart tip", ItemCategory.FLETCHING_COMPONENT);
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
		assertCategory("Kharedst's memoirs", ItemCategory.TELEPORT);
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
		assertCategory("Burnt fish", ItemCategory.FOOD);
		assertCategory("Burnt lobster", ItemCategory.FOOD);
		assertCategory("Burnt swordfish", ItemCategory.FOOD);
		assertCategory("Apples(5)", ItemCategory.FOOD);
		assertCategory("Bananas(5)", ItemCategory.FOOD);
		assertCategory("Strawberries(1)", ItemCategory.FOOD);
		assertCategory("Cooking apple", ItemCategory.FOOD);
		assertCategory("Redberries", ItemCategory.FOOD);
		assertCategory("Golovanova fruit top", ItemCategory.FOOD);
		assertCategory("Beer", ItemCategory.FOOD, "Drink");
		assertCategory("Dwarven stout", ItemCategory.FOOD, "Drink");
		assertCategory("Superantipoison(4)", ItemCategory.POTION);
		assertCategory("Anti-venom(4)", ItemCategory.POTION);
		assertCategory("Sanfew serum(4)", ItemCategory.POTION);
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
		assertCategory("Compost", ItemCategory.SEED);
		assertCategory("Saltpetre", ItemCategory.SEED);
		assertCategory("Mark of grace", ItemCategory.COLLECTIBLE);
		assertCategory("Frog token", ItemCategory.COLLECTIBLE);
		assertCategory("Ancient shard", ItemCategory.COLLECTIBLE);
		assertCategory("Lizardman fang", ItemCategory.COLLECTIBLE);
		assertCategory("Molch pearl", ItemCategory.COLLECTIBLE);
		assertCategory("Oyster pearl", ItemCategory.GEM);
	}

	private static void assertCategory(String itemName, ItemCategory expectedCategory, String... actions)
	{
		assertEquals(itemName, expectedCategory, ItemCategory.categorize(itemName, actions));
	}
}
