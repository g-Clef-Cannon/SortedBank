package com.sortedbank;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CombatSupplyFilterTest
{
	@Test
	public void includesFoodForEveryCombatStyle()
	{
		for (CombatStyle style : CombatStyle.values())
		{
			assertTrue(CombatSupplyFilter.matches(ItemCategory.FOOD, "Shark", style));
		}
	}

	@Test
	public void includesCommonCombatPotionsForEveryCombatStyle()
	{
		for (CombatStyle style : CombatStyle.values())
		{
			assertTrue(CombatSupplyFilter.matches(ItemCategory.POTION, "Prayer potion(4)", style));
			assertTrue(CombatSupplyFilter.matches(ItemCategory.POTION, "Super defence(4)", style));
			assertTrue(CombatSupplyFilter.matches(ItemCategory.POTION, "Super restore(4)", style));
			assertTrue(CombatSupplyFilter.matches(ItemCategory.POTION, "Saradomin brew(4)", style));
			assertTrue(CombatSupplyFilter.matches(ItemCategory.POTION, "Sanfew serum(4)", style));
			assertTrue(CombatSupplyFilter.matches(ItemCategory.POTION, "Antidote++(4)", style));
		}
	}

	@Test
	public void includesStyleSpecificPotionsOnlyForMatchingStyles()
	{
		assertTrue(CombatSupplyFilter.matches(ItemCategory.POTION, "Super strength(4)", CombatStyle.MELEE));
		assertFalse(CombatSupplyFilter.matches(ItemCategory.POTION, "Super strength(4)", CombatStyle.RANGED));
		assertFalse(CombatSupplyFilter.matches(ItemCategory.POTION, "Super strength(4)", CombatStyle.MAGIC));

		assertTrue(CombatSupplyFilter.matches(ItemCategory.POTION, "Ranging potion(4)", CombatStyle.RANGED));
		assertFalse(CombatSupplyFilter.matches(ItemCategory.POTION, "Ranging potion(4)", CombatStyle.MELEE));
		assertFalse(CombatSupplyFilter.matches(ItemCategory.POTION, "Ranging potion(4)", CombatStyle.MAGIC));

		assertTrue(CombatSupplyFilter.matches(ItemCategory.POTION, "Magic potion(4)", CombatStyle.MAGIC));
		assertFalse(CombatSupplyFilter.matches(ItemCategory.POTION, "Magic potion(4)", CombatStyle.MELEE));
		assertFalse(CombatSupplyFilter.matches(ItemCategory.POTION, "Magic potion(4)", CombatStyle.RANGED));
	}

	@Test
	public void excludesNonCombatPotions()
	{
		for (CombatStyle style : CombatStyle.values())
		{
			assertFalse(CombatSupplyFilter.matches(ItemCategory.POTION, "Fishing potion(4)", style));
			assertFalse(CombatSupplyFilter.matches(ItemCategory.POTION, "Hunter potion(4)", style));
		}
	}
}
