package io.github.itamarbleiberg.bacap;

import io.papermc.paper.datapack.Datapack;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

/**
 * Reports whether the bundled datapack is active and warns about setups that break it.
 * All advancement logic lives in the datapack and is run by the server itself.
 */
public final class BacapPlugin extends JavaPlugin {

    /** The official BACAP world datapack and its add-ons use file names that start like this. */
    private static final String WORLD_PACK_PREFIX = "file/BlazeandCave's Advancements Pack ";

    @Override
    public void onEnable() {
        Logger log = getSLF4JLogger();
        String packName = getPluginMeta().getName() + "/" + BacapBootstrap.DATAPACK_ID;
        Datapack pack = getServer().getDatapackManager().getPack(packName);

        if (pack == null) {
            log.error("The bundled datapack \"{}\" was not loaded, so the BACAP advancements are missing. "
                + "Look for errors earlier in the log.", packName);
            return;
        }
        if (pack.isEnabled()) {
            log.info("{} is enabled as datapack \"{}\".", BacapBootstrap.datapackTitle(getPluginMeta()), packName);
        } else {
            log.warn("The bundled datapack is disabled. Enable it with: /datapack enable \"{}\"", packName);
        }
        checkWorldDatapacks(log, packName);
    }

    /** Finds leftover copies of the BACAP datapack and add-ons that load before the plugin's pack. */
    private void checkWorldDatapacks(Logger log, String packName) {
        // Enabled packs in load order: a pack overrides the files of every pack before it.
        List<String> enabled = getServer().getDatapackManager().getEnabledPacks().stream()
            .map(Datapack::getName)
            .toList();
        int ownIndex = enabled.indexOf(packName);
        for (int i = 0; i < enabled.size(); i++) {
            String name = enabled.get(i);
            if (!name.startsWith(WORLD_PACK_PREFIX)) {
                continue;
            }
            String rest = name.substring(WORLD_PACK_PREFIX.length());
            if (!rest.isEmpty() && Character.isDigit(rest.charAt(0))) {
                // "BlazeandCave's Advancements Pack <version>": the datapack this plugin replaces.
                log.warn("The world datapack \"{}\" is also enabled, but this plugin already provides BACAP. "
                    + "Delete it from the world's datapacks folder and restart the server.", name);
            } else if (i < ownIndex) {
                // An official add-on (Hardcore, Terralith, ...) must load after the main pack.
                log.warn("The add-on datapack \"{}\" loads before BACAP, so BACAP overrides its changes. "
                    + "Run /function blazeandcave:global_install once to fix the load order.", name);
            }
        }
    }
}
