package com.dansoftware.libraryapp.main.init;

import com.dansoftware.libraryapp.db.DatabaseMeta;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

/**
 * This class responsible for checking the application arguments
 */
public class AppArgumentHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationInitializer.class);

    private DatabaseMeta database;

    public AppArgumentHandler(List<String> args) {
        if (CollectionUtils.isEmpty(args)) {
            return;
        }

        String filePath = args.get(0);
        File file = new File(filePath);

        if (!file.exists()) {
            LOGGER.error("File that passed as an argument does not exists: '" + file.getAbsolutePath() + '\'');
            return;
        }

        this.database = new DatabaseMeta(file);
    }

    public Optional<DatabaseMeta> getDB() {
        return Optional.ofNullable(database);
    }
}
