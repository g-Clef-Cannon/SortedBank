package com.sortedbank;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.ScriptID;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemEquipmentStats;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemStats;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptPostFired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(
	name = "Sorted Bank",
	description = "Automatically sort bank items client-side by various sort methods",
	tags = {"bank", "sort", "organize", "items"}
)
public class SortedBankPlugin extends Plugin
{
	private static final Logger log = LoggerFactory.getLogger(SortedBankPlugin.class);
	private static final int DEFAULT_X_PITCH = 48;
	private static final int DEFAULT_Y_PITCH = 36;
	private static final int DEFAULT_START_X = 51;
	private static final int DEFAULT_START_Y = 42;
	private static final int DEFAULT_COLUMNS = 8;
	private static final int DEFAULT_ITEM_WIDTH = 36;
	private static final int DEFAULT_ITEM_HEIGHT = 32;
	private static final int GHOST_OPACITY = 150;
	private static final int HEADER_HEIGHT = 16;
	private static final int HEADER_TEXT_COLOR = 0xFF981F;
	private static final int TOGGLE_BUTTON_Y = 4;
	private static final int TOGGLE_BUTTON_WIDTH = 66;
	private static final int TOGGLE_BUTTON_HEIGHT = 18;
	private static final int[] TOGGLE_BUTTON_X_CANDIDATES = {84, 112, 64, 260, 354};
	private static final int SORTED_TAB_X = 50;
	private static final int SORTED_TAB_Y = 39;
	private static final int SORTED_TAB_HEIGHT = 18;
	private static final int SORTED_TAB_GAP = 10;
	private static final SortedTab[] SORTED_TABS = {
		SortedTab.ALL,
		SortedTab.TELEPORTATION,
		SortedTab.SKILLING,
		SortedTab.MELEE_BIS,
		SortedTab.RANGED_BIS,
		SortedTab.MAGE_BIS
	};
	private final Set<Widget> ghostWidgets = new HashSet<>();
	private final Set<Widget> headerWidgets = new HashSet<>();
	private final Map<Widget, WidgetState> standardLayout = new HashMap<>();
	private final List<Widget> sortedTabWidgets = new ArrayList<>();
	private WidgetState standardItemContainer;
	private Widget toggleButton;
	private SortedTab selectedTab = SortedTab.ALL;
	private boolean sortedBankEnabled = true;
	private boolean sortedLayoutApplied;
	private boolean lastUiVisible;

	@Inject
	private Client client;

	@Inject
	private SortedBankConfig config;

	@Inject
	private ItemManager itemManager;

