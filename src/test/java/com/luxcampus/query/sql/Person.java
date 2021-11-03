package com.luxcampus.query.sql;

@Table
public class Person {
    @Column
    @AutoIncrement
    @PrimaryKey
    private int id;
    @Column
    private String name;
    @Column
    private int age;
    private boolean isDating;
    @Column
    private Gender gender;

    public Person(int id, String name, int age, boolean isDating, Gender gender) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.isDating = isDating;
        this.gender = gender;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public boolean isDating() {
        return isDating;
    }

    public Gender getGender() {
        return gender;
    }
}
