package com.sortedbank;

final class ItemSortKey
{
	private static final int UNKNOWN = 999;

	private ItemSortKey()
	{
	}

	static String categorySortKey(ItemCategory category, String itemName)
	{
		String name = normalize(itemName);
		switch (category)
		{
			case MELEE_WEAPON:
				return meleeWeaponKey(name);
			case RANGED_WEAPON:
				return rangedWeaponKey(name);
			case MAGIC_WEAPON:
				return magicWeaponKey(name);
			case MELEE_ARMOR:
				return armorKey(name);
			case RANGED_ARMOR:
				return rangedArmorKey(name);
			case MAGIC_ARMOR:
				return magicArmorKey(name);
			case JEWELRY:
				return jewelryKey(name);
			case COSMETIC:
				return cosmeticKey(name);
			case RUNE:
				return key(0, runeRank(name), 0, name);
			case AMMO:
				return ammoKey(name);
			case POTION:
				return potionKey(name);
			case FOOD:
				return foodKey(name);
			case TOOL:
				return toolKey(name);
			case SEED:
				return seedKey(name);
			case HERB:
				return herbKey(name);
			case LOG:
				return key(0, logRank(name), 0, name);
			case ORE:
				return oreAndBarKey(name);
			case GEM:
				return gemKey(name);
			case KEY:
				return key(keyGroupRank(name), keyItemRank(name), 0, name);
			case QUEST_ITEM:
				return questItemKey(name);
			case PRAYER_ITEM:
				return prayerItemKey(name);
			case FLETCHING_COMPONENT:
				return fletchingKey(name);
			case PROCESSED_MATERIAL:
				return processedMaterialKey(name);
			case CONTAINER:
				return containerKey(name);
			case COLLECTIBLE:
				return collectibleKey(name);
			default:
				return key(UNKNOWN, UNKNOWN, 0, name);
		}
	}

	private static String meleeWeaponKey(String name)
	{
		return key(weaponTypeRank(name), materialRank(name), poisonRank(name), name);
	}

	private static String rangedWeaponKey(String name)
	{
		if (name.contains("crossbow"))
		{
			return key(0, materialRank(name), 0, name);
		}
		if (name.contains("shortbow"))
		{
			return key(1, logRank(name), 0, name);
		}
		if (name.contains("longbow"))
		{
			return key(2, logRank(name), 0, name);
		}
		return key(UNKNOWN, materialRank(name), 0, name);
	}

	private static String magicWeaponKey(String name)
	{
		if (name.equals("staff"))
		{
			return key(0, 0, 0, name);
		}
		if (name.equals("magic staff"))
		{
			return key(1, 0, 0, name);
		}
		if (name.startsWith("staff of "))
		{
			return key(2, elementRank(name), 0, name);
		}
		if (name.contains("battlestaff"))
		{
			return key(3, elementRank(name), 0, name);
		}
		return key(UNKNOWN, elementRank(name), 0, name);
	}

	private static String armorKey(String name)
	{
		if (name.startsWith("shayzien "))
		{
			return key(80, parenNumber(name), armorSlotRank(name), name);
		}
		return key(materialRank(name), armorSlotRank(name), 0, name);
	}

	private static String rangedArmorKey(String name)
	{
		return key(rangedArmorMaterialRank(name), armorSlotRank(name), 0, name);
	}

	private static String magicArmorKey(String name)
	{
		return key(magicArmorSetRank(name), armorSlotRank(name), 0, name);
	}

	private static String jewelryKey(String name)
	{
		return key(jewelryTypeRank(name), jewelryMaterialRank(name), 0, name);
	}

	private static String cosmeticKey(String name)
	{
		return key(cosmeticSetRank(name), armorSlotRank(name), 0, name);
	}

	private static String ammoKey(String name)
	{
		if (name.contains("arrow"))
		{
			return key(0, materialRank(name), 0, name);
		}
		if (name.contains("bolt"))
		{
			return key(1, materialRank(name), 0, name);
		}
		if (name.contains("knife"))
		{
			return key(2, materialRank(name), 0, name);
		}
		if (name.contains("dart"))
		{
			return key(3, materialRank(name), 0, name);
		}
		if (name.contains("javelin"))
		{
			return key(4, materialRank(name), 0, name);
		}
		return key(UNKNOWN, materialRank(name), 0, name);
	}

	private static String potionKey(String name)
	{
		return key(0, potionRank(stripDoseSuffix(name)), doseSortRank(name), stripDoseSuffix(name));
	}

	private static String foodKey(String name)
	{
		String baseName = stripRawOrBurntPrefix(name);
		int fishRank = fishRank(baseName);
		if (fishRank != UNKNOWN)
		{
			return key(0, fishRank, foodStateRank(name), name);
		}
		if (name.contains("cake"))
		{
			return key(1, bakedFoodRank(name), 0, name);
		}
		int bakedFoodRank = bakedFoodRank(name);
		if (bakedFoodRank != UNKNOWN)
		{
			return key(1, bakedFoodRank, 0, name);
		}
		int fruitRank = fruitRank(name);
		if (fruitRank != UNKNOWN)
		{
			return key(2, fruitRank, basketQuantityRank(name), name);
		}
		int vegetableRank = vegetableRank(name);
		if (vegetableRank != UNKNOWN)
		{
			return key(3, vegetableRank, 0, name);
		}
		if (name.equals("beer") || name.equals("dwarven stout"))
		{
			return key(4, drinkRank(name), 0, name);
		}
		int preparedFoodRank = preparedFoodRank(name);
		if (preparedFoodRank != UNKNOWN)
		{
			return key(5, preparedFoodRank, 0, name);
		}
		return key(UNKNOWN, UNKNOWN, 0, name);
	}

