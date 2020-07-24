package com.dansoftware.libraryapp.gui.updateview;

import com.dansoftware.libraryapp.gui.entry.Context;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.update.UpdateSearcher;
import org.jetbrains.annotations.NotNull;

public class UpdateActivity {

    private final Context context;
    private final UpdateSearcher.UpdateSearchResult updateSearchResult;

    public UpdateActivity(@NotNull Context context,
                          @NotNull UpdateSearcher.UpdateSearchResult updateSearchResult) {
        this.context = context;
        this.updateSearchResult = updateSearchResult;
    }

    public void show(boolean showError) {
        updateSearchResult.ifFailed(!showError ? null : exception -> {
            context.showErrorDialog(
                    I18N.getAlertMsg("update.activity.failed.title"),
                    I18N.getAlertMsg("update.activity.failed.msg"),
                    exception, buttonType -> {
                    });
        }).ifNewUpdateAvailable(updateInformation -> {
            System.out.println("Update available");
        });
    }
}
