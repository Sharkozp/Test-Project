package com.sharkozp.testproject.system.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by oleksandr on 1/24/16.
 */
@DatabaseTable(tableName = "persons")
public class Person {
    @DatabaseField
    private String status;
    @DatabaseField
    private String location;
    @DatabaseField
    private String photo;
    @DatabaseField(id = true)
    private Integer id;

    public Person() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
