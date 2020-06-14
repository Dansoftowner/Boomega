package com.dansoftware.libraryapp.main.init.step.impl;

import com.dansoftware.libraryapp.appdata.config.ConfigurationBase;
import com.dansoftware.libraryapp.main.Globals;
import com.dansoftware.libraryapp.main.init.step.Step;
import com.dansoftware.libraryapp.update.UpdateSearcher;
import com.dansoftware.libraryapp.update.loader.BaseLoader;
import com.dansoftware.libraryapp.update.notifier.GUINotifier;

public class UpdateCheck implements Step {
    @Override
    public void call() {
        if (ConfigurationBase.getGlobal().isSearchUpdatesOn()) {
            UpdateSearcher updateSearcher = new UpdateSearcher(Globals.VERSION_INFO, new BaseLoader(), new GUINotifier());
            updateSearcher.search();
        }
    }
}
