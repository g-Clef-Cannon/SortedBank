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
	}

	@Test
	public void categorizesCombatAndTeleportItems()
	{
		assertCategory("Water rune", ItemCategory.RUNE);
		assertCategory("Varrock teleport", ItemCategory.TELEPORT);
		assertCategory("Ring of dueling(8)", ItemCategory.TELEPORT);
		assertCategory("Ruby ring", ItemCategory.JEWELRY);
		assertCategory("Bronze arrow", ItemCategory.AMMO);
		assertCategory("Bronze knife", ItemCategory.AMMO, "Wield");
		assertCategory("Bronze knives", ItemCategory.AMMO);
	}

	private static void assertCategory(String itemName, ItemCategory expectedCategory, String... actions)
	{
		assertEquals(itemName, expectedCategory, ItemCategory.categorize(itemName, actions));
	}
}
