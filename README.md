# Sorted Bank

Sorted Bank is a RuneLite plugin that reorganizes the bank interface client-side without moving the actual items in your bank.

## Features

- Sort bank items alphabetically, by value, by item ID, or by category.
- Show category headers for common item groups.
- Use custom client-side tabs for all items, teleport items and runes, skilling supplies, and combat-style gear summaries.
- Optionally show faded ghost placeholders for missing items in common item groups.
- Toggle the sorted view on and off from inside the bank.

Sorted Bank only changes how bank widgets are displayed locally. It does not withdraw, deposit, rearrange, tag, or otherwise perform actions on your items.

## Configuration

- **Sort Method**: Choose alphabetical, value high-to-low, value low-to-high, item ID, or category sorting.
- **Price Source**: Use Grand Exchange or high alchemy prices for value-based sorting.
- **Show Ghost Items**: Show faded placeholders for missing items in supported groups.

## Development

Run the plugin in developer mode with:

```sh
./gradlew run
```

Build the plugin with:

```sh
./gradlew build
```
