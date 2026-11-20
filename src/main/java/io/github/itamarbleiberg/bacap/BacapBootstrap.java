package io.github.itamarbleiberg.bacap;

import io.papermc.paper.datapack.Datapack;
import io.papermc.paper.datapack.DatapackRegistrar;
import io.papermc.paper.datapack.DiscoveredDatapack;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import net.kyori.adventure.text.Component;

/**
 * Registers the bundled, unmodified BlazeandCave's Advancements Pack datapack with the server.
 *
 * <p>Paper fires the datapack discovery event before the worlds load and again on every
 * {@code /minecraft:reload}, so the pack is discovered every time the event runs.
 */
public final class BacapBootstrap implements PluginBootstrap {

    /** Folder inside the plugin jar that contains the datapack (pack.mcmeta and data/). */
    static final String DATAPACK_RESOURCE = "/bacap";

    /** Paper prefixes this with the plugin name, so the pack is called "BACAP/bacap". */
    static final String DATAPACK_ID = "bacap";

    /** The plugin version is the BACAP version, for example "BlazeandCave's Advancements Pack 1.20.2". */
    static String datapackTitle(PluginMeta meta) {
        return "BlazeandCave's Advancements Pack " + meta.getVersion();
    }

    private boolean reportedIncompatible;

    @Override
    public void bootstrap(BootstrapContext context) {
        Component title = Component.text(datapackTitle(context.getPluginMeta()));
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY, event -> {
            DatapackRegistrar registrar = event.registrar();
            URL datapack = BacapBootstrap.class.getResource(DATAPACK_RESOURCE);
            if (datapack == null) {
                context.getLogger().error("The datapack folder {} is missing from the plugin jar. "
                    + "The jar is damaged; download or build it again.", DATAPACK_RESOURCE);
                return;
            }
            try {
                DiscoveredDatapack pack = registrar.discoverPack(datapack.toURI(), DATAPACK_ID,
                    configurer -> configurer.title(title));
                if (pack == null) {
                    context.getLogger().error("Paper could not read the bundled datapack at {}.", datapack);
                } else if (pack.getCompatibility() != Datapack.Compatibility.COMPATIBLE && !reportedIncompatible) {
                    // Logged before the server parses the pack, so it appears ahead of any parse errors.
                    reportedIncompatible = true;
                    context.getLogger().error("This BACAP version does not support this Minecraft version "
                        + "(its datapack is marked {}). The server may fail to start or some advancements may "
                        + "not work. Use the BACAP version made for this Minecraft version.", pack.getCompatibility());
                }
            } catch (URISyntaxException | IOException e) {
                context.getLogger().error("Could not register the bundled datapack.", e);
            }
        });
    }
}
