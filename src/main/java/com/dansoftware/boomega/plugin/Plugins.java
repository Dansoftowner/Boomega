package com.dansoftware.boomega.plugin;

import com.dansoftware.boomega.plugin.api.BoomegaPlugin;
import com.dansoftware.boomega.plugin.api.RegisteredPlugin;
import com.dansoftware.boomega.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Responsible for loading and accessing the application plugins.
 *
 * @author Daniel Gyoerffy
 */
public class Plugins {

    private static final Logger logger = LoggerFactory.getLogger(Plugins.class);

    private static final Plugins INSTANCE = new Plugins();

    private final List<BoomegaPlugin> plugins = new ArrayList<>();
    private boolean loaded;

    private Plugins() { }

    /**
     * Loads and instantiates all the plugins found in the plugin-directory.
     *
     * @throws AlreadyLoadedException if this method is called more than once
     */
    public synchronized void load() {
        if (!loaded) {
            plugins.addAll(
                    PluginClassLoader.getInstance().getAllClasses().stream()
                            .filter(classRef -> classRef.getAnnotation(RegisteredPlugin.class) != null)
                            .filter(BoomegaPlugin.class::isAssignableFrom)
                            .filter(classRef -> !Modifier.isAbstract(classRef.getModifiers()))
                            .peek(classRef -> logger.debug("Found plugin class: {}", classRef.getName()))
                            .map(ReflectionUtils::tryConstructObject)
                            .filter(Objects::nonNull)
                            .map(BoomegaPlugin.class::cast)
                            .peek(BoomegaPlugin::init)
                            .collect(Collectors.toList())
            );
        } else {
            throw new AlreadyLoadedException();
        }
    }

    /**
     * @return a read-only list with all the {@link BoomegaPlugin} objects
     */
    public List<BoomegaPlugin> getAll() {
        return Collections.unmodifiableList(plugins);
    }

    /**
     * Searches plugins that have the given location
     *
     * @param url the location
     * @return the list of {@link BoomegaPlugin} objects
     */
    public List<BoomegaPlugin> getPluginsOfFile(URL url) {
        return plugins.stream()
                .filter(it -> it.getClass().getProtectionDomain().getCodeSource().getLocation().equals(url))
                .collect(Collectors.toList());
    }

    /**
     * Searches plugins with the given type.
     *
     * @param classRef the class-reference of the type
     * @param <P> the type, subtype of the {@link BoomegaPlugin}
     * @return the list of plugin objects
     */
    @SuppressWarnings("unchecked")
    public <P extends BoomegaPlugin> List<P> of(Class<P> classRef) {
        return plugins.stream()
                .filter(it -> it.getClass().isAssignableFrom(classRef))
                .map(it -> (P) it)
                .collect(Collectors.toList());
    }

    /**
     * @return the count of the loaded plugin files
     */
    public int pluginFileCount() {
        return PluginClassLoader.getInstance().getReadPluginsCount();
    }

    /**
     * @return the count of the constructed plugin objects
     */
    public int pluginObjectCount() {
        return plugins.size();
    }

    public static Plugins getInstance() {
        return INSTANCE;
    }

    public static class AlreadyLoadedException extends RuntimeException {
    }
}
