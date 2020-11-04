package com.dansoftware.libraryapp.main;

import javafx.application.Platform;
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

    public ApplicationRestart() {
    }

    public void restartApp() throws RestartException {
        Optional<OSProcess> appProcess = getAppProcess();
        if (appProcess.isPresent()) {
            try {
                Runtime.getRuntime().exec(appProcess.get().getCommandLine());
            } catch (IOException e) {
                throw new RestartException("Couldn't execute the starter command with the OS", e);
            }
        } else throw new RestartException("Couldn't identify the process by PID");

        Platform.exit();
    }

    private Optional<OSProcess> getAppProcess() {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
        int currentPID = operatingSystem.getProcessId();
        return operatingSystem.getProcesses().stream()
                .filter(osProcess -> osProcess.getProcessID() == currentPID)
                .findAny();
    }

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