	private static String toolKey(String name)
	{
		if (name.contains("pickaxe"))
		{
			return key(0, materialRank(name), 0, name);
		}
		if (name.endsWith(" axe") && !name.contains("battleaxe"))
		{
			return key(1, materialRank(name), 0, name);
		}
		if (isFishingTool(name))
		{
			return key(2, fishingToolRank(name), 0, name);
		}
		if (name.equals("butterfly net"))
		{
			return key(2, 7, 0, name);
		}
		if (isFarmingTool(name))
		{
			return key(3, farmingToolRank(name), 0, name);
		}
		if (name.endsWith(" talisman"))
		{
			return key(4, runeRank(name), 0, name);
		}
		if (name.endsWith(" mould"))
		{
			return key(5, mouldRank(name), 0, name);
		}
		if (isCraftingTool(name))
		{
			return key(6, craftingToolRank(name), 0, name);
		}
		if (isLightSource(name))
		{
			return key(7, lightSourceRank(name), 0, name);
		}
		int miscToolRank = miscToolRank(name);
		if (miscToolRank != UNKNOWN)
		{
			return key(8, miscToolRank, 0, name);
		}
		return key(UNKNOWN, UNKNOWN, 0, name);
	}

	private static String seedKey(String name)
	{
		if (name.equals("compost") || name.equals("supercompost") || name.equals("ultracompost") || name.equals("saltpetre"))
		{
			return key(0, farmingSupplyRank(name), 0, name);
		}
		int allotmentRank = allotmentSeedRank(name);
		if (allotmentRank != UNKNOWN)
		{
			return key(1, allotmentRank, 0, name);
		}
		int flowerRank = flowerSeedRank(name);
		if (flowerRank != UNKNOWN)
		{
			return key(2, flowerRank, 0, name);
		}
		int hopsRank = hopsSeedRank(name);
		if (hopsRank != UNKNOWN)
		{
			return key(3, hopsRank, 0, name);
		}
		int bushRank = bushSeedRank(name);
		if (bushRank != UNKNOWN)
		{
			return key(4, bushRank, 0, name);
		}
		int herbRank = herbRank(stripSeedSuffix(name));
		if (herbRank != UNKNOWN)
		{
			return key(5, herbRank, 0, name);
		}
		int treeRank = treeSeedRank(name);
		if (treeRank != UNKNOWN)
		{
			return key(6, treeRank, 0, name);
		}
		int fruitTreeRank = fruitTreeSeedRank(name);
		if (fruitTreeRank != UNKNOWN)
		{
			return key(7, fruitTreeRank, 0, name);
		}
		int specialSeedRank = specialSeedRank(name);
		if (specialSeedRank != UNKNOWN)
		{
			return key(8, specialSeedRank, 0, name);
		}
		return key(UNKNOWN, UNKNOWN, 0, name);
	}

	private static String herbKey(String name)
	{
		if (name.startsWith("grimy "))
		{
			return key(0, herbRank(name.substring("grimy ".length())), 0, name);
		}
		int herbRank = herbRank(name);
		if (herbRank != UNKNOWN)
		{
			return key(1, herbRank, 0, name);
		}
		return key(2, herbloreSecondaryRank(name), 0, name);
	}

	private static String oreAndBarKey(String name)
	{
		if (name.endsWith(" ore") || name.equals("coal"))
		{
			return key(0, oreRank(name), 0, name);
		}
		if (name.endsWith(" bar"))
		{
			return key(1, barRank(name), 0, name);
		}
		return key(UNKNOWN, UNKNOWN, 0, name);
	}

	private static String gemKey(String name)
	{
		if (name.startsWith("uncut "))
		{
			return key(0, gemRank(name), 0, name);
		}
		if (name.endsWith(" pearl"))
		{
			return key(2, gemRank(name), 0, name);
		}
		return key(1, gemRank(name), 0, name);
	}

	private static String questItemKey(String name)
	{
		if (name.endsWith(" bead"))
		{
			return key(0, beadRank(name), 0, name);
		}
		if (name.startsWith("scroll box"))
		{
			return key(1, clueTierRank(name), 0, name);
		}
		if (name.startsWith("clue scroll"))
		{
			return key(2, clueTierRank(name), 0, name);
		}
		if (name.startsWith("tattered "))
		{
			return key(3, tatteredPageRank(name), 0, name);
		}
		return key(UNKNOWN, UNKNOWN, 0, name);
	}

	private static String prayerItemKey(String name)
	{
		if (name.endsWith(" ashes") || name.equals("ashes"))
		{
			return key(0, ashesRank(name), 0, name);
		}
		if (name.equals("bones") || name.endsWith(" bones"))
		{
			return key(1, boneRank(name), 0, name);
		}
		if (name.startsWith("ensouled ") && name.endsWith(" head"))
		{
			return key(2, ensouledHeadRank(name), 0, name);
		}
		return key(3, prayerMiscRank(name), 0, name);
	}

