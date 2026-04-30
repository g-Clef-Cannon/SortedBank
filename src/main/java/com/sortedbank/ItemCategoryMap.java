package com.sortedbank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

final class ItemCategoryMap
{
	private static final String RESOURCE = "/com/sortedbank/item-categories.csv";
	private static final Map<Integer, ItemCategory> CATEGORIES = loadCategories();

	private ItemCategoryMap()
	{
	}

	static ItemCategory get(int itemId)
	{
		return CATEGORIES.get(itemId);
	}

	private static Map<Integer, ItemCategory> loadCategories()
	{
		InputStream inputStream = ItemCategoryMap.class.getResourceAsStream(RESOURCE);
		if (inputStream == null)
		{
			throw new IllegalStateException("Missing item category resource: " + RESOURCE);
		}

		Map<Integer, ItemCategory> categories = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				if (line.isEmpty() || line.startsWith("id,"))
				{
					continue;
				}

				String[] parts = line.split(",", 2);
				if (parts.length != 2)
				{
					throw new IllegalStateException("Invalid item category row: " + line);
				}

				categories.put(Integer.parseInt(parts[0]), ItemCategory.valueOf(parts[1]));
			}
		}
		catch (IOException | IllegalArgumentException ex)
		{
			throw new IllegalStateException("Failed to load item categories", ex);
		}

		return categories;
	}
}
