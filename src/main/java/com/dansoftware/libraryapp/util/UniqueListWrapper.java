package com.dansoftware.libraryapp.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class UniqueListWrapper<T> implements List<T> {

    public static final class NonUniqueElementException extends RuntimeException {
        public NonUniqueElementException() {
        }

        public NonUniqueElementException(Object o) {
            super("Cannot add object '" + o + "' - UniqueList rejected it");
        }
    }

    private final HashSet<T> tracker;
    private final List<T> decorated;

    public UniqueListWrapper(@NotNull List<T> decorated) {
        this.decorated = Objects.requireNonNull(decorated, "The decorated-list mustn't be null");
        this.tracker = new HashSet<>();
    }

    @Override
    public int size() {
        return decorated.size();
    }

    @Override
    public boolean isEmpty() {
        return decorated.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return decorated.contains(o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return decorated.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return decorated.toArray();
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return decorated.toArray(a);
    }

    @Override
    public boolean add(T t) {
        if (!this.tracker.add(t))
            throw new NonUniqueElementException(t);

        decorated.add(t);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return decorated.remove(o) && tracker.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return decorated.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        throw new UnsupportedOperationException("addAll(int, Collection) is not supported");
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return this.decorated.removeAll(c) && this.tracker.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return this.decorated.retainAll(c) && this.tracker.retainAll(c);
    }

    @Override
    public void clear() {
        this.decorated.clear();
        this.tracker.clear();
    }

    @Override
    public T get(int index) {
        return this.decorated.get(index);
    }

    @Override
    public T set(int index, T element) {
        return this.decorated.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        if (this.tracker.add(element)) {
            this.decorated.add(index, element);
            return;
        }

        throw new NonUniqueElementException(element);
    }

    @Override
    public T remove(int index) {
        T removed = this.decorated.remove(index);
        this.tracker.remove(removed);
        return removed;
    }

    @Override
    public int indexOf(Object o) {
        return this.decorated.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.decorated.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator() {
        return this.decorated.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return this.decorated.listIterator();
    }

    @NotNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return this.decorated.subList(fromIndex, toIndex);
    }
}
