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
        Class clazz = value.getClass();

        Table tableAnnotation = (Table) clazz.getAnnotation(Table.class);
        if(tableAnnotation == null) {
            throw new IllegalArgumentException("Annotation @Table not found in class "
                    + clazz.getName());
        }

        String tableName = tableAnnotation.name().isEmpty() ?
                toSnakeCase(clazz.getSimpleName()) : tableAnnotation.name();

        StringBuilder stringBuilder = new StringBuilder("UPDATE ");
        stringBuilder.append(tableName);
        stringBuilder.append(" SET ");

        StringJoiner stringJoiner = new StringJoiner(", ");

        for (Field declaredField : clazz.getDeclaredFields()) {
            Column columnAnnotation = declaredField.getAnnotation(Column.class);

            if(columnAnnotation != null
                    && declaredField.getAnnotation(AutoIncrement.class) == null
                    && declaredField.getAnnotation(PrimaryKey.class) == null) {
                declaredField.setAccessible(true);

                String fieldName = getColumnName(declaredField);
                StringBuilder result = new StringBuilder(fieldName);
                result.append("=");

                try {
                    result.append(getValue(declaredField, value));
                    stringJoiner.add(result);
                } catch (IllegalAccessException exception) {
                    exception.printStackTrace();
                }
            }
        }

        if(stringJoiner.length() == 0) {
            throw new IllegalArgumentException("Annotations @Column not found in class " + clazz.getName());
        }

        Field idField = getPrimaryKey(clazz);
        String keyFieldName = getColumnName(idField);

        stringBuilder.append(stringJoiner);
        stringBuilder.append(" WHERE ");
        stringBuilder.append(keyFieldName);
        stringBuilder.append("=");
        try {
            stringBuilder.append(getValue(idField, value));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        stringBuilder.append(";");
        return stringBuilder.toString();
    }

    @Override
    public String getById(Class clazz, Object id) {
        if(id == null) {
            throw new NullPointerException("Null ID is not supported in getById()");
        }

        String all = getAll(clazz);
        Field idField = getPrimaryKey(clazz);

        Column keyFieldAnnotation = idField.getAnnotation(Column.class);
        String keyFieldName = keyFieldAnnotation.name().isEmpty() ?
                toSnakeCase(idField.getName()) : keyFieldAnnotation.name();

        return all.substring(0, all.length() - 1) + " WHERE " + keyFieldName + "=" + id + ";";
    }

    @Override
    public String delete(Class clazz, Object id) {
        if(id == null) {
            throw new NullPointerException("Null ID is not supported in delete()");
        }

        Table table = (Table) clazz.getAnnotation(Table.class);
        if(table == null) {
            throw new IllegalArgumentException("Annotation @Table not found in class " + clazz.getName());
        }
        StringBuilder result = new StringBuilder("DELETE FROM ");
        String className = table.name().isEmpty() ? toSnakeCase(clazz.getSimpleName()) : table.name();

        Field idField = getPrimaryKey(clazz);
        String keyFieldName = getColumnName(idField);

        result.append(className);
        result.append(" WHERE ");
        result.append(keyFieldName);
        result.append("=");
        result.append(id);
        result.append(";");
        return result.toString();
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

    Field getPrimaryKey(Class clazz) {
        Field idField = null;
        for (Field declaredField : clazz.getDeclaredFields()) {
            declaredField.setAccessible(true);
            PrimaryKey primaryKey = declaredField.getAnnotation(PrimaryKey.class);
            if(primaryKey != null) {
                if(idField != null) {
                    throw new IllegalStateException("@PrimaryKey annotation can`t use twice in " + clazz.getName());
                }
                idField = declaredField;
            }
        }

        if(idField == null) {
            throw new IllegalArgumentException("Missed @PrimaryKey annotation in " + clazz.getName());
        }

        if(idField.getAnnotation(Column.class) == null) {
            throw new IllegalArgumentException("Annotations @Column not found in class "
                    + clazz.getName() + " at field '" + idField.getName() + "'");
        }

        return idField;
    }

    String getColumnName(Field field) {
        Column keyFieldAnnotation = field.getAnnotation(Column.class);
        String keyFieldName = keyFieldAnnotation.name().isEmpty() ?
                toSnakeCase(field.getName()) : keyFieldAnnotation.name();

        return keyFieldName;
    }

    String getValue(Field field, Object value) throws IllegalAccessException {
        StringBuilder result = new StringBuilder();
        field.setAccessible(true);
        Object fieldValue = field.get(value);
        if(field.getType().isPrimitive()) {
            result.append(fieldValue.toString());
        } else {
            result.append("'");
            result.append(fieldValue);
            result.append("'");
        }
        return result.toString();
    }
}
