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

package com.dansoftware.boomega.gui.entry;

import com.dansoftware.boomega.database.api.Database;
import com.dansoftware.boomega.database.api.DatabaseMeta;
import com.dansoftware.boomega.gui.api.Context;
import com.dansoftware.boomega.gui.databaseview.DatabaseActivity;
import com.dansoftware.boomega.gui.login.DatabaseLoginListener;
import com.dansoftware.boomega.gui.login.LoginActivity;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.stream.Collectors;

/**
 * An {@link EntryActivity} puts the {@link LoginActivity} and {@link DatabaseActivity} activity together.
 *
 * <p>
 * When an {@link EntryActivity} is started, it creates a {@link LoginActivity} and shows that and after the {@link LoginActivity} exited,
 * it launches a {@link DatabaseActivity}.
 *
 * <p>
 * Implements the {@link Context} interface so we can send message-requests to the backing activity whether it's a {@link LoginActivity}
 * or a {@link DatabaseActivity}.
 *
 * <p>
 * The active {@link EntryActivity} objects are tracked so they are accessible through the {@link #getShowingEntries()}.
 *
 * @author Daniel Gyorffy
 */
public class EntryActivity implements DatabaseLoginListener {

    private static final Logger logger = LoggerFactory.getLogger(EntryActivity.class);

    private static final List<WeakReference<EntryActivity>> instances =
            Collections.synchronizedList(new LinkedList<>());

    private Context subContext;

    public EntryActivity() {
        instances.add(new WeakReference<>(this));
    }

    @Override
    public void onUsedDatabaseOpened(@NotNull DatabaseMeta databaseMeta) {
        logger.debug("Database login request");
        Optional<Context> databaseActivity =
                DatabaseActivity.getByDatabase(databaseMeta)
                        .map(DatabaseActivity::getContext);
        if (databaseActivity.isPresent()) {
            logger.debug("Found database activity");
            databaseActivity.get().toFrontRequest();
        }
    }

    @Override
    public void onDatabaseOpened(@NotNull Database database) {
        var mainActivity = new DatabaseActivity(database);
        this.subContext = mainActivity.getContext();
        mainActivity.show();
    }

    /**
     * Starts the {@link EntryActivity}.
     */
    public void show() {
        if (!this.isShowing()) {
            var loginActivity = new LoginActivity(this);
            this.subContext = loginActivity.getContext();
            loginActivity.show();
        }
    }

    public boolean isShowing() {
        return subContext != null && subContext.isShowing();
    }

    public @NotNull Context getContext() {
        return subContext;
    }

    /**
     * Returns a list of {@link EntryActivity} objects that are showing.
     *
     * @return the {@link EntryActivity} list
     */
    public static List<EntryActivity> getShowingEntries() {
        return instances.stream()
                .map(WeakReference::get)
                .filter(Objects::nonNull)
                .filter(EntryActivity::isShowing)
                .collect(Collectors.toList());
    }
}
