package com.dansoftware.dock.util;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ListObserver {
    private final List<BooleanBinding> observables;

    public ListObserver(int initialCapacity) {
        this.observables = new ArrayList<>(initialCapacity);
    }

    public void observeEmpty(ObservableList<?> items, Consumer<Boolean> handler) {
        BooleanBinding binding = Bindings.isEmpty(items);
        binding.addListener((observable, oldValue, newValue) -> handler.accept(newValue));
        observables.add(binding);
    }

}