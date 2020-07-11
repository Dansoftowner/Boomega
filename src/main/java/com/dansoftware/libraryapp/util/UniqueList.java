package com.dansoftware.libraryapp.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class UniqueList<T> implements List<T> {

    private HashSet<T> hashSet;

    private List<T> wrapped;

    public UniqueList(@NotNull List<T> wrapped) {
        this.wrapped = Objects.requireNonNull(wrapped);
        this.hashSet = new HashSet<>(wrapped);
    }

    @Override
    public int size() {
        return wrapped.size();
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return wrapped.contains(o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return wrapped.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return wrapped.toArray();
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return wrapped.toArray(a);
    }

    @Override
    public boolean add(T t) {
        boolean result;
        if ((result = this.hashSet.add(t))) {
            this.wrapped.add(t);
        }

        return result;
    }

    @Override
    public boolean remove(Object o) {
        return this.wrapped.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return wrapped.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        return this.wrapped.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return this.wrapped.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return this.retainAll(c);
    }

    @Override
    public void clear() {
        this.wrapped.clear();
    }

    @Override
    public T get(int index) {
        return this.wrapped.get(index);
    }

    @Override
    public T set(int index, T element) {
        return this.wrapped.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        this.wrapped.set(index, element);
    }

    @Override
    public T remove(int index) {
        return this.wrapped.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.wrapped.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.wrapped.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator() {
        return this.wrapped.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return this.wrapped.listIterator(index);
    }

    @NotNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return this.wrapped.subList(fromIndex, toIndex);
    }
}
