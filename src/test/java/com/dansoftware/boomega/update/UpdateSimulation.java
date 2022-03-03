/*
 * Boomega
 * Copyright (c) 2020-2022  Daniel Gyoerffy
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

package com.dansoftware.boomega.update;

import com.dansoftware.boomega.config.DummyConfigSource;
import com.dansoftware.boomega.config.source.ConfigSource;
import com.dansoftware.boomega.di.DIService;
import com.dansoftware.boomega.gui.app.BaseBoomegaApplication;
import com.dansoftware.boomega.gui.app.BoomegaApp;
import com.dansoftware.boomega.plugin.DummyPluginService;
import com.dansoftware.boomega.plugin.api.PluginService;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

// TODO: unfinished
public class UpdateSimulation {

    static {
        System.setProperty("boomega.version", "0.0.0");
        DIService.initModules(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ConfigSource.class).to(DummyConfigSource.class);
                bind(PluginService.class).to(DummyPluginService.class);
                bind(ReleasesFetcher.class).to(DummyReleasesFetcher.class);
                bind(String.class).annotatedWith(Names.named("appVersion"))
                        .toInstance(System.getProperty("boomega.version"));
            }
        });
    }

    public static void main(String[] args) {
        BaseBoomegaApplication.launchApp(BoomegaApp.class);
    }

    private static class DummyReleasesFetcher implements ReleasesFetcher {

        @NotNull
        @Override
        public Releases fetchReleases() {
            var releases = new Releases();
            releases.add(buildSimpleRelease());
            return releases;
        }

        private Release buildSimpleRelease() {
            var release = new Release();
            release.setAssets(buildReleaseAssets());
            release.setVersion("1.0.0");
            release.setDescription(description());
            return release;
        }

        private String description() {
            return """
                    # New dummy release
                    
                    * Feature 1
                    * Feature 2
                    * Feature 3
                    """;
        }

        private List<ReleaseAsset> buildReleaseAssets() {
            return List.of(
                    buildSimpleReleaseAsset("Boomega_1.0.0.exe", "exe", 92 * (int)Math.pow(1024, 2)),
                    buildSimpleReleaseAsset("Boomega_1.0.0.msi", "msi", 85 * (int)Math.pow(1024, 2)),
                    buildSimpleReleaseAsset("Boomega_1.0.0.zip", "msi", 102 * (int)Math.pow(1024, 2))
            );
        }


        private ReleaseAsset buildSimpleReleaseAsset(String name, String contentType, int size) {
            return new ReleaseAsset() {
                {
                    setName(name);
                    setContentType(contentType);
                    setSize(size);
                }

                @NotNull
                @Override
                public InputStream openStream() {
                    return new ByteArrayInputStream(new byte[size]);
                }
            };
        }
    }
}
