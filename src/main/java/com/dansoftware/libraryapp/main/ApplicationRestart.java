package com.dansoftware.libraryapp.main;

import javafx.application.Platform;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * An {@link ApplicationRestart} object can restart the application.
 *
 * @author Daniel Gyorffy
 */
public class ApplicationRestart {

    public ApplicationRestart() {
    }

    private List<String> getJvmOptions() {
        return ManagementFactory.getRuntimeMXBean().getInputArguments();
    }

    private String getProcessExecutable() {
        return ProcessHandle.current().info().command()
                .orElseGet(() -> String.join(File.separator, System.getProperty("java.home"), "bin", "java"));
    }

    private String getCodeSourceLocation() {
        return getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm();
    }

    private String getCommandLine() {
        var cmdBuilder = new StringBuilder();
        String processExecutable = getProcessExecutable();
        cmdBuilder.append(processExecutable);

        if (FilenameUtils.getBaseName(processExecutable).endsWith("java")) {
            String codeSourceLocation = getCodeSourceLocation();
            if (!codeSourceLocation.endsWith(".jar")) {
                return null;
            }

            //the app is running as a regular java app
            cmdBuilder.append(StringUtils.SPACE);
            getJvmOptions().stream()
                    .map(jvmArg -> StringUtils.SPACE + jvmArg)
                    .forEach(cmdBuilder::append);
            cmdBuilder.append("-jar ").append(codeSourceLocation);
        }

        return cmdBuilder.toString();
    }

    public void restartApp() throws IOException {
        Runtime.getRuntime().exec(getCommandLine());
        Platform.exit();
    }
}
