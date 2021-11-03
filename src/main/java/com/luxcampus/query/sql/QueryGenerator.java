package com.luxcampus.query.sql;

import com.luxcampus.query.Query;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
                toSnakeCase(clazz.getSimpleName()) : table.name();

        StringJoiner stringJoiner = new StringJoiner(", ");

        for (Field declaredField : clazz.getDeclaredFields()) {
            Column column = declaredField.getAnnotation(Column.class);
            if(column != null) {
                String columnName = column.name().isEmpty() ? toSnakeCase(declaredField.getName()) : column.name();
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
        Class clazz = value.getClass();

        Table tableAnnotation = (Table) clazz.getAnnotation(Table.class);
        if(tableAnnotation == null) {
            throw new IllegalArgumentException("Annotation @Table not found in class "
                    + clazz.getName());
        }

        String tableName = tableAnnotation.name().isEmpty() ?
                toSnakeCase(clazz.getSimpleName()) : tableAnnotation.name();

        StringBuilder stringBuilder = new StringBuilder("INSERT INTO ");
        stringBuilder.append(tableName);
        stringBuilder.append("(");

        StringJoiner fieldsStringJoiner = new StringJoiner(", ");
        StringJoiner dataStringJoiner = new StringJoiner(", ");

        for (Field declaredField : clazz.getDeclaredFields()) {
            Column columnAnnotation = declaredField.getAnnotation(Column.class);

            if(columnAnnotation != null &&
                    declaredField.getAnnotation(AutoIncrement.class) == null) {
                String fieldName = columnAnnotation.name().isEmpty() ?
                        toSnakeCase(declaredField.getName()) : columnAnnotation.name();
                fieldsStringJoiner.add(fieldName);

                declaredField.setAccessible(true);

                try {
                    Object fieldValue = declaredField.get(value);
                    if(declaredField.getType().isPrimitive()) {
                        dataStringJoiner.add(fieldValue.toString());
                    } else {
                        dataStringJoiner.add("'" + fieldValue + "'");
                    }
                } catch (IllegalAccessException exception) {
                    exception.printStackTrace();
                }
            }
        }

        if(fieldsStringJoiner.length() == 0) {
            throw new IllegalArgumentException("Annotations @Column not found in class " + clazz.getName());
        }

        stringBuilder.append(fieldsStringJoiner);
        stringBuilder.append(") VALUES(");
        stringBuilder.append(dataStringJoiner);
        stringBuilder.append(");");

        return stringBuilder.toString();
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

    String toSnakeCase(String name) {
        StringBuilder className = new StringBuilder(name);

        int classNameLength = className.length();
        for (int i = 0; i < classNameLength; i++) {
            char symbol = className.charAt(i);
            if(Character.isUpperCase(symbol)) {
                StringBuilder newChar = new StringBuilder(Character.toString(symbol).toLowerCase(Locale.ROOT));
                if(i > 0) {
                    newChar.insert(0, "_");
                }
                className.replace(i, i + 1, newChar.toString());
            }

        }

        return className.toString();
    }
}
