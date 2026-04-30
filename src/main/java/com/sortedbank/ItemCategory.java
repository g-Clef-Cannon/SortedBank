package com.sortedbank;

import net.runelite.api.ItemComposition;

public enum ItemCategory
{
	CURRENCY(0, "Currency"),
	MELEE_WEAPON(1, "Melee Weapons"),
	RANGED_WEAPON(2, "Ranged Weapons"),
	MAGIC_WEAPON(3, "Magic Weapons"),
	MELEE_ARMOR(4, "Melee Armor"),
	RANGED_ARMOR(5, "Ranged Armor"),
	MAGIC_ARMOR(6, "Magic Armor"),
	COSMETIC(7, "Cosmetics"),
	RUNE(8, "Runes"),
	AMMO(9, "Ammunition"),
	POTION(10, "Potions"),
	FOOD(11, "Food"),
	TELEPORT(12, "Teleportation"),
	TOOL(13, "Tools & Skilling"),
	SEED(14, "Seeds & Farming"),
	HERB(15, "Herbs"),
	LOG(16, "Logs"),
	ORE(17, "Ores & Bars"),
	GEM(18, "Gems"),
	KEY(19, "Keys"),
	QUEST_ITEM(20, "Quest & Lore"),
	PRAYER_ITEM(21, "Prayer Items"),
	FLETCHING_COMPONENT(22, "Fletching Components"),
	PROCESSED_MATERIAL(23, "Processed Materials"),
	JEWELRY(24, "Jewelry"),
	CONSTRUCTION(25, "Construction"),
	CONTAINER(26, "Containers"),
	COLLECTIBLE(27, "Collectibles"),
	MISC(99, "Miscellaneous");

	private final int sortOrder;
	private final String displayName;

	ItemCategory(int sortOrder, String displayName)
	{
		this.sortOrder = sortOrder;
		this.displayName = displayName;
	}

	public int getSortOrder()
	{
		return sortOrder;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}

	public static ItemCategory categorize(ItemComposition comp)
	{
		if (comp == null)
		{
			return MISC;
		}

		String name = comp.getName().toLowerCase();
		String[] actions = comp.getInventoryActions();
		boolean isEquippable = hasAction(actions, "Wield") || hasAction(actions, "Wear") || hasAction(actions, "Equip");

		if (name.equals("coins") || name.equals("platinum token"))
		{
			return CURRENCY;
		}

		// Runes
		if (name.endsWith(" rune") || name.equals("wrath rune") || name.equals("soul rune"))
		{
			return RUNE;
		}

		// Ammunition
		if (name.contains("arrow") || name.contains("bolt") || name.contains("dart")
			|| name.contains(" knives") || name.endsWith(" knife(p)")
			|| name.endsWith(" knife(p+)") || name.endsWith(" knife(p++)")
			|| name.contains("javelin") || name.contains("thrownaxe"))
		{
			return AMMO;
		}

		// Potions
		if (name.contains("potion(") || name.contains("potion (")
			|| name.contains("brew(") || name.contains("brew (")
			|| name.contains("restore(") || name.contains("restore (")
			|| name.contains("mix(") || name.contains("mix ("))
		{
			return POTION;
		}

		// Food
		if (hasAction(actions, "Eat"))
		{
			return FOOD;
		}

		// Seeds
		if (name.endsWith(" seed") || name.endsWith(" seeds")
			|| name.equals("acorn") || name.contains("sapling"))
		{
			return SEED;
		}

		// Herbs
		if (isHerb(name))
		{
			return HERB;
		}

		// Logs
		if (name.equals("logs") || name.endsWith(" logs"))
		{
			return LOG;
		}

		// Ores & Bars
		if (name.endsWith(" ore") || name.endsWith(" bar")
			|| name.equals("amethyst") || name.equals("coal"))
		{
			return ORE;
		}

		// Gems
		if (name.contains("sapphire") || name.contains("emerald") || name.contains("ruby")
			|| name.contains("diamond") || name.contains("dragonstone") || name.contains("onyx")
			|| name.contains("zenyte") || name.contains("opal") || name.contains("jade")
			|| name.contains("topaz"))
		{
			return GEM;
		}

		// Teleportation
		if (name.contains("teleport") || name.contains("tab") && name.contains("tele")
			|| name.contains("scroll of redirection") || name.equals("games necklace")
			|| name.contains("ring of dueling") || name.contains("amulet of glory")
			|| name.contains("skills necklace") || name.contains("combat bracelet")
			|| name.contains("ring of wealth"))
		{
			return TELEPORT;
		}

		// Tools & Skilling
		if (name.contains("pickaxe") || name.contains("axe") && !name.contains("battleaxe")
			|| name.contains("harpoon") || name.contains("hammer") || name.contains("chisel")
			|| name.contains("tinderbox") || name.contains("knife") && !isEquippable
			|| name.contains("needle") || name.contains("fishing rod") || name.contains("net")
			|| name.contains("spade") || name.contains("rake") || name.contains("seed dibber")
			|| name.contains("secateurs") || name.contains("watering can"))
		{
			return TOOL;
		}

		// Weapons & Armor (equipment)
		if (isEquippable)
		{
			if (isMagicWeapon(name))
			{
				return MAGIC_WEAPON;
			}
			if (isRangedWeapon(name))
			{
				return RANGED_WEAPON;
			}
			if (isMeleeWeapon(name))
			{
				return MELEE_WEAPON;
			}
			if (isCosmetic(name))
			{
				return COSMETIC;
			}
			if (isMagicArmor(name))
			{
				return MAGIC_ARMOR;
			}
			if (isRangedArmor(name))
			{
				return RANGED_ARMOR;
			}
			if (isMeleeArmor(name))
			{
				return MELEE_ARMOR;
			}
			return COSMETIC;
		}

		return MISC;
	}

