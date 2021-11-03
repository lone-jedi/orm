package com.luxcampus.query;

public interface Query {
    String getAll(Class clazz);

    String insert(Object value);

    String update(Object value);

    String getById(Class clazz, Object id);

    String delete(Class clazz, Object id);
}
