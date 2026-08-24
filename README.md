# Serene Seasons: Player Minimum

A small Forge 1.20.1 companion mod for [Serene Seasons](https://www.curseforge.com/minecraft/mc-mods/serene-seasons) that adds a `seasonCyclePlayerMinimum` gamerule, letting you freeze season progression whenever fewer than a configured number of players are online.

## Usage

```
/gamerule seasonCyclePlayerMinimum <count>
```

Set `<count>` to the minimum number of players that must be online for the season cycle to keep advancing. While the online player count is below that threshold, the season clock is frozen; it resumes the moment enough players are on. Set it to `0` (the default) to disable this behavior entirely and let Serene Seasons behave as normal.

## Compatibility

- Works standalone against stock Serene Seasons.
- Also works correctly if [Serene Seasons Fix - Revived](https://www.curseforge.com/minecraft/mc-mods/serene-seasons-fix-revived) is installed alongside it — this mod's check runs before Revived's own season-progression override, so the player-minimum freeze is respected either way.
- Does not modify or depend on any files from either mod; it only adds a Mixin-based check ahead of Serene Seasons' own tick handling.

## Installation

Drop the jar into your `mods` folder alongside Serene Seasons (Forge 1.20.1, Forge 47+). No configuration file — the gamerule is the only setting.

## License

[MIT](LICENSE)
