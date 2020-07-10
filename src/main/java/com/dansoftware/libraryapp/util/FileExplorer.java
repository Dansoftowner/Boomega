package com.dansoftware.libraryapp.util;

import com.sun.javafx.PlatformUtil;

import java.io.File;
import java.io.IOException;

public abstract class FileExplorer {

    private FileExplorer() {
    }

    protected abstract void showImpl(File file);

    private static final class WindowsFileExplorer extends FileExplorer {

        @Override
        protected void showImpl(File file) {
            String path = file.getPath();
            String command = "explorer.exe /select, \"" + path + "\"";
            try {
                Runtime.getRuntime().exec(command);
            } catch (IOException ignored) {
                //
            }
        }
    }

    private static final class LinuxFileExplorer extends FileExplorer {

        @Override
        protected void showImpl(File file) {
            String path = file.getAbsoluteFile().getAbsolutePath();
            String command = "nautilus " + path;
            try {
                Runtime.getRuntime().exec(command);
            } catch (IOException ignored) {
                //
            }
        }
    }

    private static final class MacFileExplorer extends FileExplorer {

        @Override
        protected void showImpl(File file) {
            throw new UnsupportedOperationException("Opening files on Mac NOT SUPPORTED YET");
        }
    }

    public static void show(File file) {
        if (PlatformUtil.isWindows()) {
            new WindowsFileExplorer().showImpl(file);
        } else if (PlatformUtil.isLinux()) {
            new LinuxFileExplorer().showImpl(file);
        } else if (PlatformUtil.isMac()) {
            new MacFileExplorer().showImpl(file);
        }
    }
}
