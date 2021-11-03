package com.luxcampus.query.sql;

@Table(name = "Car")
public class Car {
    @Column
    @PrimaryKey
    private int id;
    @Column(name = "car_name")
    private String name;
    @Column(name = "car_age")
    private int age;

    public Car(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
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
}
