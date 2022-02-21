/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.plugin;

import com.dansoftware.boomega.plugin.api.BoomegaPlugin;
import com.dansoftware.boomega.plugin.api.DisabledPlugin;
import com.dansoftware.boomega.plugin.api.PluginService;
import com.dansoftware.boomega.util.ReflectionUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Responsible for loading and accessing the application plugins located in the
 * plugin directory.
 *
 * @author Daniel Gyoerffy
 */
public class RealtimePluginService implements PluginService {

    private static final Logger logger = LoggerFactory.getLogger(RealtimePluginService.class);

    private static final RealtimePluginService INSTANCE = new RealtimePluginService();

    private final List<BoomegaPlugin> plugins = new LinkedList<>();
    private volatile boolean loaded;

    @Inject
    private RealtimePluginService() {
    }

    @Override
    public @NotNull List<BoomegaPlugin> getAll() {
        return Collections.unmodifiableList(plugins);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <P extends BoomegaPlugin> @NotNull List<P> of(@NotNull Class<P> classRef) {
        load();
        return plugins.stream()
                .filter(it -> classRef.isAssignableFrom(it.getClass()))
                .map(it -> (P) it)
                .toList();
    }

    @NotNull
    @Override
    public List<BoomegaPlugin> pluginsOfLocation(@NotNull URL url) {
        return plugins.stream()
                .filter(it -> it.getClass().getProtectionDomain().getCodeSource().getLocation().equals(url))
                .toList();
    }

    @Override
    public int getPluginFileCount() {
        return PluginClassLoader.getInstance().getReadPluginsCount();
    }

    @Override
    public synchronized void load() {
        if (!loaded) {
            plugins.addAll(
                    PluginClassLoader.getInstance().listAllClasses().stream()
                            .filter(this::isPluginUsable)
                            .peek(classRef -> logger.debug("Found plugin class: {}", classRef.getName()))
                            .map(ReflectionUtils::tryConstructObject)
                            .filter(Objects::nonNull)
                            .map(BoomegaPlugin.class::cast)
                            .peek(BoomegaPlugin::init)
                            .toList()
            );
            loaded = true;
        }
    }

    private boolean isPluginUsable(@NotNull Class<?> classRef) {
        return BoomegaPlugin.class.isAssignableFrom(classRef) &&
                !Modifier.isAbstract(classRef.getModifiers()) &&
                !classRef.isAnnotationPresent(DisabledPlugin.class);
    }

    public static RealtimePluginService getInstance() {
        return INSTANCE;
    }
}