	private static String fletchingKey(String name)
	{
		if (name.equals("arrow shaft") || name.equals("feather") || name.equals("headless arrow"))
		{
			return key(0, fletchingBaseRank(name), 0, name);
		}
		if (name.endsWith(" arrowtips"))
		{
			return key(1, materialRank(name), 0, name);
		}
		if (name.endsWith(" bolt tips") || name.endsWith(" dart tip"))
		{
			return key(2, gemRank(name), 0, name);
		}
		if (name.endsWith(" bolts (unf)"))
		{
			return key(3, materialRank(name), 0, name);
		}
		if (name.equals("bow string") || name.equals("bowstring"))
		{
			return key(4, 0, 0, name);
		}
		if (name.endsWith("bow (u)"))
		{
			return key(5, logRank(name), bowTypeRank(name), name);
		}
		if (name.equals("javelin shaft"))
		{
			return key(6, 0, 0, name);
		}
		return key(UNKNOWN, UNKNOWN, 0, name);
	}

	private static String processedMaterialKey(String name)
	{
		if (name.equals("flax") || name.equals("ball of wool") || name.equals("thread") || name.contains("fabric"))
		{
			return key(0, textileRank(name), 0, name);
		}
		if (name.equals("seaweed") || name.equals("soda ash") || name.equals("bucket of sand") || name.equals("molten glass"))
		{
			return key(1, glassMaterialRank(name), 0, name);
		}
		if (name.contains("leather") || name.contains("dragonhide"))
		{
			return key(2, leatherRank(name), 0, name);
		}
		if (name.endsWith(" plank") || name.endsWith(" planks"))
		{
			return key(3, logRank(name), 0, name);
		}
		if (name.endsWith(" nails") || name.endsWith(" studs"))
		{
			return key(4, materialRank(name), 0, name);
		}
		if (name.equals("papyrus") || name.equals("rock-shell chunk"))
		{
			return key(5, processedMiscRank(name), 0, name);
		}
		return key(UNKNOWN, UNKNOWN, 0, name);
	}

	private static String containerKey(String name)
	{
		if (name.equals("basket") || name.contains("sack"))
		{
			return key(0, containerRank(name), 0, name);
		}
		if (name.equals("pot") || name.equals("bowl") || name.equals("bucket") || name.equals("jug"))
		{
			return key(1, containerRank(name), 0, name);
		}
		if (name.equals("vial") || name.equals("beer glass") || name.contains("fishbowl"))
		{
			return key(2, containerRank(name), 0, name);
		}
		if (name.contains("pack"))
		{
			return key(3, containerRank(name), 0, name);
		}
		return key(UNKNOWN, UNKNOWN, 0, name);
	}

	private static String collectibleKey(String name)
	{
		if (name.equals("mark of grace"))
		{
			return key(0, 0, 0, name);
		}
		if (name.equals("ancient shard"))
		{
			return key(1, 0, 0, name);
		}
		if (name.equals("lizardman fang"))
		{
			return key(2, 0, 0, name);
		}
		if (name.equals("frog token"))
		{
			return key(3, 0, 0, name);
		}
		if (name.equals("molch pearl"))
		{
			return key(4, 0, 0, name);
		}
		return key(UNKNOWN, UNKNOWN, 0, name);
	}

	private static int materialRank(String name)
	{
		if (containsWord(name, "bronze"))
		{
			return 1;
		}
		if (containsWord(name, "iron"))
		{
			return 2;
		}
		if (containsWord(name, "steel"))
		{
			return 6;
		}
		if (containsWord(name, "black"))
		{
			return 10;
		}
		if (containsWord(name, "mithril"))
		{
			return 21;
		}
		if (containsWord(name, "adamant") || containsWord(name, "adamantite"))
		{
			return 31;
		}
		if (containsWord(name, "rune") || containsWord(name, "runite"))
		{
			return 41;
		}
		if (containsWord(name, "dragon"))
		{
			return 60;
		}
		if (containsWord(name, "oak"))
		{
			return 15;
		}
		if (containsWord(name, "willow"))
		{
			return 30;
		}
		if (containsWord(name, "maple"))
		{
			return 45;
		}
		if (containsWord(name, "yew"))
		{
			return 60;
		}
		return UNKNOWN;
	}

	private static int weaponTypeRank(String name)
	{
		if (name.contains("dagger"))
		{
			return 0;
		}
		if (name.endsWith(" sword"))
		{
			return 1;
		}
		if (name.contains("longsword"))
		{
			return 2;
		}
		if (name.contains("scimitar"))
		{
			return 3;
		}
		if (name.contains("mace"))
		{
			return 4;
		}
		if (name.contains("warhammer"))
		{
			return 5;
		}
		if (name.contains("battleaxe"))
		{
			return 6;
		}
		if (name.contains("spear"))
		{
			return 7;
		}
		if (name.contains("claws"))
		{
			return 8;
		}
		return UNKNOWN;
	}

	private static int poisonRank(String name)
	{
		if (name.contains("(p++)"))
		{
			return 3;
		}
		if (name.contains("(p+)"))
		{
			return 2;
		}
		if (name.contains("(p)"))
		{
			return 1;
		}
		return 0;
	}

