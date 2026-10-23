# My Ammo Highlighter

Highlights **your own** ammo lying on the ground (bolts, arrows, darts, knives, javelins), so you can find and pick it back up after a fight.

## How it works
- Remembers every ammo type you equip during the session, so piles stay highlighted even after your quiver runs out.
- Only highlights items the game marks as yours, so other players' drops are ignored (can be turned off).
- Outlines each tile and labels it with the item name and the combined stack count.

## Settings
| Setting | What it does |
|---|---|
| Only my drops | Ignore ammo owned by other players |
| Include thrown weapons | Also track darts, knives, javelins, thrownaxes and chinchompas you wield |
| Extra items | Comma-separated names to always highlight; wildcards allowed (e.g. `*bolts*`) |
| Tile color / Fill tile | Highlight color and whether the tile is shaded |
| Show name and count | Label above the tile |

How this differs from the built-in Ground Items plugin: Ground Items highlights by item name for everyone's drops. This plugin follows whatever ammo you're actually using and only shows your own drops, with nothing to configure.
