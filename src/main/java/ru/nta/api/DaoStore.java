package ru.nta.api;

import java.util.List;

public interface DaoStore<T> extends AutoCloseable {
    void save(T post);

    List<T> getAll();

    T findById(String id);

}