	private static int armorSlotRank(String name)
	{
		if (name.contains("hat") || name.contains("helm") || name.contains("cowl") || name.contains("mask"))
		{
			return 0;
		}
		if (name.contains("body") || name.contains("top") || name.contains("shirt") || name.contains("robe top"))
		{
			return 1;
		}
		if (name.contains("legs") || name.contains("platelegs") || name.contains("plateskirt")
			|| name.contains("chaps") || name.contains("greaves") || name.contains("trousers")
			|| name.contains("shorts") || name.contains("bottoms") || name.equals("shade robe")
			|| name.contains("robe"))
		{
			return 2;
		}
		if (name.contains("shield") || name.contains("kiteshield") || name.contains("sq shield"))
		{
			return 3;
		}
		if (name.contains("gloves") || name.contains("vambraces") || name.contains("gauntlets"))
		{
			return 4;
		}
		if (name.contains("boots"))
		{
			return 5;
		}
		if (name.contains("cape"))
		{
			return 6;
		}
		return UNKNOWN;
	}

	private static int rangedArmorMaterialRank(String name)
	{
		if (name.contains("leather"))
		{
			return 0;
		}
		if (name.contains("d'hide") || name.contains("dragonhide"))
		{
			return dragonhideRank(name);
		}
		return UNKNOWN;
	}

	private static int dragonhideRank(String name)
	{
		if (name.contains("green"))
		{
			return 40;
		}
		if (name.contains("blue"))
		{
			return 50;
		}
		if (name.contains("red"))
		{
			return 60;
		}
		if (name.contains("black"))
		{
			return 70;
		}
		return materialRank(name);
	}

	private static int magicArmorSetRank(String name)
	{
		if (name.contains("wizard"))
		{
			return 0;
		}
		if (name.contains("black robe"))
		{
			return 1;
		}
		if (name.contains("shade robe"))
		{
			return 2;
		}
		if (name.contains("xerician"))
		{
			return 3;
		}
		return UNKNOWN;
	}

	private static int jewelryTypeRank(String name)
	{
		if (name.contains("ring"))
		{
			return 0;
		}
		if (name.contains("necklace"))
		{
			return 1;
		}
		if (name.contains("bracelet"))
		{
			return 2;
		}
		if (name.contains("amulet"))
		{
			return 3;
		}
		return UNKNOWN;
	}

	private static int jewelryMaterialRank(String name)
	{
		if (name.contains("gold"))
		{
			return 0;
		}
		if (name.contains("sapphire"))
		{
			return 1;
		}
		if (name.contains("emerald"))
		{
			return 2;
		}
		if (name.contains("ruby"))
		{
			return 3;
		}
		if (name.contains("diamond"))
		{
			return 4;
		}
		if (name.contains("dragonstone"))
		{
			return 5;
		}
		if (name.contains("onyx"))
		{
			return 6;
		}
		if (name.contains("defence"))
		{
			return 20;
		}
		if (name.contains("magic"))
		{
			return 21;
		}
		if (name.contains("strength"))
		{
			return 22;
		}
		return UNKNOWN;
	}

	private static int cosmeticSetRank(String name)
	{
		if (name.contains("beekeeper"))
		{
			return 0;
		}
		if (name.contains("camo"))
		{
			return 1;
		}
		if (name.contains("lederhosen"))
		{
			return 2;
		}
		if (name.contains("mime"))
		{
			return 3;
		}
		if (name.contains("zombie"))
		{
			return 4;
		}
		return UNKNOWN;
	}

	private static int runeRank(String name)
	{
		if (name.contains("air"))
		{
			return 1;
		}
		if (name.contains("mind"))
		{
			return 2;
		}
		if (name.contains("water"))
		{
			return 5;
		}
		if (name.contains("earth"))
		{
			return 9;
		}
		if (name.contains("mud"))
		{
			return 13;
		}
		if (name.contains("fire"))
		{
			return 14;
		}
		if (name.contains("body"))
		{
			return 20;
		}
		if (name.contains("cosmic"))
		{
			return 27;
		}
		if (name.contains("chaos"))
		{
			return 35;
		}
		if (name.contains("astral"))
		{
			return 40;
		}
		if (name.contains("nature"))
		{
			return 44;
		}
		if (name.contains("law"))
		{
			return 54;
		}
		if (name.contains("death"))
		{
			return 65;
		}
		if (name.contains("blood"))
		{
			return 77;
		}
		if (name.contains("soul"))
		{
			return 90;
		}
		if (name.contains("wrath"))
		{
			return 95;
		}
		return UNKNOWN;
	}

	private static int elementRank(String name)
	{
		return runeRank(name);
	}

	private static int potionRank(String baseName)
	{
		if (baseName.equals("attack potion"))
		{
			return 0;
		}
		if (baseName.equals("super attack"))
		{
			return 1;
		}
		if (baseName.equals("strength potion"))
		{
			return 2;
		}
		if (baseName.equals("super strength"))
		{
			return 3;
		}
		if (baseName.equals("defence potion"))
		{
			return 4;
		}
		if (baseName.equals("super defence"))
		{
			return 5;
		}
		if (baseName.equals("prayer potion"))
		{
			return 6;
		}
		if (baseName.equals("restore potion"))
		{
			return 7;
		}
		if (baseName.equals("super restore"))
		{
			return 8;
		}
		if (baseName.equals("sanfew serum"))
		{
			return 9;
		}
		if (baseName.equals("saradomin brew"))
		{
			return 10;
		}
		if (baseName.equals("ranging potion") || baseName.equals("divine ranging potion"))
		{
			return 20;
		}
		if (baseName.equals("bastion potion") || baseName.equals("divine bastion potion"))
		{
			return 21;
		}
		if (baseName.equals("magic potion") || baseName.equals("divine magic potion"))
		{
			return 30;
		}
		if (baseName.equals("battlemage potion") || baseName.equals("divine battlemage potion"))
		{
			return 31;
		}
		if (baseName.equals("forgotten brew"))
		{
			return 32;
		}
		if (baseName.startsWith("superantipoison") || baseName.contains("antidote") || baseName.contains("anti-venom"))
		{
			return 40;
		}
		if (baseName.startsWith("weapon poison"))
		{
			return 41;
		}
		return UNKNOWN;
	}

