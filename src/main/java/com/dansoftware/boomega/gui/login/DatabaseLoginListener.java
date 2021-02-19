package com.dansoftware.boomega.gui.login;

import com.dansoftware.boomega.db.Database;
import org.jetbrains.annotations.NotNull;

public interface DatabaseLoginListener {
    void onDatabaseOpened(@NotNull Database database);
}
