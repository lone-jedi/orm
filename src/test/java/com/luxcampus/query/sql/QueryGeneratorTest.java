package com.luxcampus.query.sql;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryGeneratorTest {
    final private QueryGenerator queryGenerator = new QueryGenerator();

    @Test
    public void testGetAll() {
        String expected = "SELECT id, name, age, gender FROM person;";
        String actual = queryGenerator.getAll(Person.class);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetAllWithoutTableAnnotation() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                queryGenerator.getAll(Empty.class));
        try {
            queryGenerator.getAll(Empty.class);
        } catch(IllegalArgumentException exception) {
            assertEquals("Annotation @Table not found in class com.luxcampus.query.sql.Empty", exception.getMessage());
        }
    }

    @Test
    public void testGetAllWithoutColumnAnnotation() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                queryGenerator.getAll(WithoutColumn.class));
        try {
            queryGenerator.getAll(WithoutColumn.class);
        } catch(IllegalArgumentException exception) {
            assertEquals("Annotations @Column not found in class com.luxcampus.query.sql.WithoutColumn", exception.getMessage());
        }
    }

    @Test
    public void testGetAllWithColumnAndTableAnnotationName() {
        String expected = "SELECT id, car_name, car_age FROM Car;";
        String actual = queryGenerator.getAll(Car.class);

        assertEquals(expected, actual);
    }

    @Test
    public void testInsertWithAutoIncrement() throws IllegalAccessException {
        Person person = new Person(2, "Alex", 20, false, Gender.Male);
        String expected = "INSERT INTO person(name, age, gender) VALUES('Alex', 20, 'Male');";

        assertEquals(expected, queryGenerator.insert(person));
    }

    @DisplayName("Test insert() without @AutoIncrement and with @Column and @Table names")
    @Test
    public void testInsertWithoutAutoIncrementAndWithColumnAndTableAnnotationsName() throws IllegalAccessException {
        Car car = new Car(3, "Ferrari", 8);
        String expected = "INSERT INTO Car(id, car_name, car_age) VALUES(3, 'Ferrari', 8);";

        assertEquals(expected, queryGenerator.insert(car));
    }

    @Test
    public void testInsertWithoutTableAnnotation() throws IllegalAccessException {
        Empty empty = new Empty();
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                queryGenerator.insert(empty));
        try {
            queryGenerator.insert(empty);
        } catch(IllegalArgumentException exception) {
            assertEquals("Annotation @Table not found in class com.luxcampus.query.sql.Empty", exception.getMessage());
        }
    }

    @Test
    public void testInsertWithoutColumnAnnotation() throws IllegalAccessException{
        WithoutColumn withoutColumn = new WithoutColumn();
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                queryGenerator.insert(withoutColumn));
        try {
            queryGenerator.insert(withoutColumn);
        } catch(IllegalArgumentException exception) {
            assertEquals("Annotations @Column not found in class com.luxcampus.query.sql.WithoutColumn", exception.getMessage());
        }
    }

    @Test
    public void testUpdate() {
        Person person = new Person(2, "Alex", 20, false, Gender.Male);
        String expected = "UPDATE person SET name='Alex', age=20, gender='Male' WHERE id=2;";
        assertEquals(expected, queryGenerator.update(person));
    }

    @Test
    public void testUpdateWithColumnAndAnnotationsName() {
        Car car = new Car(4, "BMW", 11);
        String expected = "UPDATE Car SET car_name='BMW', car_age=11 WHERE id=4;";
        assertEquals(expected, queryGenerator.update(car));
    }

    @Test
    public void testUpdateWithoutTableAnnotation() {
        Empty empty = new Empty();
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                queryGenerator.update(empty));
        try {
            queryGenerator.update(empty);
        } catch(IllegalArgumentException exception) {
            assertEquals("Annotation @Table not found in class com.luxcampus.query.sql.Empty", exception.getMessage());
        }
    }

    @Test
    public void testUpdateWithoutColumnAnnotation() {
        WithoutColumn withoutColumn = new WithoutColumn();
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                queryGenerator.update(withoutColumn));
        try {
            queryGenerator.update(withoutColumn);
        } catch(IllegalArgumentException exception) {
            assertEquals("Annotations @Column not found in class com.luxcampus.query.sql.WithoutColumn", exception.getMessage());
        }
    }

    @Test
    public void testGetById() {
        String expected = "SELECT id, name, age, gender FROM person WHERE id=2;";
        assertEquals(expected, queryGenerator.getById(Person.class, 2));
    }

    @Test
    public void testGetByIdWithColumnAndAnnotationsName() {
        String expected = "SELECT id, car_name, car_age FROM Car WHERE id=11;";
        assertEquals(expected, queryGenerator.getById(Car.class, 11));
    }

    @Test
    public void testGetByIdWithoutTableAnnotation() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                queryGenerator.getById(Empty.class, 11));
        try {
            queryGenerator.getById(Empty.class, 11);
        } catch(IllegalArgumentException exception) {
            assertEquals("Annotation @Table not found in class com.luxcampus.query.sql.Empty", exception.getMessage());
        }
    }

    @Test
    public void testGetByIdWithoutColumnAnnotation() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                queryGenerator.getById(WithoutColumn.class, 11));
        try {
            queryGenerator.getById(WithoutColumn.class, 11);
        } catch(IllegalArgumentException exception) {
            assertEquals("Annotations @Column not found in class com.luxcampus.query.sql.WithoutColumn", exception.getMessage());
        }
    }

    @Test
    public void testGetByIdFromNullId() {
        Assertions.assertThrows(NullPointerException.class, () ->
                queryGenerator.getById(WithoutColumn.class, null));
        try {
            queryGenerator.getById(WithoutColumn.class, null);
        } catch(NullPointerException exception) {
            assertEquals("Null ID is not supported in getById()", exception.getMessage());
        }
    }

    @Test
    public void testDelete() {
        String expected = "DELETE FROM person WHERE id=2;";
        assertEquals(expected, queryGenerator.delete(Person.class, 2));
    }

    @Test
    public void testDeleteWithColumnAndAnnotationsName() {
        String expected = "DELETE FROM Car WHERE id=2;";
        assertEquals(expected, queryGenerator.delete(Car.class, 11));
    }

    @Test
    public void testDeleteWithoutTableAnnotation() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                queryGenerator.delete(Empty.class, 11));
        try {
            queryGenerator.delete(Empty.class, 11);
        } catch(IllegalArgumentException exception) {
            assertEquals("Annotation @Table not found in class com.luxcampus.query.sql.Empty", exception.getMessage());
        }
    }

    @Test
    public void testDeleteWithoutColumnAnnotation() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                queryGenerator.delete(WithoutColumn.class, 11));
        try {
            queryGenerator.delete(WithoutColumn.class, 11);
        } catch(IllegalArgumentException exception) {
            assertEquals("Annotations @Column not found in class com.luxcampus.query.sql.WithoutColumn", exception.getMessage());
        }
    }

    @Test
    public void testDeleteFromNullId() {
        Assertions.assertThrows(NullPointerException.class, () ->
                queryGenerator.delete(WithoutColumn.class, null));
        try {
            queryGenerator.delete(WithoutColumn.class, null);
        } catch(NullPointerException exception) {
            assertEquals("Null ID is not supported in delete()", exception.getMessage());
        }
    }

    @Test
    public void testDeleteWithoutPrimaryKeyAnnotation() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                queryGenerator.delete(Phone.class, 3));
        try {
            queryGenerator.delete(Phone.class, 3);
        } catch(IllegalArgumentException exception) {
            assertEquals("Missed @PrimaryKey annotation in com.luxcampus.query.sql.Phone", exception.getMessage());
        }
    }

    @Test
    public void testGetByIdWithoutPrimaryKeyAnnotation() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                queryGenerator.getById(Phone.class, 3));
        try {
            queryGenerator.getById(Phone.class, 3);
        } catch(IllegalArgumentException exception) {
            assertEquals("Missed @PrimaryKey annotation in com.luxcampus.query.sql.Phone", exception.getMessage());
        }
    }
    @Test
    public void testToSnakeCase() {
        assertEquals("string_builder", queryGenerator.toSnakeCase("StringBuilder"));
        assertEquals("illegal_argument_exception", queryGenerator.toSnakeCase("IllegalArgumentException"));
    }
}