	private static int logRank(String name)
	{
		if (name.equals("logs") || name.contains("shortbow") || name.contains("longbow"))
		{
			return name.contains("oak") || name.contains("willow") || name.contains("maple") || name.contains("yew")
				? woodRank(name)
				: 1;
		}
		return woodRank(name);
	}

	private static int woodRank(String name)
	{
		if (name.contains("oak"))
		{
			return 15;
		}
		if (name.contains("willow"))
		{
			return 30;
		}
		if (name.contains("teak"))
		{
			return 35;
		}
		if (name.contains("maple"))
		{
			return 45;
		}
		if (name.contains("mahogany"))
		{
			return 50;
		}
		if (name.contains("yew"))
		{
			return 60;
		}
		if (name.contains("magic"))
		{
			return 75;
		}
		if (name.contains("redwood"))
		{
			return 90;
		}
		return 1;
	}

	private static int herbRank(String name)
	{
		if (name.equals("guam leaf"))
		{
			return 3;
		}
		if (name.equals("marrentill"))
		{
			return 5;
		}
		if (name.equals("tarromin"))
		{
			return 11;
		}
		if (name.equals("harralander"))
		{
			return 20;
		}
		if (name.equals("ranarr weed") || name.equals("ranarr"))
		{
			return 25;
		}
		if (name.equals("toadflax"))
		{
			return 30;
		}
		if (name.equals("irit leaf") || name.equals("irit"))
		{
			return 40;
		}
		if (name.equals("avantoe"))
		{
			return 48;
		}
		if (name.equals("kwuarm"))
		{
			return 54;
		}
		if (name.equals("snapdragon"))
		{
			return 59;
		}
		if (name.equals("cadantine"))
		{
			return 65;
		}
		if (name.equals("lantadyme"))
		{
			return 67;
		}
		if (name.equals("dwarf weed") || name.equals("dwarf"))
		{
			return 70;
		}
		if (name.equals("torstol"))
		{
			return 75;
		}
		return UNKNOWN;
	}

	private static int herbloreSecondaryRank(String name)
	{
		if (name.equals("red spiders' eggs"))
		{
			return 10;
		}
		if (name.equals("limpwurt root"))
		{
			return 20;
		}
		if (name.equals("bird nest") || name.equals("crushed nest"))
		{
			return 30;
		}
		if (name.equals("potato cactus"))
		{
			return 40;
		}
		return UNKNOWN;
	}

	private static int oreRank(String name)
	{
		if (name.equals("tin ore"))
		{
			return 1;
		}
		if (name.equals("copper ore"))
		{
			return 1;
		}
		if (name.equals("iron ore"))
		{
			return 15;
		}
		if (name.equals("coal"))
		{
			return 30;
		}
		if (name.equals("gold ore"))
		{
			return 40;
		}
		if (name.equals("mithril ore"))
		{
			return 55;
		}
		if (name.equals("adamantite ore"))
		{
			return 70;
		}
		if (name.equals("runite ore"))
		{
			return 85;
		}
		return UNKNOWN;
	}

	private static int barRank(String name)
	{
		if (name.equals("bronze bar"))
		{
			return 1;
		}
		if (name.equals("iron bar"))
		{
			return 15;
		}
		if (name.equals("steel bar"))
		{
			return 30;
		}
		if (name.equals("gold bar"))
		{
			return 40;
		}
		if (name.equals("mithril bar"))
		{
			return 50;
		}
		if (name.equals("adamantite bar"))
		{
			return 70;
		}
		if (name.equals("runite bar"))
		{
			return 85;
		}
		return UNKNOWN;
	}

	private static int gemRank(String name)
	{
		if (name.contains("opal"))
		{
			return 1;
		}
		if (name.contains("jade"))
		{
			return 13;
		}
		if (name.contains("topaz"))
		{
			return 16;
		}
		if (name.contains("sapphire"))
		{
			return 20;
		}
		if (name.contains("emerald"))
		{
			return 27;
		}
		if (name.contains("ruby"))
		{
			return 34;
		}
		if (name.contains("diamond"))
		{
			return 43;
		}
		if (name.contains("dragonstone"))
		{
			return 55;
		}
		if (name.contains("onyx"))
		{
			return 67;
		}
		if (name.contains("zenyte"))
		{
			return 89;
		}
		if (name.contains("pearl"))
		{
			return 100;
		}
		return UNKNOWN;
	}

	private static int fishRank(String name)
	{
		if (name.equals("burnt fish") || name.equals("fish"))
		{
			return 0;
		}
		if (name.contains("sardine"))
		{
			return 5;
		}
		if (name.contains("herring"))
		{
			return 10;
		}
		if (name.contains("mackerel"))
		{
			return 16;
		}
		if (name.contains("tuna"))
		{
			return 30;
		}
		if (name.contains("lobster"))
		{
			return 40;
		}
		if (name.contains("bass"))
		{
			return 46;
		}
		if (name.contains("swordfish"))
		{
			return 50;
		}
		if (name.contains("shark"))
		{
			return 76;
		}
		return UNKNOWN;
	}