	private static boolean isMagicWeapon(String name)
	{
		return name.contains("staff") || name.contains("wand") || name.contains("trident")
			|| name.contains("sceptre") || name.contains("scepter") || name.contains("kodai")
			|| name.contains("harmonised nightmare staff") || name.contains("volatile nightmare staff")
			|| name.contains("eldritch nightmare staff");
	}

	private static boolean isRangedWeapon(String name)
	{
		return name.contains("bow") || name.contains("crossbow") || name.contains("blowpipe")
			|| name.contains("ballista") || name.contains("chinchompa") || name.contains("atlatl");
	}

	private static boolean isMeleeWeapon(String name)
	{
		return name.contains("sword") || name.contains("scimitar") || name.contains("mace")
			|| name.contains("dagger") || name.contains("battleaxe") || name.contains("halberd")
			|| name.contains("spear") || name.contains("hasta") || name.contains("whip")
			|| name.contains("godsword") || name.contains("rapier") || name.contains("blade")
			|| name.contains("sabre") || name.contains("2h") || name.contains("claws")
			|| name.contains("maul") || name.contains("warhammer") || name.contains("defender");
	}

	private static boolean isHerb(String name)
	{
		return name.startsWith("grimy ")
			|| name.equals("guam leaf")
			|| name.equals("marrentill")
			|| name.equals("tarromin")
			|| name.equals("harralander")
			|| name.equals("ranarr weed")
			|| name.equals("toadflax")
			|| name.equals("irit leaf")
			|| name.equals("avantoe")
			|| name.equals("kwuarm")
			|| name.equals("snapdragon")
			|| name.equals("cadantine")
			|| name.equals("lantadyme")
			|| name.equals("dwarf weed")
			|| name.equals("torstol");
	}

	private static boolean isMagicArmor(String name)
	{
		return name.contains("robe") || name.contains("wizard") || name.contains("mystic")
			|| name.contains("infinity") || name.contains("ancestral") || name.contains("ahrim")
			|| name.contains("virtus") || name.contains("dagon'hai") || name.contains("splitbark")
			|| name.contains("swampbark") || name.contains("bloodbark") || name.contains("lunar");
	}

	private static boolean isRangedArmor(String name)
	{
		return name.contains("hide") || name.contains("dragonhide") || name.contains("d'hide")
			|| name.contains("leather") || name.contains("karil") || name.contains("armadyl")
			|| name.contains("masori") || name.contains("pegasian") || name.contains("ranger");
	}

	private static boolean isMeleeArmor(String name)
	{
		return name.contains("helm") || name.contains("full helm") || name.contains("med helm")
			|| name.contains("platebody") || name.contains("platelegs") || name.contains("plateskirt")
			|| name.contains("chainbody") || name.contains("kiteshield") || name.contains("sq shield")
			|| name.contains("shield") || name.contains("boots") || name.contains("gloves")
			|| name.contains("gauntlets") || name.contains("torva") || name.contains("bandos")
			|| name.contains("barrows") || name.contains("verac") || name.contains("dharok")
			|| name.contains("guthan") || name.contains("torag") || name.contains("justiciar")
			|| name.contains("obsidian") || name.contains("fighter torso");
	}

	private static boolean isCosmetic(String name)
	{
		return name.contains("partyhat") || name.contains("h'ween mask") || name.contains("santa hat")
			|| name.contains("elegant") || name.contains("beret") || name.contains("boater")
			|| name.contains("cavalier") || name.contains("headband") || name.contains("mime")
			|| name.contains("clown") || name.contains("lederhosen") || name.contains("flared trousers")
			|| name.contains("gnome scarf") || name.contains("holiday") || name.contains("costume")
			|| name.contains("ornament") || name.contains("ornate") || name.contains("cosmetic");
	}

	private static boolean hasAction(String[] actions, String action)
	{
		if (actions == null)
		{
			return false;
		}
		for (String a : actions)
		{
			if (a != null && a.equalsIgnoreCase(action))
			{
				return true;
			}
		}
		return false;
	}
}
