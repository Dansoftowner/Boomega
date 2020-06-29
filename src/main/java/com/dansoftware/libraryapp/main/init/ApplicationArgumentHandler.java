package com.dansoftware.libraryapp.main.init;

import com.dansoftware.libraryapp.db.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

/**
 * This class responsible for checking the application arguments
 */
public class ApplicationArgumentHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationInitializer.class);

    private Account account;

    public ApplicationArgumentHandler(List<String> args) {
        if (!args.isEmpty()) {

            String filePath = args.get(0);
            File file = new File(filePath);

            if (file.exists()) {
                account = new Account(file);
            } else {
                LOGGER.error("Couldn't open file: " + file.getAbsolutePath(), new FileNotFoundException(filePath));
            }
        }
    }

    public Optional<Account> getAccount() {
        return Optional.ofNullable(account);
    }
}