	private static int foodStateRank(String name)
	{
		if (name.startsWith("raw "))
		{
			return 0;
		}
		if (name.startsWith("burnt "))
		{
			return 2;
		}
		return 1;
	}

	private static int fruitRank(String name)
	{
		if (name.contains("banana"))
		{
			return 0;
		}
		if (name.contains("apple"))
		{
			return 1;
		}
		if (name.contains("strawberr"))
		{
			return 2;
		}
		if (name.contains("jangerberries"))
		{
			return 3;
		}
		if (name.contains("redberries"))
		{
			return 4;
		}
		if (name.contains("lemon"))
		{
			return 5;
		}
		if (name.contains("lime"))
		{
			return 6;
		}
		if (name.contains("pineapple"))
		{
			return 7;
		}
		if (name.contains("papaya"))
		{
			return 8;
		}
		if (name.contains("golovanova"))
		{
			return 9;
		}
		if (name.contains("strange fruit"))
		{
			return 10;
		}
		return UNKNOWN;
	}

	private static int bakedFoodRank(String name)
	{
		if (name.equals("bread"))
		{
			return 0;
		}
		if (name.equals("roll"))
		{
			return 1;
		}
		if (name.equals("baguette"))
		{
			return 2;
		}
		if (name.equals("chocolate bar"))
		{
			return 3;
		}
		if (name.equals("cake"))
		{
			return 10;
		}
		if (name.equals("2/3 cake"))
		{
			return 11;
		}
		if (name.equals("slice of cake"))
		{
			return 12;
		}
		if (name.equals("chocolate cake"))
		{
			return 13;
		}
		if (name.equals("chocolate slice"))
		{
			return 14;
		}
		return UNKNOWN;
	}

	private static int vegetableRank(String name)
	{
		if (name.equals("cabbage"))
		{
			return 0;
		}
		if (name.contains("potato"))
		{
			return 1;
		}
		return UNKNOWN;
	}

	private static int preparedFoodRank(String name)
	{
		if (name.equals("field ration"))
		{
			return 0;
		}
		if (name.equals("kebab"))
		{
			return 1;
		}
		if (name.endsWith(" pie"))
		{
			return 2;
		}
		return UNKNOWN;
	}

	private static int drinkRank(String name)
	{
		if (name.equals("beer"))
		{
			return 0;
		}
		if (name.equals("dwarven stout"))
		{
			return 1;
		}
		return UNKNOWN;
	}

	private static int fishingToolRank(String name)
	{
		if (name.equals("small fishing net"))
		{
			return 0;
		}
		if (name.equals("big fishing net"))
		{
			return 1;
		}
		if (name.equals("fishing rod"))
		{
			return 2;
		}
		if (name.equals("fly fishing rod"))
		{
			return 3;
		}
		if (name.equals("fishing bait") || name.equals("bait pack") || name.equals("fish offcuts"))
		{
			return 4;
		}
		if (name.equals("harpoon"))
		{
			return 5;
		}
		if (name.equals("lobster pot"))
		{
			return 6;
		}
		return UNKNOWN;
	}

	private static int farmingToolRank(String name)
	{
		if (name.equals("rake"))
		{
			return 0;
		}
		if (name.equals("seed dibber"))
		{
			return 1;
		}
		if (name.startsWith("watering can"))
		{
			return 2;
		}
		if (name.equals("spade"))
		{
			return 3;
		}
		return UNKNOWN;
	}

	private static int mouldRank(String name)
	{
		if (name.equals("ring mould"))
		{
			return 0;
		}
		if (name.equals("necklace mould"))
		{
			return 1;
		}
		if (name.equals("bracelet mould"))
		{
			return 2;
		}
		if (name.equals("amulet mould"))
		{
			return 3;
		}
		if (name.equals("holy mould"))
		{
			return 4;
		}
		if (name.equals("sickle mould"))
		{
			return 5;
		}
		if (name.equals("tiara mould"))
		{
			return 6;
		}
		if (name.equals("bolt mould"))
		{
			return 7;
		}
		return UNKNOWN;
	}

	private static int craftingToolRank(String name)
	{
		if (name.equals("knife"))
		{
			return 0;
		}
		if (name.equals("chisel"))
		{
			return 1;
		}
		if (name.equals("needle"))
		{
			return 2;
		}
		if (name.equals("glassblowing pipe"))
		{
			return 3;
		}
		if (name.equals("hammer"))
		{
			return 4;
		}
		return UNKNOWN;
	}

	private static int lightSourceRank(String name)
	{
		if (name.contains("torch"))
		{
			return 0;
		}
		if (name.contains("candle lantern"))
		{
			return 1;
		}
		if (name.contains("oil lamp"))
		{
			return 2;
		}
		return UNKNOWN;
	}

	private static int miscToolRank(String name)
	{
		if (name.equals("tinderbox"))
		{
			return 0;
		}
		if (name.equals("rope"))
		{
			return 1;
		}
		if (name.equals("lockpick"))
		{
			return 2;
		}
		if (name.equals("dynamite"))
		{
			return 3;
		}
		return UNKNOWN;
	}

	private static int farmingSupplyRank(String name)
	{
		if (name.equals("compost"))
		{
			return 0;
		}
		if (name.equals("supercompost"))
		{
			return 1;
		}
		if (name.equals("ultracompost"))
		{
			return 2;
		}
		if (name.equals("saltpetre"))
		{
			return 3;
		}
		return UNKNOWN;
	}

