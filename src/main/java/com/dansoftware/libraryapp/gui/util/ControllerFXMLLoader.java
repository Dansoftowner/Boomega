package com.dansoftware.libraryapp.gui.util;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

/**
 * A {@link ControllerFXMLLoader} is an {@link FXMLLoader} implementation that can use
 * a custom object as controller for the loaded fxml.
 *
 * @author Daniel Gyorffy
 */
public class ControllerFXMLLoader extends FXMLLoader {

    public ControllerFXMLLoader(Initializable controller, URL location, ResourceBundle resources) {
        super(location, resources, null, (Class<?> controllerClass) -> {
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
        }, StandardCharsets.UTF_8);
    }

    @Override
    public <T> T load() throws RuntimeException {
        try {
            return super.load();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
