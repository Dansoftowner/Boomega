package com.dansoftware.libraryapp.db.util;

import java.util.*;

public class RecordCollection<T> implements Collection<T> {

    private final Map<Integer, T> map;

    public RecordCollection() {
        this.map = new HashMap<>();
    }

    public Record addGet(T record) {
        int hashCode = record.hashCode();

        Record temp;
        if ((temp = map.get(hashCode)) == null) {
            map.put(hashCode, record);
            return record;
        }

        return temp;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.map.containsValue(o);
    }

    @Override
    public Iterator<T> iterator() {
        return this.map.values().iterator();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("RecordCollection cannot be converted to array");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("RecordCollection cannot be converted to array");
    }

    @Override
    public boolean add(T record) {
        return addGet(record) == record;
    }

    @Override
    public boolean remove(Object o) {
        return map.remove(o.hashCode()) != null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("RecordCollection does not support 'containsAll'");
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("RecordCollection does not support 'addAll'");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("RecordCollection does not support 'removeAll'");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("RecordCollection does not support 'retainAll'");
    }

    @Override
    public void clear() {
        this.map.clear();
    }
}
