package com.luxcampus.query.sql;

@Table
public class Phone {
    @Column
    private int id;
    @Column
    public String model;
    @Column
    protected boolean isNew;

    public Phone(int id, String model, boolean isNew) {
        this.id = id;
        this.model = model;
        this.isNew = isNew;
    }

    public int getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public boolean isNew() {
        return isNew;
    }
}