	private static int allotmentSeedRank(String name)
	{
		if (name.equals("potato seed"))
		{
			return 1;
		}
		if (name.equals("onion seed"))
		{
			return 5;
		}
		if (name.equals("cabbage seed"))
		{
			return 7;
		}
		if (name.equals("tomato seed"))
		{
			return 12;
		}
		if (name.equals("sweetcorn seed"))
		{
			return 20;
		}
		if (name.equals("strawberry seed"))
		{
			return 31;
		}
		if (name.equals("watermelon seed"))
		{
			return 47;
		}
		if (name.equals("snape grass seed"))
		{
			return 61;
		}
		return UNKNOWN;
	}

	private static int flowerSeedRank(String name)
	{
		if (name.equals("marigold seed"))
		{
			return 2;
		}
		if (name.equals("rosemary seed"))
		{
			return 11;
		}
		if (name.equals("nasturtium seed"))
		{
			return 24;
		}
		if (name.equals("woad seed"))
		{
			return 25;
		}
		if (name.equals("limpwurt seed"))
		{
			return 26;
		}
		return UNKNOWN;
	}

	private static int hopsSeedRank(String name)
	{
		if (name.equals("barley seed"))
		{
			return 3;
		}
		if (name.equals("hammerstone seed"))
		{
			return 4;
		}
		if (name.equals("asgarnian seed"))
		{
			return 8;
		}
		if (name.equals("jute seed"))
		{
			return 13;
		}
		if (name.equals("yanillian seed"))
		{
			return 16;
		}
		if (name.equals("krandorian seed"))
		{
			return 21;
		}
		if (name.equals("wildblood seed"))
		{
			return 28;
		}
		return UNKNOWN;
	}

	private static int bushSeedRank(String name)
	{
		if (name.equals("jangerberry seed"))
		{
			return 48;
		}
		if (name.equals("whiteberry seed"))
		{
			return 59;
		}
		if (name.equals("poison ivy seed"))
		{
			return 70;
		}
		return UNKNOWN;
	}

	private static int treeSeedRank(String name)
	{
		if (name.equals("acorn"))
		{
			return 15;
		}
		if (name.equals("willow seed"))
		{
			return 30;
		}
		if (name.equals("maple seed"))
		{
			return 45;
		}
		if (name.equals("yew seed"))
		{
			return 60;
		}
		if (name.equals("magic seed"))
		{
			return 75;
		}
		if (name.equals("spirit seed"))
		{
			return 83;
		}
		return UNKNOWN;
	}

	private static int fruitTreeSeedRank(String name)
	{
		if (name.equals("apple tree seed"))
		{
			return 27;
		}
		if (name.equals("banana tree seed"))
		{
			return 33;
		}
		if (name.equals("orange tree seed"))
		{
			return 39;
		}
		if (name.equals("curry tree seed"))
		{
			return 42;
		}
		if (name.equals("pineapple seed"))
		{
			return 51;
		}
		if (name.equals("papaya tree seed"))
		{
			return 57;
		}
		if (name.equals("palm tree seed"))
		{
			return 68;
		}
		return UNKNOWN;
	}

	private static int specialSeedRank(String name)
	{
		if (name.equals("belladonna seed"))
		{
			return 0;
		}
		if (name.equals("mushroom spore"))
		{
			return 1;
		}
		if (name.equals("potato cactus seed"))
		{
			return 2;
		}
		return UNKNOWN;
	}

	private static int keyGroupRank(String name)
	{
		if (name.startsWith("dark totem"))
		{
			return 0;
		}
		if (name.contains("half of key"))
		{
			return 1;
		}
		return 2;
	}

	private static int keyItemRank(String name)
	{
		if (name.equals("dark totem base"))
		{
			return 0;
		}
		if (name.equals("dark totem middle"))
		{
			return 1;
		}
		if (name.equals("dark totem top"))
		{
			return 2;
		}
		if (name.equals("loop half of key"))
		{
			return 0;
		}
		if (name.equals("tooth half of key"))
		{
			return 1;
		}
		return UNKNOWN;
	}

	private static int beadRank(String name)
	{
		if (name.equals("black bead"))
		{
			return 0;
		}
		if (name.equals("white bead"))
		{
			return 1;
		}
		if (name.equals("red bead"))
		{
			return 2;
		}
		if (name.equals("yellow bead"))
		{
			return 3;
		}
		return UNKNOWN;
	}

	private static int clueTierRank(String name)
	{
		if (name.contains("beginner"))
		{
			return 0;
		}
		if (name.contains("easy"))
		{
			return 1;
		}
		if (name.contains("medium"))
		{
			return 2;
		}
		if (name.contains("hard"))
		{
			return 3;
		}
		if (name.contains("elite"))
		{
			return 4;
		}
		if (name.contains("master"))
		{
			return 5;
		}
		return UNKNOWN;
	}

	private static int tatteredPageRank(String name)
	{
		if (name.contains("moon"))
		{
			return 0;
		}
		if (name.contains("sun"))
		{
			return 1;
		}
		if (name.contains("temple"))
		{
			return 2;
		}
		return UNKNOWN;
	}

	private static int ashesRank(String name)
	{
		if (name.equals("ashes"))
		{
			return 0;
		}
		if (name.equals("vile ashes"))
		{
			return 1;
		}
		return UNKNOWN;
	}