	@Inject
	private ClientThread clientThread;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Sorted Bank started");
		clientThread.invokeLater(this::sortBank);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Sorted Bank stopped");
		clientThread.invokeLater(() ->
		{
			restoreBank();
			hideToggleButton();
		});
	}

	@Provides
	SortedBankConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SortedBankConfig.class);
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (event.getScriptId() == ScriptID.BANKMAIN_BUILD)
		{
			log.debug("BANKMAIN_BUILD fired: sortedBankEnabled={}, selectedTab={}, sortedLayoutApplied={}",
				sortedBankEnabled, selectedTab, sortedLayoutApplied);
			addOrUpdateToggleButton();
			if (!sortedBankEnabled)
			{
				return;
			}
			sortedLayoutApplied = false;
			sortBank();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!"sortedbank".equals(event.getGroup()))
		{
			return;
		}
		clientThread.invokeLater(this::sortBank);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (client.getWidget(ComponentID.BANK_CONTAINER) == null)
		{
			if (lastUiVisible)
			{
				log.debug("Bank closed; managed UI no longer visible");
				lastUiVisible = false;
			}
			return;
		}

		addOrUpdateToggleButton();
		if (sortedBankEnabled && client.getWidget(ComponentID.BANK_ITEM_CONTAINER) != null)
		{
			hideBankTabStrip();
			addOrUpdateSortedTabs();
		}
		logUiState("game tick");
	}

	private void sortBank()
	{
		addOrUpdateToggleButton();

		Widget bankContainer = client.getWidget(ComponentID.BANK_ITEM_CONTAINER);
		if (bankContainer == null || bankContainer.getDynamicChildren() == null)
		{
			log.debug("sortBank skipped: itemContainer={}, dynamicChildren={}", bankContainer,
				bankContainer == null ? null : bankContainer.getDynamicChildren());
			return;
		}

		Widget[] children = bankContainer.getDynamicChildren();
		if (!sortedLayoutApplied)
		{
			standardLayout.clear();
			resetManagedWidgets(children);
			captureStandardLayout(children);
		}
		else
		{
			restoreStandardLayout(children, false);
		}

		// Use standard bank slot geometry (autodetection is unreliable because
		// after our first sort, the widgets carry our re-laid-out positions).
		int startX = DEFAULT_START_X;
		int startY = DEFAULT_START_Y;
		int xPitch = DEFAULT_X_PITCH;
		int yPitch = DEFAULT_Y_PITCH;
		int columns = DEFAULT_COLUMNS;

		hideBankTabStrip();
		addOrUpdateSortedTabs();
		logUiState("sortBank");

		List<BankItem> realItems = new ArrayList<>();
		List<Widget> emptySlots = new ArrayList<>();

		for (Widget child : children)
		{
			int itemId = child.getItemId();
			if (itemId > 0)
			{
				realItems.add(new BankItem(child, canonicalizeItemId(itemId), child.getItemQuantity(), false));
			}
			else
			{
				emptySlots.add(child);
			}
		}

		if (realItems.isEmpty())
		{
			return;
		}

		// Ghost items: find active groups and add missing items
		List<BankItem> ghostItems = new ArrayList<>();
		if (config.showGhostItems())
		{
			ghostItems = buildGhostItems(realItems, emptySlots);
		}
		List<Widget> headerSlots = getHeaderSlots(emptySlots, ghostItems);

		// Combine real + ghost items and sort
		List<BankItem> allItems = new ArrayList<>(realItems);
		allItems.addAll(ghostItems);
		Set<Integer> selectedItemIds = getSelectedTabItemIds(allItems);
		allItems.removeIf(item -> !selectedItemIds.contains(item.itemId));
		hideUnselectedItems(children, allItems);
		allItems.sort(buildComparator());

		boolean useHeaders = config.sortMethod() == SortMethod.CATEGORY;

		if (useHeaders)
		{
			layoutWithHeaders(allItems, headerSlots, ghostItems, children, columns, xPitch, yPitch, startX, startY);
		}
		else
		{
			layoutFlat(allItems, ghostItems, children, columns, xPitch, yPitch, startX, startY);
		}
		sortedLayoutApplied = true;
	}

	private Set<Integer> getSelectedTabItemIds(List<BankItem> items)
	{
		Set<Integer> itemIds = new HashSet<>();
		if (selectedTab.isBisTab())
		{
			itemIds.addAll(getBestInSlotItemIds(items, selectedTab));
			return itemIds;
		}

		for (BankItem item : items)
		{
			if (selectedTab.matches(this, item.itemId))
			{
				itemIds.add(item.itemId);
			}
		}
		return itemIds;
	}

	private Set<Integer> getBestInSlotItemIds(List<BankItem> items, SortedTab tab)
	{
		Map<Integer, Integer> bestScoreBySlot = new HashMap<>();
		Map<Integer, Set<Integer>> bestItemIdsBySlot = new HashMap<>();

		for (BankItem item : items)
		{
			if (tab == SortedTab.MAGE_BIS && getCategory(item.itemId) == ItemCategory.RUNE)
			{
				bestItemIdsBySlot.computeIfAbsent(-2, ignored -> new HashSet<>()).add(item.itemId);
				continue;
			}

			if (tab == SortedTab.MAGE_BIS && isStaff(item.itemId))
			{
				bestItemIdsBySlot.computeIfAbsent(-1, ignored -> new HashSet<>()).add(item.itemId);
				continue;
			}

			ItemStats itemStats = itemManager.getItemStats(item.itemId);
			if (itemStats == null || !itemStats.isEquipable() || itemStats.getEquipment() == null)
			{
				continue;
			}

			ItemEquipmentStats equipment = itemStats.getEquipment();
			int score = tab.bisScore(equipment);
			if (score <= 0)
			{
				continue;
			}

			int slot = equipment.getSlot();
			int bestScore = bestScoreBySlot.getOrDefault(slot, Integer.MIN_VALUE);
			if (score > bestScore)
			{
				bestScoreBySlot.put(slot, score);
				Set<Integer> itemIds = new HashSet<>();
				itemIds.add(item.itemId);
				bestItemIdsBySlot.put(slot, itemIds);
			}
			else if (score == bestScore)
			{
				bestItemIdsBySlot.computeIfAbsent(slot, ignored -> new HashSet<>()).add(item.itemId);
			}
		}

		Set<Integer> itemIds = new HashSet<>();
		for (Set<Integer> slotItemIds : bestItemIdsBySlot.values())
		{
			itemIds.addAll(slotItemIds);
		}
		return itemIds;
	}

	private boolean isStaff(int itemId)
	{
		String name = getItemName(itemId).toLowerCase();
		return name.contains("staff");
	}

	private void hideUnselectedItems(Widget[] children, List<BankItem> visibleItems)
	{
		Set<Widget> visibleWidgets = new HashSet<>();
		for (BankItem item : visibleItems)
		{
			visibleWidgets.add(item.widget);
		}

		for (Widget child : children)
		{
			if (child.getItemId() > 0 && !visibleWidgets.contains(child))
			{
				child.setHidden(true);
				child.revalidate();
			}
		}
	}

	private void addOrUpdateToggleButton()
	{
		Widget bankContainer = client.getWidget(ComponentID.BANK_CONTAINER);
		if (bankContainer == null)
		{
			if (toggleButton != null)
			{
				log.debug("Dropping toggleButton reference because BANK_CONTAINER is null");
			}
			toggleButton = null;
			return;
		}

		if (toggleButton != null && !isChildOf(bankContainer, toggleButton))
		{
			log.info("Recreating detached toggleButton: existing={}, parent={}, bankContainer={}",
				toggleButton, toggleButton.getParent(), bankContainer);
			toggleButton = null;
		}

		if (toggleButton == null || toggleButton.getParent() != bankContainer)
		{
			log.debug("Creating toggleButton: existing={}, existingParent={}, bankContainer={}",
				toggleButton, toggleButton == null ? null : toggleButton.getParent(), bankContainer);
			toggleButton = bankContainer.createChild(-1, WidgetType.TEXT);
			toggleButton.setTextColor(HEADER_TEXT_COLOR);
			toggleButton.setTextShadowed(true);
			toggleButton.setFontId(495);
			toggleButton.setName("Sorted Bank");
			toggleButton.setAction(0, "Toggle");
			toggleButton.setHasListener(true);
			toggleButton.setOnOpListener((JavaScriptCallback) ev -> toggleSortedBank());
		}

		toggleButton.setOriginalY(TOGGLE_BUTTON_Y);
		toggleButton.setOriginalWidth(TOGGLE_BUTTON_WIDTH);
		toggleButton.setOriginalHeight(TOGGLE_BUTTON_HEIGHT);
		toggleButton.setText(sortedBankEnabled ? "Sort: On" : "Sort: Off");
		toggleButton.setOriginalX(findToggleButtonX(bankContainer));
		toggleButton.setHidden(false);
		toggleButton.revalidate();
	}

	private int findToggleButtonX(Widget bankContainer)
	{
		for (int candidateX : TOGGLE_BUTTON_X_CANDIDATES)
		{
			if (!overlapsVisibleBankWidget(bankContainer.getStaticChildren(), candidateX)
				&& !overlapsVisibleBankWidget(bankContainer.getDynamicChildren(), candidateX))
			{
				return candidateX;
			}
		}

		return TOGGLE_BUTTON_X_CANDIDATES[0];
	}

	private boolean overlapsVisibleBankWidget(Widget[] widgets, int candidateX)
	{
		if (widgets == null)
		{
			return false;
		}

		for (Widget widget : widgets)
		{
			if (widget == null || widget == toggleButton || widget.isSelfHidden())
			{
				continue;
			}

			if (overlaps(candidateX, TOGGLE_BUTTON_Y, TOGGLE_BUTTON_WIDTH, TOGGLE_BUTTON_HEIGHT,
				widget.getOriginalX(), widget.getOriginalY(), widget.getOriginalWidth(), widget.getOriginalHeight()))
			{
				return true;
			}
		}

		return false;
	}

	private boolean overlaps(int x1, int y1, int width1, int height1, int x2, int y2, int width2, int height2)
	{
		return width1 > 0
			&& height1 > 0
			&& width2 > 0
			&& height2 > 0
			&& x1 < x2 + width2
			&& x1 + width1 > x2
			&& y1 < y2 + height2
			&& y1 + height1 > y2;
	}

	private void toggleSortedBank()
	{
		sortedBankEnabled = !sortedBankEnabled;
		addOrUpdateToggleButton();
		if (sortedBankEnabled)
		{
			sortBank();
		}
		else
		{
			restoreBank();
		}
	}

	private void hideToggleButton()
	{
		if (toggleButton != null)
		{
			toggleButton.setHidden(true);
			toggleButton.revalidate();
			toggleButton = null;
		}
	}

	private void layoutFlat(List<BankItem> allItems, List<BankItem> ghostItems,
		Widget[] children, int columns, int xPitch, int yPitch, int startX, int startY)
	{
		int index = 0;
		for (BankItem item : allItems)
		{
			int col = index % columns;
			int row = index / columns;
			positionItem(item, startX + col * xPitch, startY + row * yPitch);
			index++;
		}

		// Position remaining empty slots after items
		positionEmptySlots(children, ghostItems, index, columns, xPitch, yPitch, startX, startY);

		int totalRows = (allItems.size() + columns - 1) / columns;
		int totalHeight = startY + totalRows * yPitch;
		Widget bankContainer = client.getWidget(ComponentID.BANK_ITEM_CONTAINER);
		if (bankContainer != null)
		{
			updateScrollHeightPreservingPosition(bankContainer, totalHeight);
		}
	}

	private void layoutWithHeaders(List<BankItem> allItems, List<Widget> emptySlots,
		List<BankItem> ghostItems, Widget[] children, int columns, int xPitch, int yPitch,
		int startX, int startY)
	{
		// Group items by category in sorted order
		Map<ItemCategory, List<BankItem>> grouped = new LinkedHashMap<>();
		for (BankItem item : allItems)
		{
			ItemCategory cat = getCategory(item.itemId);
			grouped.computeIfAbsent(cat, k -> new ArrayList<>()).add(item);
		}

		int yOffset = startY;
		int emptySlotIdx = 0;
		List<Widget> activeHeaderWidgets = new ArrayList<>();

		for (Map.Entry<ItemCategory, List<BankItem>> entry : grouped.entrySet())
		{
			ItemCategory category = entry.getKey();
			List<BankItem> items = entry.getValue();

			// Create a header using an empty slot widget
			if (emptySlotIdx < emptySlots.size())
			{
				Widget headerWidget = emptySlots.get(emptySlotIdx++);
				activeHeaderWidgets.add(headerWidget);
				headerWidgets.add(headerWidget);

				// Convert this slot into a text header
				headerWidget.setType(WidgetType.TEXT);
				headerWidget.setText(category.getDisplayName());
				headerWidget.setTextColor(HEADER_TEXT_COLOR);
				headerWidget.setFontId(495);
				headerWidget.setTextShadowed(true);
				headerWidget.setOriginalX(startX);
				headerWidget.setOriginalY(yOffset);
				headerWidget.setOriginalWidth(columns * xPitch);
				headerWidget.setOriginalHeight(HEADER_HEIGHT);
				headerWidget.setItemId(-1);
				headerWidget.setItemQuantity(0);
				headerWidget.setOpacity(0);
				headerWidget.setHasListener(false);
				headerWidget.setHidden(false);
				headerWidget.revalidate();

				yOffset += HEADER_HEIGHT;
			}

			// Lay out items in this category
			int index = 0;
			for (BankItem item : items)
			{
				int col = index % columns;
				int row = index / columns;
				positionItem(item, startX + col * xPitch, yOffset + row * yPitch);
				index++;
			}

			int rowsUsed = (items.size() + columns - 1) / columns;
			yOffset += rowsUsed * yPitch;
		}

		// Hide remaining empty slots (not used as ghosts or headers)
		for (Widget child : children)
		{
			if (child.getItemId() <= 0
				&& !isUsedAsGhost(child, ghostItems)
				&& !activeHeaderWidgets.contains(child))
			{
				child.setOriginalX(0);
				child.setOriginalY(yOffset);
				child.setOriginalWidth(0);
				child.setOriginalHeight(0);
				child.setHidden(true);
				child.revalidate();
			}
		}

		// Update container scroll height to fit all content
		Widget bankContainer = client.getWidget(ComponentID.BANK_ITEM_CONTAINER);
		if (bankContainer != null)
		{
			updateScrollHeightPreservingPosition(bankContainer, yOffset);
		}
	}

	private void updateScrollHeightPreservingPosition(Widget bankContainer, int scrollHeight)
	{
		int scrollY = bankContainer.getScrollY();
		int maxScrollY = Math.max(0, scrollHeight - bankContainer.getOriginalHeight());
		bankContainer.setScrollHeight(scrollHeight);
		bankContainer.setScrollY(Math.min(scrollY, maxScrollY));
		bankContainer.revalidateScroll();
	}

	private void addOrUpdateSortedTabs()
	{
		Widget bankContainer = client.getWidget(ComponentID.BANK_CONTAINER);
		Widget tabContainer = client.getWidget(ComponentID.BANK_TAB_CONTAINER);
		if (bankContainer == null || tabContainer == null)
		{
			if (!sortedTabWidgets.isEmpty())
			{
				log.debug("Clearing sortedTabWidgets because containers missing: bankContainer={}, tabContainer={}",
					bankContainer, tabContainer);
			}
			sortedTabWidgets.clear();
			return;
		}

		if (!sortedTabWidgets.isEmpty()
			&& (sortedTabWidgets.get(0).getParent() != bankContainer || !allChildrenOf(bankContainer, sortedTabWidgets)))
		{
			log.info("Clearing stale sortedTabWidgets: firstParent={}, attachedTabs={}/{}, bankContainer={}",
				sortedTabWidgets.get(0).getParent(), countAttachedChildren(bankContainer, sortedTabWidgets),
				sortedTabWidgets.size(), bankContainer);
			sortedTabWidgets.clear();
		}

		while (sortedTabWidgets.size() < SORTED_TABS.length)
		{
			Widget tab = bankContainer.createChild(-1, WidgetType.TEXT);
			log.debug("Creating sorted tab widget index={} parent={}", sortedTabWidgets.size(), bankContainer);
			tab.setTextShadowed(true);
			tab.setFontId(495);
			tab.setHasListener(true);
			sortedTabWidgets.add(tab);
		}

		int x = SORTED_TAB_X;
		int y = SORTED_TAB_Y;
		for (int i = 0; i < sortedTabWidgets.size(); i++)
		{
			Widget tab = sortedTabWidgets.get(i);
			if (i >= SORTED_TABS.length)
			{
				tab.setHidden(true);
				continue;
			}

			SortedTab sortedTab = SORTED_TABS[i];
			int width = sortedTab.width;
			tab.setName(sortedTab.displayName);
			tab.setText(sortedTab.displayName);
			tab.setTextColor(sortedTab == selectedTab ? 0xFFFF00 : HEADER_TEXT_COLOR);
			tab.setAction(0, "Select");
			tab.setOnOpListener((JavaScriptCallback) ev -> selectSortedTab(sortedTab));
			tab.setOriginalX(x);
			tab.setOriginalY(y);
			tab.setOriginalWidth(width);
			tab.setOriginalHeight(SORTED_TAB_HEIGHT);
			tab.setHidden(false);
			tab.revalidate();
			x += width + SORTED_TAB_GAP;
		}
	}

	private void selectSortedTab(SortedTab tab)
	{
		selectedTab = tab;
		sortedLayoutApplied = false;
		sortBank();
	}

	private void hideBankTabStrip()
	{
		Widget tabContainer = client.getWidget(ComponentID.BANK_TAB_CONTAINER);
		if (tabContainer != null && !tabContainer.isSelfHidden())
		{
			log.debug("Hiding native bank tab container");
			tabContainer.setHidden(true);
		}
		setTagTabsHidden(true);
	}

	private void showBankTabStrip()
	{
		restoreItemContainerBounds();
		Widget tabContainer = client.getWidget(ComponentID.BANK_TAB_CONTAINER);
		if (tabContainer != null && tabContainer.isSelfHidden())
		{
			tabContainer.setHidden(false);
		}
		setTagTabsHidden(false);
	}

	private void expandItemContainerIntoHiddenTabs(Widget itemContainer)
	{
		Widget tabContainer = client.getWidget(ComponentID.BANK_TAB_CONTAINER);
		if (tabContainer == null)
		{
			return;
		}

		if (standardItemContainer == null)
		{
			standardItemContainer = new WidgetState(itemContainer);
		}

		int topDelta = itemContainer.getOriginalY() - tabContainer.getOriginalY();
		if (topDelta <= 0)
		{
			return;
		}

		itemContainer.setOriginalY(tabContainer.getOriginalY());
		itemContainer.setOriginalHeight(itemContainer.getOriginalHeight() + topDelta);
		itemContainer.revalidate();
		itemContainer.revalidateScroll();
	}

	private void restoreItemContainerBounds()
	{
		Widget itemContainer = client.getWidget(ComponentID.BANK_ITEM_CONTAINER);
		if (itemContainer == null || standardItemContainer == null)
		{
			return;
		}

		standardItemContainer.restore(itemContainer);
		standardItemContainer = null;
		itemContainer.revalidate();
		itemContainer.revalidateScroll();
	}

	/**
	 * Toggles visibility of the Bank Tags plugin's vertical tag-tab strip.
	 * The strip is added dynamically by the Bank Tags plugin to the left side
	 * of the bank container, so we identify it by position (x < items startX,
	 * below the title bar).
	 */
	private void setTagTabsHidden(boolean hidden)
	{
		Widget bankContainer = client.getWidget(ComponentID.BANK_CONTAINER);
		if (bankContainer == null)
		{
			return;
		}

		Widget[][] childArrays = new Widget[][] {
			bankContainer.getStaticChildren(),
			bankContainer.getDynamicChildren()
		};
		for (Widget[] arr : childArrays)
		{
			if (arr == null)
			{
				continue;
			}
			for (Widget child : arr)
			{
				if (child == null || child == toggleButton || sortedTabWidgets.contains(child))
				{
					continue;
				}
				int x = child.getOriginalX();
				int y = child.getOriginalY();
				int w = child.getOriginalWidth();
				// Left strip: positioned to the left of the items area, below the title bar
				if (x >= 0 && x < DEFAULT_START_X && w > 0 && w <= DEFAULT_START_X && y > 30)
				{
					if (child.isSelfHidden() != hidden)
					{
						log.debug("{} possible tag-tab widget: widget={}, x={}, y={}, w={}",
							hidden ? "Hiding" : "Showing", child, x, y, w);
						child.setHidden(hidden);
					}
				}
			}
		}
	}

	private void positionItem(BankItem item, int x, int y)
	{
		Widget w = item.widget;
		w.setOriginalX(x);
		w.setOriginalY(y);
		w.setHidden(false);
		w.revalidate();

		if (item.isGhost)
		{
			ghostWidgets.add(w);
			w.setOpacity(GHOST_OPACITY);
			w.setItemQuantity(0);
			w.setItemQuantityMode(0);
			w.setAction(0, null);
			w.setAction(1, null);
			w.setAction(2, null);
			w.setAction(3, null);
			w.setAction(4, null);
			w.setHasListener(false);
		}
		else
		{
			w.setOpacity(0);
		}

		// Block drag on all items
		w.setDragDeadZone(Integer.MAX_VALUE);
		w.setDragDeadTime(Integer.MAX_VALUE);
	}

	private void resetManagedWidgets(Widget[] children)
	{
		Set<Widget> activeChildren = new HashSet<>();
		for (Widget child : children)
		{
			activeChildren.add(child);
			if (ghostWidgets.contains(child) && child.getItemId() > 0 && child.getItemQuantity() == 0)
			{
				resetEmptySlot(child);
			}
			if (headerWidgets.contains(child) && child.getItemId() <= 0)
			{
				resetEmptySlot(child);
			}
		}
		ghostWidgets.retainAll(activeChildren);
		headerWidgets.retainAll(activeChildren);
	}

	private void captureStandardLayout(Widget[] children)
	{
		for (Widget child : children)
		{
			standardLayout.put(child, new WidgetState(child));
		}
	}

	private void restoreStandardLayout(Widget[] children, boolean clearSnapshot)
	{
		for (Widget child : children)
		{
			WidgetState state = standardLayout.get(child);
			if (state != null)
			{
				state.restore(child);
			}
			else if (ghostWidgets.contains(child) || headerWidgets.contains(child))
			{
				resetEmptySlot(child);
			}
			child.setDragDeadZone(0);
			child.setDragDeadTime(0);
			if (child.getItemId() <= 0)
			{
				child.setHidden(true);
				child.setOriginalWidth(0);
				child.setOriginalHeight(0);
			}
			child.revalidate();
		}
		ghostWidgets.clear();
		headerWidgets.clear();
		sortedLayoutApplied = false;
		if (clearSnapshot)
		{
			standardLayout.clear();
		}
	}

	private void resetEmptySlot(Widget widget)
	{
		widget.setType(WidgetType.GRAPHIC);
		widget.setText("");
		widget.setOriginalWidth(DEFAULT_ITEM_WIDTH);
		widget.setOriginalHeight(DEFAULT_ITEM_HEIGHT);
		widget.setItemId(-1);
		widget.setItemQuantity(0);
		widget.setItemQuantityMode(0);
		widget.setOpacity(0);
		widget.setHasListener(false);
		widget.setHidden(false);
		widget.revalidate();
	}

	private void positionEmptySlots(Widget[] children, List<BankItem> ghostItems,
		int startIndex, int columns, int xPitch, int yPitch, int startX, int startY)
	{
		int emptyIndex = startIndex;
		for (Widget child : children)
		{
			if (child.getItemId() <= 0 && !isUsedAsGhost(child, ghostItems))
			{
				int col = emptyIndex % columns;
				int row = emptyIndex / columns;
				child.setOriginalX(startX + col * xPitch);
				child.setOriginalY(startY + row * yPitch);
				child.revalidate();
				emptyIndex++;
			}
		}
	}

	private List<BankItem> buildGhostItems(List<BankItem> realItems, List<Widget> emptySlots)
	{
		Set<Integer> ownedIds = new HashSet<>();
		for (BankItem item : realItems)
		{
			ownedIds.add(item.itemId);
		}

		// Find all active groups (groups where at least one item is owned)
		Set<int[]> activeGroups = new HashSet<>();
		for (int itemId : ownedIds)
		{
			activeGroups.addAll(ItemGroup.getGroupsForItem(itemId));
		}

		// Collect missing item IDs from active groups
		List<Integer> missingIds = new ArrayList<>();
		Set<Integer> alreadyAdded = new HashSet<>();
		for (int[] group : activeGroups)
		{
			for (int id : group)
			{
				if (!ownedIds.contains(id) && alreadyAdded.add(id))
				{
					missingIds.add(id);
				}
			}
		}

		// Assign ghost items to available empty slot widgets
		List<BankItem> ghosts = new ArrayList<>();
		int slotIdx = 0;
		for (int missingId : missingIds)
		{
			if (slotIdx >= emptySlots.size())
			{
				break;
			}
			Widget slot = emptySlots.get(slotIdx++);
			slot.setItemId(missingId);
			slot.setItemQuantity(1);
			ghosts.add(new BankItem(slot, missingId, 0, true));
		}

		return ghosts;
	}

	private List<Widget> getHeaderSlots(List<Widget> emptySlots, List<BankItem> ghostItems)
	{
		List<Widget> slots = new ArrayList<>();
		for (Widget slot : emptySlots)
		{
			if (!isUsedAsGhost(slot, ghostItems))
			{
				slots.add(slot);
			}
		}
		return slots;
	}

	private boolean isUsedAsGhost(Widget widget, List<BankItem> ghostItems)
	{
		for (BankItem ghost : ghostItems)
		{
			if (ghost.widget == widget)
			{
				return true;
			}
		}
		return false;
	}

	private Comparator<BankItem> buildComparator()
	{
		SortMethod method = config.sortMethod();
		switch (method)
		{
			case ALPHABETICAL:
				return Comparator.comparing(item -> getItemName(item.itemId));
			case VALUE:
				return Comparator.<BankItem, Long>comparing(item -> getItemPrice(item.itemId)).reversed()
					.thenComparing(item -> getItemName(item.itemId));
			case VALUE_ASC:
				return Comparator.<BankItem, Long>comparing(item -> getItemPrice(item.itemId))
					.thenComparing(item -> getItemName(item.itemId));
			case ITEM_ID:
				return Comparator.comparingInt(item -> item.itemId);
			case CATEGORY:
				return Comparator.<BankItem, Integer>comparing(item -> getCategory(item.itemId).getSortOrder())
					.thenComparingInt(item -> item.itemId);
			default:
				return Comparator.comparing(item -> getItemName(item.itemId));
		}
	}

	private String getItemName(int itemId)
	{
		ItemComposition comp = itemManager.getItemComposition(canonicalizeItemId(itemId));
		return comp != null ? comp.getName() : "";
	}

	private long getItemPrice(int itemId)
	{
		itemId = canonicalizeItemId(itemId);
		if (config.priceSource() == PriceSource.HIGH_ALCH)
		{
			ItemComposition comp = itemManager.getItemComposition(itemId);
			return comp != null ? comp.getHaPrice() : 0;
		}
		return itemManager.getItemPrice(itemId);
	}

	private ItemCategory getCategory(int itemId)
	{
		itemId = canonicalizeItemId(itemId);
		ItemComposition comp = itemManager.getItemComposition(itemId);
		return ItemCategory.categorize(comp);
	}

	private int canonicalizeItemId(int itemId)
	{
		return itemManager.canonicalize(itemId);
	}

	private void restoreBank()
	{
		Widget bankContainer = client.getWidget(ComponentID.BANK_ITEM_CONTAINER);
		if (bankContainer == null || bankContainer.getDynamicChildren() == null)
		{
			return;
		}

		// Reset drag blocking, opacity, and hidden state on all children
		restoreStandardLayout(bankContainer.getDynamicChildren(), false);

		// Restore the tab strip we hid
		showBankTabStrip();
		hideSortedTabs();
		restoreNativeBankTabs();
		if (toggleButton != null)
		{
			toggleButton.setHidden(false);
			toggleButton.revalidate();
		}

		bankContainer.revalidate();
		bankContainer.revalidateScroll();
	}

	private void hideSortedTabs()
	{
		for (Widget tab : sortedTabWidgets)
		{
			tab.setHidden(true);
			tab.revalidate();
		}
	}

	private void restoreNativeBankTabs()
	{
		Widget tabContainer = client.getWidget(ComponentID.BANK_TAB_CONTAINER);
		if (tabContainer == null)
		{
			return;
		}

		restoreNativeBankTabChildren(tabContainer.getStaticChildren());
		restoreNativeBankTabChildren(tabContainer.getDynamicChildren());
	}

	private void restoreNativeBankTabChildren(Widget[] children)
	{
		if (children == null)
		{
			return;
		}

		for (Widget child : children)
		{
			if (child != null && !sortedTabWidgets.contains(child))
			{
				child.setHidden(false);
				child.revalidate();
			}
		}
	}

	private enum SortedTab
	{
		ALL("All", 24),
		TELEPORTATION("Tele", 32),
		SKILLING("Skill", 34),
		MELEE_BIS("Melee", 42),
		RANGED_BIS("Range", 46),
		MAGE_BIS("Mage", 38);

		private final String displayName;
		private final int width;

		SortedTab(String displayName, int width)
		{
			this.displayName = displayName;
			this.width = width;
		}

		private boolean matches(SortedBankPlugin plugin, int itemId)
		{
			if (this == ALL)
			{
				return true;
			}

			String name = plugin.getItemName(itemId).toLowerCase();
			ItemCategory category = plugin.getCategory(itemId);
			switch (this)
			{
				case TELEPORTATION:
					return category == ItemCategory.TELEPORT
						|| category == ItemCategory.RUNE;
				case SKILLING:
					return category == ItemCategory.TOOL
						|| category == ItemCategory.SEED
						|| category == ItemCategory.HERB
						|| category == ItemCategory.LOG
						|| category == ItemCategory.ORE
						|| category == ItemCategory.GEM
						|| category == ItemCategory.PRAYER_ITEM
						|| category == ItemCategory.FLETCHING_COMPONENT
						|| category == ItemCategory.PROCESSED_MATERIAL
						|| category == ItemCategory.CONSTRUCTION;
				case MELEE_BIS:
				case RANGED_BIS:
				case MAGE_BIS:
					return false;
				default:
					return true;
			}
		}

		private boolean isBisTab()
		{
			return this == MELEE_BIS || this == RANGED_BIS || this == MAGE_BIS;
		}

		private int bisScore(ItemEquipmentStats equipment)
		{
			int defenceScore = defenceScore(equipment);
			switch (this)
			{
				case MELEE_BIS:
					int meleeAttack = Math.max(equipment.getAstab(), Math.max(equipment.getAslash(), equipment.getAcrush()));
					if (meleeAttack < 0)
					{
						return 0;
					}
					return (meleeAttack + equipment.getStr() * 2) * 1000 + defenceScore;
				case RANGED_BIS:
					if (equipment.getArange() < 0)
					{
						return 0;
					}
					return (equipment.getArange() + equipment.getRstr() * 2) * 1000 + defenceScore;
				case MAGE_BIS:
					if (equipment.getAmagic() < 0)
					{
						return 0;
					}
					return (equipment.getAmagic() + Math.round(equipment.getMdmg() * 100)) * 1000 + defenceScore;
				default:
					return 0;
			}
		}

		private static int defenceScore(ItemEquipmentStats equipment)
		{
			return equipment.getDstab()
				+ equipment.getDslash()
				+ equipment.getDcrush()
				+ equipment.getDmagic()
				+ equipment.getDrange();
		}
	}

	private void logUiState(String source)
	{
		if (!log.isDebugEnabled())
		{
			return;
		}

		boolean toggleVisible = toggleButton != null && !toggleButton.isSelfHidden();
		Widget bankContainer = client.getWidget(ComponentID.BANK_CONTAINER);
		boolean toggleAttached = bankContainer != null && toggleButton != null && isChildOf(bankContainer, toggleButton);
		int visibleTabs = 0;
		int attachedTabs = bankContainer == null ? 0 : countAttachedChildren(bankContainer, sortedTabWidgets);
		for (Widget tab : sortedTabWidgets)
		{
			if (tab != null && !tab.isSelfHidden())
			{
				visibleTabs++;
			}
		}

		boolean uiVisible = toggleVisible || visibleTabs > 0;
		if (uiVisible != lastUiVisible || !toggleVisible || visibleTabs != SORTED_TABS.length
			|| !toggleAttached || attachedTabs != SORTED_TABS.length)
		{
			log.debug("Managed bank UI state after {}: toggle={} toggleAttached={} toggleParent={} tabs={}/{} attachedTabs={}/{} selectedTab={} sortedEnabled={}",
				source, toggleVisible, toggleAttached, toggleButton == null ? null : toggleButton.getParent(),
				visibleTabs, SORTED_TABS.length, attachedTabs, SORTED_TABS.length, selectedTab, sortedBankEnabled);
		}
		lastUiVisible = uiVisible;
	}

	private boolean allChildrenOf(Widget parent, List<Widget> children)
	{
		return countAttachedChildren(parent, children) == children.size();
	}

	private int countAttachedChildren(Widget parent, List<Widget> children)
	{
		int count = 0;
		for (Widget child : children)
		{
			if (isChildOf(parent, child))
			{
				count++;
			}
		}
		return count;
	}

	private boolean isChildOf(Widget parent, Widget child)
	{
		if (parent == null || child == null || child.getParent() != parent)
		{
			return false;
		}

		return containsWidget(parent.getStaticChildren(), child)
			|| containsWidget(parent.getDynamicChildren(), child)
			|| containsWidget(parent.getNestedChildren(), child);
	}

	private boolean containsWidget(Widget[] children, Widget widget)
	{
		if (children == null)
		{
			return false;
		}

		for (Widget child : children)
		{
			if (child == widget)
			{
				return true;
			}
		}
		return false;
	}

	private static class BankItem
	{
		final Widget widget;
		final int itemId;
		final int quantity;
		final boolean isGhost;

		BankItem(Widget widget, int itemId, int quantity, boolean isGhost)
		{
			this.widget = widget;
			this.itemId = itemId;
			this.quantity = quantity;
			this.isGhost = isGhost;
		}
	}

	private static class WidgetState
	{
		final int type;
		final int originalX;
		final int originalY;
		final int originalWidth;
		final int originalHeight;
		final int itemId;
		final int itemQuantity;
		final int itemQuantityMode;
		final int opacity;
		final boolean hidden;
		final String text;

		WidgetState(Widget widget)
		{
			this.type = widget.getType();
			this.originalX = widget.getOriginalX();
			this.originalY = widget.getOriginalY();
			this.originalWidth = widget.getOriginalWidth();
			this.originalHeight = widget.getOriginalHeight();
			this.itemId = widget.getItemId();
			this.itemQuantity = widget.getItemQuantity();
			this.itemQuantityMode = widget.getItemQuantityMode();
			this.opacity = widget.getOpacity();
			this.hidden = widget.isSelfHidden();
			this.text = widget.getText();
		}

		void restore(Widget widget)
		{
			widget.setType(type);
			widget.setOriginalX(originalX);
			widget.setOriginalY(originalY);
			widget.setOriginalWidth(originalWidth);
			widget.setOriginalHeight(originalHeight);
			widget.setItemId(itemId);
			widget.setItemQuantity(itemQuantity);
			widget.setItemQuantityMode(itemQuantityMode);
			widget.setOpacity(opacity);
			widget.setHidden(hidden);
			widget.setText(text);
		}
	}
}
