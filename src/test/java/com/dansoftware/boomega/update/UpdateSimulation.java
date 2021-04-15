package com.dansoftware.boomega.update;

import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.config.source.InMemorySource;
import com.dansoftware.boomega.main.Main;
import com.dansoftware.boomega.main.PropertiesResponsible;
import com.dansoftware.boomega.util.adapter.VersionInteger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UpdateSimulation {

    static {
        PropertiesResponsible.setupSystemProperties();
        UpdateSearcher.setInstanceFactory(FakeUpdateSearcher::new);
        initPreferences();
    }

    private static void initPreferences() {
        Preferences.setDefault(new Preferences(new InMemorySource() {
            @Override
            public boolean isCreated() {
                return false;
            }
        }));
    }

    public static void main(String[] args) {
        Main.main(new String[0]);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class FakeUpdateSearcher extends UpdateSearcher {

        FakeUpdateSearcher(@NotNull VersionInteger base) {
        }

        private String newVersion() {
            return Integer.toString(Integer.MAX_VALUE);
        }

        @Override
        public @NotNull UpdateSearchResult search() {
            UpdateSearchResult updateSearchResult = new UpdateSearchResult();
            updateSearchResult.setInformation(loadInfo());
            updateSearchResult.setNewUpdate(true);

            return updateSearchResult;
        }

        @Override
        protected UpdateInformation loadInfo() {
            return new UpdateInformation(newVersion(), "", buildFakeBinaries()) {
                @NotNull
                @Override
                public Reader reviewReader() {
                    return new InputStreamReader(getClass().getResourceAsStream("/com/dansoftware/boomega/update/sample-review.md"), StandardCharsets.UTF_8) {
                        @Override
                        public int read(char[] cbuf, int off, int len) throws IOException {
                            sleep(1_000);
                            return super.read(cbuf, off, len);
                        }
                    };
                }
            };
        }

        private List<DownloadableBinary> buildFakeBinaries() {
            return List.of(
                    new FakeBinary("Exe installer", "exe", 112),
                    new FakeBinary("Zip archive", "zip", 140),
                    new FakeBinary("Jar archive", "jar", 40)
            );
        }
    }

    static class FakeBinary extends DownloadableBinary {

        public FakeBinary(@NotNull String name, @NotNull String fileExtension, int size) {
            super(name, fileExtension, "", size);
        }

        @NotNull
        @Override
        public InputStream openStream() {
            return new InputStream() {
                private int fakeSize = getSize() * 1024 * 1024;

                @Override
                public int read() throws IOException {
                    sleep(500);
                    return fakeSize--;
                }
            };
        }
    }
}
