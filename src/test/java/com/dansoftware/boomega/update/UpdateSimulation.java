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
        DIService.initModules(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ConfigSource.class).to(DummyConfigSource.class);

                bind(PluginService.class).to(DummyPluginService.class);

                bind(ReleasesFetcher.class).to(DummyReleasesFetcher.class);
                bind(String.class).annotatedWith(Names.named("appVersion")).toInstance("0.0.0");
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
            release.setAssets(List.of(buildSimpleReleaseAsset()));
            release.setVersion("1.0.0");
            release.setDescription("# New dummy release\n* Feature 1\n* Feature 2\n* Feature 3");
            return release;
        }

        private ReleaseAsset buildSimpleReleaseAsset() {
            return new ReleaseAsset() {
                {
                    setName("boomega");
                    setContentType("pkg");
                    setSize(5092);
                }

                @NotNull
                @Override
                public InputStream openStream() {
                    return new ByteArrayInputStream(new byte[5092]);
                }
            };
        }
    }
}
