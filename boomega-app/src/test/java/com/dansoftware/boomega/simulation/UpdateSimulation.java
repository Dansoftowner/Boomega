/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
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

package com.dansoftware.boomega.simulation;

import com.dansoftware.boomega.config.DummyConfigSource;
import com.dansoftware.boomega.config.source.ConfigSource;
import com.dansoftware.boomega.di.DIService;
import com.dansoftware.boomega.gui.app.BaseBoomegaApplication;
import com.dansoftware.boomega.gui.app.BoomegaApp;
import com.dansoftware.boomega.main.bindings.ConcurrencyModule;
import com.dansoftware.boomega.plugin.api.PluginService;
import com.dansoftware.boomega.simulation.util.DelayedByteArrayInputStream;
import com.dansoftware.boomega.simulation.util.DummyPluginService;
import com.dansoftware.boomega.update.Release;
import com.dansoftware.boomega.update.ReleaseAsset;
import com.dansoftware.boomega.update.Releases;
import com.dansoftware.boomega.update.ReleasesFetcher;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.List;

public class UpdateSimulation {

    static {
        DIService.initModules(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ConfigSource.class).to(DummyConfigSource.class);
                bind(PluginService.class).to(DummyPluginService.class);
                bind(ReleasesFetcher.class).to(DummyReleasesFetcher.class);
                bind(String.class).annotatedWith(Names.named("appVersion"))
                        .toInstance(System.getProperty("boomega.version"));
            }
        }, new ConcurrencyModule());
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
            release.setVersion(System.getProperty("update.next_version"));
            release.setDescription(description());
            release.setWebsite("https://github.com/Dansoftowner/Boomega");
            return release;
        }

        private String description() {
            return """
                    > Note: it's a fake update
                    # Boomega v%s
                                        
                    * Full support for everything
                    * New features for everything
                    * Bug fixes
                                        
                    | Bug name | Bug id |
                    | -------- | ------ |
                    | Can't load preferences | **#144** |
                    | Crash when open record editor | **#123** |
                                        
                                       
                    > Boomega (C) - Daniel Gyoerffy
                                        
                    """.formatted(System.getProperty("update.next_version"));
        }

        private List<ReleaseAsset> buildReleaseAssets() {
            return List.of(
                    new FakeReleaseAsset(fileName("all", "jar"), "application/java-archive", MBs(74)),
                    new FakeReleaseAsset(fileName("1amd64-linux", "deb"), "application/vnd.debian.binary-package", MBs(130)),
                    new FakeReleaseAsset(fileName("linux", "tar.xz"), "application/x-xz", MBs(130)),
                    new FakeReleaseAsset(fileName("win", "exe"), "application/octet-stream", MBs(132)),
                    new FakeReleaseAsset(fileName("win", "msi"), "application/octet-stream", MBs(131)),
                    new FakeReleaseAsset(fileName("win", "zip"), "application/zip", MBs(130))
            );
        }

        private String fileName(String postfix, String extension) {
            return "Boomega-%s-%s.%s".formatted(System.getProperty("update.next_version"), postfix, extension);
        }

        private static int MBs(int bytes) {
            return bytes * (int) Math.pow(1024, 2);
        }
    }

    private static class FakeReleaseAsset extends ReleaseAsset {
        FakeReleaseAsset(String name, String contentType, int size) {
            setName(name);
            setContentType(contentType);
            setSize(size);
        }

        @NotNull
        @Override
        public InputStream openStream() {
            return new DelayedByteArrayInputStream(new byte[(int) getSize()], 1);
        }
    }
}