	private static int boneRank(String name)
	{
		if (name.equals("bones"))
		{
			return 0;
		}
		if (name.equals("big bones"))
		{
			return 1;
		}
		if (name.equals("dragon bones"))
		{
			return 2;
		}
		return UNKNOWN;
	}

	private static int ensouledHeadRank(String name)
	{
		if (name.contains("giant"))
		{
			return 0;
		}
		if (name.contains("demon"))
		{
			return 1;
		}
		if (name.contains("dragon"))
		{
			return 2;
		}
		return UNKNOWN;
	}

	private static int prayerMiscRank(String name)
	{
		if (name.equals("bird's egg"))
		{
			return 0;
		}
		if (name.equals("long bone"))
		{
			return 1;
		}
		return UNKNOWN;
	}

	private static int fletchingBaseRank(String name)
	{
		if (name.equals("arrow shaft"))
		{
			return 0;
		}
		if (name.equals("feather"))
		{
			return 1;
		}
		if (name.equals("headless arrow"))
		{
			return 2;
		}
		return UNKNOWN;
	}

	private static int bowTypeRank(String name)
	{
		if (name.contains("shortbow"))
		{
			return 0;
		}
		if (name.contains("longbow"))
		{
			return 1;
		}
		return UNKNOWN;
	}

	private static int textileRank(String name)
	{
		if (name.equals("flax"))
		{
			return 0;
		}
		if (name.equals("ball of wool"))
		{
			return 1;
		}
		if (name.equals("thread"))
		{
			return 2;
		}
		if (name.contains("fabric"))
		{
			return 3;
		}
		return UNKNOWN;
	}

	private static int glassMaterialRank(String name)
	{
		if (name.equals("seaweed"))
		{
			return 0;
		}
		if (name.equals("soda ash"))
		{
			return 1;
		}
		if (name.equals("bucket of sand"))
		{
			return 2;
		}
		if (name.equals("molten glass"))
		{
			return 3;
		}
		return UNKNOWN;
	}

	private static int leatherRank(String name)
	{
		if (name.equals("leather"))
		{
			return 0;
		}
		if (name.contains("dragonhide"))
		{
			return 1;
		}
		if (name.contains("dragon leather"))
		{
			return 2;
		}
		return UNKNOWN;
	}

	private static int processedMiscRank(String name)
	{
		if (name.equals("papyrus"))
		{
			return 0;
		}
		if (name.equals("rock-shell chunk"))
		{
			return 1;
		}
		return UNKNOWN;
	}

	private static int containerRank(String name)
	{
		if (name.equals("basket"))
		{
			return 0;
		}
		if (name.contains("sack"))
		{
			return 1;
		}
		if (name.equals("pot"))
		{
			return 2;
		}
		if (name.equals("bowl"))
		{
			return 3;
		}
		if (name.equals("bucket"))
		{
			return 4;
		}
		if (name.equals("jug"))
		{
			return 5;
		}
		if (name.equals("vial"))
		{
			return 6;
		}
		if (name.equals("beer glass"))
		{
			return 7;
		}
		if (name.contains("fishbowl"))
		{
			return 8;
		}
		if (name.contains("pack"))
		{
			return 9;
		}
		return UNKNOWN;
	}

	private static boolean isFishingTool(String name)
	{
		return name.contains("fishing") || name.contains("bait") || name.contains("harpoon")
			|| name.equals("lobster pot") || name.equals("fish offcuts");
	}

	private static boolean isFarmingTool(String name)
	{
		return name.equals("rake") || name.equals("seed dibber") || name.startsWith("watering can")
			|| name.equals("spade");
	}

	private static boolean isCraftingTool(String name)
	{
		return name.equals("knife") || name.equals("chisel") || name.equals("needle")
			|| name.equals("glassblowing pipe") || name.equals("hammer");
	}

	private static boolean isLightSource(String name)
	{
		return name.contains("torch") || name.contains("candle lantern") || name.contains("oil lamp");
	}

	private static int parenNumber(String name)
	{
		int end = name.lastIndexOf(')');
		int start = name.lastIndexOf('(');
		if (start < 0 || end <= start + 1)
		{
			return UNKNOWN;
		}
		try
		{
			return Integer.parseInt(name.substring(start + 1, end));
		}
		catch (NumberFormatException ex)
		{
			return UNKNOWN;
		}
	}

	private static int basketQuantityRank(String name)
	{
		int amount = parenNumber(name);
		return amount == UNKNOWN ? 0 : 10 - amount;
	}

	private static int doseSortRank(String name)
	{
		int dose = parenNumber(name);
		return dose == UNKNOWN ? 0 : 10 - dose;
	}

	private static String stripRawOrBurntPrefix(String name)
	{
		if (name.startsWith("raw "))
		{
			return name.substring("raw ".length());
		}
		if (name.startsWith("burnt "))
		{
			return name.substring("burnt ".length());
		}
		return name;
	}

	private static String stripSeedSuffix(String name)
	{
		return name.endsWith(" seed") ? name.substring(0, name.length() - " seed".length()) : name;
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

	private static boolean containsWord(String name, String word)
	{
		return (" " + name + " ").contains(" " + word + " ");
	}

	private static String normalize(String itemName)
	{
		return itemName == null ? "" : itemName.toLowerCase();
	}

	private static String key(int group, int rank, int variant, String name)
	{
		return String.format("%03d:%03d:%03d:%s", group, rank, variant, name);
	}
}
