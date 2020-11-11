package com.dansoftware.libraryapp.update;

import com.dansoftware.libraryapp.main.Main;
import com.dansoftware.libraryapp.util.adapter.VersionInteger;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class UpdateTest {

    public static void main(String[] args) {
        UpdateSearcher.setInstanceFactory(versionInteger -> new AliasUpdateSearcher(versionInteger, mapArgumentsToDownloadable(args)));
        Main.main(new String[0]);
    }

    private static List<DownloadableBinary> mapArgumentsToDownloadable(String[] args) {
        var list = new ArrayList<DownloadableBinary>(args.length);
        try {
            for (String arg : args)
                list.add(new DownloadableBinary("X Installer", FilenameUtils.getExtension(arg), new File(arg).toURI().toURL().toString()));
            return list;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class AliasUpdateSearcher extends UpdateSearcher {

        private final VersionInteger base;
        private final List<DownloadableBinary> downloadableBinaries;

        AliasUpdateSearcher(@NotNull VersionInteger base, List<DownloadableBinary> downloadableBinaries) {
            this.base = base;
            this.downloadableBinaries = downloadableBinaries;
        }

        private String newVersion() {
            return (base.getValue() + 1) + ".0.0";
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
            return new UpdateInformation(newVersion(), "https://pastebin.com/raw/wJ3tciKc", this.downloadableBinaries);
        }
    }
}
