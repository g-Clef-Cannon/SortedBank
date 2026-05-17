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
	JEWELRY(7, "Jewelry"),
	COSMETIC(8, "Cosmetics"),
	RUNE(9, "Runes"),
	AMMO(10, "Ammunition"),
	POTION(11, "Potions"),
	FOOD(12, "Food"),
	TELEPORT(13, "Teleportation"),
	TOOL(14, "Tools & Skilling"),
	SEED(15, "Seeds & Farming"),
	HERB(16, "Herbs"),
	LOG(17, "Logs"),
	ORE(18, "Ores & Bars"),
	GEM(19, "Gems"),
	KEY(20, "Keys"),
	QUEST_ITEM(21, "Quest & Lore"),
	PRAYER_ITEM(22, "Prayer Items"),
	FLETCHING_COMPONENT(23, "Fletching Components"),
	PROCESSED_MATERIAL(24, "Processed Materials"),
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

		return categorize(comp.getName(), comp.getInventoryActions());
	}

	static ItemCategory categorize(String itemName, String[] actions)
	{
		String name = itemName.toLowerCase();
		boolean isEquippable = hasAction(actions, "Wield") || hasAction(actions, "Wear") || hasAction(actions, "Equip");

		if (name.equals("coins") || name.equals("platinum token"))
		{
			return CURRENCY;
		}

		// Keys
		if (name.endsWith(" key") || name.startsWith("crystal key")
			|| name.startsWith("dark totem"))
		{
			return KEY;
		}

		// Quest, clues, and lore
		if (name.startsWith("clue scroll") || name.startsWith("casket")
			|| name.startsWith("scroll box")
			|| name.equals("tattered page") || name.startsWith("tattered page ")
			|| name.endsWith(" page") && (name.contains("tattered") || name.contains("ancient")
				|| name.contains("dusty") || name.contains("damp") || name.contains("torn"))
			|| name.endsWith(" bead")
			|| name.contains("journal") || name.contains("diary") || name.contains("book")
			|| name.contains("letter") || name.contains("note") && !name.contains("bank note"))
		{
			return QUEST_ITEM;
		}

		// Runes
		if (name.endsWith(" rune") || name.equals("wrath rune") || name.equals("soul rune"))
		{
			return RUNE;
		}

		// Fletching components
		if (name.equals("feather") || name.equals("arrow shaft") || name.equals("headless arrow")
			|| name.equals("javelin shaft") || name.equals("bow string") || name.equals("bowstring")
			|| name.endsWith(" arrowtips") || name.endsWith(" bolt tips")
			|| name.contains("unfinished bolts") || name.endsWith(" bolts (unf)") || name.endsWith("(u)"))
		{
			return FLETCHING_COMPONENT;
		}

		// Ammunition
		if (name.contains("arrow") || name.contains("bolt") || name.contains("dart")
			|| name.contains(" knives") || name.endsWith(" knife") && !name.equals("knife") && isEquippable
			|| name.endsWith(" knife(p)")
			|| name.endsWith(" knife(p+)") || name.endsWith(" knife(p++)")
			|| name.contains("javelin") || name.contains("thrownaxe"))
		{
			return AMMO;
		}

		// Potions
		if (name.contains("potion(") || name.contains("potion (")
			|| name.contains("brew(") || name.contains("brew (")
			|| name.contains("restore(") || name.contains("restore (")
			|| name.contains("mix(") || name.contains("mix (")
			|| name.contains("antipoison") || name.contains("antidote")
			|| name.startsWith("weapon poison") || isDosePotion(name))
		{
			return POTION;
		}

		// Food
		if (hasAction(actions, "Eat") || hasAction(actions, "Drink")
			|| name.startsWith("raw ") && isRawFood(name)
			|| name.equals("beer") || name.equals("dwarven stout"))
		{
			return FOOD;
		}

		// Prayer
		if (name.equals("bones") || name.endsWith(" bones") || name.contains("bonemeal")
			|| name.endsWith(" ashes") || name.equals("ashes")
			|| name.equals("long bone") || name.equals("curved bone")
			|| name.startsWith("ensouled ") && name.endsWith(" head")
			|| name.equals("bird's egg"))
		{
			return PRAYER_ITEM;
		}

		// Seeds
		if (name.endsWith(" seed") || name.endsWith(" seeds")
			|| name.equals("acorn") || name.contains("sapling")
			|| name.endsWith(" spore"))
		{
			return SEED;
		}

		// Herbs
		if (isHerb(name) || isHerbloreIngredient(name))
		{
			return HERB;
		}

		// Collectibles and non-coin currencies
		if (name.equals("mark of grace") || name.equals("frog token")
			|| name.equals("ancient shard") || name.equals("lizardman fang"))
		{
			return COLLECTIBLE;
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

		// Containers
		if (name.equals("vial") || name.equals("vial of water") || name.equals("bucket")
			|| name.equals("bucket of water") || name.equals("jug") || name.equals("jug of water")
			|| name.equals("pot") || name.equals("bowl") || name.equals("empty sack")
			|| name.contains("jug pack") || name.equals("beer glass"))
		{
			return CONTAINER;
		}

		// Processed materials
		if (name.endsWith(" leather") || name.equals("leather") || name.endsWith(" plank")
			|| name.endsWith(" planks") || name.endsWith(" cloth") || name.equals("wool")
			|| name.endsWith(" nails") || name.endsWith("dragonhide") || name.equals("thread")
			|| name.equals("papyrus") || name.contains("fabric") || name.endsWith(" chunk"))
		{
			return PROCESSED_MATERIAL;
		}

		// Gems
		if (!isJewelryName(name)
			&& (name.contains("sapphire") || name.contains("emerald") || name.contains("ruby")
				|| name.contains("diamond") || name.contains("dragonstone") || name.contains("onyx")
				|| name.contains("zenyte") || name.contains("opal") || name.contains("jade")
				|| name.contains("topaz")))
		{
			return GEM;
		}

		// Teleportation
		if (name.contains("teleport") || name.contains("tab") && name.contains("tele")
			|| name.contains("scroll of redirection") || name.equals("games necklace")
			|| name.contains("ring of dueling") || name.contains("amulet of glory")
			|| name.contains("skills necklace") || name.contains("combat bracelet")
			|| name.contains("ring of wealth") || name.equals("xeric's talisman"))
		{
			return TELEPORT;
		}

		// Jewelry
		if (isJewelryName(name))
		{
			return JEWELRY;
		}

		// Tools & Skilling
		if (name.contains("pickaxe") || name.contains("axe") && !name.contains("battleaxe")
			|| name.contains("harpoon") || name.equals("hammer") || name.contains("chisel")
			|| name.contains("tinderbox") || name.contains("knife") && !isEquippable
			|| name.contains("needle") || name.contains("fishing rod") || name.contains("net")
			|| name.contains("fishing bait")
			|| name.contains("spade") || name.contains("rake") || name.contains("seed dibber")
			|| name.contains("secateurs") || name.contains("watering can")
			|| name.equals("dynamite") || isRunecraftingTalisman(name) || name.equals("rope"))
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

	private static boolean isJewelryName(String name)
	{
		return containsWord(name, "ring") || containsWord(name, "amulet") || containsWord(name, "necklace")
			|| containsWord(name, "bracelet") || containsWord(name, "signet") || containsWord(name, "talisman")
			&& !isRunecraftingTalisman(name);
	}

	private static boolean containsWord(String name, String word)
	{
		return (" " + name + " ").contains(" " + word + " ");
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

	private static boolean isHerbloreIngredient(String name)
	{
		return name.equals("limpwurt root")
			|| name.equals("red spiders' eggs")
			|| name.equals("bird nest")
			|| name.equals("crushed nest")
			|| name.equals("snape grass")
			|| name.equals("white berries")
			|| name.equals("potato cactus")
			|| name.equals("mort myre fungus")
			|| name.equals("blue dragon scale")
			|| name.equals("wine of zamorak");
	}

	private static boolean isRawFood(String name)
	{
		return name.contains("beef") || name.contains("chicken") || name.contains("rabbit")
			|| name.contains("rat meat") || name.contains("bear meat") || name.contains("bird meat")
			|| name.contains("shrimp") || name.contains("anchovies") || name.contains("sardine")
			|| name.contains("herring") || name.contains("mackerel") || name.contains("trout")
			|| name.contains("cod") || name.contains("pike") || name.contains("salmon")
			|| name.contains("tuna") || name.contains("lobster") || name.contains("bass")
			|| name.contains("swordfish") || name.contains("monkfish") || name.contains("shark")
			|| name.contains("karambwan") || name.contains("anglerfish");
	}

	private static boolean isDosePotion(String name)
	{
		return hasDoseSuffix(name) && (name.startsWith("attack")
			|| name.startsWith("strength")
			|| name.startsWith("defence")
			|| name.startsWith("super attack")
			|| name.startsWith("super strength")
			|| name.startsWith("super defence")
			|| name.startsWith("ranging")
			|| name.startsWith("magic")
			|| name.startsWith("prayer")
			|| name.startsWith("energy")
			|| name.startsWith("super energy")
			|| name.startsWith("stamina")
			|| name.startsWith("combat")
			|| name.startsWith("super combat")
			|| name.startsWith("hunter")
			|| name.startsWith("fishing")
			|| name.startsWith("agility")
			|| name.startsWith("antifire")
			|| name.startsWith("super antifire")
			|| name.startsWith("extended antifire"));
	}

	private static boolean hasDoseSuffix(String name)
	{
		return name.endsWith("(1)") || name.endsWith("(2)") || name.endsWith("(3)") || name.endsWith("(4)");
	}

	private static boolean isRunecraftingTalisman(String name)
	{
		return name.endsWith(" talisman") && !name.equals("xeric's talisman");
	}

	private static boolean isMagicArmor(String name)
	{
		return name.contains("robe") || name.contains("wizard") || name.contains("mystic")
			|| name.contains("infinity") || name.contains("ancestral") || name.contains("ahrim")
			|| name.contains("virtus") || name.contains("dagon'hai") || name.contains("splitbark")
			|| name.contains("swampbark") || name.contains("bloodbark") || name.contains("lunar")
			|| name.contains("xerician");
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
			|| name.contains("obsidian") || name.contains("fighter torso") || name.contains("shayzien");
	}

	private static boolean isCosmetic(String name)
	{
		return name.contains("partyhat") || name.contains("h'ween mask") || name.contains("santa hat")
			|| name.contains("elegant") || name.contains("beret") || name.contains("boater")
			|| name.contains("cavalier") || name.contains("headband") || name.contains("mime")
			|| name.contains("clown") || name.contains("lederhosen") || name.contains("flared trousers")
			|| name.contains("gnome scarf") || name.contains("holiday") || name.contains("costume")
			|| name.contains("ornament") || name.contains("ornate") || name.contains("cosmetic")
			|| name.contains("beekeeper") || name.contains("zombie") || name.contains("camo");
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
