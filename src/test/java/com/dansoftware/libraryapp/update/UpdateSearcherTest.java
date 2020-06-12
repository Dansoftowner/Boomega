package com.dansoftware.libraryapp.update;

import com.dansoftware.libraryapp.main.VersionInfo;
import com.dansoftware.libraryapp.update.util.RecordedNotifier;
import com.dansoftware.libraryapp.update.util.StaticLoader;
import com.dansoftware.libraryapp.update.util.ThrowLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UpdateSearcherTest {

    private final RecordedNotifier recordedNotifier;

    public UpdateSearcherTest() {
        recordedNotifier = new RecordedNotifier();
    }

    @Test
    public void testNewUpdate() {
        UpdateSearcher updateSearcher =
                new UpdateSearcher(new VersionInfo("0.0.0"), new StaticLoader("0.0.1"), recordedNotifier);

        updateSearcher.search();

        Assertions.assertTrue(recordedNotifier.isUpdateNotified());
    }

    @Test
    public void testNoUpdate() {
        UpdateSearcher updateSearcher =
                new UpdateSearcher(new VersionInfo("0.1.0"), new StaticLoader("0.0.1"), recordedNotifier);

        updateSearcher.search();

        Assertions.assertFalse(recordedNotifier.isUpdateNotified());
    }

    @Test
    public void testExceptionHandling() {
        UpdateSearcher updateSearcher =
                new UpdateSearcher(new VersionInfo("0.1.0"), new ThrowLoader(), recordedNotifier);

        updateSearcher.search();

        Assertions.assertTrue(recordedNotifier.isExceptionNotified());
    }

}
