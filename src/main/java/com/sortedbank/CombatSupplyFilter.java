package com.sortedbank;

final class CombatSupplyFilter
{
	private CombatSupplyFilter()
	{
	}

	static boolean matches(ItemCategory category, String itemName, CombatStyle style)
	{
		if (category == ItemCategory.FOOD)
		{
			return true;
		}
		if (category != ItemCategory.POTION || style == null)
		{
			return false;
		}

		String name = stripDoseSuffix(normalize(itemName));
		return isCommonCombatPotion(name) || isStylePotion(name, style);
	}

	private static boolean isCommonCombatPotion(String name)
	{
		return name.equals("prayer potion")
			|| name.equals("defence potion")
			|| name.equals("super defence")
			|| name.equals("restore potion")
			|| name.equals("super restore")
			|| name.equals("sanfew serum")
			|| name.equals("saradomin brew")
			|| name.contains("antipoison")
			|| name.contains("antidote")
			|| name.contains("anti-venom");
	}

	private static boolean isStylePotion(String name, CombatStyle style)
	{
		switch (style)
		{
			case MELEE:
				return isMeleePotion(name);
			case RANGED:
				return isRangedPotion(name) || isWeaponPoison(name);
			case MAGIC:
				return isMagicPotion(name);
			default:
				return false;
		}
	}

	private static boolean isMeleePotion(String name)
	{
		return name.equals("attack potion")
			|| name.equals("strength potion")
			|| name.equals("super attack")
			|| name.equals("super strength")
			|| name.equals("combat potion")
			|| name.equals("super combat potion")
			|| name.equals("divine super combat potion")
			|| name.equals("zamorak brew")
			|| isWeaponPoison(name);
	}

	private static boolean isRangedPotion(String name)
	{
		return name.equals("ranging potion")
			|| name.equals("bastion potion")
			|| name.equals("divine ranging potion")
			|| name.equals("divine bastion potion");
	}

	private static boolean isMagicPotion(String name)
	{
		return name.equals("magic potion")
			|| name.equals("battlemage potion")
			|| name.equals("divine magic potion")
			|| name.equals("divine battlemage potion")
			|| name.equals("forgotten brew");
	}

	private static boolean isWeaponPoison(String name)
	{
		return name.startsWith("weapon poison");
	}

	private static String normalize(String itemName)
	{
		return itemName == null ? "" : itemName.toLowerCase();
	}

	private static String stripDoseSuffix(String name)
	{
		if (name.length() < 3 || name.charAt(name.length() - 1) != ')')
		{
			return name;
		}

		char dose = name.charAt(name.length() - 2);
		int openParenIndex = name.length() - 3;
		return openParenIndex >= 0 && name.charAt(openParenIndex) == '(' && dose >= '1' && dose <= '4'
			? name.substring(0, openParenIndex)
			: name;
	}
}
