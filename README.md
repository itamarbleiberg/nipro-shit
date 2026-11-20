# BlazeandCave's Advancements Pack 1.20.2 as a Paper plugin

This plugin contains [BlazeandCave's Advancements Pack](https://bit.ly/3u9BXTr) (BACAP) 1.20.2 by
Cavinator1, which adds over 1000 advancements. You install it like any other plugin instead of
copying a datapack into the world folder.

The datapack inside the plugin is the original `BlazeandCave's Advancements Pack 1.20.2.zip`, copied
into the jar unchanged. The plugin registers it with Paper's datapack API, and the server then runs
it exactly as it runs a world datapack: same advancements, rewards, trophies, messages, scoreboards
and config menu.

## Requirements

- **Paper 1.21.11** (tested with build 132). Paper forks such as Purpur should also work, but they
  were not tested.
- **Java 21** or newer.
- BACAP 1.20.2 is made for Minecraft 1.21.11 only. On Paper 26.3 the server fails to start with it,
  the same as with the datapack version; the plugin logs an error that names the cause.
- Spigot and CraftBukkit are not supported. They have no API for loading a datapack from a plugin.

## Install

1. Stop the server.
2. Put `BACAP-1.20.2.jar` in the `plugins` folder.
3. Start the server. The console shows:
   `[BACAP] BlazeandCave's Advancements Pack 1.20.2 is enabled as datapack "BACAP/bacap".`

`/datapack list` shows the pack as `[BACAP/bacap (plugin)]`.

## Switching from the datapack

1. Stop the server and back up the world.
2. Delete `BlazeandCave's Advancements Pack 1.20.2.zip` from `world/datapacks`.
3. Put the jar in `plugins` and start the server.

Advancement progress, scores and BACAP settings are kept. If you forget step 2, the plugin logs a
warning about the second copy.

If you use official add-ons (Hardcore, Terralith, Nullscape, Amplified Nether), keep them in
`world/datapacks`. On an existing world the plugin's pack is added after them, which would hide
their changes. The plugin logs a warning when this happens; run
`/function blazeandcave:global_install` once to fix the order.

## Using BACAP

Nothing changes compared to the datapack. Open the config menu with `/function blazeandcave:config`.

To turn BACAP off, run `/datapack disable "BACAP/bacap"`. It stays off after restarts until you run
`/datapack enable "BACAP/bacap"`. To remove it, delete the jar. Back up the world before removing
or updating BACAP.

## Building

```sh
./gradlew build
```

The jar is written to `build/libs/BACAP-1.20.2.jar`. The build needs JDK 21 or newer and reads the
datapack from `BlazeandCave's Advancements Pack 1.20.2.zip` in this folder.

To update to a new BACAP version, replace the zip and set `version` in `build.gradle.kts` to the
new BACAP version (the zip name is derived from it). If the new version targets another Minecraft
version, also update the `paper-api` dependency and `api-version` in
`src/main/resources/paper-plugin.yml`.

### How it works

- `build.gradle.kts` copies the zip's contents into the jar under `bacap/`.
- `BacapBootstrap` registers that folder through Paper's `DATAPACK_DISCOVERY` lifecycle event. Paper
  fires the event at startup and on every `/minecraft:reload`.
- `BacapPlugin` logs whether the pack is enabled and warns about leftover copies of the datapack and
  add-ons in the wrong load order.

## Credits and licence

BlazeandCave's Advancements Pack is made by Cavinator1. Its licence is included in the jar
(`bacap/licence.txt`) and in the zip. It allows personal use, use on servers and personal
modifications. It does **not** allow re-uploading or redistributing the pack in any form, modified
or not, so do not publish the built jar.
