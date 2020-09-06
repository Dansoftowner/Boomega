package com.dansoftware.libraryapp.gui.login.form;

import com.dansoftware.libraryapp.db.Database;
import org.jetbrains.annotations.NotNull;

public interface DatabaseLoginListener {
    void onDatabaseOpened(@NotNull Database database);
}
