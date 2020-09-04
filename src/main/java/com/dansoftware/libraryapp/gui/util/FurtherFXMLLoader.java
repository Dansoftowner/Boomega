package com.dansoftware.libraryapp.gui.util;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.util.Callback;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

/**
 * A {@link FurtherFXMLLoader} is an {@link FXMLLoader} implementation that has
 * additional features and behaviour.
 *
 * @author Daniel Gyorffy
 */
public class FurtherFXMLLoader extends FXMLLoader {

    private static class ControllerFactory implements Callback<Class<?>, Object> {
        private final Initializable controller;

        ControllerFactory(Initializable controller) {
            this.controller = controller;
        }

        @Override
        public Object call(Class<?> controllerClass) {
            if (controller.getClass() != controllerClass) {
                throw new ClassCastException(
                        String.format(
                                "Controller object '%s' cannot be used as a '%s'",
                                controller.getClass(),
                                controllerClass
                        )
                );
            }

            return controller;
        }
    }

    public FurtherFXMLLoader(@NotNull Initializable controller, @NotNull URL location, ResourceBundle resources) {
        super(location, resources, null, new ControllerFactory(controller), StandardCharsets.UTF_8);
    }

    @Override
    public <T> T load() throws RuntimeException {
        try {
            return super.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
