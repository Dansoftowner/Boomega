package com.dansoftware.boomega.update;

import com.dansoftware.boomega.config.source.ConfigSource;
import com.dansoftware.boomega.config.source.InMemorySource;
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

    private static class DummyConfigSource extends InMemorySource {
        @Override
        public boolean isCreated() {
            return false;
        }
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
