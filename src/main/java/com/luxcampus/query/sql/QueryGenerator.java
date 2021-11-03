package com.luxcampus.query.sql;

import com.luxcampus.query.Query;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.StringJoiner;

public class QueryGenerator implements Query {
    @Override
    public String getAll(Class clazz) {
        StringBuilder stringBuilder = new StringBuilder("SELECT ");

        Table table = (Table) clazz.getAnnotation(Table.class);

        if(table == null) {
            throw new IllegalArgumentException("Annotation @Table not found in class "
                    + clazz.getName());
        }

        String tableName = table.name().isEmpty() ?
                clazz.getSimpleName().toLowerCase(Locale.ROOT) : table.name();

        StringJoiner stringJoiner = new StringJoiner(", ");

        for (Field declaredField : clazz.getDeclaredFields()) {
            Column column = declaredField.getAnnotation(Column.class);
            if(column != null) {
                String columnName = column.name().isEmpty() ? declaredField.getName() : column.name();
                stringJoiner.add(columnName);
            }
        }

        if(stringJoiner.length() == 0) {
            throw new IllegalArgumentException("Annotations @Column not found in class " + clazz.getName());
        }

        stringBuilder.append(stringJoiner);
        stringBuilder.append(" FROM ");
        stringBuilder.append(tableName);
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    @Override
    public String insert(Object value) {
        return null;
    }

    @Override
    public String update(Object value) {
        return null;
    }

    @Override
    public String getById(Class clazz, Object id) {
        return null;
    }

    @Override
    public String delete(Class clazz, Object id) {
        return null;
    }
}
