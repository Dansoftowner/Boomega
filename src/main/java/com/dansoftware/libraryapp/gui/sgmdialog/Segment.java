package com.dansoftware.libraryapp.gui.sgmdialog;

import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public class Segment {

    private final Node content;
    private final Supplier<Node> contentSupplier;

    private final String title;

    public Segment(@NotNull String title, @NotNull Node content) {
        this.title = Objects.requireNonNull(title);
        this.content = content;
        this.contentSupplier = () -> null;
    }

    public Segment(@NotNull String title, @NotNull Supplier<Node> contentSupplier) {
        this.title = Objects.requireNonNull(title);
        this.contentSupplier = contentSupplier;
        this.content = null;
    }

    public String getTitle() {
        return title;
    }

    public Node getContent() {
        return content == null ? contentSupplier.get() : content;
    }
}
