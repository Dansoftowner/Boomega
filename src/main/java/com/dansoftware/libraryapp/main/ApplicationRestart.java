package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.instance.ApplicationInstanceService;
import com.dansoftware.libraryapp.util.OsInfo;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.io.IOException;
import java.util.Optional;

/**
 * An {@link ApplicationRestart} object can restart the application.
 *
 * @author Daniel Gyorffy
 */
public class ApplicationRestart {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationRestart.class);

    private static final char NUL_CHAR = '\00';
    private static final char SPACE = '\u0020';

    /**
     * Restarts the application.
     *
     * @throws RestartException if the restart process doesn't proceeds for some reason
     */
    public void restartApp() throws RestartException {
        try {
            Optional<OSProcess> appProcess = getAppProcess();
            if (appProcess.isPresent()) {
                ApplicationInstanceService.release();
                Runtime.getRuntime().exec(getCommandLine(appProcess.get()));
            } else throw new RestartException("Couldn't identify the process by PID");
            Platform.exit();
        } catch (IOException e) {
            throw new RestartException("Couldn't execute the starter command with the OS", e);
        }
    }

    private Optional<OSProcess> getAppProcess() {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
        int currentPID = operatingSystem.getProcessId();
        return Optional.ofNullable(operatingSystem.getProcess(currentPID));
    }

    private String getCommandLine(@NotNull OSProcess osProcess) {
        if (OsInfo.isLinux() || OsInfo.isMac()) {
            return osProcess.getCommandLine().replace(NUL_CHAR, SPACE);
        }
        return osProcess.getCommandLine();
    }

    /**
     * Occurs when an {@link ApplicationRestart} couldn't restart the application
     * for some reason.
     */
    public static final class RestartException extends Exception {
        private RestartException(String message) {
            super(message);
        }

        private RestartException(String message, Throwable cause) {
            super(message, cause);
        }

        private RestartException(Throwable cause) {
            super(cause);
        }
    }
}
